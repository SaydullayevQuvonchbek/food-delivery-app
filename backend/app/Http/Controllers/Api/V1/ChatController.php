<?php

namespace App\Http\Controllers\Api\V1;

use App\Http\Controllers\Controller;
use App\Models\Chat;
use App\Models\Message;
use App\Models\User;
use Illuminate\Http\Request;

class ChatController extends Controller
{
    public function index(Request $request)
    {
        $userId = $request->user()->id;

        if (! Chat::where('user_id', $userId)->exists()) {
            $this->createSupportChat($request->user());
        }

        $chats = Chat::with(['courier:id,full_name,phone,avatar_url,role'])
            ->withCount(['messages as unread_count' => function ($q) use ($userId) {
                $q->where('is_read', false)->where('sender_id', '!=', $userId);
            }])
            ->where('user_id', $userId)
            ->get()
            ->map(function (Chat $chat) {
                $last = $chat->messages()->latest('id')->first();
                return [
                    'id' => $chat->id,
                    'order_id' => $chat->order_id,
                    'courier' => $chat->courier,
                    'unread_count' => (int) $chat->unread_count,
                    'last_message' => $last?->text,
                    'last_message_at' => $last?->created_at,
                    'updated_at' => $chat->updated_at,
                ];
            });

        return response()->json(['success' => true, 'data' => $chats]);
    }

    public function messages(Request $request, $chatId)
    {
        $chat = $this->authorizedChat($request, $chatId);
        if (! $chat) {
            return response()->json(['success' => false, 'message' => 'Chat topilmadi'], 404);
        }

        // Suhbatni ochganda qarshi tomon xabarlarini o'qilgan deb belgilaymiz
        Message::where('chat_id', $chat->id)
            ->where('sender_id', '!=', $request->user()->id)
            ->where('is_read', false)
            ->update(['is_read' => true]);

        $messages = Message::where('chat_id', $chat->id)->oldest('id')->get();

        return response()->json(['success' => true, 'data' => $messages]);
    }

    public function sendMessage(Request $request, $chatId)
    {
        $chat = $this->authorizedChat($request, $chatId);
        if (! $chat) {
            return response()->json(['success' => false, 'message' => 'Chat topilmadi'], 404);
        }

        $validated = $request->validate([
            'text' => 'required|string|max:2000',
            'media_url' => 'nullable|string|max:2048'
        ]);

        $message = Message::create([
            'chat_id' => $chat->id,
            'sender_id' => $request->user()->id,
            'text' => $validated['text'],
            'media_url' => $validated['media_url'] ?? null,
            'is_read' => false
        ]);

        $chat->touch();

        return response()->json(['success' => true, 'data' => $message], 201);
    }

    /**
     * Chat faqat so'rov yuborgan foydalanuvchiga (yoki uning kuryeriga) tegishli bo'lsa qaytariladi.
     */
    private function authorizedChat(Request $request, $chatId): ?Chat
    {
        $userId = $request->user()->id;

        return Chat::where('id', $chatId)
            ->where(function ($q) use ($userId) {
                $q->where('user_id', $userId)->orWhere('courier_id', $userId);
            })
            ->first();
    }

    private function createSupportChat(User $user): void
    {
        $supportAgent = User::whereIn('role', ['courier', 'operator', 'admin'])
            ->where('id', '!=', $user->id)
            ->orderByRaw("FIELD(role, 'operator', 'admin', 'courier')")
            ->first();

        if (! $supportAgent) {
            return; // Hech qanday xodim yo'q - bo'sh ro'yxat qaytadi
        }

        $chat = Chat::create([
            'user_id' => $user->id,
            'courier_id' => $supportAgent->id,
            'order_id' => null
        ]);

        Message::create([
            'chat_id' => $chat->id,
            'sender_id' => $supportAgent->id,
            'text' => "Salom! Buyurtmangiz yoki savollaringiz bo'yicha yordam berishga tayyorman 😊",
            'is_read' => false
        ]);
    }
}

<?php

namespace App\Http\Controllers\Api\V1;

use App\Http\Controllers\Controller;
use App\Models\Chat;
use App\Models\Message;
use Illuminate\Http\Request;

class ChatController extends Controller
{
    public function index(Request $request)
    {
        $chats = Chat::with(['courier', 'messages' => fn($q) => $q->latest()->limit(1)])
            ->where('user_id', $request->user()->id)
            ->get();

        if ($chats->isEmpty()) {
            $courier = \App\Models\User::where('role', 'courier')->first() ?? $request->user();
            $newChat = Chat::create([
                'user_id' => $request->user()->id,
                'courier_id' => $courier->id,
                'order_id' => null
            ]);
            Message::create([
                'chat_id' => $newChat->id,
                'sender_id' => $courier->id,
                'text' => "Salom! Buyurtmangiz yoki savollaringiz bo'yicha yordam berishga tayyorman 😊",
                'is_read' => false
            ]);
            $chats = Chat::with(['courier', 'messages' => fn($q) => $q->latest()->limit(1)])
                ->where('user_id', $request->user()->id)
                ->get();
        }

        return response()->json(['success' => true, 'data' => $chats]);
    }

    public function messages($chatId)
    {
        $messages = Message::where('chat_id', $chatId)->oldest()->get();
        return response()->json(['success' => true, 'data' => $messages]);
    }

    public function sendMessage(Request $request, $chatId)
    {
        $validated = $request->validate([
            'text' => 'required|string',
            'media_url' => 'nullable|string'
        ]);

        $message = Message::create([
            'chat_id' => $chatId,
            'sender_id' => $request->user()->id,
            'text' => $validated['text'],
            'media_url' => $validated['media_url'] ?? null,
            'is_read' => false
        ]);

        return response()->json(['success' => true, 'data' => $message], 201);
    }
}
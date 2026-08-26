<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\Chat;
use App\Models\Message;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

class ChatController extends Controller
{
    public function index(Request $request)
    {
        $chats = Chat::with(['user', 'messages' => fn($q) => $q->latest()->limit(1)])
            ->latest('updated_at')
            ->get();

        $selectedChat = null;
        $messages = collect();

        if ($chatId = $request->get('chat_id', $chats->first()?->id)) {
            $selectedChat = Chat::with('user')->find($chatId);
            if ($selectedChat) {
                $messages = Message::with('sender')->where('chat_id', $chatId)->oldest()->get();
            }
        }

        return view('admin.chat.index', compact('chats', 'selectedChat', 'messages'));
    }

    public function reply(Request $request, $chatId)
    {
        $request->validate([
            'text' => 'required|string|max:1000'
        ]);

        $chat = Chat::findOrFail($chatId);

        Message::create([
            'chat_id' => $chat->id,
            'sender_id' => Auth::id(),
            'text' => $request->text,
            'is_read' => false
        ]);

        $chat->touch();

        if ($request->wantsJson() || $request->ajax()) {
            return response()->json(['success' => true, 'message' => 'Xabar yuborildi']);
        }

        return back()->with('success', 'Xabar yuborildi.');
    }

    public function getMessagesApi($chatId)
    {
        $chat = Chat::with('user')->findOrFail($chatId);
        $messages = Message::with('sender')->where('chat_id', $chatId)->oldest()->get();
        
        // Mark incoming messages as read
        Message::where('chat_id', $chatId)
            ->where('sender_id', '!=', Auth::id())
            ->update(['is_read' => true]);

        return response()->json([
            'success' => true,
            'chat' => [
                'id' => $chat->id,
                'user_name' => $chat->user->full_name ?? 'Mijoz',
                'user_phone' => $chat->user->phone ?? 'Tel ko\'rsatilmagan'
            ],
            'messages' => $messages->map(fn($m) => [
                'id' => $m->id,
                'sender_id' => $m->sender_id,
                'is_me' => $m->sender_id == Auth::id(),
                'text' => $m->text,
                'time' => $m->created_at->format('H:i')
            ])
        ]);
    }
}
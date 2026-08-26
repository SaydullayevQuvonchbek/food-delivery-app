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

        return back()->with('success', 'Xabar yuborildi.');
    }
}
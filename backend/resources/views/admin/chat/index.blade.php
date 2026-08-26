@extends('admin.layouts.app')

@section('title', 'Mijozlar Bilan Jonli Chat')

@section('content')
<div class="bg-white rounded-3xl border border-slate-100 shadow-sm overflow-hidden flex h-[calc(100vh-140px)]">

    <!-- Left: Chat List -->
    <div class="w-80 border-r border-slate-100 flex flex-col flex-shrink-0">
        <div class="p-4 border-b border-slate-100 bg-slate-50/50">
            <h3 class="text-sm font-black text-slate-800">Mijozlar Muloqotlari</h3>
            <p class="text-[10px] text-slate-400">Mobil ilovadan yuborilgan savollar</p>
        </div>

        <div class="flex-1 overflow-y-auto divide-y divide-slate-50">
            @forelse($chats as $chat)
            <a href="{{ route('admin.chat.index', ['chat_id' => $chat->id]) }}" class="p-4 flex items-center space-x-3 transition block {{ $selectedChat && $selectedChat->id == $chat->id ? 'bg-orange-50/80 border-l-4 border-primary' : 'hover:bg-slate-50' }}">
                <div class="w-10 h-10 rounded-full bg-slate-200 flex items-center justify-center font-bold text-slate-700 text-xs flex-shrink-0">
                    {{ substr($chat->user->full_name ?? 'M', 0, 1) }}
                </div>
                <div class="flex-1 min-w-0">
                    <div class="flex items-center justify-between">
                        <h4 class="text-xs font-bold text-slate-800 truncate">{{ $chat->user->full_name ?? 'Mijoz' }}</h4>
                        <span class="text-[10px] text-slate-400">{{ $chat->updated_at->format('H:i') }}</span>
                    </div>
                    <p class="text-[11px] text-slate-500 truncate mt-0.5">
                        {{ $chat->messages->first()?->text ?? 'Yangi chat boshlandi...' }}
                    </p>
                </div>
            </a>
            @empty
            <div class="p-8 text-center text-slate-400 text-xs">
                Hozircha faol chatlar yo'q
            </div>
            @endforelse
        </div>
    </div>

    <!-- Right: Message View -->
    <div class="flex-1 flex flex-col min-w-0 bg-slate-50/30">
        @if($selectedChat)
            <!-- Chat Header -->
            <div class="p-4 border-b border-slate-100 bg-white flex items-center justify-between">
                <div class="flex items-center space-x-3">
                    <div class="w-10 h-10 rounded-full bg-primary/10 text-primary flex items-center justify-center font-bold text-sm">
                        {{ substr($selectedChat->user->full_name ?? 'M', 0, 1) }}
                    </div>
                    <div>
                        <h4 class="text-sm font-bold text-slate-800">{{ $selectedChat->user->full_name ?? 'Mijoz' }}</h4>
                        <p class="text-[11px] text-slate-400">{{ $selectedChat->user->phone ?? 'Telefon raqami yo\'q' }}</p>
                    </div>
                </div>
            </div>

            <!-- Messages Area -->
            <div class="flex-1 overflow-y-auto p-6 space-y-4">
                @foreach($messages as $msg)
                <div class="flex {{ $msg->sender_id == Auth::id() ? 'justify-end' : 'justify-start' }}">
                    <div class="max-w-md rounded-2xl p-4 text-xs shadow-sm {{ $msg->sender_id == Auth::id() ? 'bg-primary text-white rounded-br-none' : 'bg-white text-slate-800 border border-slate-100 rounded-bl-none' }}">
                        <p class="text-sm leading-relaxed">{{ $msg->text }}</p>
                        <span class="block text-[10px] mt-1.5 {{ $msg->sender_id == Auth::id() ? 'text-white/70 text-right' : 'text-slate-400' }}">
                            {{ $msg->created_at->format('H:i') }}
                        </span>
                    </div>
                </div>
                @endforeach
            </div>

            <!-- Reply Input Bar -->
            <div class="p-4 bg-white border-t border-slate-100">
                <form action="{{ route('admin.chat.reply', $selectedChat->id) }}" method="POST" class="flex items-center space-x-2">
                    @csrf
                    <input type="text" name="text" required placeholder="Javob xabarini yozing..." class="flex-1 px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary focus:bg-white">
                    <button type="submit" class="px-5 py-3 bg-primary hover:bg-primary-dark text-white font-bold rounded-xl text-sm shadow-md shadow-orange-500/30 transition">
                        <i class="fa-solid fa-paper-plane mr-1"></i>
                        <span>Yuborish</span>
                    </button>
                </form>
            </div>
        @else
            <div class="flex-1 flex flex-col items-center justify-center p-8 text-center text-slate-400">
                <i class="fa-solid fa-comments text-5xl mb-3 text-slate-300"></i>
                <p class="text-sm font-bold text-slate-600">Suhbat tanlanmagan</p>
                <p class="text-xs text-slate-400 mt-1">Chap tarafdan kerakli mijozni tanlang</p>
            </div>
        @endif
    </div>
</div>
@endsection
@extends('admin.layouts.app')

@section('title', 'Buyurtmalar Boshqaruvi')

@section('content')
<div class="space-y-6">

    <!-- Header & Search Bar -->
    <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
            <h3 class="text-xl font-black text-slate-800">Barcha Buyurtmalar</h3>
            <p class="text-xs text-slate-500 mt-0.5">Tushgan buyurtmalarni qabul qilish, kuryerga biriktirish va nazorat qilish</p>
        </div>

        <form method="GET" action="{{ route('admin.orders.index') }}" class="flex items-center space-x-2">
            <input type="hidden" name="status" value="{{ $status }}">
            <div class="relative">
                <input type="text" name="search" value="{{ request('search') }}" placeholder="№ Buyurtma yoki telefon..." class="w-64 pl-9 pr-4 py-2 bg-white border border-slate-200 rounded-xl text-xs focus:outline-none focus:ring-2 focus:ring-primary">
                <i class="fa-solid fa-magnifying-glass absolute left-3 top-2.5 text-slate-400 text-xs"></i>
            </div>
            <button type="submit" class="px-4 py-2 bg-slate-900 text-white rounded-xl text-xs font-bold hover:bg-slate-800 transition">Qidirish</button>
        </form>
    </div>

    <!-- Status Tabs -->
    <div class="flex items-center space-x-2 overflow-x-auto pb-2">
        <a href="{{ route('admin.orders.index', ['status' => 'all']) }}" class="px-4 py-2 rounded-xl text-xs font-bold transition whitespace-nowrap {{ $status === 'all' ? 'bg-primary text-white shadow-md shadow-orange-500/20' : 'bg-white text-slate-600 border border-slate-200 hover:bg-slate-50' }}">
            Barchasi ({{ $counts['all'] }})
        </a>
        <a href="{{ route('admin.orders.index', ['status' => 'pending']) }}" class="px-4 py-2 rounded-xl text-xs font-bold transition whitespace-nowrap {{ $status === 'pending' ? 'bg-amber-500 text-white shadow-md' : 'bg-white text-amber-600 border border-amber-200 hover:bg-amber-50' }}">
            🟡 Kutilmoqda ({{ $counts['pending'] }})
        </a>
        <a href="{{ route('admin.orders.index', ['status' => 'preparing']) }}" class="px-4 py-2 rounded-xl text-xs font-bold transition whitespace-nowrap {{ $status === 'preparing' ? 'bg-blue-600 text-white shadow-md' : 'bg-white text-blue-600 border border-blue-200 hover:bg-blue-50' }}">
            🔵 Tayyorlanmoqda ({{ $counts['preparing'] }})
        </a>
        <a href="{{ route('admin.orders.index', ['status' => 'on_the_way']) }}" class="px-4 py-2 rounded-xl text-xs font-bold transition whitespace-nowrap {{ $status === 'on_the_way' ? 'bg-purple-600 text-white shadow-md' : 'bg-white text-purple-600 border border-purple-200 hover:bg-purple-50' }}">
            🟣 Kuryerda / Yo'lda ({{ $counts['on_the_way'] }})
        </a>
        <a href="{{ route('admin.orders.index', ['status' => 'delivered']) }}" class="px-4 py-2 rounded-xl text-xs font-bold transition whitespace-nowrap {{ $status === 'delivered' ? 'bg-emerald-600 text-white shadow-md' : 'bg-white text-emerald-600 border border-emerald-200 hover:bg-emerald-50' }}">
            🟢 Yetkazildi ({{ $counts['delivered'] }})
        </a>
        <a href="{{ route('admin.orders.index', ['status' => 'cancelled']) }}" class="px-4 py-2 rounded-xl text-xs font-bold transition whitespace-nowrap {{ $status === 'cancelled' ? 'bg-rose-600 text-white shadow-md' : 'bg-white text-rose-600 border border-rose-200 hover:bg-rose-50' }}">
            🔴 Bekor qilingan ({{ $counts['cancelled'] }})
        </a>
    </div>

    <!-- Orders Table -->
    <div class="bg-white rounded-2xl border border-slate-100 shadow-sm overflow-hidden">
        <div class="overflow-x-auto">
            <table class="w-full text-left text-xs">
                <thead class="bg-slate-50 border-b border-slate-100 text-slate-400 uppercase tracking-wider font-bold">
                    <tr>
                        <th class="py-3.5 px-4">Buyurtma №</th>
                        <th class="py-3.5 px-4">Mijoz & Tel</th>
                        <th class="py-3.5 px-4">Mahsulotlar</th>
                        <th class="py-3.5 px-4">Jami Summa</th>
                        <th class="py-3.5 px-4">Kuryer</th>
                        <th class="py-3.5 px-4">Holati</th>
                        <th class="py-3.5 px-4 text-right">Amallar</th>
                    </tr>
                </thead>
                <tbody class="divide-y divide-slate-100 font-medium text-slate-700">
                    @forelse($orders as $order)
                    <tr class="hover:bg-slate-50/80 transition">
                        <!-- Order ID -->
                        <td class="py-4 px-4 font-bold text-slate-900">
                            <a href="{{ route('admin.orders.show', $order->id) }}" class="text-primary hover:underline font-extrabold">
                                {{ $order->order_number }}
                            </a>
                            <p class="text-[10px] text-slate-400 font-normal">{{ $order->created_at->format('H:i, d.m.Y') }}</p>
                        </td>

                        <!-- Customer -->
                        <td class="py-4 px-4">
                            <p class="font-bold text-slate-800">{{ $order->user->full_name ?? 'Mijoz' }}</p>
                            <p class="text-slate-500 font-mono text-[11px]">{{ $order->user->phone ?? 'Tel yo\'q' }}</p>
                        </td>

                        <!-- Items Summary -->
                        <td class="py-4 px-4 max-w-xs">
                            <div class="space-y-0.5">
                                @foreach($order->items as $item)
                                    <p class="truncate text-[11px]"><span class="font-bold text-primary">{{ $item->quantity }}x</span> {{ $item->food->name ?? 'Taom' }}</p>
                                @endforeach
                            </div>
                        </td>

                        <!-- Total Price -->
                        <td class="py-4 px-4 font-bold text-slate-900 text-sm">
                            {{ number_format($order->total_amount, 0, '.', ' ') }} so'm
                            <span class="block text-[10px] font-normal text-slate-400 uppercase">{{ $order->payment_method }}</span>
                        </td>

                        <!-- Courier -->
                        <td class="py-4 px-4">
                            @if($order->delivery && $order->delivery->courier)
                                <span class="inline-flex items-center text-xs text-slate-700 font-bold bg-slate-100 px-2.5 py-1 rounded-lg">
                                    <i class="fa-solid fa-person-biking mr-1.5 text-primary"></i>
                                    {{ $order->delivery->courier->full_name }}
                                </span>
                            @else
                                <form action="{{ route('admin.orders.assign-courier', $order->id) }}" method="POST" class="inline-flex items-center">
                                    @csrf
                                    <select name="courier_id" onchange="this.form.submit()" class="text-xs bg-slate-50 border border-slate-200 rounded-lg px-2 py-1 focus:ring-1 focus:ring-primary font-normal">
                                        <option value="">+ Kuryer biriktirish</option>
                                        @foreach($couriers as $courier)
                                            <option value="{{ $courier->id }}">{{ $courier->full_name }} ({{ $courier->phone }})</option>
                                        @endforeach
                                    </select>
                                </form>
                            @endif
                        </td>

                        <!-- Status Selector Form -->
                        <td class="py-4 px-4">
                            <form action="{{ route('admin.orders.status', $order->id) }}" method="POST" class="inline">
                                @csrf
                                <select name="status" onchange="this.form.submit()" class="text-xs font-bold rounded-lg px-2.5 py-1 border transition cursor-pointer
                                    {{ $order->status === 'pending' ? 'bg-amber-50 text-amber-700 border-amber-200' : '' }}
                                    {{ $order->status === 'preparing' ? 'bg-blue-50 text-blue-700 border-blue-200' : '' }}
                                    {{ $order->status === 'on_the_way' ? 'bg-purple-50 text-purple-700 border-purple-200' : '' }}
                                    {{ $order->status === 'delivered' ? 'bg-emerald-50 text-emerald-700 border-emerald-200' : '' }}
                                    {{ $order->status === 'cancelled' ? 'bg-rose-50 text-rose-700 border-rose-200' : '' }}
                                ">
                                    <option value="pending" {{ $order->status == 'pending' ? 'selected' : '' }}>🟡 Kutilmoqda</option>
                                    <option value="preparing" {{ $order->status == 'preparing' ? 'selected' : '' }}>🔵 Tayyorlanmoqda</option>
                                    <option value="on_the_way" {{ $order->status == 'on_the_way' ? 'selected' : '' }}>🟣 Kuryerda (Yo'lda)</option>
                                    <option value="delivered" {{ $order->status == 'delivered' ? 'selected' : '' }}>🟢 Yetkazildi</option>
                                    <option value="cancelled" {{ $order->status == 'cancelled' ? 'selected' : '' }}>🔴 Bekor qilish</option>
                                </select>
                            </form>
                        </td>

                        <!-- Actions (Show & Print) -->
                        <td class="py-4 px-4 text-right space-x-1.5 whitespace-nowrap">
                            <a href="{{ route('admin.orders.show', $order->id) }}" class="px-2.5 py-1.5 bg-slate-100 hover:bg-primary hover:text-white text-slate-700 rounded-lg text-xs font-bold transition inline-flex items-center space-x-1">
                                <i class="fa-solid fa-eye"></i>
                                <span>Ko'rish</span>
                            </a>
                            <a href="{{ route('admin.orders.print', $order->id) }}" target="_blank" class="px-2.5 py-1.5 bg-slate-800 hover:bg-slate-950 text-white rounded-lg text-xs font-bold transition inline-flex items-center space-x-1">
                                <i class="fa-solid fa-print"></i>
                                <span>Chek</span>
                            </a>
                        </td>
                    </tr>
                    @empty
                    <tr>
                        <td colspan="7" class="py-12 text-center text-slate-400">
                            <i class="fa-solid fa-inbox text-3xl mb-2 block"></i>
                            Ushbu toifada buyurtmalar mavjud emas.
                        </td>
                    </tr>
                    @endforelse
                </tbody>
            </table>
        </div>

        <!-- Pagination Links -->
        <div class="p-4 border-t border-slate-100">
            {{ $orders->links() }}
        </div>
    </div>
</div>
@endsection
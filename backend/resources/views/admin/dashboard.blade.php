@extends('admin.layouts.app')

@section('title', 'Boshqaruv Paneli (Dashboard)')

@section('content')
<div class="space-y-6">

    <!-- 4 Stats Cards -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-5">
        <!-- Card 1: Today Revenue -->
        <div class="bg-white p-5 rounded-2xl border border-slate-100 shadow-sm flex items-center justify-between">
            <div>
                <p class="text-xs font-bold text-slate-400 uppercase tracking-wider">Bugungi Tushum</p>
                <h3 class="text-2xl font-black text-slate-800 mt-1">{{ number_format($todayRevenue, 0, '.', ' ') }} <span class="text-xs font-semibold text-slate-400">so'm</span></h3>
                <p class="text-xs text-emerald-600 font-medium mt-1"><i class="fa-solid fa-arrow-trend-up mr-1"></i>Bugun amalga oshgan</p>
            </div>
            <div class="w-12 h-12 rounded-2xl bg-emerald-50 text-emerald-600 flex items-center justify-center text-xl">
                <i class="fa-solid fa-wallet"></i>
            </div>
        </div>

        <!-- Card 2: Today Orders -->
        <div class="bg-white p-5 rounded-2xl border border-slate-100 shadow-sm flex items-center justify-between">
            <div>
                <p class="text-xs font-bold text-slate-400 uppercase tracking-wider">Bugungi Buyurtmalar</p>
                <h3 class="text-2xl font-black text-slate-800 mt-1">{{ $todayOrdersCount }} <span class="text-xs font-semibold text-slate-400">ta</span></h3>
                <p class="text-xs text-blue-600 font-medium mt-1"><i class="fa-solid fa-clock mr-1"></i>24 soat ichida</p>
            </div>
            <div class="w-12 h-12 rounded-2xl bg-blue-50 text-blue-600 flex items-center justify-center text-xl">
                <i class="fa-solid fa-bag-shopping"></i>
            </div>
        </div>

        <!-- Card 3: Pending / Kitchen -->
        <div class="bg-white p-5 rounded-2xl border border-slate-100 shadow-sm flex items-center justify-between">
            <div>
                <p class="text-xs font-bold text-slate-400 uppercase tracking-wider">Faol / Oshxonada</p>
                <h3 class="text-2xl font-black text-primary mt-1">{{ $pendingOrdersCount }} <span class="text-xs font-semibold text-slate-400">ta</span></h3>
                <p class="text-xs text-orange-500 font-medium mt-1"><i class="fa-solid fa-fire mr-1"></i>Tayyorlanmoqda</p>
            </div>
            <div class="w-12 h-12 rounded-2xl bg-orange-50 text-primary flex items-center justify-center text-xl">
                <i class="fa-solid fa-kitchen-set"></i>
            </div>
        </div>

        <!-- Card 4: Total Customers -->
        <div class="bg-white p-5 rounded-2xl border border-slate-100 shadow-sm flex items-center justify-between">
            <div>
                <p class="text-xs font-bold text-slate-400 uppercase tracking-wider">Jami Mijozlar</p>
                <h3 class="text-2xl font-black text-slate-800 mt-1">{{ $totalCustomers }} <span class="text-xs font-semibold text-slate-400">ta</span></h3>
                <p class="text-xs text-purple-600 font-medium mt-1"><i class="fa-solid fa-users mr-1"></i>Mobil ilovada</p>
            </div>
            <div class="w-12 h-12 rounded-2xl bg-purple-50 text-purple-600 flex items-center justify-center text-xl">
                <i class="fa-solid fa-user-group"></i>
            </div>
        </div>
    </div>

    <!-- Quick Action / Live Alert Banner -->
    <div class="bg-gradient-to-r from-orange-500 to-amber-500 rounded-3xl p-6 text-white shadow-xl flex flex-col md:flex-row items-center justify-between gap-4">
        <div class="space-y-1">
            <h3 class="text-xl font-bold flex items-center gap-2">
                <i class="fa-solid fa-bell animate-bounce"></i>
                <span>Jonli Buyurtmalar Doskasi</span>
            </h3>
            <p class="text-white/90 text-sm">Yangi buyurtmalar avtomatik tarzda ovozli signal bilan tushadi va ekranda yangilanadi.</p>
        </div>
        <a href="{{ route('admin.orders.index') }}" class="px-6 py-3 bg-white text-orange-600 font-bold rounded-2xl shadow-lg hover:bg-orange-50 transition text-sm flex items-center space-x-2">
            <span>Buyurtmalarni Ko'rish</span>
            <i class="fa-solid fa-arrow-right"></i>
        </a>
    </div>

    <!-- 2 Column Layout: Recent Orders & Popular Foods -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">

        <!-- Recent Orders (2 Columns) -->
        <div class="lg:col-span-2 bg-white rounded-2xl border border-slate-100 shadow-sm p-6">
            <div class="flex items-center justify-between mb-5">
                <div>
                    <h3 class="text-base font-bold text-slate-800">Oxirgi Buyurtmalar</h3>
                    <p class="text-xs text-slate-400">Mijozlardan kelib tushgan yangi buyurtmalar</p>
                </div>
                <a href="{{ route('admin.orders.index') }}" class="text-xs font-bold text-primary hover:underline">Barchasini ko'rish &rarr;</a>
            </div>

            <div class="overflow-x-auto">
                <table class="w-full text-left text-xs">
                    <thead>
                        <tr class="border-b border-slate-100 text-slate-400 uppercase tracking-wider font-semibold">
                            <th class="pb-3">Buyurtma №</th>
                            <th class="pb-3">Mijoz</th>
                            <th class="pb-3">Summa</th>
                            <th class="pb-3">Holat</th>
                            <th class="pb-3 text-right">Amal</th>
                        </tr>
                    </thead>
                    <tbody class="divide-y divide-slate-100 text-slate-700 font-medium">
                        @forelse($recentOrders as $order)
                        <tr class="hover:bg-slate-50 transition">
                            <td class="py-3 font-bold text-slate-800">
                                <a href="{{ route('admin.orders.show', $order->id) }}" class="text-primary hover:underline">
                                    {{ $order->order_number }}
                                </a>
                                <p class="text-[10px] text-slate-400 font-normal">{{ $order->created_at->format('H:i, d.m.Y') }}</p>
                            </td>
                            <td class="py-3">
                                <p class="font-bold text-slate-800">{{ $order->user->full_name ?? 'Mijoz' }}</p>
                                <p class="text-[10px] text-slate-400">{{ $order->user->phone ?? 'Tel yo\'q' }}</p>
                            </td>
                            <td class="py-3 font-bold text-slate-800">
                                {{ number_format($order->total_amount, 0, '.', ' ') }} so'm
                            </td>
                            <td class="py-3">
                                @if($order->status == 'pending')
                                    <span class="px-2.5 py-1 rounded-full text-[10px] font-bold bg-amber-100 text-amber-800">Kutilmoqda</span>
                                @elseif($order->status == 'preparing')
                                    <span class="px-2.5 py-1 rounded-full text-[10px] font-bold bg-blue-100 text-blue-800">Tayyorlanmoqda</span>
                                @elseif($order->status == 'on_the_way')
                                    <span class="px-2.5 py-1 rounded-full text-[10px] font-bold bg-purple-100 text-purple-800">Yo'lda</span>
                                @elseif($order->status == 'delivered')
                                    <span class="px-2.5 py-1 rounded-full text-[10px] font-bold bg-emerald-100 text-emerald-800">Yetkazildi</span>
                                @else
                                    <span class="px-2.5 py-1 rounded-full text-[10px] font-bold bg-rose-100 text-rose-800">Bekor</span>
                                @endif
                            </td>
                            <td class="py-3 text-right space-x-1">
                                <a href="{{ route('admin.orders.show', $order->id) }}" class="p-1.5 bg-slate-100 hover:bg-primary hover:text-white text-slate-600 rounded-lg text-xs transition" title="Batafsil">
                                    <i class="fa-solid fa-eye"></i>
                                </a>
                                <a href="{{ route('admin.orders.print', $order->id) }}" target="_blank" class="p-1.5 bg-slate-100 hover:bg-slate-800 hover:text-white text-slate-600 rounded-lg text-xs transition" title="Chek chiqarish">
                                    <i class="fa-solid fa-print"></i>
                                </a>
                            </td>
                        </tr>
                        @empty
                        <tr>
                            <td colspan="5" class="py-8 text-center text-slate-400">Hozircha buyurtmalar yo'q</td>
                        </tr>
                        @endforelse
                    </tbody>
                </table>
            </div>
        </div>

        <!-- Top Selling Foods (1 Column) -->
        <div class="bg-white rounded-2xl border border-slate-100 shadow-sm p-6">
            <div class="flex items-center justify-between mb-5">
                <h3 class="text-base font-bold text-slate-800">Ommabop Taomlar</h3>
                <a href="{{ route('admin.foods.create') }}" class="text-xs font-bold text-primary hover:underline">+ Yangi</a>
            </div>

            <div class="space-y-4">
                @foreach($topFoods as $food)
                <div class="flex items-center space-x-3 p-2 rounded-xl hover:bg-slate-50 transition">
                    <img src="{{ $food->image_url ?? 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=100' }}" alt="{{ $food->name }}" class="w-12 h-12 rounded-xl object-cover border border-slate-100 flex-shrink-0">
                    <div class="flex-1 min-w-0">
                        <h4 class="text-xs font-bold text-slate-800 truncate">{{ $food->name }}</h4>
                        <p class="text-[10px] text-slate-400">{{ $food->category->name ?? 'Kategoriya' }}</p>
                        <p class="text-xs font-bold text-primary">{{ number_format($food->price, 0, '.', ' ') }} so'm</p>
                    </div>
                    <div>
                        <span class="text-xs font-bold text-emerald-600 bg-emerald-50 px-2 py-0.5 rounded-md border border-emerald-100">
                            ★ {{ number_format($food->rating, 1) }}
                        </span>
                    </div>
                </div>
                @endforeach
            </div>
        </div>
    </div>
</div>
@endsection
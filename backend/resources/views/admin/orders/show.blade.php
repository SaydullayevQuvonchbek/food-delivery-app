@extends('admin.layouts.app')

@section('title', 'Buyurtma Tafsilotlari - ' . $order->order_number)

@section('content')
<div class="max-w-5xl mx-auto space-y-6">

    <!-- Header Navigation -->
    <div class="flex items-center justify-between">
        <a href="{{ route('admin.orders.index') }}" class="text-xs font-bold text-slate-500 hover:text-slate-900 inline-flex items-center space-x-1.5">
            <i class="fa-solid fa-arrow-left"></i>
            <span>Barcha buyurtmalarga qaytish</span>
        </a>

        <div class="flex items-center space-x-2">
            <a href="{{ route('admin.orders.print', $order->id) }}" target="_blank" class="px-4 py-2 bg-slate-900 text-white rounded-xl text-xs font-bold hover:bg-slate-800 transition flex items-center space-x-2 shadow-sm">
                <i class="fa-solid fa-print"></i>
                <span>Chek chiqarish (80mm)</span>
            </a>
        </div>
    </div>

    <!-- Main Grid -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">

        <!-- Left 2 Cols: Order Items & Pricing -->
        <div class="lg:col-span-2 space-y-6">

            <!-- Card: Items List -->
            <div class="bg-white rounded-2xl border border-slate-100 shadow-sm p-6">
                <h3 class="text-base font-black text-slate-800 mb-4 flex items-center justify-between">
                    <span>Buyurtma qilingan taomlar</span>
                    <span class="text-xs font-bold text-slate-400 font-mono">{{ $order->order_number }}</span>
                </h3>

                <div class="divide-y divide-slate-100">
                    @foreach($order->items as $item)
                    <div class="py-3.5 flex items-center justify-between text-xs">
                        <div class="flex items-center space-x-3">
                            <img src="{{ $item->food->image_url ?? 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=100' }}" class="w-12 h-12 rounded-xl object-cover border border-slate-100">
                            <div>
                                <h4 class="font-bold text-slate-800 text-sm">{{ $item->food->name ?? 'Taom' }}</h4>
                                <p class="text-slate-400">{{ number_format($item->unit_price, 0, '.', ' ') }} so'm × {{ $item->quantity }} ta</p>
                            </div>
                        </div>
                        <p class="font-black text-slate-800 text-sm">{{ number_format($item->total_price, 0, '.', ' ') }} so'm</p>
                    </div>
                    @endforeach
                </div>

                <!-- Price Breakdown -->
                <div class="mt-6 pt-4 border-t border-slate-100 space-y-2 text-xs text-slate-600">
                    <div class="flex justify-between">
                        <span>Mahsulotlar summasi:</span>
                        <span class="font-bold">{{ number_format($order->subtotal, 0, '.', ' ') }} so'm</span>
                    </div>
                    <div class="flex justify-between">
                        <span>Yetkazib berish narxi:</span>
                        <span class="font-bold">{{ number_format($order->delivery_fee, 0, '.', ' ') }} so'm</span>
                    </div>
                    @if($order->discount_amount > 0)
                    <div class="flex justify-between text-emerald-600 font-bold">
                        <span>Chegirma:</span>
                        <span>- {{ number_format($order->discount_amount, 0, '.', ' ') }} so'm</span>
                    </div>
                    @endif
                    <div class="flex justify-between text-base font-black text-slate-900 pt-3 border-t border-slate-200">
                        <span>Jami to'lov:</span>
                        <span class="text-primary">{{ number_format($order->total_amount, 0, '.', ' ') }} so'm</span>
                    </div>
                </div>
            </div>
        </div>

        <!-- Right 1 Col: Customer & Status Control -->
        <div class="space-y-6">

            <!-- Card: Status Update -->
            <div class="bg-white rounded-2xl border border-slate-100 shadow-sm p-6">
                <h3 class="text-sm font-black text-slate-800 mb-3">Holatni O'zgartirish</h3>

                <form action="{{ route('admin.orders.status', $order->id) }}" method="POST" class="space-y-3">
                    @csrf
                    <div>
                        <select name="status" class="w-full text-xs font-bold p-3 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-primary">
                            <option value="pending" {{ $order->status == 'pending' ? 'selected' : '' }}>🟡 Kutilmoqda (Pending)</option>
                            <option value="preparing" {{ $order->status == 'preparing' ? 'selected' : '' }}>🔵 Tayyorlanmoqda (Preparing)</option>
                            <option value="on_the_way" {{ $order->status == 'on_the_way' ? 'selected' : '' }}>🟣 Kuryerda (On the way)</option>
                            <option value="delivered" {{ $order->status == 'delivered' ? 'selected' : '' }}>🟢 Yetkazildi (Delivered)</option>
                            <option value="cancelled" {{ $order->status == 'cancelled' ? 'selected' : '' }}>🔴 Bekor qilish (Cancelled)</option>
                        </select>
                    </div>

                    <button type="submit" class="w-full py-2.5 bg-primary hover:bg-primary-dark text-white rounded-xl font-bold text-xs shadow-md shadow-orange-500/30 transition">
                        Holatni Saqlash
                    </button>
                </form>
            </div>

            <!-- Card: Customer Info -->
            <div class="bg-white rounded-2xl border border-slate-100 shadow-sm p-6 text-xs space-y-3">
                <h3 class="text-sm font-black text-slate-800 mb-2">Mijoz Ma'lumotlari</h3>
                <div>
                    <p class="text-slate-400 font-medium">Ismi:</p>
                    <p class="font-bold text-slate-800 text-sm">{{ $order->user->full_name ?? 'Noma\'lum' }}</p>
                </div>
                <div>
                    <p class="text-slate-400 font-medium">Telefon:</p>
                    <a href="tel:{{ $order->user->phone ?? '' }}" class="font-bold text-primary text-sm hover:underline">
                        {{ $order->user->phone ?? 'Telefon kiritilmagan' }}
                    </a>
                </div>
                <div>
                    <p class="text-slate-400 font-medium">Yetkazish Manzili:</p>
                    <p class="font-bold text-slate-800 mt-0.5">
                        {{ $order->address->address_line ?? 'Toshkent shahri (Standart)' }}
                    </p>
                </div>
                <div>
                    <p class="text-slate-400 font-medium">To'lov usuli:</p>
                    <p class="font-bold text-slate-800 uppercase">{{ $order->payment_method }} ({{ $order->payment_status }})</p>
                </div>
            </div>

            <!-- Card: Courier Info -->
            <div class="bg-white rounded-2xl border border-slate-100 shadow-sm p-6 text-xs space-y-3">
                <h3 class="text-sm font-black text-slate-800 mb-2">Yetkazib Beruvchi Kuryer</h3>
                <form action="{{ route('admin.orders.assign-courier', $order->id) }}" method="POST" class="space-y-3">
                    @csrf
                    <select name="courier_id" class="w-full text-xs font-medium p-2.5 bg-slate-50 border border-slate-200 rounded-xl focus:ring-2 focus:ring-primary">
                        <option value="">Kuryerni tanlang...</option>
                        @foreach($couriers as $courier)
                            <option value="{{ $courier->id }}" {{ $order->delivery && $order->delivery->courier_id == $courier->id ? 'selected' : '' }}>
                                {{ $courier->full_name }} ({{ $courier->phone }})
                            </option>
                        @endforeach
                    </select>
                    <button type="submit" class="w-full py-2 bg-slate-900 hover:bg-slate-800 text-white rounded-xl font-bold text-xs transition">
                        Kuryerni Biriktirish
                    </button>
                </form>
            </div>
        </div>
    </div>
</div>
@endsection
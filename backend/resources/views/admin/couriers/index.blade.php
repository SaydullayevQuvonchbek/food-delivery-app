@extends('admin.layouts.app')

@section('title', 'Kuryerlar Boshqaruvi')

@section('content')
<div class="space-y-6" x-data="{ showModal: false }">

    <!-- Header Actions -->
    <div class="flex items-center justify-between">
        <div>
            <h3 class="text-xl font-black text-slate-800">Yetkazib Beruvchi Kuryerlar</h3>
            <p class="text-xs text-slate-500 mt-0.5">Kuryerlar ro'yxati, telefon raqamlari va faollik holati</p>
        </div>

        <button @click="showModal = true" class="px-4 py-2.5 bg-primary hover:bg-primary-dark text-white rounded-xl text-xs font-bold transition flex items-center space-x-2 shadow-lg shadow-orange-500/30">
            <i class="fa-solid fa-user-plus"></i>
            <span>Yangi Kuryer Qo'shish</span>
        </button>
    </div>

    <!-- Couriers Grid -->
    <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-5">
        @forelse($couriers as $courier)
        <div class="bg-white rounded-2xl border border-slate-100 shadow-sm p-5 flex flex-col justify-between hover:shadow-md transition">
            <div class="flex items-center space-x-3.5">
                <div class="w-12 h-12 rounded-2xl bg-orange-50 text-primary flex items-center justify-center text-xl flex-shrink-0">
                    <i class="fa-solid fa-person-biking"></i>
                </div>
                <div class="min-w-0">
                    <h4 class="font-bold text-slate-800 text-sm truncate">{{ $courier->full_name }}</h4>
                    <a href="tel:{{ $courier->phone }}" class="text-xs font-bold text-primary hover:underline block truncate">{{ $courier->phone ?? 'Tel yo\'q' }}</a>
                </div>
            </div>

            <div class="mt-4 pt-3 border-t border-slate-100 flex items-center justify-between text-xs">
                <div>
                    <span class="text-[10px] text-slate-400 block font-medium">Faol yetkazishlar:</span>
                    <span class="font-bold text-slate-800">{{ $courier->active_deliveries_count }} ta buyurtma</span>
                </div>

                <form action="{{ route('admin.couriers.toggle', $courier->id) }}" method="POST" class="inline">
                    @csrf
                    <button type="submit" class="px-3 py-1 rounded-full text-[10px] font-bold transition {{ $courier->status === 'active' ? 'bg-emerald-50 text-emerald-600 hover:bg-emerald-100' : 'bg-slate-100 text-slate-400 hover:bg-slate-200' }}">
                        {{ $courier->status === 'active' ? '● Faol' : '○ Nofaol' }}
                    </button>
                </form>
            </div>
        </div>
        @empty
        <div class="col-span-full py-16 text-center text-slate-400 bg-white rounded-2xl border border-slate-100">
            <i class="fa-solid fa-motorcycle text-4xl mb-3 block text-slate-300"></i>
            Hozircha kuryerlar ro'yxatga olinmagan.
        </div>
        @endforelse
    </div>

    <!-- Add Courier Modal -->
    <div x-show="showModal" x-cloak class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm">
        <div @click.away="showModal = false" class="bg-white rounded-3xl p-6 max-w-md w-full shadow-2xl border border-slate-100">
            <h3 class="text-lg font-bold text-slate-800 mb-4">Yangi Kuryer Qo'shish</h3>

            <form action="{{ route('admin.couriers.store') }}" method="POST" class="space-y-4">
                @csrf
                <div>
                    <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">F.I.O. (Ism Familiya) *</label>
                    <input type="text" name="full_name" required placeholder="Aziz Rahimov" class="w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:ring-2 focus:ring-primary">
                </div>

                <div>
                    <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">Telefon Raqami *</label>
                    <input type="text" name="phone" required placeholder="+998 90 123 45 67" class="w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:ring-2 focus:ring-primary">
                </div>

                <div>
                    <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">Email *</label>
                    <input type="email" name="email" required placeholder="courier1@insof.uz" class="w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:ring-2 focus:ring-primary">
                </div>

                <div>
                    <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">Parol *</label>
                    <input type="password" name="password" required placeholder="••••••••" class="w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:ring-2 focus:ring-primary">
                </div>

                <div class="flex justify-end space-x-2 pt-3">
                    <button type="button" @click="showModal = false" class="px-4 py-2 bg-slate-100 text-slate-600 rounded-xl text-xs font-bold">Bekor qilish</button>
                    <button type="submit" class="px-5 py-2 bg-primary text-white rounded-xl text-xs font-bold shadow-md shadow-orange-500/30">Qo'shish</button>
                </div>
            </form>
        </div>
    </div>
</div>
@endsection
@extends('admin.layouts.app')

@section('title', 'Promokodlar va Chegirmalar')

@section('content')
<div class="space-y-6" x-data="{ showModal: false }">

    <!-- Header Actions -->
    <div class="flex items-center justify-between">
        <div>
            <h3 class="text-xl font-black text-slate-800">Promokodlar va Aksiya Chegirmalari</h3>
            <p class="text-xs text-slate-500 mt-0.5">Mijozlar buyurtma berishda kiritadigan chegirma kodlarini boshqarish</p>
        </div>

        <button @click="showModal = true" class="px-4 py-2.5 bg-primary hover:bg-primary-dark text-white rounded-xl text-xs font-bold transition flex items-center space-x-2 shadow-lg shadow-orange-500/30">
            <i class="fa-solid fa-ticket"></i>
            <span>Yangi Promokod</span>
        </button>
    </div>

    <!-- Promos Grid -->
    <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-5">
        @forelse($promotions as $promo)
        <div class="bg-white rounded-2xl border border-slate-100 shadow-sm p-5 flex flex-col justify-between hover:shadow-md transition relative overflow-hidden">
            <div class="flex items-center justify-between">
                <span class="px-3 py-1 bg-orange-50 text-primary font-black text-sm rounded-xl font-mono border border-orange-100 tracking-wider">
                    {{ $promo->code }}
                </span>
                <span class="px-2 py-0.5 rounded-full text-[10px] font-bold {{ $promo->is_active ? 'bg-emerald-50 text-emerald-600' : 'bg-slate-100 text-slate-400' }}">
                    {{ $promo->is_active ? 'Faol' : 'Nofaol' }}
                </span>
            </div>

            <div class="mt-4">
                <h4 class="text-sm font-bold text-slate-800">{{ $promo->title ?? 'Chegirma' }}</h4>
                <p class="text-xl font-black text-slate-900 mt-1">
                    @if($promo->discount_type === 'percent')
                        {{ number_format($promo->discount_value, 0) }}% <span class="text-xs text-slate-400 font-normal">chegirma</span>
                    @else
                        {{ number_format($promo->discount_value, 0, '.', ' ') }} <span class="text-xs text-slate-400 font-normal">so'm chegirma</span>
                    @endif
                </p>
                <p class="text-[11px] text-slate-400 mt-1">
                    Min. buyurtma: {{ number_format($promo->min_order_amount, 0, '.', ' ') }} so'm
                </p>
                @if($promo->valid_until)
                <p class="text-[10px] text-slate-400 mt-0.5">
                    Amal qilish muddati: {{ $promo->valid_until->format('d.m.Y') }} gacha
                </p>
                @endif
            </div>

            <div class="mt-5 pt-3 border-t border-slate-100 flex items-center justify-between text-xs">
                <span class="text-slate-400 text-[11px]">
                    Ishlatilgan: <b class="text-slate-700">{{ $promo->usage_count }} marta</b>
                </span>

                <div class="flex items-center space-x-1">
                    <form action="{{ route('admin.promotions.toggle', $promo->id) }}" method="POST" class="inline">
                        @csrf
                        <button type="submit" title="Holatni o'zgartirish" class="p-2 bg-slate-100 hover:bg-slate-900 hover:text-white text-slate-600 rounded-lg text-xs transition">
                            <i class="fa-solid fa-power-off"></i>
                        </button>
                    </form>

                    <form action="{{ route('admin.promotions.destroy', $promo->id) }}" method="POST" onsubmit="return confirm('Promokodni o\'chirmoqchimisiz?')" class="inline">
                        @csrf
                        @method('DELETE')
                        <button type="submit" class="p-2 bg-slate-100 hover:bg-rose-600 hover:text-white text-slate-600 rounded-lg text-xs transition">
                            <i class="fa-solid fa-trash"></i>
                        </button>
                    </form>
                </div>
            </div>
        </div>
        @empty
        <div class="col-span-full py-16 text-center text-slate-400 bg-white rounded-2xl border border-slate-100">
            <i class="fa-solid fa-ticket text-4xl mb-3 block text-slate-300"></i>
            Hozircha promokodlar yaratilmagan.
        </div>
        @endforelse
    </div>

    <!-- Create Promo Modal -->
    <div x-show="showModal" x-cloak class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm">
        <div @click.away="showModal = false" class="bg-white rounded-3xl p-6 max-w-md w-full shadow-2xl border border-slate-100">
            <h3 class="text-lg font-bold text-slate-800 mb-4">Yangi Promokod Yaratish</h3>

            <form action="{{ route('admin.promotions.store') }}" method="POST" class="space-y-4">
                @csrf
                <div>
                    <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">Promokod Nomi (Kodi) *</label>
                    <input type="text" name="code" required placeholder="Masalan: INSOF2026" class="w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm font-mono font-bold uppercase focus:ring-2 focus:ring-primary">
                </div>

                <div>
                    <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">Aksiya Sarlavhasi</label>
                    <input type="text" name="title" placeholder="Yangi mijozlar uchun 15% chegirma" class="w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:ring-2 focus:ring-primary">
                </div>

                <div class="grid grid-cols-2 gap-3">
                    <div>
                        <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">Chegirma Turi *</label>
                        <select name="discount_type" required class="w-full px-3 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs focus:ring-2 focus:ring-primary">
                            <option value="fixed">So'mda (Aniq summa)</option>
                            <option value="percent">Foizda (%)</option>
                        </select>
                    </div>
                    <div>
                        <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">Miqdori *</label>
                        <input type="number" name="discount_value" required placeholder="15000 yoki 15" class="w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:ring-2 focus:ring-primary">
                    </div>
                </div>

                <div class="grid grid-cols-2 gap-3">
                    <div>
                        <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">Min. Buyurtma (so'm)</label>
                        <input type="number" name="min_order_amount" placeholder="50000" class="w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:ring-2 focus:ring-primary">
                    </div>
                    <div>
                        <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">Muddati (Gacha)</label>
                        <input type="date" name="valid_until" class="w-full px-3 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs focus:ring-2 focus:ring-primary">
                    </div>
                </div>

                <div class="flex justify-end space-x-2 pt-3">
                    <button type="button" @click="showModal = false" class="px-4 py-2 bg-slate-100 text-slate-600 rounded-xl text-xs font-bold">Bekor qilish</button>
                    <button type="submit" class="px-5 py-2 bg-primary text-white rounded-xl text-xs font-bold shadow-md shadow-orange-500/30">Promokodni Yaratish</button>
                </div>
            </form>
        </div>
    </div>
</div>
@endsection
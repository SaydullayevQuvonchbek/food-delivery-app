@extends('admin.layouts.app')

@section('title', 'Taomlar Menyusi')

@section('content')
<div class="space-y-6">

    <!-- Header Actions -->
    <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
            <h3 class="text-xl font-black text-slate-800">Barcha Taomlar</h3>
            <p class="text-xs text-slate-500 mt-0.5">Menyudagi taomlarni tahrirlash, rasm yuklash va narxlarni boshqarish</p>
        </div>

        <div class="flex items-center space-x-3">
            <a href="{{ route('admin.foods.create') }}" class="px-4 py-2.5 bg-primary hover:bg-primary-dark text-white rounded-xl text-xs font-bold transition flex items-center space-x-2 shadow-lg shadow-orange-500/30">
                <i class="fa-solid fa-plus"></i>
                <span>Yangi Taom Qo'shish</span>
            </a>
        </div>
    </div>

    <!-- Filters & Category Chips -->
    <div class="flex items-center space-x-2 overflow-x-auto pb-2">
        <a href="{{ route('admin.foods.index') }}" class="px-3.5 py-1.5 rounded-xl text-xs font-bold transition whitespace-nowrap {{ !request('category_id') ? 'bg-slate-900 text-white' : 'bg-white text-slate-600 border border-slate-200 hover:bg-slate-50' }}">
            Barchasi
        </a>
        @foreach($categories as $cat)
        <a href="{{ route('admin.foods.index', ['category_id' => $cat->id]) }}" class="px-3.5 py-1.5 rounded-xl text-xs font-bold transition whitespace-nowrap {{ request('category_id') == $cat->id ? 'bg-primary text-white shadow-md' : 'bg-white text-slate-600 border border-slate-200 hover:bg-slate-50' }}">
            <span>{{ $cat->icon }}</span> {{ $cat->name }}
        </a>
        @endforeach
    </div>

    <!-- Foods Grid -->
    <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-5">
        @forelse($foods as $food)
        <div class="bg-white rounded-2xl border border-slate-100 shadow-sm overflow-hidden flex flex-col group hover:shadow-md transition">
            <!-- Image & Status Badge -->
            <div class="relative h-44 bg-slate-100 overflow-hidden">
                <img src="{{ $food->image_url ?? 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400' }}" alt="{{ $food->name }}" class="w-full h-full object-cover group-hover:scale-105 transition duration-300">
                <div class="absolute top-3 left-3 flex items-center space-x-1.5">
                    @if($food->is_available)
                        <span class="px-2.5 py-1 rounded-full text-[10px] font-bold bg-emerald-500 text-white shadow-sm">Sotuvda</span>
                    @else
                        <span class="px-2.5 py-1 rounded-full text-[10px] font-bold bg-rose-500 text-white shadow-sm">Stop-list (Tugagan)</span>
                    @endif
                </div>
                <div class="absolute top-3 right-3 bg-black/60 backdrop-blur-sm text-white px-2 py-0.5 rounded-lg text-[10px] font-bold">
                    ★ {{ number_format($food->rating, 1) }}
                </div>
            </div>

            <!-- Content -->
            <div class="p-4 flex-1 flex flex-col justify-between space-y-3">
                <div>
                    <span class="text-[10px] font-bold text-slate-400 uppercase tracking-wider">{{ $food->category->name ?? 'Kategoriya' }}</span>
                    <h4 class="font-bold text-slate-800 text-sm line-clamp-1 mt-0.5">{{ $food->name }}</h4>
                    <p class="text-slate-400 text-xs line-clamp-2 mt-1">{{ $food->description }}</p>
                </div>

                <div class="pt-3 border-t border-slate-100 flex items-center justify-between">
                    <div>
                        <p class="text-sm font-black text-primary">{{ number_format($food->price, 0, '.', ' ') }} <span class="text-[10px] font-normal text-slate-400">so'm</span></p>
                    </div>

                    <!-- Actions -->
                    <div class="flex items-center space-x-1.5">
                        <!-- Toggle Availability Form -->
                        <form action="{{ route('admin.foods.toggle', $food->id) }}" method="POST" class="inline">
                            @csrf
                            <button type="submit" title="{{ $food->is_available ? 'Stop-listga qo\'yish' : 'Sotuvga qaytarish' }}" class="p-2 rounded-lg text-xs font-bold transition {{ $food->is_available ? 'bg-emerald-50 text-emerald-600 hover:bg-emerald-100' : 'bg-rose-50 text-rose-600 hover:bg-rose-100' }}">
                                <i class="fa-solid fa-power-off"></i>
                            </button>
                        </form>

                        <a href="{{ route('admin.foods.edit', $food->id) }}" class="p-2 bg-slate-100 hover:bg-slate-900 hover:text-white text-slate-600 rounded-lg text-xs transition" title="Tahrirlash">
                            <i class="fa-solid fa-pen-to-square"></i>
                        </a>

                        <form action="{{ route('admin.foods.destroy', $food->id) }}" method="POST" onsubmit="return confirm('Rostdan ham ushbu taomni o\'chirmoqchimisiz?')" class="inline">
                            @csrf
                            @method('DELETE')
                            <button type="submit" class="p-2 bg-slate-100 hover:bg-rose-600 hover:text-white text-slate-600 rounded-lg text-xs transition" title="O'chirish">
                                <i class="fa-solid fa-trash"></i>
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
        @empty
        <div class="col-span-full py-16 text-center text-slate-400 bg-white rounded-2xl border border-slate-100">
            <i class="fa-solid fa-utensils text-4xl mb-3 block text-slate-300"></i>
            Hozircha menyuda taomlar yo'q.
        </div>
        @endforelse
    </div>

    <!-- Pagination -->
    <div class="p-4 bg-white rounded-2xl border border-slate-100">
        {{ $foods->links() }}
    </div>
</div>
@endsection
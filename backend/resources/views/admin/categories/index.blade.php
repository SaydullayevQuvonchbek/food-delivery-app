@extends('admin.layouts.app')

@section('title', 'Kategoriyalar Boshqaruvi')

@section('content')
<div class="space-y-6" x-data="{ showModal: false, editMode: false, catId: '', catName: '', catIcon: '', catOrder: '' }">

    <!-- Header Actions -->
    <div class="flex items-center justify-between">
        <div>
            <h3 class="text-xl font-black text-slate-800">Menyu Kategoriyalari</h3>
            <p class="text-xs text-slate-500 mt-0.5">Taomlar toifalari, tartib raqamlari va emoji ikonkalari</p>
        </div>

        <button @click="editMode = false; catName = ''; catIcon = '🍔'; catOrder = ''; showModal = true" class="px-4 py-2.5 bg-primary hover:bg-primary-dark text-white rounded-xl text-xs font-bold transition flex items-center space-x-2 shadow-lg shadow-orange-500/30">
            <i class="fa-solid fa-plus"></i>
            <span>Yangi Kategoriya</span>
        </button>
    </div>

    <!-- Categories Grid -->
    <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-5">
        @foreach($categories as $category)
        <div class="bg-white rounded-2xl border border-slate-100 shadow-sm p-5 flex flex-col justify-between hover:shadow-md transition">
            <div class="flex items-center justify-between">
                <div class="w-12 h-12 rounded-2xl bg-orange-50 flex items-center justify-center text-2xl">
                    {{ $category->icon }}
                </div>
                <span class="text-xs font-bold text-slate-400 font-mono">#{{ $category->order_index }}</span>
            </div>

            <div class="mt-4">
                <h4 class="text-base font-bold text-slate-800">{{ $category->name }}</h4>
                <p class="text-xs text-slate-400 mt-0.5">{{ $category->foods_count }} ta taom mavjud</p>
            </div>

            <div class="mt-5 pt-3 border-t border-slate-100 flex items-center justify-between">
                <span class="px-2.5 py-1 rounded-full text-[10px] font-bold {{ $category->is_active ? 'bg-emerald-50 text-emerald-600' : 'bg-slate-100 text-slate-400' }}">
                    {{ $category->is_active ? 'Faol' : 'Nofaol' }}
                </span>

                <div class="flex items-center space-x-1">
                    <button @click="editMode = true; catId = '{{ $category->id }}'; catName = '{{ $category->name }}'; catIcon = '{{ $category->icon }}'; catOrder = '{{ $category->order_index }}'; showModal = true" class="p-2 bg-slate-100 hover:bg-slate-900 hover:text-white text-slate-600 rounded-lg text-xs transition">
                        <i class="fa-solid fa-pen-to-square"></i>
                    </button>
                    <form action="{{ route('admin.categories.destroy', $category->id) }}" method="POST" onsubmit="return confirm('Kategoriyani o\'chirmoqchimisiz?')" class="inline">
                        @csrf
                        @method('DELETE')
                        <button type="submit" class="p-2 bg-slate-100 hover:bg-rose-600 hover:text-white text-slate-600 rounded-lg text-xs transition">
                            <i class="fa-solid fa-trash"></i>
                        </button>
                    </form>
                </div>
            </div>
        </div>
        @endforeach
    </div>

    <!-- Add/Edit Category Modal -->
    <div x-show="showModal" x-cloak class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm">
        <div @click.away="showModal = false" class="bg-white rounded-3xl p-6 max-w-md w-full shadow-2xl border border-slate-100">
            <h3 class="text-lg font-bold text-slate-800 mb-4" x-text="editMode ? 'Kategoriyani Tahrirlash' : 'Yangi Kategoriya Qo\'shish'"></h3>

            <form :action="editMode ? '{{ url('admin/categories') }}/' + catId : '{{ route('admin.categories.store') }}'" method="POST" class="space-y-4">
                @csrf
                <template x-if="editMode">
                    <input type="hidden" name="_method" value="PUT">
                </template>

                <div>
                    <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">Kategoriya Nomi *</label>
                    <input type="text" name="name" x-model="catName" required placeholder="Masalan: Lavash" class="w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:ring-2 focus:ring-primary">
                </div>

                <div>
                    <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">Emoji Ikonka *</label>
                    <input type="text" name="icon" x-model="catIcon" required placeholder="🍔" class="w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:ring-2 focus:ring-primary">
                </div>

                <div>
                    <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">Tartib Raqami</label>
                    <input type="number" name="order_index" x-model="catOrder" placeholder="1" class="w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:ring-2 focus:ring-primary">
                </div>

                <div class="flex justify-end space-x-2 pt-3">
                    <button type="button" @click="showModal = false" class="px-4 py-2 bg-slate-100 text-slate-600 rounded-xl text-xs font-bold">Bekor qilish</button>
                    <button type="submit" class="px-5 py-2 bg-primary text-white rounded-xl text-xs font-bold shadow-md shadow-orange-500/30">Saqlash</button>
                </div>
            </form>
        </div>
    </div>
</div>
@endsection
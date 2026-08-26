@extends('admin.layouts.app')

@section('title', 'Taomni Tahrirlash - ' . $food->name)

@section('content')
<div class="max-w-3xl mx-auto space-y-6">

    <!-- Header Navigation -->
    <div class="flex items-center justify-between">
        <a href="{{ route('admin.foods.index') }}" class="text-xs font-bold text-slate-500 hover:text-slate-900 inline-flex items-center space-x-1.5">
            <i class="fa-solid fa-arrow-left"></i>
            <span>Menyuga qaytish</span>
        </a>
    </div>

    <!-- Form Card -->
    <div class="bg-white rounded-3xl border border-slate-100 shadow-sm p-8">
        <h3 class="text-xl font-black text-slate-800 mb-6">Taomni Tahrirlash</h3>

        <form action="{{ route('admin.foods.update', $food->id) }}" method="POST" enctype="multipart/form-data" class="space-y-6">
            @csrf
            @method('PUT')

            <!-- Name & Category -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
                <div>
                    <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">Taom Nomi *</label>
                    <input type="text" name="name" value="{{ old('name', $food->name) }}" required class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary focus:bg-white">
                </div>

                <div>
                    <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">Kategoriya *</label>
                    <select name="category_id" required class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary focus:bg-white">
                        @foreach($categories as $cat)
                            <option value="{{ $cat->id }}" {{ old('category_id', $food->category_id) == $cat->id ? 'selected' : '' }}>{{ $cat->icon }} {{ $cat->name }}</option>
                        @endforeach
                    </select>
                </div>
            </div>

            <!-- Prices -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
                <div>
                    <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">Narxi (so'm) *</label>
                    <input type="number" name="price" value="{{ old('price', $food->price) }}" required class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary focus:bg-white">
                </div>

                <div>
                    <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">Eski Narxi / Chegirmasiz (so'm)</label>
                    <input type="number" name="original_price" value="{{ old('original_price', $food->original_price) }}" class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary focus:bg-white">
                </div>
            </div>

            <!-- Description -->
            <div>
                <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">Tavsifi / Tarkibi</label>
                <textarea name="description" rows="3" class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary focus:bg-white">{{ old('description', $food->description) }}</textarea>
            </div>

            <!-- Current Image Preview & Upload -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-5 items-center">
                <div>
                    <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">Yangi Rasm Fayli</label>
                    <input type="file" name="image" accept="image/*" class="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-xs file:mr-3 file:py-2 file:px-4 file:rounded-lg file:border-0 file:text-xs file:font-bold file:bg-primary file:text-white hover:file:bg-primary-dark">
                </div>

                <div>
                    <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">Yoki Rasm Havolasi (URL)</label>
                    <input type="url" name="image_url" value="{{ old('image_url', $food->image_url) }}" class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary focus:bg-white">
                </div>
            </div>

            <!-- Options Toggles -->
            <div class="flex items-center space-x-6 pt-2">
                <label class="flex items-center space-x-2 text-xs font-bold text-slate-700 cursor-pointer">
                    <input type="checkbox" name="is_available" value="1" {{ $food->is_available ? 'checked' : '' }} class="w-4 h-4 text-primary rounded border-slate-300 focus:ring-primary">
                    <span>Sotuvda mavjud</span>
                </label>

                <label class="flex items-center space-x-2 text-xs font-bold text-slate-700 cursor-pointer">
                    <input type="checkbox" name="is_featured" value="1" {{ $food->is_featured ? 'checked' : '' }} class="w-4 h-4 text-primary rounded border-slate-300 focus:ring-primary">
                    <span>Tavsiya etiladi (Top)</span>
                </label>

                <label class="flex items-center space-x-2 text-xs font-bold text-slate-700 cursor-pointer">
                    <input type="checkbox" name="is_free_delivery" value="1" {{ $food->is_free_delivery ? 'checked' : '' }} class="w-4 h-4 text-primary rounded border-slate-300 focus:ring-primary">
                    <span>Bepul yetkazish</span>
                </label>
            </div>

            <!-- Submit Button -->
            <div class="pt-4 border-t border-slate-100 flex justify-end space-x-3">
                <a href="{{ route('admin.foods.index') }}" class="px-6 py-3 bg-slate-100 text-slate-600 font-bold rounded-xl text-xs hover:bg-slate-200 transition">Bekor qilish</a>
                <button type="submit" class="px-6 py-3 bg-primary hover:bg-primary-dark text-white font-bold rounded-xl text-xs shadow-lg shadow-orange-500/30 transition">O'zgarishlarni Saqlash</button>
            </div>
        </form>
    </div>
</div>
@endsection
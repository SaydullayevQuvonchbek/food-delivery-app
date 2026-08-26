@extends('admin.layouts.app')

@section('title', 'Kompaniya va Tizim Sozlamalari')

@section('content')
<div class="max-w-4xl mx-auto space-y-6">

    <div class="bg-white rounded-3xl border border-slate-100 shadow-sm p-8">
        <h3 class="text-xl font-black text-slate-800 mb-6">Restoran & Yetkazib Berish Parametrlari</h3>

        <form action="{{ route('admin.settings.update') }}" method="POST" class="space-y-6">
            @csrf

            <!-- Company Info -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
                <div>
                    <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">Kompaniya / Restoran Nomi</label>
                    <input type="text" name="company_name" value="Insof Delivery" required class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:ring-2 focus:ring-primary">
                </div>

                <div>
                    <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">Aloqa Telefoni</label>
                    <input type="text" name="phone" value="+998 71 200 00 00" required class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:ring-2 focus:ring-primary">
                </div>
            </div>

            <!-- Working Hours -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
                <div>
                    <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">Ish Boshlanish Vaqti</label>
                    <input type="time" name="open_time" value="09:00" class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:ring-2 focus:ring-primary">
                </div>

                <div>
                    <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">Ish Yakunlanish Vaqti</label>
                    <input type="time" name="close_time" value="23:00" class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:ring-2 focus:ring-primary">
                </div>
            </div>

            <!-- Delivery Pricing -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
                <div>
                    <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">Standart Yetkazib Berish Narxi (so'm)</label>
                    <input type="number" name="delivery_fee" value="10000" class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:ring-2 focus:ring-primary">
                </div>

                <div>
                    <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">Bepul Yetkazish Chegarasi (so'm)</label>
                    <input type="number" name="free_delivery_threshold" value="100000" class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:ring-2 focus:ring-primary">
                </div>
            </div>

            <div class="pt-4 border-t border-slate-100 flex justify-end">
                <button type="submit" class="px-6 py-3 bg-primary hover:bg-primary-dark text-white font-bold rounded-xl text-xs shadow-lg shadow-orange-500/30 transition">
                    Sozlamalarni Saqlash
                </button>
            </div>
        </form>
    </div>
</div>
@endsection
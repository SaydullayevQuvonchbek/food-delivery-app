<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\Promotion;
use Illuminate\Http\Request;

class PromotionController extends Controller
{
    public function index()
    {
        $promotions = Promotion::latest()->get();
        return view('admin.promotions.index', compact('promotions'));
    }

    public function store(Request $request)
    {
        $validated = $request->validate([
            'code' => 'required|string|max:50|unique:promotions,code',
            'title' => 'nullable|string|max:255',
            'discount_type' => 'required|in:fixed,percent',
            'discount_value' => 'required|numeric|min:1',
            'min_order_amount' => 'nullable|numeric|min:0',
            'valid_until' => 'nullable|date',
            'is_active' => 'nullable|boolean'
        ]);

        Promotion::create([
            'code' => strtoupper(trim($validated['code'])),
            'title' => $validated['title'] ?? 'Chegirma promokodi',
            'discount_type' => $validated['discount_type'],
            'discount_value' => $validated['discount_value'],
            'min_order_amount' => $validated['min_order_amount'] ?? 0.00,
            'valid_until' => $validated['valid_until'] ?? null,
            'is_active' => $request->boolean('is_active', true),
            'usage_count' => 0
        ]);

        return back()->with('success', 'Yangi promokod muvaffaqiyatli yaratildi.');
    }

    public function toggle($id)
    {
        $promo = Promotion::findOrFail($id);
        $promo->is_active = !$promo->is_active;
        $promo->save();

        return back()->with('success', 'Promokod holati o\'zgartirildi.');
    }

    public function destroy($id)
    {
        $promo = Promotion::findOrFail($id);
        $promo->delete();

        return back()->with('success', 'Promokod o\'chirildi.');
    }
}
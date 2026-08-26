<?php

namespace App\Http\Controllers\Api\V1;

use App\Http\Controllers\Controller;
use App\Models\Promotion;
use Illuminate\Http\Request;

class PromotionController extends Controller
{
    /** Aktiv aksiyalar ro'yxati (mobil ilovadagi banner uchun) */
    public function index()
    {
        $promotions = Promotion::where('is_active', true)
            ->where(function ($q) {
                $q->whereNull('valid_until')->orWhereDate('valid_until', '>=', now()->toDateString());
            })
            ->orderByDesc('id')
            ->get(['id', 'code', 'title', 'discount_type', 'discount_value', 'min_order_amount', 'valid_until']);

        return response()->json(['success' => true, 'data' => $promotions]);
    }

    /**
     * Promo kodni savat summasiga nisbatan tekshiradi va chegirma miqdorini qaytaradi.
     * Mobil ilova endi chegirmani o'zi "o'ylab topmaydi".
     */
    public function validateCode(Request $request)
    {
        $validated = $request->validate([
            'code' => 'required|string|max:50',
            'subtotal' => 'required|numeric|min:0'
        ]);

        $subtotal = (float) $validated['subtotal'];

        $promo = Promotion::query()
            ->where('code', strtoupper(trim($validated['code'])))
            ->where('is_active', true)
            ->where(function ($q) {
                $q->whereNull('valid_until')->orWhereDate('valid_until', '>=', now()->toDateString());
            })
            ->first();

        if (! $promo) {
            return response()->json([
                'success' => false,
                'message' => 'Bunday promo kod topilmadi yoki muddati tugagan'
            ], 404);
        }

        if ($subtotal < $promo->min_order_amount) {
            return response()->json([
                'success' => false,
                'message' => 'Bu promo kod kamida ' . number_format($promo->min_order_amount, 0, '.', ' ') . " so'mlik buyurtmaga amal qiladi"
            ], 422);
        }

        $discount = $promo->discount_type === 'percent'
            ? round(($subtotal * $promo->discount_value) / 100, 2)
            : (float) $promo->discount_value;

        $discount = min($discount, $subtotal);

        return response()->json([
            'success' => true,
            'message' => 'Promo kod qabul qilindi',
            'data' => [
                'code' => $promo->code,
                'title' => $promo->title,
                'discount_type' => $promo->discount_type,
                'discount_amount' => $discount,
            ]
        ]);
    }
}

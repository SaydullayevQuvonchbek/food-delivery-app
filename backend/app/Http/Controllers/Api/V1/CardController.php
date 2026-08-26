<?php

namespace App\Http\Controllers\Api\V1;

use App\Http\Controllers\Controller;
use App\Models\SavedCard;
use Illuminate\Http\Request;

class CardController extends Controller
{
    private const MAX_CARDS = 10;

    public function index(Request $request)
    {
        $cards = SavedCard::where('user_id', $request->user()->id)
            ->orderByDesc('is_default')
            ->orderBy('id')
            ->get();

        return response()->json(['success' => true, 'data' => $cards]);
    }

    public function store(Request $request)
    {
        $validated = $request->validate([
            'card_holder_name' => 'required|string|max:255',
            'card_number' => 'required|string|min:12|max:23',
            'expiry_date' => ['required', 'string', 'regex:/^(0[1-9]|1[0-2])\/\d{2}$/'],
            'card_type' => 'nullable|string|in:MasterCard,Visa,Humo,Uzcard,Paypal,Apple Pay',
            'is_default' => 'nullable|boolean'
        ], [
            'expiry_date.regex' => 'Amal qilish muddati MM/YY ko\'rinishida bo\'lishi kerak'
        ]);

        $digits = preg_replace('/\D/', '', $validated['card_number']);
        if (strlen($digits) < 12 || strlen($digits) > 19) {
            return response()->json([
                'success' => false,
                'message' => 'Karta raqami noto\'g\'ri kiritilgan'
            ], 422);
        }

        $user = $request->user();

        if (SavedCard::where('user_id', $user->id)->count() >= self::MAX_CARDS) {
            return response()->json([
                'success' => false,
                'message' => 'Saqlangan kartalar soni chegarasiga yetdingiz'
            ], 422);
        }

        $lastFour = substr($digits, -4);
        $isFirstCard = ! SavedCard::where('user_id', $user->id)->exists();
        $makeDefault = $isFirstCard || ! empty($validated['is_default']);

        if ($makeDefault) {
            SavedCard::where('user_id', $user->id)->update(['is_default' => false]);
        }

        // PAN va CVV hech qachon saqlanmaydi - faqat oxirgi 4 raqam
        $card = SavedCard::create([
            'user_id' => $user->id,
            'card_holder_name' => $validated['card_holder_name'],
            'last_four' => $lastFour,
            'expiry_date' => $validated['expiry_date'],
            'card_type' => $validated['card_type'] ?? $this->detectCardType($digits),
            'is_default' => $makeDefault
        ]);

        return response()->json(['success' => true, 'message' => 'Card saved successfully', 'data' => $card], 201);
    }

    public function destroy(Request $request, $id)
    {
        $card = SavedCard::where('user_id', $request->user()->id)->find($id);

        if (! $card) {
            return response()->json(['success' => false, 'message' => 'Karta topilmadi'], 404);
        }

        $wasDefault = $card->is_default;
        $card->delete();

        if ($wasDefault) {
            $next = SavedCard::where('user_id', $request->user()->id)->orderBy('id')->first();
            $next?->update(['is_default' => true]);
        }

        return response()->json(['success' => true, 'message' => 'Card deleted successfully']);
    }

    private function detectCardType(string $digits): string
    {
        return match (true) {
            str_starts_with($digits, '4') => 'Visa',
            str_starts_with($digits, '8600') => 'Uzcard',
            str_starts_with($digits, '9860') => 'Humo',
            default => 'MasterCard',
        };
    }
}

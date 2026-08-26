<?php

namespace App\Http\Controllers\Api\V1;

use App\Http\Controllers\Controller;
use App\Models\SavedCard;
use Illuminate\Http\Request;

class CardController extends Controller
{
    public function index(Request $request)
    {
        $cards = SavedCard::where('user_id', $request->user()->id)->get();
        return response()->json(['success' => true, 'data' => $cards]);
    }

    public function store(Request $request)
    {
        $validated = $request->validate([
            'card_holder_name' => 'required|string|max:255',
            'card_number' => 'required|string|min:12|max:19',
            'expiry_date' => 'required|string|max:7',
            'card_type' => 'nullable|string|in:MasterCard,Visa,Paypal,Apple Pay'
        ]);

        $lastFour = substr(str_replace(' ', '', $validated['card_number']), -4);

        $card = SavedCard::create([
            'user_id' => $request->user()->id,
            'card_holder_name' => $validated['card_holder_name'],
            'last_four' => $lastFour,
            'expiry_date' => $validated['expiry_date'],
            'card_type' => $validated['card_type'] ?? 'MasterCard',
            'is_default' => false
        ]);

        return response()->json(['success' => true, 'message' => 'Card saved successfully', 'data' => $card], 201);
    }

    public function destroy(Request $request, $id)
    {
        $card = SavedCard::where('user_id', $request->user()->id)->findOrFail($id);
        $card->delete();

        return response()->json(['success' => true, 'message' => 'Card deleted successfully']);
    }
}
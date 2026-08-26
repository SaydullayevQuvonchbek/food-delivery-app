<?php

namespace App\Http\Controllers\Api\V1;

use App\Http\Controllers\Controller;
use App\Models\UserAddress;
use Illuminate\Http\Request;

class AddressController extends Controller
{
    public function index(Request $request)
    {
        $addresses = UserAddress::where('user_id', $request->user()->id)->get();
        return response()->json([
            'success' => true,
            'data' => $addresses
        ]);
    }

    public function store(Request $request)
    {
        $validated = $request->validate([
            'label' => 'nullable|string|max:50',
            'address_line' => 'required|string|max:255',
            'house_number' => 'nullable|string|max:50',
            'city' => 'nullable|string|max:100',
            'is_default' => 'nullable|boolean'
        ]);

        if (!empty($validated['is_default'])) {
            UserAddress::where('user_id', $request->user()->id)->update(['is_default' => false]);
        }

        $address = UserAddress::create([
            'user_id' => $request->user()->id,
            'label' => $validated['label'] ?? 'Home',
            'address_line' => $validated['address_line'],
            'house_number' => $validated['house_number'] ?? '',
            'city' => $validated['city'] ?? 'Tashkent',
            'is_default' => $validated['is_default'] ?? true
        ]);

        return response()->json([
            'success' => true,
            'message' => 'Address added successfully',
            'data' => $address
        ], 201);
    }

    public function destroy(Request $request, $id)
    {
        $address = UserAddress::where('user_id', $request->user()->id)->findOrFail($id);
        $address->delete();

        return response()->json([
            'success' => true,
            'message' => 'Address deleted successfully'
        ]);
    }
}
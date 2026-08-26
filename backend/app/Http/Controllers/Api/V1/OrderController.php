<?php

namespace App\Http\Controllers\Api\V1;

use App\Http\Controllers\Controller;
use App\Models\Order;
use App\Models\OrderItem;
use App\Models\Food;
use App\Models\Delivery;
use Illuminate\Http\Request;
use Illuminate\Support\Str;

class OrderController extends Controller
{
    public function index(Request $request)
    {
        $orders = Order::with(['items.food', 'courier', 'address'])
            ->where('user_id', $request->user()->id)
            ->latest()
            ->get();

        return response()->json(['success' => true, 'data' => $orders]);
    }

    public function store(Request $request)
    {
        $validated = $request->validate([
            'items' => 'required|array|min:1',
            'items.*.food_id' => 'required|exists:foods,id',
            'items.*.quantity' => 'required|integer|min:1',
            'address_id' => 'nullable|exists:user_addresses,id',
            'payment_method' => 'required|string',
            'promo_code' => 'nullable|string'
        ]);

        $subtotal = 0;
        $orderItemsData = [];

        foreach ($validated['items'] as $itemData) {
            $food = Food::findOrFail($itemData['food_id']);
            $itemTotal = $food->price * $itemData['quantity'];
            $subtotal += $itemTotal;

            $orderItemsData[] = [
                'food_id' => $food->id,
                'quantity' => $itemData['quantity'],
                'unit_price' => $food->price,
                'total_price' => $itemTotal,
            ];
        }

        $discount = (!empty($validated['promo_code'])) ? 10900.00 : 0.00;
        $deliveryFee = 0.00;
        $tax = $subtotal * 0.10;
        $total = max(0, $subtotal + $deliveryFee + $tax - $discount);

        $order = Order::create([
            'order_number' => 'ORD-' . strtoupper(Str::random(8)),
            'user_id' => $request->user()->id,
            'address_id' => $validated['address_id'] ?? null,
            'status' => 'preparing',
            'subtotal' => $subtotal,
            'delivery_fee' => $deliveryFee,
            'discount_amount' => $discount,
            'tax_amount' => $tax,
            'total_amount' => $total,
            'payment_method' => $validated['payment_method'],
            'payment_status' => 'paid'
        ]);

        foreach ($orderItemsData as $item) {
            $order->items()->create($item);
        }

        // Assign courier and create tracking record safely
        $courier = \App\Models\User::where('role', 'courier')->first() ?? $request->user();
        if ($courier) {
            try {
                Delivery::create([
                    'order_id' => $order->id,
                    'courier_id' => $courier->id,
                    'current_lat' => 41.2995,
                    'current_lng' => 69.2401,
                    'status' => 'on_the_way',
                    'estimated_delivery_time' => now()->addMinutes(30)
                ]);
            } catch (\Exception $e) {
                // Keep order created even if delivery tracking record fails
            }
        }

        return response()->json([
            'success' => true,
            'message' => 'Order placed successfully',
            'data' => $order->load(['items.food', 'delivery'])
        ], 201);
    }

    public function show($id)
    {
        $order = Order::with(['items.food', 'courier', 'address', 'delivery'])->findOrFail($id);
        return response()->json(['success' => true, 'data' => $order]);
    }
}
<?php

namespace App\Http\Controllers\Api\V1;

use App\Http\Controllers\Controller;
use App\Models\Delivery;
use App\Models\Food;
use App\Models\Order;
use App\Models\Promotion;
use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Log;
use Illuminate\Support\Str;

class OrderController extends Controller
{
    /** Yetkazib berish narxi va soliq stavkasi bitta joyda (mobil ilova bilan bir xil) */
    public const DELIVERY_FEE = 0.00;
    public const TAX_RATE = 0.10;

    public function index(Request $request)
    {
        $orders = Order::with(['items.food', 'courier:id,full_name,phone,avatar_url', 'address', 'delivery'])
            ->where('user_id', $request->user()->id)
            ->latest()
            ->get();

        return response()->json(['success' => true, 'data' => $orders]);
    }

    public function store(Request $request)
    {
        $validated = $request->validate([
            'items' => 'required|array|min:1',
            'items.*.food_id' => 'required|integer|exists:foods,id',
            'items.*.quantity' => 'required|integer|min:1|max:50',
            'address_id' => 'nullable|integer|exists:user_addresses,id',
            'payment_method' => 'required|string|in:card,cash,apple_pay,paypal',
            'promo_code' => 'nullable|string|max:50',
            'notes' => 'nullable|string|max:500'
        ]);

        // Manzil boshqa foydalanuvchiga tegishli bo'lmasligi kerak
        if (! empty($validated['address_id'])) {
            $ownsAddress = $request->user()->addresses()->where('id', $validated['address_id'])->exists();
            if (! $ownsAddress) {
                return response()->json([
                    'success' => false,
                    'message' => 'Ko\'rsatilgan manzil topilmadi'
                ], 422);
            }
        }

        $foodIds = collect($validated['items'])->pluck('food_id')->unique();
        $foods = Food::whereIn('id', $foodIds)->get()->keyBy('id');

        $unavailable = $foods->filter(fn ($food) => ! $food->is_available);
        if ($unavailable->isNotEmpty()) {
            return response()->json([
                'success' => false,
                'message' => 'Ba\'zi taomlar hozircha mavjud emas: ' . $unavailable->pluck('name')->implode(', ')
            ], 422);
        }

        $subtotal = 0;
        $orderItemsData = [];

        foreach ($validated['items'] as $itemData) {
            $food = $foods[$itemData['food_id']];
            $itemTotal = round($food->price * $itemData['quantity'], 2);
            $subtotal += $itemTotal;

            $orderItemsData[] = [
                'food_id' => $food->id,
                'quantity' => $itemData['quantity'],
                'unit_price' => $food->price,
                'total_price' => $itemTotal,
            ];
        }

        [$discount, $promo] = $this->resolveDiscount($validated['promo_code'] ?? null, $subtotal);

        $deliveryFee = self::DELIVERY_FEE;
        $tax = round($subtotal * self::TAX_RATE, 2);
        $total = max(0, round($subtotal + $deliveryFee + $tax - $discount, 2));

        $order = DB::transaction(function () use ($request, $validated, $orderItemsData, $subtotal, $deliveryFee, $discount, $tax, $total, $promo) {
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
                'payment_status' => $validated['payment_method'] === 'cash' ? 'pending' : 'paid',
                'notes' => $validated['notes'] ?? null,
            ]);

            $order->items()->createMany($orderItemsData);

            if ($promo) {
                $promo->increment('usage_count');
            }

            return $order;
        });

        // Kuryer biriktirish - buyurtma yaratilgandan keyin, alohida (xato bo'lsa buyurtma saqlanib qoladi)
        $courier = User::where('role', 'courier')->where('status', 'active')->inRandomOrder()->first();
        if ($courier) {
            try {
                $order->update(['courier_id' => $courier->id]);
                Delivery::create([
                    'order_id' => $order->id,
                    'courier_id' => $courier->id,
                    'current_lat' => 41.2995,
                    'current_lng' => 69.2401,
                    'status' => 'on_the_way',
                    'estimated_delivery_time' => now()->addMinutes(30)
                ]);
            } catch (\Throwable $e) {
                Log::warning('Delivery tracking yaratilmadi: ' . $e->getMessage());
            }
        }

        return response()->json([
            'success' => true,
            'message' => 'Order placed successfully',
            'data' => $order->fresh()->load(['items.food', 'delivery', 'courier:id,full_name,phone,avatar_url', 'address'])
        ], 201);
    }

    public function show(Request $request, $id)
    {
        $order = Order::with(['items.food', 'courier:id,full_name,phone,avatar_url', 'address', 'delivery'])
            ->where('user_id', $request->user()->id)
            ->find($id);

        if (! $order) {
            return response()->json(['success' => false, 'message' => 'Buyurtma topilmadi'], 404);
        }

        return response()->json(['success' => true, 'data' => $order]);
    }

    /**
     * @return array{0: float, 1: ?Promotion}
     */
    private function resolveDiscount(?string $code, float $subtotal): array
    {
        if (empty($code)) {
            return [0.00, null];
        }

        $promo = Promotion::query()
            ->where('code', strtoupper(trim($code)))
            ->where('is_active', true)
            ->where(function ($q) {
                $q->whereNull('valid_until')->orWhereDate('valid_until', '>=', now()->toDateString());
            })
            ->first();

        if (! $promo || $subtotal < $promo->min_order_amount) {
            return [0.00, null];
        }

        $discount = $promo->discount_type === 'percent'
            ? round(($subtotal * $promo->discount_value) / 100, 2)
            : (float) $promo->discount_value;

        return [min($discount, $subtotal), $promo];
    }
}

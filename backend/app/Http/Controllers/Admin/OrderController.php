<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\Order;
use App\Models\User;
use App\Models\Delivery;
use Illuminate\Http\Request;

class OrderController extends Controller
{
    public function index(Request $request)
    {
        $status = $request->get('status', 'all');
        $query = Order::with(['user', 'items.food', 'delivery.courier', 'address'])->latest();

        if ($status !== 'all') {
            $query->where('status', $status);
        }

        if ($search = $request->get('search')) {
            $query->where(function($q) use ($search) {
                $q->where('order_number', 'like', "%{$search}%")
                  ->orWhereHas('user', function($uq) use ($search) {
                      $uq->where('full_name', 'like', "%{$search}%")
                         ->orWhere('phone', 'like', "%{$search}%");
                  });
            });
        }

        $orders = $query->paginate(15);
        $couriers = User::where('role', 'courier')->get();

        $counts = [
            'all' => Order::count(),
            'pending' => Order::where('status', 'pending')->count(),
            'preparing' => Order::where('status', 'preparing')->count(),
            'on_the_way' => Order::where('status', 'on_the_way')->count(),
            'delivered' => Order::where('status', 'delivered')->count(),
            'cancelled' => Order::where('status', 'cancelled')->count(),
        ];

        return view('admin.orders.index', compact('orders', 'couriers', 'status', 'counts'));
    }

    public function show($id)
    {
        $order = Order::with(['user', 'items.food', 'delivery.courier', 'address'])->findOrFail($id);
        $couriers = User::where('role', 'courier')->get();
        return view('admin.orders.show', compact('order', 'couriers'));
    }

    public function updateStatus(Request $request, $id)
    {
        $request->validate([
            'status' => 'required|in:pending,preparing,on_the_way,delivered,cancelled'
        ]);

        $order = Order::findOrFail($id);
        $order->status = $request->status;
        $order->save();

        if ($request->status === 'on_the_way' && $request->filled('courier_id')) {
            Delivery::updateOrCreate(
                ['order_id' => $order->id],
                [
                    'courier_id' => $request->courier_id,
                    'status' => 'on_the_way',
                    'current_lat' => 41.2995,
                    'current_lng' => 69.2401,
                    'estimated_delivery_time' => now()->addMinutes(30)
                ]
            );
        }

        return back()->with('success', "Buyurtma holati '{$request->status}' ga o'zgartirildi.");
    }

    public function assignCourier(Request $request, $id)
    {
        $request->validate([
            'courier_id' => 'required|exists:users,id'
        ]);

        $order = Order::findOrFail($id);
        $order->status = 'on_the_way';
        $order->save();

        Delivery::updateOrCreate(
            ['order_id' => $order->id],
            [
                'courier_id' => $request->courier_id,
                'status' => 'on_the_way',
                'current_lat' => 41.2995,
                'current_lng' => 69.2401,
                'estimated_delivery_time' => now()->addMinutes(30)
            ]
        );

        return back()->with('success', "Kuryer muvaffaqiyatli biriktirildi.");
    }

    public function printReceipt($id)
    {
        $order = Order::with(['user', 'items.food', 'delivery.courier', 'address'])->findOrFail($id);
        return view('admin.orders.print', compact('order'));
    }
}
<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\Order;
use App\Models\Food;
use App\Models\User;
use App\Models\Delivery;
use Illuminate\Http\Request;
use Carbon\Carbon;

class DashboardController extends Controller
{
    public function index()
    {
        $today = Carbon::today();

        $todayRevenue = Order::whereDate('created_at', $today)->sum('total_amount');
        $todayOrdersCount = Order::whereDate('created_at', $today)->count();
        $pendingOrdersCount = Order::whereIn('status', ['pending', 'preparing'])->count();
        $totalCustomers = User::where('role', 'customer')->count();
        $activeCouriers = User::where('role', 'courier')->count();

        $recentOrders = Order::with(['user', 'items.food', 'delivery.courier'])
            ->latest()
            ->take(8)
            ->get();

        $topFoods = Food::with('category')
            ->where('is_available', true)
            ->take(5)
            ->get();

        return view('admin.dashboard', compact(
            'todayRevenue',
            'todayOrdersCount',
            'pendingOrdersCount',
            'totalCustomers',
            'activeCouriers',
            'recentOrders',
            'topFoods'
        ));
    }

    public function liveStats()
    {
        $pendingCount = Order::where('status', 'pending')->count();
        $latestOrder = Order::latest()->first();

        return response()->json([
            'pending_count' => $pendingCount,
            'latest_order_id' => $latestOrder ? $latestOrder->id : null,
            'latest_order_number' => $latestOrder ? $latestOrder->order_number : null,
            'latest_order_total' => $latestOrder ? number_format($latestOrder->total_amount, 0, '.', ' ') . ' so\'m' : null,
        ]);
    }
}
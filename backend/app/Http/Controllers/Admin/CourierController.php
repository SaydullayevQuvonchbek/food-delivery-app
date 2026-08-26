<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\User;
use App\Models\Delivery;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;

class CourierController extends Controller
{
    public function index()
    {
        $couriers = User::where('role', 'courier')
            ->withCount(['deliveries as active_deliveries_count' => function($q) {
                $q->where('status', 'on_the_way');
            }])
            ->latest()
            ->get();

        return view('admin.couriers.index', compact('couriers'));
    }

    public function store(Request $request)
    {
        $validated = $request->validate([
            'full_name' => 'required|string|max:255',
            'phone' => 'required|string|max:20|unique:users,phone',
            'email' => 'required|email|unique:users,email',
            'password' => 'required|string|min:6',
        ]);

        User::create([
            'full_name' => $validated['full_name'],
            'phone' => $validated['phone'],
            'email' => $validated['email'],
            'password' => Hash::make($validated['password']),
            'role' => 'courier',
            'status' => 'active',
        ]);

        return back()->with('success', 'Yangi kuryer muvaffaqiyatli qo\'shildi.');
    }

    public function toggleStatus($id)
    {
        $courier = User::where('role', 'courier')->findOrFail($id);
        $courier->status = $courier->status === 'active' ? 'inactive' : 'active';
        $courier->save();

        return back()->with('success', "Kuryer holati o'zgartirildi.");
    }
}
<?php

namespace App\Http\Controllers\Api\V1;

use App\Http\Controllers\Controller;
use App\Models\Category;
use App\Models\Food;
use Illuminate\Http\Request;

class FoodController extends Controller
{
    public function categories()
    {
        $categories = Category::where('is_active', true)
            ->orderBy('order_index')
            ->get(['id', 'name', 'icon', 'order_index']);

        return response()->json(['success' => true, 'data' => $categories]);
    }

    public function index(Request $request)
    {
        $query = Food::query()->where('is_available', true);

        if ($request->filled('category_id')) {
            $query->where('category_id', (int) $request->input('category_id'));
        }

        if ($request->filled('search')) {
            $search = trim($request->input('search'));
            $query->where(function ($q) use ($search) {
                $q->where('name', 'like', "%{$search}%")
                  ->orWhere('description', 'like', "%{$search}%");
            });
        }

        $foods = $query->orderByDesc('is_featured')
            ->orderByDesc('rating')
            ->limit((int) $request->input('limit', 200))
            ->get();

        return response()->json(['success' => true, 'data' => $foods]);
    }

    public function show($id)
    {
        $food = Food::find($id);

        if (! $food) {
            return response()->json(['success' => false, 'message' => 'Taom topilmadi'], 404);
        }

        $recommended = Food::where('category_id', $food->category_id)
            ->where('id', '!=', $food->id)
            ->where('is_available', true)
            ->limit(6)
            ->get();

        return response()->json([
            'success' => true,
            'data' => [
                'food' => $food,
                'recommended' => $recommended
            ]
        ]);
    }
}

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
        $categories = Category::where('is_active', true)->orderBy('order_index')->get();
        return response()->json(['success' => true, 'data' => $categories]);
    }

    public function index(Request $request)
    {
        $query = Food::with('category')->where('is_available', true);

        if ($request->has('category_id')) {
            $query->where('category_id', $request->category_id);
        }

        if ($request->has('search') && !empty($request->search)) {
            $search = $request->search;
            $query->where(function ($q) use ($search) {
                $q->where('name', 'like', "%{$search}%")
                  ->orWhere('description', 'like', "%{$search}%");
            });
        }

        $foods = $query->get();

        return response()->json(['success' => true, 'data' => $foods]);
    }

    public function show($id)
    {
        $food = Food::with('category')->findOrFail($id);
        $recommended = Food::where('category_id', $food->category_id)->where('id', '!=', $id)->limit(6)->get();

        return response()->json([
            'success' => true,
            'data' => [
                'food' => $food,
                'recommended' => $recommended
            ]
        ]);
    }
}
<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\Food;
use App\Models\Category;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\File;

class FoodController extends Controller
{
    public function index(Request $request)
    {
        $query = Food::with('category')->latest();

        if ($categoryId = $request->get('category_id')) {
            $query->where('category_id', $categoryId);
        }

        if ($search = $request->get('search')) {
            $query->where('name', 'like', "%{$search}%");
        }

        $foods = $query->paginate(12);
        $categories = Category::all();

        return view('admin.foods.index', compact('foods', 'categories'));
    }

    public function create()
    {
        $categories = Category::all();
        return view('admin.foods.create', compact('categories'));
    }

    public function store(Request $request)
    {
        $validated = $request->validate([
            'name' => 'required|string|max:255',
            'category_id' => 'required|exists:categories,id',
            'description' => 'nullable|string',
            'price' => 'required|numeric|min:0',
            'original_price' => 'nullable|numeric|min:0',
            'rating' => 'nullable|numeric|min:0|max:5',
            'delivery_time' => 'nullable|string|max:50',
            'image' => 'nullable|image|mimes:jpeg,png,jpg,webp|max:3072',
            'image_url' => 'nullable|string',
            'is_available' => 'nullable|boolean',
            'is_featured' => 'nullable|boolean',
            'is_free_delivery' => 'nullable|boolean',
        ]);

        $imageUrl = $validated['image_url'] ?? null;
        if ($request->hasFile('image')) {
            $file = $request->file('image');
            $fileName = time() . '_' . uniqid() . '.' . $file->getClientOriginalExtension();
            $file->move(public_path('uploads/foods'), $fileName);
            $imageUrl = url('uploads/foods/' . $fileName);
        }

        Food::create([
            'name' => $validated['name'],
            'category_id' => $validated['category_id'],
            'description' => $validated['description'] ?? '',
            'price' => $validated['price'],
            'original_price' => $validated['original_price'] ?? $validated['price'],
            'rating' => $validated['rating'] ?? 4.8,
            'review_count' => 0,
            'distance' => '1.5 km',
            'delivery_time' => $validated['delivery_time'] ?? '20-30 min',
            'image_url' => $imageUrl,
            'is_available' => $request->boolean('is_available', true),
            'is_featured' => $request->boolean('is_featured', false),
            'is_free_delivery' => $request->boolean('is_free_delivery', false),
        ]);

        return redirect()->route('admin.foods.index')->with('success', 'Taom muvaffaqiyatli qo\'shildi.');
    }

    public function edit($id)
    {
        $food = Food::findOrFail($id);
        $categories = Category::all();
        return view('admin.foods.edit', compact('food', 'categories'));
    }

    public function update(Request $request, $id)
    {
        $food = Food::findOrFail($id);

        $validated = $request->validate([
            'name' => 'required|string|max:255',
            'category_id' => 'required|exists:categories,id',
            'description' => 'nullable|string',
            'price' => 'required|numeric|min:0',
            'original_price' => 'nullable|numeric|min:0',
            'rating' => 'nullable|numeric|min:0|max:5',
            'delivery_time' => 'nullable|string|max:50',
            'image' => 'nullable|image|mimes:jpeg,png,jpg,webp|max:3072',
            'image_url' => 'nullable|string',
            'is_available' => 'nullable|boolean',
            'is_featured' => 'nullable|boolean',
            'is_free_delivery' => 'nullable|boolean',
        ]);

        $imageUrl = $food->image_url;
        if ($request->hasFile('image')) {
            $file = $request->file('image');
            $fileName = time() . '_' . uniqid() . '.' . $file->getClientOriginalExtension();
            $file->move(public_path('uploads/foods'), $fileName);
            $imageUrl = url('uploads/foods/' . $fileName);
        } elseif ($request->filled('image_url')) {
            $imageUrl = $validated['image_url'];
        }

        $food->update([
            'name' => $validated['name'],
            'category_id' => $validated['category_id'],
            'description' => $validated['description'] ?? '',
            'price' => $validated['price'],
            'original_price' => $validated['original_price'] ?? $validated['price'],
            'rating' => $validated['rating'] ?? $food->rating,
            'delivery_time' => $validated['delivery_time'] ?? $food->delivery_time,
            'image_url' => $imageUrl,
            'is_available' => $request->boolean('is_available', true),
            'is_featured' => $request->boolean('is_featured', false),
            'is_free_delivery' => $request->boolean('is_free_delivery', false),
        ]);

        return redirect()->route('admin.foods.index')->with('success', 'Taom muvaffaqiyatli yangilandi.');
    }

    public function toggleAvailability($id)
    {
        $food = Food::findOrFail($id);
        $food->is_available = !$food->is_available;
        $food->save();

        $statusText = $food->is_available ? 'Mavjud (Sotuvda)' : 'Stop-listga qo\'yildi (Mavjud emas)';
        return back()->with('success', "Taom holati: {$statusText}");
    }

    public function destroy($id)
    {
        $food = Food::findOrFail($id);
        $food->delete();

        return redirect()->route('admin.foods.index')->with('success', 'Taom o\'chirildi.');
    }
}
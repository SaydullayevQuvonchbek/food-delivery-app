<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\Category;
use Illuminate\Http\Request;

class CategoryController extends Controller
{
    public function index()
    {
        $categories = Category::withCount('foods')->orderBy('order_index')->get();
        return view('admin.categories.index', compact('categories'));
    }

    public function store(Request $request)
    {
        $validated = $request->validate([
            'name' => 'required|string|max:100',
            'icon' => 'nullable|string|max:50',
            'order_index' => 'nullable|integer',
        ]);

        Category::create([
            'name' => $validated['name'],
            'icon' => $validated['icon'] ?? '🍔',
            'order_index' => $validated['order_index'] ?? (Category::count() + 1),
            'is_active' => true,
        ]);

        return back()->with('success', 'Yangi kategoriya qo\'shildi.');
    }

    public function update(Request $request, $id)
    {
        $category = Category::findOrFail($id);

        $validated = $request->validate([
            'name' => 'required|string|max:100',
            'icon' => 'nullable|string|max:50',
            'order_index' => 'nullable|integer',
            'is_active' => 'nullable|boolean',
        ]);

        $category->update([
            'name' => $validated['name'],
            'icon' => $validated['icon'] ?? $category->icon,
            'order_index' => $validated['order_index'] ?? $category->order_index,
            'is_active' => $request->boolean('is_active', true),
        ]);

        return back()->with('success', 'Kategoriya yangilandi.');
    }

    public function destroy($id)
    {
        $category = Category::findOrFail($id);
        $category->delete();

        return back()->with('success', 'Kategoriya o\'chirildi.');
    }
}
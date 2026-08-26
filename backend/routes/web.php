<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Admin\AuthController as AdminAuthController;
use App\Http\Controllers\Admin\DashboardController;
use App\Http\Controllers\Admin\OrderController as AdminOrderController;
use App\Http\Controllers\Admin\FoodController as AdminFoodController;
use App\Http\Controllers\Admin\CategoryController as AdminCategoryController;
use App\Http\Controllers\Admin\CourierController as AdminCourierController;
use App\Http\Controllers\Admin\ChatController as AdminChatController;
use App\Http\Controllers\Admin\SettingController as AdminSettingController;

Route::get('/', function () {
    return redirect()->route('admin.login');
});

// Admin Auth Routes
Route::prefix('admin')->group(function () {
    Route::get('/login', [AdminAuthController::class, 'showLogin'])->name('admin.login');
    Route::post('/login', [AdminAuthController::class, 'login'])->name('admin.login.submit');
    Route::post('/logout', [AdminAuthController::class, 'logout'])->name('admin.logout');

    // Protected Admin Routes
    Route::middleware([\App\Http\Middleware\AdminMiddleware::class])->group(function () {
        Route::get('/', [DashboardController::class, 'index'])->name('admin.dashboard');
        Route::get('/api/live-stats', [DashboardController::class, 'liveStats'])->name('admin.live-stats');

        // Orders
        Route::get('/orders', [AdminOrderController::class, 'index'])->name('admin.orders.index');
        Route::get('/orders/{id}', [AdminOrderController::class, 'show'])->name('admin.orders.show');
        Route::post('/orders/{id}/status', [AdminOrderController::class, 'updateStatus'])->name('admin.orders.status');
        Route::post('/orders/{id}/assign-courier', [AdminOrderController::class, 'assignCourier'])->name('admin.orders.assign-courier');
        Route::get('/orders/{id}/print', [AdminOrderController::class, 'printReceipt'])->name('admin.orders.print');

        // Foods
        Route::get('/foods', [AdminFoodController::class, 'index'])->name('admin.foods.index');
        Route::get('/foods/create', [AdminFoodController::class, 'create'])->name('admin.foods.create');
        Route::post('/foods', [AdminFoodController::class, 'store'])->name('admin.foods.store');
        Route::get('/foods/{id}/edit', [AdminFoodController::class, 'edit'])->name('admin.foods.edit');
        Route::put('/foods/{id}', [AdminFoodController::class, 'update'])->name('admin.foods.update');
        Route::post('/foods/{id}/toggle', [AdminFoodController::class, 'toggleAvailability'])->name('admin.foods.toggle');
        Route::delete('/foods/{id}', [AdminFoodController::class, 'destroy'])->name('admin.foods.destroy');

        // Categories
        Route::get('/categories', [AdminCategoryController::class, 'index'])->name('admin.categories.index');
        Route::post('/categories', [AdminCategoryController::class, 'store'])->name('admin.categories.store');
        Route::put('/categories/{id}', [AdminCategoryController::class, 'update'])->name('admin.categories.update');
        Route::delete('/categories/{id}', [AdminCategoryController::class, 'destroy'])->name('admin.categories.destroy');

        // Couriers
        Route::get('/couriers', [AdminCourierController::class, 'index'])->name('admin.couriers.index');
        Route::post('/couriers', [AdminCourierController::class, 'store'])->name('admin.couriers.store');
        Route::post('/couriers/{id}/toggle', [AdminCourierController::class, 'toggleStatus'])->name('admin.couriers.toggle');

        // Live Chat
        Route::get('/chat', [AdminChatController::class, 'index'])->name('admin.chat.index');
        Route::post('/chat/{chatId}/reply', [AdminChatController::class, 'reply'])->name('admin.chat.reply');

        // Settings
        Route::get('/settings', [AdminSettingController::class, 'index'])->name('admin.settings.index');
        Route::post('/settings', [AdminSettingController::class, 'update'])->name('admin.settings.update');
    });
});
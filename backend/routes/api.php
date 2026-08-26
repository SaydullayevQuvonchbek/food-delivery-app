<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\V1\AuthController;
use App\Http\Controllers\Api\V1\FoodController;
use App\Http\Controllers\Api\V1\OrderController;
use App\Http\Controllers\Api\V1\ChatController;
use App\Http\Controllers\Api\V1\CardController;
use App\Http\Controllers\Api\V1\NotificationController;
use App\Http\Controllers\Api\V1\PromotionController;
use App\Http\Controllers\Api\V1\AddressController;

Route::prefix('v1')->group(function () {

    // Public Auth - brute-force'ga qarshi qat'iy limit
    Route::middleware('throttle:6,1')->group(function () {
        Route::post('/auth/register', [AuthController::class, 'register']);
        Route::post('/auth/login', [AuthController::class, 'login']);
        Route::post('/auth/forgot-password', [AuthController::class, 'forgotPassword']);
        Route::post('/auth/verify-otp', [AuthController::class, 'verifyOtp']);
        Route::post('/auth/reset-password', [AuthController::class, 'resetPassword']);
    });

    // Public Catalog
    Route::middleware('throttle:60,1')->group(function () {
        Route::get('/categories', [FoodController::class, 'categories']);
        Route::get('/foods', [FoodController::class, 'index']);
        Route::get('/foods/{id}', [FoodController::class, 'show']);
        Route::get('/promotions', [PromotionController::class, 'index']);
    });

    // Protected Routes (Sanctum)
    Route::middleware(['auth:sanctum', 'throttle:120,1'])->group(function () {
        // Auth / Profile
        Route::get('/auth/profile', [AuthController::class, 'profile']);
        Route::put('/auth/profile', [AuthController::class, 'updateProfile']);
        Route::post('/auth/logout', [AuthController::class, 'logout']);

        // Orders
        Route::get('/orders', [OrderController::class, 'index']);
        Route::post('/orders', [OrderController::class, 'store']);
        Route::get('/orders/{id}', [OrderController::class, 'show']);

        // Promo kodni tekshirish
        Route::post('/promotions/validate', [PromotionController::class, 'validateCode']);

        // Saved Cards
        Route::get('/cards', [CardController::class, 'index']);
        Route::post('/cards', [CardController::class, 'store']);
        Route::delete('/cards/{id}', [CardController::class, 'destroy']);

        // User Addresses
        Route::get('/addresses', [AddressController::class, 'index']);
        Route::post('/addresses', [AddressController::class, 'store']);
        Route::delete('/addresses/{id}', [AddressController::class, 'destroy']);

        // Chats
        Route::get('/chats', [ChatController::class, 'index']);
        Route::get('/chats/{chatId}/messages', [ChatController::class, 'messages']);
        Route::post('/chats/{chatId}/messages', [ChatController::class, 'sendMessage']);

        // Notifications
        Route::get('/notifications', [NotificationController::class, 'index']);
        Route::post('/notifications/{id}/read', [NotificationController::class, 'markAsRead']);
        Route::post('/notifications/read-all', [NotificationController::class, 'markAllAsRead']);
    });
});

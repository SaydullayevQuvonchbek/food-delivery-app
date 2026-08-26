<?php

use Illuminate\Support\Facades\Route;

Route::get('/', function () {
    return response()->json([
        'app' => 'Food Delivery API',
        'status' => 'online',
        'version' => '1.0.0',
        'docs' => '/api/v1'
    ]);
});
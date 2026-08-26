<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Promotion extends Model
{
    protected $fillable = [
        'code',
        'title',
        'discount_type',
        'discount_value',
        'min_order_amount',
        'valid_until',
        'is_active',
        'usage_count'
    ];

    protected $casts = [
        'discount_value' => 'float',
        'min_order_amount' => 'float',
        'valid_until' => 'date',
        'is_active' => 'boolean',
        'usage_count' => 'integer'
    ];
}
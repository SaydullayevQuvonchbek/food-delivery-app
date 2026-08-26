<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Food extends Model
{
    protected $table = 'foods';

    protected $fillable = [
        'category_id',
        'name',
        'description',
        'price',
        'original_price',
        'rating',
        'review_count',
        'distance',
        'delivery_time',
        'is_free_delivery',
        'image_url',
        'is_featured',
        'is_available'
    ];

    public function category()
    {
        return $this->belongsTo(Category::class);
    }
}
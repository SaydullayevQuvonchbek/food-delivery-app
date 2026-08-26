<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Category extends Model
{
    protected $fillable = ['name', 'icon', 'order_index', 'is_active'];

    public function foods()
    {
        return $this->hasMany(Food::class);
    }
}
<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class SavedCard extends Model
{
    protected $fillable = [
        'user_id',
        'card_holder_name',
        'last_four',
        'expiry_date',
        'card_type',
        'is_default'
    ];

    public function user()
    {
        return $this->belongsTo(User::class);
    }
}
<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('promotions', function (Blueprint $table) {
            $table->id();
            $table->string('code')->unique();
            $table->string('title')->nullable();
            $table->enum('discount_type', ['fixed', 'percent'])->default('fixed');
            $table->decimal('discount_value', 10, 2)->default(0.00);
            $table->decimal('min_order_amount', 10, 2)->default(0.00);
            $table->date('valid_until')->nullable();
            $table->boolean('is_active')->default(true);
            $table->integer('usage_count')->default(0);
            $table->timestamps();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('promotions');
    }
};
<?php

use Illuminate\Support\Facades\Artisan;

Artisan::command('inspire', function () {
    $this->comment('Food Delivery App backend active!');
})->purpose('Display an inspiring quote');
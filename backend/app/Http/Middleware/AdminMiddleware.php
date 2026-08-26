<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

class AdminMiddleware
{
    public function handle(Request $request, Closure $next)
    {
        if (!Auth::check()) {
            return redirect()->route('admin.login');
        }

        $user = Auth::user();
        if (!in_array($user->role, ['admin', 'operator', 'manager'])) {
            Auth::logout();
            return redirect()->route('admin.login')->withErrors(['email' => 'Ushbu panelga kirish huquqingiz yo\'q.']);
        }

        return $next($request);
    }
}
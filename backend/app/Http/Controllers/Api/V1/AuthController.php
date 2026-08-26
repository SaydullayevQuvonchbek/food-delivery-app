<?php

namespace App\Http\Controllers\Api\V1;

use App\Http\Controllers\Controller;
use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Cache;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Str;
use Illuminate\Validation\ValidationException;

class AuthController extends Controller
{
    private const OTP_TTL = 600;          // 10 daqiqa
    private const RESET_TOKEN_TTL = 900;  // 15 daqiqa

    public function register(Request $request)
    {
        $validated = $request->validate([
            'full_name' => 'required|string|max:255',
            'email' => 'required|string|email|max:255|unique:users',
            'password' => 'required|string|min:8',
            'phone' => 'nullable|string|max:20|unique:users',
        ]);

        $user = User::create([
            'full_name' => $validated['full_name'],
            'email' => $validated['email'],
            'password' => Hash::make($validated['password']),
            'phone' => $validated['phone'] ?? null,
            'role' => 'customer',
            'status' => 'active',
        ]);

        $token = $user->createToken('mobile_app')->plainTextToken;

        return response()->json([
            'success' => true,
            'message' => 'Account created successfully',
            'data' => [
                'user' => $user,
                'token' => $token,
            ]
        ], 201);
    }

    public function login(Request $request)
    {
        $validated = $request->validate([
            'email' => 'required|string|email',
            'password' => 'required|string',
        ]);

        $user = User::where('email', $validated['email'])->first();

        if (! $user || ! Hash::check($validated['password'], $user->password)) {
            throw ValidationException::withMessages([
                'email' => ['The provided credentials do not match our records.'],
            ]);
        }

        if ($user->status !== 'active') {
            return response()->json([
                'success' => false,
                'message' => 'Ushbu hisob bloklangan. Qollab-quvvatlash xizmatiga murojaat qiling.'
            ], 403);
        }

        // Bir foydalanuvchi uchun eski mobil tokenlar to'planib qolmasligi uchun
        $user->tokens()->where('name', 'mobile_app')->delete();
        $token = $user->createToken('mobile_app')->plainTextToken;

        return response()->json([
            'success' => true,
            'message' => 'Logged in successfully',
            'data' => [
                'user' => $user,
                'token' => $token,
            ]
        ]);
    }

    public function forgotPassword(Request $request)
    {
        $validated = $request->validate([
            'email' => 'required|email|exists:users,email',
            'channel' => 'nullable|in:email,whatsapp'
        ]);

        $email = strtolower(trim($validated['email']));
        $otp = config('app.debug') ? '9627' : (string) random_int(1000, 9999);

        Cache::put($this->otpKey($email), $otp, self::OTP_TTL);

        $payload = ['otp_expires_in' => self::OTP_TTL];
        if (config('app.debug')) {
            $payload['demo_otp'] = $otp;
        }

        return response()->json([
            'success' => true,
            'message' => 'Verification code sent successfully',
            'data' => $payload
        ]);
    }

    public function verifyOtp(Request $request)
    {
        $validated = $request->validate([
            'email' => 'required|email|exists:users,email',
            'otp' => 'required|string|size:4'
        ]);

        $email = strtolower(trim($validated['email']));
        $expected = Cache::get($this->otpKey($email));

        if (! $expected || ! hash_equals((string) $expected, (string) $validated['otp'])) {
            return response()->json([
                'success' => false,
                'message' => 'Invalid or expired OTP code'
            ], 422);
        }

        Cache::forget($this->otpKey($email));

        // Parolni almashtirish uchun bir martalik token
        $resetToken = Str::random(64);
        Cache::put($this->resetKey($email), hash('sha256', $resetToken), self::RESET_TOKEN_TTL);

        return response()->json([
            'success' => true,
            'message' => 'OTP verified successfully',
            'data' => [
                'reset_token' => $resetToken,
                'expires_in' => self::RESET_TOKEN_TTL
            ]
        ]);
    }

    public function resetPassword(Request $request)
    {
        $validated = $request->validate([
            'email' => 'required|email|exists:users,email',
            'reset_token' => 'required|string',
            'password' => 'required|string|min:8|confirmed',
        ]);

        $email = strtolower(trim($validated['email']));
        $stored = Cache::get($this->resetKey($email));

        if (! $stored || ! hash_equals($stored, hash('sha256', $validated['reset_token']))) {
            return response()->json([
                'success' => false,
                'message' => 'Parolni tiklash muddati tugagan. Kodni qaytadan sorang.'
            ], 422);
        }

        $user = User::where('email', $email)->first();
        $user->password = Hash::make($validated['password']);
        $user->save();

        Cache::forget($this->resetKey($email));
        $user->tokens()->delete(); // Barcha qurilmalardan chiqarish

        return response()->json([
            'success' => true,
            'message' => 'Password reset successfully'
        ]);
    }

    public function profile(Request $request)
    {
        return response()->json([
            'success' => true,
            'data' => $request->user()
        ]);
    }

    public function updateProfile(Request $request)
    {
        $user = $request->user();

        $validated = $request->validate([
            'full_name' => 'sometimes|string|max:255',
            'email' => 'sometimes|email|max:255|unique:users,email,' . $user->id,
            'phone' => 'sometimes|nullable|string|max:20|unique:users,phone,' . $user->id,
            'date_of_birth' => 'sometimes|nullable|string|max:20',
            'gender' => 'sometimes|string|in:Male,Female,Other',
            'avatar_url' => 'sometimes|nullable|string'
        ]);

        $user->update($validated);

        return response()->json([
            'success' => true,
            'message' => 'Profile updated successfully',
            'data' => $user->fresh()
        ]);
    }

    public function logout(Request $request)
    {
        $request->user()->currentAccessToken()->delete();

        return response()->json([
            'success' => true,
            'message' => 'Logged out successfully'
        ]);
    }

    private function otpKey(string $email): string
    {
        return 'pwd_otp:' . sha1($email);
    }

    private function resetKey(string $email): string
    {
        return 'pwd_reset:' . sha1($email);
    }
}

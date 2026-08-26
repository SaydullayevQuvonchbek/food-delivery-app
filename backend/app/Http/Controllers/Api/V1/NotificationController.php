<?php

namespace App\Http\Controllers\Api\V1;

use App\Http\Controllers\Controller;
use App\Models\AppNotification;
use Illuminate\Http\Request;

class NotificationController extends Controller
{
    public function index(Request $request)
    {
        $notifications = AppNotification::where('user_id', $request->user()->id)
            ->latest()
            ->limit(100)
            ->get();

        return response()->json([
            'success' => true,
            'data' => $notifications,
            'meta' => [
                'unread_count' => AppNotification::where('user_id', $request->user()->id)
                    ->where('is_read', false)
                    ->count()
            ]
        ]);
    }

    public function markAsRead(Request $request, $id)
    {
        $notification = AppNotification::where('user_id', $request->user()->id)->find($id);

        if (! $notification) {
            return response()->json(['success' => false, 'message' => 'Bildirishnoma topilmadi'], 404);
        }

        $notification->update(['is_read' => true]);

        return response()->json(['success' => true, 'message' => 'O\'qilgan deb belgilandi']);
    }

    public function markAllAsRead(Request $request)
    {
        AppNotification::where('user_id', $request->user()->id)
            ->where('is_read', false)
            ->update(['is_read' => true]);

        return response()->json(['success' => true, 'message' => 'Barchasi o\'qilgan deb belgilandi']);
    }
}

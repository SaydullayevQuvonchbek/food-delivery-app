package com.fooddelivery.data.storage

import android.content.Context
import android.content.SharedPreferences

actual class LocalStorage(private val prefs: SharedPreferences) {
    actual fun getString(key: String): String? = prefs.getString(key, null)
    actual fun setString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }
    actual fun getBoolean(key: String, default: Boolean): Boolean = prefs.getBoolean(key, default)
    actual fun setBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }
    actual fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }
    actual fun clear() {
        prefs.edit().clear().apply()
    }
}

private var appContext: Context? = null

fun initLocalStorage(context: Context) {
    appContext = context.applicationContext
}

actual fun getLocalStorage(): LocalStorage {
    val ctx = appContext ?: throw IllegalStateException("LocalStorage not initialized. Call initLocalStorage(context) first.")
    val prefs = ctx.getSharedPreferences("food_delivery_app_prefs", Context.MODE_PRIVATE)
    return LocalStorage(prefs)
}
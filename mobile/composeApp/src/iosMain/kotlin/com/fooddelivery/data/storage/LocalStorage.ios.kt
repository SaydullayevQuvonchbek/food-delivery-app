package com.fooddelivery.data.storage

import platform.Foundation.NSUserDefaults

/**
 * iOS uchun `actual` amalga oshirilishi yo'q edi - shu sababli iosX64/iosArm64/iosSimulatorArm64
 * target'lari umuman kompilyatsiya bo'lmasdi.
 */
actual class LocalStorage(private val defaults: NSUserDefaults) {
    actual fun getString(key: String): String? = defaults.stringForKey(key)

    actual fun setString(key: String, value: String) {
        defaults.setObject(value, key)
    }

    actual fun getBoolean(key: String, default: Boolean): Boolean =
        if (defaults.objectForKey(key) == null) default else defaults.boolForKey(key)

    actual fun setBoolean(key: String, value: Boolean) {
        defaults.setBool(value, key)
    }

    actual fun remove(key: String) {
        defaults.removeObjectForKey(key)
    }

    actual fun clear() {
        val dictionary = defaults.dictionaryRepresentation()
        dictionary.keys.forEach { key ->
            (key as? String)?.let { defaults.removeObjectForKey(it) }
        }
    }
}

actual fun getLocalStorage(): LocalStorage = LocalStorage(NSUserDefaults.standardUserDefaults)

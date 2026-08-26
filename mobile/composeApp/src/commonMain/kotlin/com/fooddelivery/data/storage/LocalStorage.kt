package com.fooddelivery.data.storage

expect class LocalStorage {
    fun getString(key: String): String?
    fun setString(key: String, value: String)
    fun getBoolean(key: String, default: Boolean = false): Boolean
    fun setBoolean(key: String, value: Boolean)
    fun remove(key: String)
    fun clear()
}

expect fun getLocalStorage(): LocalStorage
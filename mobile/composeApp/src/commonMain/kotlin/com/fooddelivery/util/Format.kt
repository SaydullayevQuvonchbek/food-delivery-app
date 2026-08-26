package com.fooddelivery.util

import kotlin.math.abs
import kotlin.math.roundToLong

/**
 * Narxlar so'mda saqlanadi. Ilgari UI'da "$ 12230" ko'rinishida chiqar edi -
 * endi hamma joyda bir xil: "12 230 so'm".
 */
fun formatPrice(amount: Double, withCurrency: Boolean = true): String {
    val rounded = amount.roundToLong()
    val grouped = groupDigits(abs(rounded))
    val sign = if (rounded < 0) "-" else ""
    return if (withCurrency) "$sign$grouped so'm" else "$sign$grouped"
}

private fun groupDigits(value: Long): String {
    val digits = value.toString()
    if (digits.length <= 3) return digits

    val builder = StringBuilder()
    var counter = 0
    for (index in digits.lastIndex downTo 0) {
        builder.append(digits[index])
        counter++
        if (counter % 3 == 0 && index != 0) builder.append(' ')
    }
    return builder.reverse().toString()
}

/** Karta raqamini "8600 1234 5678 9012" ko'rinishida ajratadi */
fun formatCardNumber(input: String): String {
    val digits = input.filter { it.isDigit() }.take(19)
    return digits.chunked(4).joinToString(" ")
}

/** Foydalanuvchi kiritayotgan amal qilish muddatini MM/YY ko'rinishiga keltiradi */
fun formatExpiry(input: String): String {
    val digits = input.filter { it.isDigit() }.take(4)
    return when {
        digits.length <= 2 -> digits
        else -> digits.substring(0, 2) + "/" + digits.substring(2)
    }
}

fun isValidExpiry(value: String): Boolean {
    val parts = value.split("/")
    if (parts.size != 2 || parts[0].length != 2 || parts[1].length != 2) return false
    val month = parts[0].toIntOrNull() ?: return false
    parts[1].toIntOrNull() ?: return false
    return month in 1..12
}

fun isValidEmail(value: String): Boolean {
    val trimmed = value.trim()
    val atIndex = trimmed.indexOf('@')
    if (atIndex <= 0 || atIndex != trimmed.lastIndexOf('@')) return false
    val domain = trimmed.substring(atIndex + 1)
    return domain.contains('.') && !domain.startsWith('.') && !domain.endsWith('.') && domain.length >= 3
}

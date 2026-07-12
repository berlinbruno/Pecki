package dev.berlinbruno.pecki.utils

import java.util.Locale
import kotlin.math.abs
import kotlin.math.ln
import kotlin.math.pow

/**
 * Standard currency formatting.
 * Shows decimals only for amounts < 100.
 */
fun formatCurrency(amount: Double, currencySymbol: String): String {
    val absAmount = abs(amount)

    val formatted = if (absAmount >= 100) {
        String.format(Locale.US, "%.0f", amount)
    } else {
        String.format(Locale.US, "%.2f", amount)
            .trimEnd('0')
            .trimEnd('.')
    }

    return currencySymbol + formatted
}

/**
 * Compact currency formatting (e.g., 10K, 1.2M).
 * Used for list views where space is limited.
 */
fun formatCompactCurrency(value: Double, symbol: String): String {
    if (value == 0.0) return "${symbol}0"

    val absValue = abs(value)

    // Allow decimals only for numbers < 100
    if (absValue < 100) {
        val formatted = String.format(Locale.US, "%.2f", value)
            .trimEnd('0')
            .trimEnd('.')
        return "$symbol$formatted"
    }

    // No decimals for 4-digit numbers
    if (absValue < 10_000) {
        return "$symbol${value.toInt()}"
    }

    val units = arrayOf("", "K", "M", "B", "T", "P", "E", "Z", "Y")

    val group = (ln(absValue) / ln(1000.0))
        .toInt()
        .coerceAtMost(units.lastIndex)

    val scaled = value / 1000.0.pow(group)

    val formatted = String.format(Locale.US, "%.0f", scaled)

    return "$symbol$formatted${units[group]}"
}

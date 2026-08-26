package com.darkytm.wallet.util

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object CurrencyUtils {
    /**
     * Retrieves the currency symbol based on locale or configured currency code.
     * Easily extensible to user-selected currency codes in the future.
     */
    fun getCurrencySymbol(locale: Locale = Locale.getDefault(), customCurrencyCode: String? = null): String {
        return try {
            if (!customCurrencyCode.isNullOrBlank()) {
                Currency.getInstance(customCurrencyCode).getSymbol(locale)
            } else {
                Currency.getInstance(locale).getSymbol(locale)
            }
        } catch (_: Exception) {
            "$"
        }
    }

    /**
     * Formats an amount using the system locale currency formatter or custom currency.
     */
    fun formatAmount(
        amount: Double,
        locale: Locale = Locale.getDefault(),
        customCurrencyCode: String? = null
    ): String {
        return try {
            val formatter = NumberFormat.getCurrencyInstance(locale)
            if (!customCurrencyCode.isNullOrBlank()) {
                formatter.currency = Currency.getInstance(customCurrencyCode)
            }
            formatter.format(amount)
        } catch (_: Exception) {
            String.format(Locale.US, "$%.2f", amount)
        }
    }

    /**
     * Sanitizes decimal input so users cannot enter multiple decimal points or invalid characters.
     */
    fun sanitizeAmountInput(input: String): String {
        val filtered = input.filter { it.isDigit() || it == '.' }
        val firstDotIndex = filtered.indexOf('.')
        if (firstDotIndex == -1) return filtered
        val beforeDot = filtered.substring(0, firstDotIndex + 1)
        val afterDot = filtered.substring(firstDotIndex + 1).replace(".", "")
        return beforeDot + afterDot
    }
}

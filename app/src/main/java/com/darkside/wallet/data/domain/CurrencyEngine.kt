package com.darkside.wallet.data.domain

import java.text.NumberFormat
import java.util.*

/**
 * CurrencyEngine: Single source of truth for currency formatting and symbols.
 * Ported from Flutter version for 100% parity.
 */
object CurrencyEngine {

    /**
     * Maps a currency code to its primary locale to ensure correct
     * numbering systems (e.g., lakh/crore for INR, decimal commas for EUR).
     */
    fun getLocaleForCurrency(currencyCode: String): Locale {
        return when (currencyCode.uppercase()) {
            "INR" -> Locale("en", "IN") // 1,00,00,000 (Lakh/Crore)
            "USD" -> Locale.US          // 100,000,000
            "EUR" -> Locale.GERMANY     // 100.000.000,00
            "GBP" -> Locale.UK
            "JPY" -> Locale.JAPAN
            "CNY" -> Locale.CHINA
            "AUD" -> Locale("en", "AU")
            "CAD" -> Locale.CANADA
            "BRL" -> Locale("pt", "BR")
            "RUB" -> Locale("ru", "RU")
            "KRW" -> Locale.KOREA
            else -> Locale.US
        }
    }

    /**
     * Formats a double amount into a localized currency string.
     */
    fun formatCurrency(amount: Double, currencyCode: String, showSymbol: Boolean = true): String {
        val locale = getLocaleForCurrency(currencyCode)
        val format = NumberFormat.getCurrencyInstance(locale)
        
        // Configure currency based on code
        try {
            format.currency = Currency.getInstance(currencyCode.uppercase())
        } catch (e: Exception) {
            // Fallback if currency code is invalid
        }

        // Handle decimal digits for specific currencies
        if (currencyCode.uppercase() == "JPY" || currencyCode.uppercase() == "KRW") {
            format.maximumFractionDigits = 0
        } else {
            format.maximumFractionDigits = 2
        }

        val result = format.format(amount)
        
        return if (showSymbol) {
            result
        } else {
            // Remove symbol manually if needed, or use decimal format
            val noSymbolFormat = NumberFormat.getNumberInstance(locale)
            noSymbolFormat.maximumFractionDigits = format.maximumFractionDigits
            noSymbolFormat.minimumFractionDigits = format.maximumFractionDigits
            noSymbolFormat.format(amount)
        }
    }

    /**
     * Returns only the currency symbol for the given code.
     */
    fun getSymbol(currencyCode: String): String {
        return try {
            Currency.getInstance(currencyCode.uppercase()).symbol
        } catch (e: Exception) {
            "$"
        }
    }
}

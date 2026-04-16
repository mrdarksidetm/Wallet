package com.darkside.wallet.data.domain

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object CurrencyEngine {
    /**
     * Maps a currency code to its primary locale to ensure correct
     * numbering systems (e.g., lakh/crore for INR, decimal commas for EUR).
     */
    fun getLocaleForCurrency(currencyCode: String): Locale {
        return when (currencyCode) {
            "INR" -> Locale("en", "IN")
            "USD" -> Locale.US
            "EUR" -> Locale.GERMANY
            "GBP" -> Locale.UK
            "JPY" -> Locale.JAPAN
            "CNY" -> Locale.CHINA
            "AUD" -> Locale("en", "AU")
            "CAD" -> Locale.CANADA
            "BRL" -> Locale("pt", "BR")
            "RUB" -> Locale("ru", "RU")
            "IDR" -> Locale("id", "ID")
            "KRW" -> Locale.KOREA
            "TRY" -> Locale("tr", "TR")
            "ZAR" -> Locale("en", "ZA")
            "MXN" -> Locale("es", "MX")
            "SGD" -> Locale("en", "SG")
            "HKD" -> Locale("zh", "HK")
            "NZD" -> Locale("en", "NZ")
            "CHF" -> Locale("de", "CH")
            "AED" -> Locale("ar", "AE")
            "SAR" -> Locale("ar", "SA")
            "PKR" -> Locale("en", "PK")
            "BDT" -> Locale("bn", "BD")
            "LKR" -> Locale("en", "LKR")
            "MYR" -> Locale("ms", "MY")
            "THB" -> Locale("th", "TH")
            "VND" -> Locale("vi", "VN")
            "PHP" -> Locale("en", "PH")
            "EGP" -> Locale("ar", "EG")
            "NGN" -> Locale("en", "NG")
            "COP" -> Locale("es", "CO")
            "ARS" -> Locale("es", "AR")
            "CLP" -> Locale("es", "CL")
            "PEN" -> Locale("es", "PE")
            "TWD" -> Locale("zh", "TW")
            "KWD" -> Locale("ar", "KW")
            "QAR" -> Locale("ar", "QA")
            "OMR" -> Locale("ar", "OM")
            "BHD" -> Locale("ar", "BH")
            "ILS" -> Locale("he", "IL")
            "PLN" -> Locale("pl", "PL")
            "SEK" -> Locale("sv", "SE")
            "NOK" -> Locale("nb", "NO")
            "DKK" -> Locale("da", "DK")
            "HUF" -> Locale("hu", "HU")
            "CZK" -> Locale("cs", "CZ")
            else -> Locale.US
        }
    }

    /**
     * Formats a double amount into a localized currency string.
     */
    fun formatCurrency(amount: Double, currencyCode: String, showSymbol: Boolean = true): String {
        val locale = getLocaleForCurrency(currencyCode)
        val format = NumberFormat.getCurrencyInstance(locale)
        try {
            format.currency = Currency.getInstance(currencyCode)
        } catch (e: Exception) {
            // Fallback if currency code is invalid
        }

        if (!showSymbol) {
            val decimalFormat = NumberFormat.getNumberInstance(locale)
            decimalFormat.minimumFractionDigits = format.minimumFractionDigits
            decimalFormat.maximumFractionDigits = format.maximumFractionDigits
            return decimalFormat.format(amount)
        }
        
        return format.format(amount)
    }

    /**
     * Returns only the currency symbol for the given code.
     */
    fun getSymbol(currencyCode: String): String {
        return try {
            Currency.getInstance(currencyCode).getSymbol(getLocaleForCurrency(currencyCode))
        } catch (e: Exception) {
            currencyCode
        }
    }
}

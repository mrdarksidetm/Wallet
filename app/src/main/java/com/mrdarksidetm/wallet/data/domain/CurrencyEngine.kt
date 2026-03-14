package com.mrdarksidetm.wallet.data.domain

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Phase 26: Offline Multi-Currency Conversion
 * 
 * Allows users to log expenses during travel.
 * 
 * CRITICAL: Mathematical Precision
 * We use `BigDecimal` instead of `Double` or `Float` to prevent 
 * floating-point arithmetic errors when converting currencies natively.
 */
object CurrencyEngine {
    
    // In a real app, this rate is fetched once via a "Sync Rates" button and cached offline.
    private var cachedExchangeRate: BigDecimal = BigDecimal("1.0")

    fun updateCachedRate(newRate: Double) {
        cachedExchangeRate = BigDecimal.valueOf(newRate)
    }

    fun convertToHomeCurrency(foreignAmount: Double): Double {
        val foreign = BigDecimal.valueOf(foreignAmount)
        val homeAmount = foreign.multiply(cachedExchangeRate)
        return homeAmount.setScale(2, RoundingMode.HALF_UP).toDouble()
    }
}

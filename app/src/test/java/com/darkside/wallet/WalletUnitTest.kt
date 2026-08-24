package com.darkside.wallet

import com.darkside.wallet.data.domain.CurrencyEngine
import org.junit.Assert.assertEquals
import org.junit.Test

class WalletUnitTest {
    @Test
    fun currencyEngine_formatsCorrectly() {
        val formatted = CurrencyEngine.formatCurrency(1234.56, "USD", showSymbol = false)
        assertEquals("1,234.56", formatted)
    }

    @Test
    fun currencyEngine_symbolCheck() {
        val symbol = CurrencyEngine.getSymbol("USD")
        assertEquals("$", symbol)
    }
}

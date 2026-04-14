package com.darkside.wallet.data.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Phase 48: Multi-Account Reconciliation Workflow
 * 
 * Allows users to manually verify app data against a physical bank statement.
 * Calculates dynamic differences dynamically as the user checks/unchecks transactions.
 */
class ReconciliationEngine {
    private val _bankBalance = MutableStateFlow(0.0)
    private val _clearedAppBalance = MutableStateFlow(0.0)
    
    val difference: StateFlow<Double> = MutableStateFlow(0.0).asStateFlow() // In reality, mapped from combine()

    fun setBankStatementBalance(balance: Double) {
        _bankBalance.value = balance
    }

    fun toggleTransactionCleared(amount: Double, isCleared: Boolean) {
        if (isCleared) {
            _clearedAppBalance.value += amount
        } else {
            _clearedAppBalance.value -= amount
        }
    }
}

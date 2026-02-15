package com.antigravity.moneytracker.domain.ai

import com.antigravity.moneytracker.domain.entity.Transaction
import com.antigravity.moneytracker.domain.entity.TransactionType

class SpendingAnalyzer {

    fun analyze(transactions: List<Transaction>): String {
        if (transactions.isEmpty()) return "Start adding transactions to get smart insights."

        val totalExpense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        val totalIncome = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        
        // Simple Heuristic AI Logic
        if (totalExpense > totalIncome && totalIncome > 0) {
            return "⚠️ Alert: You are spending more than you earn this month."
        }
        
        if (totalExpense == 0.0) {
            return "Good start! No expenses recorded yet."
        }
        
        // Find top category (mock logic as we don't have full category names yet, just IDs)
        // In a real TFLite model, we would predict the category from the description here.
        
        val expenseCount = transactions.count { it.type == TransactionType.EXPENSE }
        if (expenseCount > 5) {
            return "Spending Trend: You are making frequent small purchases."
        }

        return "Finances look stable. Keep it up!"
    }
}

package com.mrdarksidetm.wallet.data.domain

import androidx.room.withTransaction
import com.mrdarksidetm.wallet.data.AppDatabase
import com.mrdarksidetm.wallet.data.TransactionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * Phase 49: Simulated "Round-Up" Savings Goals
 * 
 * Appends logic to automatically "round up" expenses and transfer the change 
 * to a savings account.
 * 
 * CRITICAL: Transaction Atomicity
 * Uses Room's `withTransaction` block so that the original expense AND the 
 * round-up transfer are committed together. If either fails, the entire block rolls back.
 */
class RoundUpEngine(private val db: AppDatabase) {
    suspend fun insertExpenseWithRoundUp(
        expense: TransactionEntity, 
        savingsAccountId: String, 
        isRoundUpEnabled: Boolean
    ) {
        withContext(Dispatchers.IO) {
            db.withTransaction {
                // 1. Insert original expense
                db.transactionDao().insertTransaction(expense)

                // 2. Calculate fractional difference if Round Up is active
                if (isRoundUpEnabled && expense.type == "Expense") {
                    val ceilValue = Math.ceil(expense.amount)
                    val difference = ceilValue - expense.amount

                    if (difference > 0) {
                        val roundUpTransfer = TransactionEntity(
                            id = UUID.randomUUID().toString(),
                            amount = difference,
                            note = "Round-up from ${expense.note ?: "expense"}",
                            date = expense.date,
                            type = "Transfer",
                            category = expense.category, // Keep original category for tracking
                            accountId = savingsAccountId
                        )
                        db.transactionDao().insertTransaction(roundUpTransfer)
                    }
                }
            }
        }
    }
}

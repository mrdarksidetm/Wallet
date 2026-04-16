package com.darkside.wallet.data.domain

import androidx.room.withTransaction
import com.darkside.wallet.data.AppDatabase
import com.darkside.wallet.data.entity.TransactionEntity
import com.darkside.wallet.data.entity.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Phase 49: Simulated "Round-Up" Savings Goals
 * 
 * Appends logic to automatically "round up" expenses and transfer the change 
 * to a savings account.
 */
class RoundUpEngine(private val db: AppDatabase) {
    suspend fun insertExpenseWithRoundUp(
        expense: TransactionEntity, 
        savingsAccountId: Long, 
        isRoundUpEnabled: Boolean
    ) {
        withContext(Dispatchers.IO) {
            db.withTransaction {
                // 1. Insert original expense
                db.transactionDao().insertTransaction(expense)

                // 2. Calculate fractional difference if Round Up is active
                if (isRoundUpEnabled && expense.type == TransactionType.EXPENSE) {
                    val ceilValue = Math.ceil(expense.amount)
                    val difference = ceilValue - expense.amount

                    if (difference > 0) {
                        val roundUpTransfer = TransactionEntity(
                            amount = difference,
                            note = "Round-up from ${expense.note ?: "expense"}",
                            date = expense.date,
                            type = TransactionType.TRANSFER,
                            categoryId = expense.categoryId, // Keep original category for tracking
                            accountId = expense.accountId,
                            transferAccountId = savingsAccountId
                        )
                        db.transactionDao().insertTransaction(roundUpTransfer)
                    }
                }
            }
        }
    }
}

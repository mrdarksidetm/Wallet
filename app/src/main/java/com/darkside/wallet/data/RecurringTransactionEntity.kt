package com.darkside.wallet.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "recurring_transactions")
data class RecurringTransactionEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val amount: Double,
    val note: String,
    val category: String,
    val type: String, // Income, Expense
    val accountId: String,
    val frequency: String, // Daily, Weekly, Monthly, Yearly
    val nextOccurrence: Long,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

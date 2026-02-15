package com.antigravity.moneytracker.domain.entity

import java.util.Date

enum class TransactionType {
    INCOME, EXPENSE, TRANSFER
}

data class Transaction(
    val id: Long = 0,
    val amount: Double,
    val description: String,
    val date: Date,
    val type: TransactionType,
    val categoryId: Long?,
    val accountId: Long?
)

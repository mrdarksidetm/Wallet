package com.mrdarksidetm.wallet.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val amount: Double,
    val category: String, // Or Category UUID
    val period: String, // Weekly, Monthly, Yearly, One-Time
    val startDate: Long,
    val endDate: Long,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

package com.antigravity.moneytracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.antigravity.moneytracker.domain.entity.TransactionType
import java.util.Date

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val description: String,
    val date: Long, // Stored as timestamp
    val type: TransactionType,
    val categoryId: Long?,
    val accountId: Long?
)

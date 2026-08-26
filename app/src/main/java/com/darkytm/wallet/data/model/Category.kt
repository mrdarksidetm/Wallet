package com.darkytm.wallet.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val iconEmoji: String = "📁",
    val colorHex: Long = 0xFF8D4F00,
    val type: TransactionType = TransactionType.EXPENSE,
    val isDefault: Boolean = false
)

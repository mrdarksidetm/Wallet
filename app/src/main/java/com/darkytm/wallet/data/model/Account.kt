package com.darkytm.wallet.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class Account(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val iconEmoji: String = "💳",
    val colorHex: Long = 0xFF8D4F00,
    val initialBalance: Double = 0.0,
    val type: AccountType = AccountType.BANK,
    val isArchived: Boolean = false
)

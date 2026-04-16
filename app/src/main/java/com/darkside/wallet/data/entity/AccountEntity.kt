package com.darkside.wallet.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import java.util.UUID

@Entity(
    tableName = "accounts",
    indices = [
        Index(value = ["uuid"], unique = true),
        Index(value = ["name"]),
        Index(value = ["validThru"]),
        Index(value = ["type"])
    ]
)
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val uuid: String = UUID.randomUUID().toString(),
    val name: String,
    val bankName: String = "",
    val number: String = "",
    val validThru: Long = System.currentTimeMillis(),
    val icon: String = "account_balance_wallet",
    val color: String = "0xFF2196F3",
    val isPredefined: Boolean = false,
    val balance: Double = 0.0,
    val isArchived: Boolean = false,
    val isDeleted: Boolean = false,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val order: Int = 0,
    val type: AccountType = AccountType.CASH
)

enum class AccountType {
    CASH,
    CARD,
    SAVINGS
}

package com.darkside.wallet.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Why UUIDs?
 * Auto-incrementing integers (Long) cause severe ID collisions during offline-first syncing.
 * If two devices create an account offline, they might both get ID '1'. When syncing to a central server,
 * one will overwrite the other. UUIDs guarantee global uniqueness across all distributed nodes.
 */
@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val bankName: String = "",
    val number: String = "",
    val validThru: Long = System.currentTimeMillis(),
    val icon: String = "account_balance_wallet",
    val color: Int = 0xFF2196F3.toInt(),
    val isPredefined: Boolean = false,
    val initialBalance: Double = 0.0,
    val balance: Double = 0.0,
    val isArchived: Boolean = false,
    val isDeleted: Boolean = false,
    val isDefault: Boolean = false,
    val isExcludedFromTotal: Boolean = false,
    val parentAccountId: String? = null,
    val order: Int = 0,
    val type: String = "cash", // cash, card, savings
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

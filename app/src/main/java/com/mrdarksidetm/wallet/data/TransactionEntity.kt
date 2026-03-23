package com.mrdarksidetm.wallet.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Why UUIDs?
 * Auto-incrementing IDs (Long) are dangerous in offline-first apps because they generate 
 * sequential IDs (1, 2, 3...) locally. When multiple devices sync to a remote server, 
 * these local IDs will inevitably collide, leading to data loss or database panics.
 * 
 * UUIDs (Universally Unique Identifiers) are generated as 128-bit strings that are 
 * practically guaranteed to be globally unique. This allows full offline creation 
 * and seamless conflict-free syncing across any number of devices.
 */
@Entity(
    tableName = "transactions",
    indices = [androidx.room.Index(value = ["accountId"])],
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TransactionEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val amount: Double,
    val date: Long,
    val type: String, // Income, Expense, Transfer
    val note: String,
    val category: String, // Keep as String name or shift to Category UUID later
    val accountId: String,
    val isArchived: Boolean = false // Updated to String to match AccountEntity UUID
)

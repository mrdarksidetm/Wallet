package com.darkside.wallet.data

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
    indices = [
        androidx.room.Index(value = ["accountId"]),
        androidx.room.Index(value = ["personId"]),
        androidx.room.Index(value = ["loanId"]),
        androidx.room.Index(value = ["transferAccountId"])
    ],
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
    val amount: Double = 0.0,
    val date: Long = System.currentTimeMillis(),
    val type: String = "expense", // income, expense, transfer
    val note: String = "",
    val categoryId: String = "",
    val accountId: String = "",
    val transferAccountId: String? = null,
    val personId: String? = null,
    val loanId: String? = null,
    val placeId: String? = null,
    val icon: String? = null,
    val color: Int? = null,
    val tags: List<String> = emptyList(), // Needs TypeConverter
    val isArchived: Boolean = false,
    val isDeleted: Boolean = false,
    val isTemplate: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

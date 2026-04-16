package com.darkside.wallet.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import androidx.room.ForeignKey
import java.util.UUID

@Entity(
    tableName = "transactions",
    indices = [
        Index(value = ["uuid"], unique = true),
        Index(value = ["date"]),
        Index(value = ["type"]),
        Index(value = ["accountId"]),
        Index(value = ["categoryId"]),
        Index(value = ["personId"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val uuid: String = UUID.randomUUID().toString(),
    val amount: Double = 0.0,
    val note: String? = null,
    val date: Long = System.currentTimeMillis(),
    val type: TransactionType = TransactionType.EXPENSE,
    val accountId: Long,
    val categoryId: Long,
    val personId: Long = 0,
    val transferAccountId: Long? = null,
    val icon: String? = null,
    val color: String? = null,
    val tags: String? = null, // Stored as CSV or JSON string
    val isArchived: Boolean = false,
    val isDeleted: Boolean = false,
    val isTemplate: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class TransactionType {
    INCOME,
    EXPENSE,
    TRANSFER
}

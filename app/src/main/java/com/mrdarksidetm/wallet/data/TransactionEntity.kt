package com.mrdarksidetm.wallet.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

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
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val amount: Double,
    val date: Long,
    val type: String, // Income, Expense, Transfer
    val note: String,
    val category: String, // e.g., "Food", "Salary"
    val accountId: Long
)

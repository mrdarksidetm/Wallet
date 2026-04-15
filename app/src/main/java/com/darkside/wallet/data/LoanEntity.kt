package com.darkside.wallet.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "loans",
    foreignKeys = [
        ForeignKey(
            entity = PersonEntity::class,
            parentColumns = ["id"],
            childColumns = ["personId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [androidx.room.Index(value = ["personId"])]
)
data class LoanEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val personId: String = "",
    val accountId: String? = null,
    val amount: Double = 0.0,
    val remainingAmount: Double = 0.0,
    val type: String = "borrowed", // lent, borrowed
    val note: String = "",
    val date: Long = System.currentTimeMillis(),
    val dueDate: Long? = null,
    val isPaid: Boolean = false,
    val isActive: Boolean = true,
    val isArchived: Boolean = false,
    val isDeleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

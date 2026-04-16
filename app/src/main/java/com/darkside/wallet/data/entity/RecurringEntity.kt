package com.darkside.wallet.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import androidx.room.ForeignKey
import java.util.UUID

@Entity(
    tableName = "recurring",
    indices = [
        Index(value = ["uuid"], unique = true),
        Index(value = ["accountId"]),
        Index(value = ["categoryId"])
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
data class RecurringEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val uuid: String = UUID.randomUUID().toString(),
    val name: String,
    val amount: Double = 0.0,
    val type: TransactionType = TransactionType.EXPENSE,
    val accountId: Long,
    val categoryId: Long,
    val transferAccountId: Long? = null,
    val frequency: RecurrenceFrequency = RecurrenceFrequency.MONTHLY,
    val nextDate: Long = System.currentTimeMillis(),
    val endDate: Long? = null,
    val isActive: Boolean = true,
    val isDeleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class RecurrenceFrequency {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY
}

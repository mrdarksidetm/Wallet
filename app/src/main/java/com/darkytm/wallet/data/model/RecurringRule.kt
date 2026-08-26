package com.darkytm.wallet.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recurring_rules",
    foreignKeys = [
        ForeignKey(
            entity = Account::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["accountId"]), Index(value = ["categoryId"])]
)
data class RecurringRule(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val type: TransactionType = TransactionType.EXPENSE,
    val accountId: Long,
    val categoryId: Long? = null,
    val frequency: RecurringFrequency = RecurringFrequency.MONTHLY,
    val startDateMillis: Long,
    val nextDueDateMillis: Long,
    val lastGeneratedDateMillis: Long? = null,
    val autoCreateTransaction: Boolean = true,
    val isActive: Boolean = true
)

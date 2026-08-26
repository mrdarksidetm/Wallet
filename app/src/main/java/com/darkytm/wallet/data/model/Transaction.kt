package com.darkytm.wallet.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * The Central Source of Truth for the entire app.
 * Every financial calculation (Account balance, Budget spending, Debt status,
 * Goal progress, Recurring execution) is derived directly from this entity.
 */
@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = Account::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Account::class,
            parentColumns = ["id"],
            childColumns = ["toAccountId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Person::class,
            parentColumns = ["id"],
            childColumns = ["personId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Goal::class,
            parentColumns = ["id"],
            childColumns = ["goalId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = RecurringRule::class,
            parentColumns = ["id"],
            childColumns = ["recurringRuleId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("accountId"),
        Index("toAccountId"),
        Index("categoryId"),
        Index("personId"),
        Index("goalId"),
        Index("recurringRuleId"),
        Index("dateMillis")
    ]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val type: TransactionType,
    val title: String = "",
    val note: String = "",
    val dateMillis: Long = System.currentTimeMillis(),

    // Core dimensional linkages:
    val accountId: Long,
    val toAccountId: Long? = null,
    val categoryId: Long? = null,
    val personId: Long? = null,
    val goalId: Long? = null,
    val recurringRuleId: Long? = null
)

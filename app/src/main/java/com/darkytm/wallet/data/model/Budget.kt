package com.darkytm.wallet.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "budgets",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["categoryId"])]
)
data class Budget(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val categoryId: Long? = null, // null means overall budget
    val limitAmount: Double,
    val period: BudgetPeriod = BudgetPeriod.MONTHLY,
    val alertThresholdPercent: Int = 80, // alert when 80% spent
    val isEnabled: Boolean = true
)

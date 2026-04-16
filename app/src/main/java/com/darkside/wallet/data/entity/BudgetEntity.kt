package com.darkside.wallet.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import androidx.room.ForeignKey
import java.util.UUID

@Entity(
    tableName = "budgets",
    indices = [
        Index(value = ["uuid"], unique = true),
        Index(value = ["categoryId"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val uuid: String = UUID.randomUUID().toString(),
    val amount: Double = 0.0,
    val categoryId: Long,
    val period: BudgetPeriod = BudgetPeriod.MONTHLY,
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long = System.currentTimeMillis(),
    val isActive: Boolean = true,
    val isDeleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class BudgetPeriod {
    WEEKLY,
    MONTHLY,
    YEARLY,
    ONE_TIME
}

package com.darkside.wallet.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import java.util.UUID

@Entity(
    tableName = "categories",
    indices = [
        Index(value = ["uuid"], unique = true),
        Index(value = ["name"]),
        Index(value = ["type"])
    ]
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val uuid: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val icon: String = "category",
    val color: String = "0xFF2196F3",
    val budgetLimit: Double? = null,
    val isBudget: Boolean = false,
    val isPredefined: Boolean = false,
    val isDeleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val type: CategoryType = CategoryType.EXPENSE
)

enum class CategoryType {
    INCOME,
    EXPENSE,
    TRANSFER
}

package com.darkside.wallet.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Why UUIDs?
 * Using Strings (UUIDs) ensures that default categories and user-generated categories
 * can safely merge from local offline databases without ID overlap during remote sync.
 */
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val icon: String = "category",
    val color: Int = 0xFF2196F3.toInt(),
    val isArchived: Boolean = false,
    val isDeleted: Boolean = false,
    val order: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

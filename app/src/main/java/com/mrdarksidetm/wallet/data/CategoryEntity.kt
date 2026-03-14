package com.mrdarksidetm.wallet.data

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
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val icon: String // Identifier for Material icon or drawable
)

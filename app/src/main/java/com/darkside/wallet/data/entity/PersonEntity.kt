package com.darkside.wallet.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import java.util.UUID

@Entity(
    tableName = "people",
    indices = [
        Index(value = ["uuid"], unique = true),
        Index(value = ["name"])
    ]
)
data class PersonEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val uuid: String = UUID.randomUUID().toString(),
    val name: String,
    val contact: String? = null,
    val avatar: String? = null,
    val color: String = "0xFF2196F3",
    val isDeleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

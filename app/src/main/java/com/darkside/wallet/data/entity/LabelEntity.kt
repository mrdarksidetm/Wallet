package com.darkside.wallet.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import java.util.UUID

@Entity(
    tableName = "labels",
    indices = [Index(value = ["uuid"], unique = true)]
)
data class LabelEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val uuid: String = UUID.randomUUID().toString(),
    val name: String,
    val color: String = "0xFF2196F3",
    val createdAt: Long = System.currentTimeMillis()
)

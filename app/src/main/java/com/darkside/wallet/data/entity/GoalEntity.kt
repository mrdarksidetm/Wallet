package com.darkside.wallet.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import androidx.room.ForeignKey
import java.util.UUID

@Entity(
    tableName = "goals",
    indices = [
        Index(value = ["uuid"], unique = true),
        Index(value = ["accountId"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val uuid: String = UUID.randomUUID().toString(),
    val name: String,
    val targetAmount: Double = 0.0,
    val currentAmount: Double = 0.0,
    val deadline: Long = System.currentTimeMillis(),
    val color: String = "0xFF2196F3",
    val icon: String? = null,
    val accountId: Long? = null,
    val isCompleted: Boolean = false,
    val isDeleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

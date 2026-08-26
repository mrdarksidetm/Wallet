package com.darkytm.wallet.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val targetAmount: Double,
    val targetDateMillis: Long? = null,
    val iconEmoji: String = "🎯",
    val colorHex: Long = 0xFF8D4F00L,
    val isCompleted: Boolean = false,
    val createdAtMillis: Long = System.currentTimeMillis()
)

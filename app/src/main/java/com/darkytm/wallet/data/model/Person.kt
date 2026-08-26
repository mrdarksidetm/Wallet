package com.darkytm.wallet.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "people")
data class Person(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String = "",
    val avatarEmoji: String = "👤",
    val note: String = "",
    val createdAtMillis: Long = System.currentTimeMillis()
)

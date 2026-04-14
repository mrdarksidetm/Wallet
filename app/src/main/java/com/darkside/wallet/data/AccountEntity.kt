package com.darkside.wallet.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Why UUIDs?
 * Auto-incrementing integers (Long) cause severe ID collisions during offline-first syncing.
 * If two devices create an account offline, they might both get ID '1'. When syncing to a central server,
 * one will overwrite the other. UUIDs guarantee global uniqueness across all distributed nodes.
 */
@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: String,
    val initialBalance: Double
)

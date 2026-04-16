package com.darkside.wallet.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import androidx.room.ForeignKey
import java.util.UUID

@Entity(
    tableName = "loans",
    indices = [
        Index(value = ["uuid"], unique = true),
        Index(value = ["personId"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = PersonEntity::class,
            parentColumns = ["id"],
            childColumns = ["personId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class LoanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val uuid: String = UUID.randomUUID().toString(),
    val personId: Long,
    val amount: Double = 0.0,
    val type: LoanType = LoanType.BORROWED,
    val dueDate: Long? = null,
    val isPaid: Boolean = false,
    val isActive: Boolean = true,
    val note: String? = null,
    val isDeleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class LoanType {
    BORROWED,
    LENT
}

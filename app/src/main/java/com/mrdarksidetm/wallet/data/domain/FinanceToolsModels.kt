package com.mrdarksidetm.wallet.data.domain

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.util.UUID

enum class LoanType {
    GIVEN, TAKEN
}

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val targetAmount: Double,
    val savedAmount: Double,
    val deadline: Long
)

@Entity(tableName = "loans")
data class Loan(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val totalAmount: Double,
    val paidAmount: Double,
    val type: LoanType
)

@Dao
interface FinanceToolsDao {
    @Query("SELECT * FROM goals ORDER BY deadline ASC")
    fun getAllGoals(): Flow<List<Goal>>

    @Query("SELECT * FROM loans")
    fun getAllLoans(): Flow<List<Loan>>
    
    // CRITICAL: Progress math is calculated in the UI using (savedAmount / targetAmount).toFloat()
    // By keeping the DAO simple, we avoid blocking the main thread during composition.
}

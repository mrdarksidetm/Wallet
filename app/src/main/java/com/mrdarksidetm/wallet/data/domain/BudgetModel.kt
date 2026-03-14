package com.mrdarksidetm.wallet.data.domain

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Entity(tableName = "budgets")
data class Budget(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val categoryId: String,
    val monthlyLimit: Double,
    val month: Int,
    val year: Int
)

@Dao
interface BudgetDao {
    /**
     * CRITICAL: Efficient Spend Calculation
     * This query dynamically calculates the sum of transactions for a given category 
     * within the specified month/year. By offloading this aggregation to the SQLite 
     * engine (using SUM), we avoid loading thousands of transaction objects into 
     * memory. This is vital for 4GB RAM hardware constraints.
     */
    @Query("""
        SELECT SUM(amount) FROM domain_transactions 
        WHERE categoryId = :categoryId 
        AND type = 'EXPENSE'
        AND date >= :startOfMonth AND date <= :endOfMonth
    """)
    fun getSpentAmountForCategory(categoryId: String, startOfMonth: Long, endOfMonth: Long): Flow<Double?>
}

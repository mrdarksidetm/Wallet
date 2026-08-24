package com.darkside.wallet.data.domain

import com.darkside.wallet.data.*
import com.darkside.wallet.data.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.system.measureTimeMillis

/**
 * Performance Audit Service (Ported from Flutter v2.1.5).
 * 
 * Measures database performance for high-load scenarios (10k transactions).
 */
class PerformanceAuditService(
    private val database: AppDatabase,
    private val transactionDao: TransactionDao,
    private val accountDao: AccountDao,
    private val categoryDao: CategoryDao
) {

    suspend fun runAudit(): Map<String, Any> = withContext(Dispatchers.IO) {
        val results = mutableMapOf<String, Any>()

        // 1. Fetch first account and category
        val accounts = accountDao.getAllAccountsOnce()
        val categories = categoryDao.getAllCategoriesOnce()

        if (accounts.isEmpty() || categories.isEmpty()) {
            return@withContext mapOf("error" to "Need at least one account and one category")
        }

        val accountId = accounts.first().id
        val categoryId = categories.first().id

        // 2. Prepare 10,000 transactions
        val transactions = List(10000) { i ->
            TransactionEntity(
                amount = (i + 1) * 1.5,
                date = System.currentTimeMillis() - (i * 60 * 1000),
                type = if (i % 2 == 0) TransactionType.EXPENSE else TransactionType.INCOME,
                accountId = accountId,
                categoryId = categoryId,
                note = "Audit Tx #$i"
            )
        }

        // 3. Measure Insertion Time
        val insertionTime = measureTimeMillis {
            database.runInTransaction {
                transactions.forEach { transactionDao.insertTransactionSync(it) }
            }
        }
        results["insertion_time_ms"] = insertionTime

        // 4. Measure Query Time (Recent 50)
        val queryRecentTime = measureTimeMillis {
            val recent = transactionDao.getRecentTransactionsSync(50)
            results["count_retrieved"] = recent.size
        }
        results["query_recent_50_ms"] = queryRecentTime

        // 5. Measure Query Time (All Expenses)
        val queryExpensesTime = measureTimeMillis {
            val expenses = transactionDao.getTransactionsByTypeSync(TransactionType.EXPENSE)
            results["expense_count"] = expenses.size
        }
        results["query_all_expenses_ms"] = queryExpensesTime

        // 6. Measure Aggregation Time (Sum)
        val aggregationTime = measureTimeMillis {
            val total = transactionDao.getTotalAmountSync() ?: 0.0
            results["total_sum"] = total
        }
        results["aggregation_sum_ms"] = aggregationTime

        results
    }
}

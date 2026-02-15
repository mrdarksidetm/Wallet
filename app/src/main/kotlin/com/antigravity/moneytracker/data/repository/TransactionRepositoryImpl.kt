package com.antigravity.moneytracker.data.repository

import com.antigravity.moneytracker.data.local.dao.TransactionDao
import com.antigravity.moneytracker.data.local.entity.TransactionEntity
import com.antigravity.moneytracker.domain.entity.Transaction
import com.antigravity.moneytracker.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date

class TransactionRepositoryImpl(
    private val dao: TransactionDao
) : TransactionRepository {

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return dao.getAllTransactions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTransactionsInRange(startDate: Long, endDate: Long): Flow<List<Transaction>> {
        return dao.getTransactionsInRange(startDate, endDate).map { entities ->
            entities.map { entities -> entities.toDomain() }
        }
    }

    override fun getTotalIncome(): Flow<Double> {
        return dao.getTotalIncome().map { it ?: 0.0 }
    }

    override fun getTotalExpense(): Flow<Double> {
        return dao.getTotalExpense().map { it ?: 0.0 }
    }

    override suspend fun addTransaction(transaction: Transaction) {
        dao.insertTransaction(transaction.toData())
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        dao.deleteTransaction(transaction.toData())
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        dao.updateTransaction(transaction.toData())
    }

    // Mappers
    private fun TransactionEntity.toDomain(): Transaction {
        return Transaction(
            id = id,
            amount = amount,
            description = description,
            date = Date(date),
            type = type,
            categoryId = categoryId,
            accountId = accountId
        )
    }

    private fun Transaction.toData(): TransactionEntity {
        return TransactionEntity(
            id = id,
            amount = amount,
            description = description,
            date = date.time,
            type = type,
            categoryId = categoryId,
            accountId = accountId
        )
    }
}

package com.darkside.wallet.data.domain

import com.darkside.wallet.data.BudgetDao
import com.darkside.wallet.data.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

class BudgetRepository(private val dao: BudgetDao) {
    fun getAllBudgets(): Flow<List<BudgetEntity>> = dao.getAllBudgets()
    fun getBudgetForCategory(categoryId: Long): Flow<BudgetEntity?> = dao.getBudgetForCategory(categoryId)
    suspend fun insertBudget(budget: BudgetEntity): Long = dao.insertBudget(budget)
    suspend fun updateBudget(budget: BudgetEntity) = dao.updateBudget(budget)
    suspend fun deleteBudget(budget: BudgetEntity) = dao.deleteBudget(budget)
}

package com.darkside.wallet.data.domain

import com.darkside.wallet.data.GoalDao
import com.darkside.wallet.data.GoalEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository for Goal operations.
 */
class GoalRepository(private val dao: GoalDao) {

    fun getAllGoals(): Flow<List<GoalEntity>> {
        return dao.getAllGoals()
    }

    suspend fun getGoalById(id: String): GoalEntity? {
        return dao.getGoalById(id)
    }

    suspend fun insertGoal(goal: GoalEntity) {
        dao.insertGoal(goal)
    }

    suspend fun updateGoal(goal: GoalEntity) {
        dao.updateGoal(goal)
    }

    suspend fun deleteGoal(goal: GoalEntity) {
        dao.deleteGoal(goal)
    }
}

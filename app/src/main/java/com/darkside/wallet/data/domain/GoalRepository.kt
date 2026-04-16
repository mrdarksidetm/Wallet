package com.darkside.wallet.data.domain

import com.darkside.wallet.data.GoalDao
import com.darkside.wallet.data.entity.GoalEntity
import kotlinx.coroutines.flow.Flow

class GoalRepository(private val dao: GoalDao) {
    fun getAllGoals(): Flow<List<GoalEntity>> = dao.getAllGoals()
    suspend fun insertGoal(goal: GoalEntity): Long = dao.insertGoal(goal)
    suspend fun updateGoal(goal: GoalEntity) = dao.updateGoal(goal)
    suspend fun deleteGoal(goal: GoalEntity) = dao.deleteGoal(goal)
}

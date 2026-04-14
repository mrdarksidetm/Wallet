package com.darkside.wallet.data.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

/**
 * Phase 19: Gamification & Financial Streaks
 * 
 * CRITICAL: Efficient Streak Calculation
 * Instead of querying thousands of transactions every app launch, 
 * this engine evaluates the last X dates from a simplified DAO projection.
 */
object StreakEngine {
    
    /**
     * Takes a list of raw transaction timestamps (ordered descending)
     * and calculates consecutive active days.
     */
    suspend fun calculateActiveStreak(sortedTimestamps: List<Long>): Int {
        return withContext(Dispatchers.Default) {
            if (sortedTimestamps.isEmpty()) return@withContext 0

            var currentStreak = 0
            val calendar = Calendar.getInstance()
            
            // Normalize current day to midnight
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            var targetDay = calendar.timeInMillis
            
            val oneDayMillis = 24 * 60 * 60 * 1000L

            for (timestamp in sortedTimestamps) {
                // Normalize transaction day to midnight
                calendar.timeInMillis = timestamp
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val txDay = calendar.timeInMillis

                if (txDay == targetDay) {
                    // Match found for this target day
                    currentStreak++
                    targetDay -= oneDayMillis // Move target back 1 day
                } else if (txDay < targetDay) {
                    // Missed a day
                    break
                }
            }
            currentStreak
        }
    }
}

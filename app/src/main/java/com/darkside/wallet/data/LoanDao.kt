package com.darkside.wallet.data

import androidx.room.*
import com.darkside.wallet.data.entity.LoanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LoanDao {
    @Query("SELECT * FROM loans WHERE isDeleted = 0 ORDER BY createdAt DESC")
    fun getAllLoans(): Flow<List<LoanEntity>>

    @Query("SELECT * FROM loans WHERE personId = :personId AND isDeleted = 0")
    fun getLoansByPerson(personId: Long): Flow<List<LoanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoan(loan: LoanEntity): Long

    @Update
    suspend fun updateLoan(loan: LoanEntity)

    @Delete
    suspend fun deleteLoan(loan: LoanEntity)
}

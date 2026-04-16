package com.darkside.wallet.data.domain

import com.darkside.wallet.data.LoanDao
import com.darkside.wallet.data.entity.LoanEntity
import kotlinx.coroutines.flow.Flow

class LoanRepository(private val dao: LoanDao) {
    fun getAllLoans(): Flow<List<LoanEntity>> = dao.getAllLoans()
    fun getLoansByPerson(personId: Long): Flow<List<LoanEntity>> = dao.getLoansByPerson(personId)
    suspend fun insertLoan(loan: LoanEntity): Long = dao.insertLoan(loan)
    suspend fun updateLoan(loan: LoanEntity) = dao.updateLoan(loan)
    suspend fun deleteLoan(loan: LoanEntity) = dao.deleteLoan(loan)
}

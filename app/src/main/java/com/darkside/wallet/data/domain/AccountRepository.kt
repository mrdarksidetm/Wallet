package com.darkside.wallet.data.domain

import com.darkside.wallet.data.AccountDao
import com.darkside.wallet.data.AccountEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository for Account operations.
 */
class AccountRepository(private val dao: AccountDao) {

    fun getAllAccounts(): Flow<List<AccountEntity>> {
        return dao.getAllAccounts()
    }

    suspend fun getAccountById(id: String): AccountEntity? {
        return dao.getAccountById(id)
    }

    suspend fun insertAccount(account: AccountEntity) {
        dao.insertAccount(account)
    }

    suspend fun updateAccount(account: AccountEntity) {
        dao.updateAccount(account)
    }

    suspend fun deleteAccount(account: AccountEntity) {
        dao.deleteAccount(account)
    }
}

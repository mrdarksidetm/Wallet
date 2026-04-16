package com.darkside.wallet.data.domain

import com.darkside.wallet.data.PersonDao
import com.darkside.wallet.data.entity.PersonEntity
import kotlinx.coroutines.flow.Flow

class PersonRepository(private val dao: PersonDao) {
    fun getAllPeople(): Flow<List<PersonEntity>> = dao.getAllPeople()
    suspend fun getPersonById(id: Long): PersonEntity? = dao.getPersonById(id)
    suspend fun insertPerson(person: PersonEntity): Long = dao.insertPerson(person)
    suspend fun updatePerson(person: PersonEntity) = dao.updatePerson(person)
    suspend fun deletePerson(person: PersonEntity) = dao.deletePerson(person)
}

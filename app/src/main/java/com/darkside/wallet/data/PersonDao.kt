package com.darkside.wallet.data

import androidx.room.*
import com.darkside.wallet.data.entity.PersonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {
    @Query("SELECT * FROM people WHERE isDeleted = 0 ORDER BY name ASC")
    fun getAllPeople(): Flow<List<PersonEntity>>

    @Query("SELECT * FROM people WHERE id = :id")
    suspend fun getPersonById(id: Long): PersonEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerson(person: PersonEntity): Long

    @Update
    suspend fun updatePerson(person: PersonEntity)

    @Delete
    suspend fun deletePerson(person: PersonEntity)
}

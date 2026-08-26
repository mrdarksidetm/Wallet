package com.darkytm.wallet.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.darkytm.wallet.data.model.Person
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {

    @Query("SELECT * FROM people ORDER BY name ASC")
    fun getAllPeople(): Flow<List<Person>>

    @Query("SELECT * FROM people WHERE id = :id")
    suspend fun getPersonById(id: Long): Person?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(person: Person): Long

    @Update
    suspend fun update(person: Person)

    @Delete
    suspend fun delete(person: Person)
}

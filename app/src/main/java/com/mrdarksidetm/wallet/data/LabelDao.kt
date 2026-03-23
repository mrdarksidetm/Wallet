package com.mrdarksidetm.wallet.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LabelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLabel(label: LabelEntity)

    @Update
    suspend fun updateLabel(label: LabelEntity)

    @Delete
    suspend fun deleteLabel(label: LabelEntity)

    @Query("SELECT * FROM labels ORDER BY name ASC")
    fun getAllLabels(): Flow<List<LabelEntity>>
}

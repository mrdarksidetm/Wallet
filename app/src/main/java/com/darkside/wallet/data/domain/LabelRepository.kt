package com.darkside.wallet.data.domain

import com.darkside.wallet.data.LabelDao
import com.darkside.wallet.data.entity.LabelEntity
import kotlinx.coroutines.flow.Flow

class LabelRepository(private val dao: LabelDao) {
    fun getAllLabels(): Flow<List<LabelEntity>> = dao.getAllLabels()
    suspend fun insertLabel(label: LabelEntity) = dao.insertLabel(label)
    suspend fun updateLabel(label: LabelEntity) = dao.updateLabel(label)
    suspend fun deleteLabel(label: LabelEntity) = dao.deleteLabel(label)
}

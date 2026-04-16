package com.darkside.wallet.data.domain

import com.darkside.wallet.data.CategoryDao
import com.darkside.wallet.data.entity.CategoryEntity
import com.darkside.wallet.data.entity.CategoryType
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val dao: CategoryDao) {
    fun getAllCategories(): Flow<List<CategoryEntity>> = dao.getAllCategories()
    fun getCategoriesByType(type: CategoryType): Flow<List<CategoryEntity>> = dao.getCategoriesByType(type)
    suspend fun getCategoryById(id: Long): CategoryEntity? = dao.getCategoryById(id)
    suspend fun insertCategory(category: CategoryEntity): Long = dao.insertCategory(category)
    suspend fun updateCategory(category: CategoryEntity) = dao.updateCategory(category)
    suspend fun deleteCategory(category: CategoryEntity) = dao.deleteCategory(category)
}

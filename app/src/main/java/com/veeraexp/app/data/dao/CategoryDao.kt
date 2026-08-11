package com.veeraexp.app.data.dao

import androidx.room.*
import com.veeraexp.app.data.entity.Category
import com.veeraexp.app.data.entity.CategoryType
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert
    suspend fun insert(category: Category): Long

    @Insert
    suspend fun insertAll(categories: List<Category>)

    @Update
    suspend fun update(category: Category)

    // Soft delete: never hard-delete a category that historical transactions reference.
    @Query("UPDATE categories SET isDeleted = 1 WHERE id = :id")
    suspend fun softDelete(id: Long)

    @Query("SELECT * FROM categories WHERE isDeleted = 0 AND type = :type ORDER BY name ASC")
    fun getByType(type: CategoryType): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE isDeleted = 0 ORDER BY type, name ASC")
    fun getAll(): Flow<List<Category>>

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun count(): Int
}

package com.approagency.drug.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.approagency.drug.data.local.entities.TestItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TestItemDao {
    @Query("SELECT * FROM testItem ORDER BY Id")
    fun getAllItems(): Flow<List<TestItemEntity>>

    @Query("SELECT * FROM testItem WHERE Group_Id = :groupId ORDER BY Id")
    fun getItemsByGroupId(groupId: Int): Flow<List<TestItemEntity>>

    @Query("SELECT * FROM testItem WHERE Id = :itemId")
    suspend fun getItemById(itemId: Int): TestItemEntity?

    @Query("""
        SELECT ti.* FROM testItem ti
        LEFT JOIN testGroup tg ON ti.Group_Id = tg.Id
        WHERE ti.Title LIKE '%' || :searchQuery || '%' 
           OR ti.Normal_Value LIKE '%' || :searchQuery || '%'
           OR ti.Detail LIKE '%' || :searchQuery || '%'
           OR tg.Fname LIKE '%' || :searchQuery || '%'
           OR tg.Ename LIKE '%' || :searchQuery || '%'
        ORDER BY 
            CASE 
                WHEN ti.Title LIKE '%' || :searchQuery || '%' THEN 1
                WHEN tg.Fname LIKE '%' || :searchQuery || '%' THEN 2
                ELSE 3
            END
    """)
    fun searchTestItems(searchQuery: String): Flow<List<TestItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: TestItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllItems(items: List<TestItemEntity>)

    @Query("DELETE FROM testItem")
    suspend fun deleteAllItems()
}
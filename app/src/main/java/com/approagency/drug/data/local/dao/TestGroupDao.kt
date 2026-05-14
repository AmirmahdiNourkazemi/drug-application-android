package com.approagency.drug.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.approagency.drug.data.local.entities.TestGroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TestGroupDao {
    @Query("SELECT * FROM testGroup ORDER BY Id")
    fun getAllGroups(): Flow<List<TestGroupEntity>>

    @Query("SELECT * FROM testGroup WHERE Id = :groupId")
    suspend fun getGroupById(groupId: Int): TestGroupEntity?

    @Query("SELECT * FROM testGroup WHERE Isparent = '1'")
    fun getParentGroups(): Flow<List<TestGroupEntity>>

    @Query("SELECT * FROM testGroup WHERE Isparent = '0'")
    fun getChildGroups(): Flow<List<TestGroupEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: TestGroupEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllGroups(groups: List<TestGroupEntity>)

    @Query("DELETE FROM testGroup")
    suspend fun deleteAllGroups()

    @Query("""
        SELECT * FROM testGroup 
        WHERE Fname LIKE '%' || :query || '%' 
           OR Ename LIKE '%' || :query || '%' 
           OR Detail LIKE '%' || :query || '%'
        ORDER BY 
            CASE 
                WHEN Fname LIKE '%' || :query || '%' THEN 1
                WHEN Ename LIKE '%' || :query || '%' THEN 2
                ELSE 3
            END
    """)
    fun searchGroups(query: String): Flow<List<TestGroupEntity>>
}
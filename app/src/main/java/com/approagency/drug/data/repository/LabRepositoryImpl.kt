package com.approagency.drug.data.repository

import com.approagency.drug.data.local.dao.TestGroupDao
import com.approagency.drug.data.local.dao.TestItemDao
import com.approagency.drug.data.local.entities.TestGroupEntity
import com.approagency.drug.data.local.entities.TestItemEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class LabRepositoryImpl (
    private val testGroupDao: TestGroupDao,
    private val testItemDao: TestItemDao
) {
    // Test Group Operations
    fun getAllGroups(): Flow<List<TestGroupEntity>> = testGroupDao.getAllGroups()
    fun getParentGroups(): Flow<List<TestGroupEntity>> = testGroupDao.getParentGroups()
    fun getChildGroups(): Flow<List<TestGroupEntity>> = testGroupDao.getChildGroups()
    suspend fun getGroupById(groupId: Int): TestGroupEntity? = testGroupDao.getGroupById(groupId)

    // Test Item Operations
    fun getAllItems(): Flow<List<TestItemEntity>> = testItemDao.getAllItems()
    fun getItemsByGroupId(groupId: Int): Flow<List<TestItemEntity>> = testItemDao.getItemsByGroupId(groupId)
    suspend fun getItemById(itemId: Int): TestItemEntity? = testItemDao.getItemById(itemId)
    fun searchTestItems(query: String): Flow<List<TestItemEntity>> = testItemDao.searchTestItems(query)


    fun searchGroupsAndItems(searchQuery: String): Flow<Pair<List<TestGroupEntity>, List<TestItemEntity>>> {
        return combine(
            testGroupDao.searchGroups(searchQuery),
            testItemDao.searchTestItems(searchQuery)
        ) { groups, items ->
            Pair(groups, items)
        }
    }
}
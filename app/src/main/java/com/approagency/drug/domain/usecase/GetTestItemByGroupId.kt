package com.approagency.drug.domain.usecase

import com.approagency.drug.data.local.entities.TestItemEntity
import com.approagency.drug.data.repository.LabRepositoryImpl
import com.approagency.drug.domain.model.TestItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetTestItemByGroupId (
    private val repository: LabRepositoryImpl
) {
    operator fun invoke(id:Int): Flow<List<TestItem>> {
       return repository.getItemsByGroupId(groupId = id).map {
           entities -> entities.map {
               entity ->
           TestItem(
               id = entity.id,
               groupId = entity.groupId,
               title = entity.title,
               normalValue = entity.normalValue,
               detail = entity.detail,
           )
       }
       }
    }
}
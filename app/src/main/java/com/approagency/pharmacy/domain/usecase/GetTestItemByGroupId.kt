package com.approagency.pharmacy.domain.usecase

import com.approagency.pharmacy.data.repository.LabRepositoryImpl
import com.approagency.pharmacy.domain.model.TestItem
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
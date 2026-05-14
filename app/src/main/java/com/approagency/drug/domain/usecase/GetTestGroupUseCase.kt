package com.approagency.drug.domain.usecase

import com.approagency.drug.data.repository.LabRepositoryImpl
import com.approagency.drug.domain.model.TestGroup
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetTestGroupUseCase (
    private val repository: LabRepositoryImpl
) {
    operator fun invoke(): Flow<List<TestGroup>> {
        return repository.getAllGroups().map { entities ->
            entities.map { entity ->
                TestGroup(
                    id = entity.id,
                    ename = entity.ename,
                    fname = entity.fname,
                    detail = entity.detail,
                    isParent = entity.isParent
                )
            }
        }
    }
}
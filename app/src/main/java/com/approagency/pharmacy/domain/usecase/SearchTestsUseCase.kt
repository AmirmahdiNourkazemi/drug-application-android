package com.approagency.pharmacy.domain.usecase


import com.approagency.pharmacy.data.repository.LabRepositoryImpl
import com.approagency.pharmacy.domain.model.TestGroup
import com.approagency.pharmacy.domain.model.TestItem
import com.approagency.pharmacy.domain.model.toTestGroup
import com.approagency.pharmacy.domain.model.toTestItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchTestsUseCase(
    private val repository: LabRepositoryImpl
) {
    operator fun invoke(query: String): Flow<Pair<List<TestGroup>, List<TestItem>>> {
        return repository.searchGroupsAndItems(query).map { (groups, items) ->
            Pair(
                groups.map { it.toTestGroup() },
                items.map { it.toTestItem() }
            )
        }
    }
}
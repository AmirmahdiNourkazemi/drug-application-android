package com.approagency.pharmacy.presentation.viewModel

import android.net.http.HttpException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.approagency.pharmacy.domain.model.TestGroup
import com.approagency.pharmacy.domain.model.TestItem
import com.approagency.pharmacy.domain.usecase.GetTestGroupUseCase
import com.approagency.pharmacy.domain.usecase.GetTestItemByGroupId
import com.approagency.pharmacy.domain.usecase.SearchTestsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LabViewModel (
    private val getTestGroupUseCase: GetTestGroupUseCase,
    private val getTestItemByGroupId: GetTestItemByGroupId,
    private val searchTestsUseCase: SearchTestsUseCase
) : ViewModel(){
    private val _testGroups = MutableStateFlow(TestGroupItem())
    val testGroups: StateFlow<TestGroupItem> = _testGroups.asStateFlow()

    private val _testItems = MutableStateFlow(TestItemState())
    val testItems: StateFlow<TestItemState> = _testItems.asStateFlow()


    private val _searchResults = MutableStateFlow(SearchResultState())
    val searchResults: StateFlow<SearchResultState> = _searchResults.asStateFlow()

    private val searchQuery = MutableStateFlow("")

    init {
        loadTestGroups()
        setupSearch()
    }
    private fun setupSearch() {
        viewModelScope.launch {
            searchQuery
                .debounce(1000) // Wait 500ms after user stops typing
                .distinctUntilChanged()
                .collect { query ->
                    if (query.isNotBlank()) {
                        performSearch(query)
                    } else {
                        _searchResults.update {
                            SearchResultState(
                                isLoading = false,
                                searchResults = null,
                                query = ""
                            )
                        }
                    }
                }
        }
    }

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
        _searchResults.update {
            it.copy(
                isLoading = query.isNotBlank(),
                query = query
            )
        }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            try {
                val result = searchTestsUseCase.invoke(query)
                result.collect { (groups, items) ->
                    _searchResults.update {
                        it.copy(
                            isLoading = false,
                            searchResults = SearchResult(groups = groups, items = items),
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _searchResults.update {
                    it.copy(
                        isLoading = false,
                        searchResults = null,
                        error = e.message ?: "خطا در جستجو"
                    )
                }
            }
        }
    }
    fun loadTestGroups(){
        _testGroups.update {
            it.copy(
                isLoading = true
            )
        }
        viewModelScope.launch {
            try {
                val result = getTestGroupUseCase.invoke();
                _testGroups.update {
                    it.copy(
                        isLoading = false,
                        testGroup = result,
                    )
                }
            } catch (e:HttpException){
                handleError(e.message)
            }

        }
    }
    fun clearSearch() {
        searchQuery.value = ""
        _searchResults.update {
            SearchResultState()
        }
    }
    fun getItemByGroupId(id:Int){
    _testItems.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val result = getTestItemByGroupId.invoke(id);
                _testItems.update {
                    it.copy(
                        isLoading = false,
                        testItem = result
                    )
                }
            }  catch (e:HttpException){
                handleError(e.message)
            }
        }
    }
    private suspend fun handleError(message: String?) {
        _testGroups.update {
            it.copy(
                isLoading = false,
                testGroup = null,
                error = message
            )
        }
    }

}

data class TestGroupItem(
    val isLoading: Boolean = false,
    val testGroup: Flow<List<TestGroup>>? = null,
    val error:String? = null
)

data class TestItemState(
    val isLoading: Boolean = false,
    val testItem: Flow<List<TestItem>>? = null,
    val error:String? = null
)
data class SearchResultState(
    val isLoading: Boolean = false,
    val searchResults: SearchResult? = null,
    val error: String? = null,
    val query: String = ""
)

data class SearchResult(
    val groups: List<TestGroup>,
    val items: List<TestItem>
)

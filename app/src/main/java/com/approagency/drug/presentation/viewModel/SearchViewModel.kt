package com.approgency.drug.presentation.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.approagency.drug.domain.model.DrugSearchResult
import com.approagency.drug.domain.usecase.SearchDrugsYabUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchDrugsUseCase: SearchDrugsYabUseCase
) : ViewModel() {

    private val _searchState = MutableStateFlow<SearchState>(SearchState.Idle)
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()
    var searchText by mutableStateOf("")
        private set

    fun updateSearchText(value: String) {
        searchText = value
    }
    private var currentQuery = ""
    private var currentPage = 1
    private var totalPages = 1
    private val allDrugs = mutableListOf<DrugSearchResult>()
    private var isLoadingMore = false
    private var hasMorePages = true

    fun searchDrugs(query: String, isNewSearch: Boolean = true) {
        if (query.length < 3) {
            _searchState.value = SearchState.Error("لطفاً حداقل ۳ حرف وارد کنید")
            return
        }

        if (isNewSearch) {
            // Reset everything for new search
            currentQuery = query
            currentPage = 1
            allDrugs.clear()
            isLoadingMore = false
            hasMorePages = true
            _searchState.value = SearchState.Loading(isNew = true)
        } else {
            // Don't load if already loading or no more pages
            if (isLoadingMore || !hasMorePages) return
            isLoadingMore = true
            _searchState.value = SearchState.LoadingMore(
                currentItems = allDrugs.toList(),
                currentPage = currentPage
            )
        }

        viewModelScope.launch {
            val result = searchDrugsUseCase(currentQuery, currentPage)

            _searchState.value = when {
                result.isSuccess -> {
                    val searchResult = result.getOrNull()!!
                    totalPages = searchResult.totalPages
                    hasMorePages = currentPage < totalPages

                    allDrugs.addAll(searchResult.drugs)
                    isLoadingMore = false

                    SearchState.Success(
                        drugs = allDrugs.toList(),
                        currentPage = searchResult.currentPage,
                        totalPages = searchResult.totalPages,
                        isLoadingMore = false,
                        hasMorePages = hasMorePages
                    )
                }
                else -> {
                    isLoadingMore = false
                    SearchState.Error(result.exceptionOrNull()?.message ?: "خطا در جستجو")
                }
            }
        }
    }

    fun loadNextPage() {
        if (hasMorePages && !isLoadingMore && currentPage < totalPages) {
            currentPage++
            searchDrugs(currentQuery, isNewSearch = false)
        }
    }

    fun resetSearch() {
        currentQuery = ""
        currentPage = 1
        totalPages = 1
        allDrugs.clear()
        isLoadingMore = false
        hasMorePages = true
        _searchState.value = SearchState.Idle
    }

    fun retryLastSearch() {
        if (currentQuery.isNotEmpty()) {
            searchDrugs(currentQuery, isNewSearch = true)
        }
    }
}

sealed class SearchState {
    object Idle : SearchState()
    data class Loading(val isNew: Boolean) : SearchState()
    data class LoadingMore(
        val currentItems: List<DrugSearchResult>,
        val currentPage: Int
    ) : SearchState()
    data class Success(
        val drugs: List<DrugSearchResult>,
        val currentPage: Int,
        val totalPages: Int,
        val isLoadingMore: Boolean = false,
        val hasMorePages: Boolean = true
    ) : SearchState()
    data class Error(val message: String) : SearchState()
}
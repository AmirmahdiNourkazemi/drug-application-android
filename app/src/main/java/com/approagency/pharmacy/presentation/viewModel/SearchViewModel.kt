package com.approagency.pharmacy.presentation.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.approagency.pharmacy.data.local.SessionManager
import com.approagency.pharmacy.domain.model.DrugSearchResult
import com.approagency.pharmacy.domain.repository.AuthRepository
import com.approagency.pharmacy.domain.usecase.SearchDrugsYabUseCase
import com.approagency.pharmacy.utils.Config
import com.approagency.pharmacy.utils.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchDrugsUseCase: SearchDrugsYabUseCase,
    private val session: SessionManager,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _searchState = MutableStateFlow<SearchState>(SearchState.Idle)
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

    /** تعداد جستجوی رایگانِ باقی‌مانده برای نمایش به کاربر. */
    val remainingFreeSearches: StateFlow<Int> = session.freeSearchCount
        .map { remaining(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = remaining(session.freeSearchCount.value)
        )

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

        if (!isNewSearch) {
            // صفحه‌بندیِ همان جستجو نیازی به بررسی مجدد سهمیه ندارد.
            if (isLoadingMore || !hasMorePages) return
            isLoadingMore = true
            _searchState.value = SearchState.LoadingMore(
                currentItems = allDrugs.toList(),
                currentPage = currentPage
            )
            viewModelScope.launch { fetchPage(isNewSearch = false) }
            return
        }

        viewModelScope.launch {
            if (!ensureCanSearch()) return@launch

            currentQuery = query
            currentPage = 1
            allDrugs.clear()
            isLoadingMore = false
            hasMorePages = true
            _searchState.value = SearchState.Loading(isNew = true)
            fetchPage(isNewSearch = true)
        }
    }

    private suspend fun fetchPage(isNewSearch: Boolean) {
        val result = searchDrugsUseCase(currentQuery, currentPage)

        _searchState.value = when {
            result.isSuccess -> {
                val searchResult = result.getOrNull()!!
                totalPages = searchResult.totalPages
                hasMorePages = currentPage < totalPages

                allDrugs.addAll(searchResult.drugs)
                isLoadingMore = false

                // فقط یک «جستجوی جدیدِ» موفق از سهمیه‌ی رایگان کم می‌کند.
                if (isNewSearch && !session.isSubscribed) {
                    session.incrementFreeSearchCount()
                }

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
                SearchState.Error(result.exceptionOrNull().toUserMessage())
            }
        }
    }

    /**
     * سهمیه‌ی جستجو را بررسی می‌کند. در صورت اتمام سهمیه‌ی رایگان، وضعیت مناسب
     * (نیاز به ورود / نیاز به اشتراک) منتشر شده و false برمی‌گردد.
     */
    private suspend fun ensureCanSearch(): Boolean {
        if (session.isSubscribed) return true
        if (session.freeSearchCount.value < Config.FREE_SEARCH_LIMIT) return true

        if (!session.isLoggedIn) {
            _searchState.value = SearchState.RequireLogin
            return false
        }
        // کاربر واردشده ولی کش اشتراک ندارد → بررسی مجدد با سرور.
        if (authRepository.refreshStatus().getOrDefault(false)) return true

        _searchState.value = SearchState.RequireSubscription
        return false
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

    private fun remaining(count: Int): Int =
        (Config.FREE_SEARCH_LIMIT - count).coerceAtLeast(0)
}

sealed class SearchState {
    object Idle : SearchState()

    /** سهمیه‌ی رایگان تمام شده و کاربر باید وارد شود. */
    object RequireLogin : SearchState()

    /** کاربر واردشده ولی اشتراک فعال ندارد. */
    object RequireSubscription : SearchState()

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

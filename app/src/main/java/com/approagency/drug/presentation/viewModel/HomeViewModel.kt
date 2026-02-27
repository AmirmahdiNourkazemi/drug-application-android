package com.approagency.drug.presentation.viewModel

import android.net.http.HttpException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.approagency.drug.data.dto.DrugListResponse
import com.approagency.drug.domain.model.DrugSearchParams
import com.approagency.drug.domain.usecase.GetDrugSearchUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel (
    private val getDrugSearchUseCase: GetDrugSearchUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(DrugSearchState())
    val uiState : StateFlow<DrugSearchState> = _uiState


    // Add SharedFlow for one-time events
    private val _event = MutableSharedFlow<HomeEvent>()
    val event = _event.asSharedFlow()

    fun searchDrugs(drugSearchParams: DrugSearchParams) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val result = getDrugSearchUseCase.invoke(drugSearchParams)
                _uiState.update { it.copy(
                    isLoading = false,
                    drugsData = result
                ) }
            } catch (e: HttpException) {
                handleError(e.message)
            }catch (e: Exception) {
                handleError(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    private suspend fun handleError(message: String?) {
        _uiState.update { it.copy(isLoading = false) }
        _event.emit(HomeEvent.ShowError(message ?: "Error occurred"))
    }

    // Clear error when consumed
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}


sealed class HomeEvent {
    data class ShowError(val message: String) : HomeEvent()
    data class NavigateToDetails(val drugId: String) : HomeEvent()
}

data class DrugSearchState(
    val isLoading: Boolean = false,
    val drugsData: Result<DrugListResponse?>? = null,
    val error:String? = null
)


package com.approagency.pharmacy.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.approagency.pharmacy.data.dto.DarmanModel
import com.approagency.pharmacy.data.dto.DrugListResponse
import com.approagency.pharmacy.data.dto.DrugModels
import com.approagency.pharmacy.domain.model.DrugSearchParams
import com.approagency.pharmacy.domain.usecase.GetDarmanUseCase
import com.approagency.pharmacy.domain.usecase.GetDrugDetailUseCase
import com.approagency.pharmacy.domain.usecase.GetDrugSearchUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel (
    private val getDrugSearchUseCase: GetDrugSearchUseCase,
    private val getDrugDetailUseCase: GetDrugDetailUseCase,
    private val getDarmanUseCase: GetDarmanUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(CombineState())
    val uiState : StateFlow<CombineState> = _uiState

    // Add SharedFlow for one-time events
    private val _event = MutableSharedFlow<HomeEvent>()
    val event = _event.asSharedFlow()
        init {
            getDarmani()
        }
    fun getDarmani() {
        _uiState.update {
            it.copy(
                darmanState = it.darmanState.copy(isLoading = true),
                showDarmanList = true
            )
        }
        viewModelScope.launch {
            try {
                val result =  getDarmanUseCase.invoke()
                _uiState.update {
                    it.copy(
                        darmanState = it.darmanState.copy(
                            isLoading = false,
                            getDarmani = result
                        )
                    )
                }
            }catch (e: Exception) {
                handleError(e.message)
            }catch (e: Exception) {
                handleError(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun searchDrugs(drugSearchParams: DrugSearchParams) {
        _uiState.update { it.copy(drugSearchState = it.drugSearchState.copy(
            isLoading = true
        ) ,  showDarmanList = false) }
        viewModelScope.launch {
            try {
                val result = getDrugSearchUseCase.invoke(drugSearchParams)
                println(result)
                _uiState.update { it.copy(
                    drugSearchState = it.drugSearchState.copy(
                        isLoading = false,
                        drugsData = result
                    )

                ) }
            } catch (e: Exception) {
                println(e.message)
                handleError(e.message)
            }catch (e: Exception) {
                println(e.message)
                handleError(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun drugDetail(cod: Int , allDrugs:Result<DrugListResponse?>?) {
        _uiState.update { it.copy(drugDetailState = it.drugDetailState.copy(
            isLoading = true
        ) ,  showDarmanList = false) }
        viewModelScope.launch {
            try {
                val result = getDrugDetailUseCase.invoke(cod)
                _uiState.update {
                    it.copy(
                        drugDetailState = it.drugDetailState.copy(
                            isLoading = false,
                            drugDetail = result,
                            isDetailVisible = true
                        ),
                        drugSearchState = it.drugSearchState.copy(
                            isLoading = false,
                            drugsData = allDrugs
                        )
                    )
                }
                println(result)
            } catch (e: Exception) {
                handleError(e.message)
            }catch (e: Exception) {
                handleError(e.localizedMessage ?: "Unknown error")
            }
        }
    }
    fun closeDetail() {
        _uiState.update {
            it.copy(
                drugDetailState = it.drugDetailState.copy(
                    isDetailVisible = false,
                    drugDetail = null   // optional, depends if you want to keep last detail cached
                )
            )
        }
    }

    private suspend fun handleError(message: String?) {
        _uiState.update { it.copy(drugSearchState = it.drugSearchState.copy(
            isLoading = false
        ) , drugDetailState = it.drugDetailState.copy(
            isLoading = false
        ))  }
        _event.emit(HomeEvent.ShowError(message ?: "Error occurred"))
    }

    // Clear error when consumed
    fun clearError() {
//        _uiState.update { it.copy(error = null) }
    }
}


sealed class HomeEvent {
    data class ShowError(val message: String) : HomeEvent()
    data class ShowDetailDrug(val drugDetail:Result<DrugModels?>?): HomeEvent()
}

data class CombineState(
    val drugSearchState: DrugSearchState = DrugSearchState() ,
    val drugDetailState: DrugDetailState =DrugDetailState(),
    val darmanState: GetDarmaniState = GetDarmaniState(),
    val showDarmanList: Boolean = true
    )

data class DrugSearchState(
    val isLoading: Boolean = false,
    val drugsData: Result<DrugListResponse?>? = null,
    val drugDetail: Result<DrugModels?>? = null,
    val error:String? = null
)

data class DrugDetailState(
    val isLoading: Boolean = false,
    val drugDetail:Result<DrugModels?>? = null,
    val isDetailVisible: Boolean = false,
    val error:String? = null
)

data class GetDarmaniState(
    val isLoading: Boolean = false,
    val getDarmani:Result<DarmanModel?>? = null,
    val error:String? = null
)


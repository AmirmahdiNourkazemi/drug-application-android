package com.approagency.pharmacy.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.approagency.pharmacy.domain.model.DrugDetail
import com.approagency.pharmacy.domain.usecase.DrugDetailYabUseCase
import com.approagency.pharmacy.utils.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DrugDetailViewModel(
    private val drugDetailUseCase: DrugDetailYabUseCase
) : ViewModel() {

    private val _detailState = MutableStateFlow<DrugDetailYabState>(DrugDetailYabState.Idle)
    val detailState: StateFlow<DrugDetailYabState> = _detailState.asStateFlow()

    fun loadDrugDetail(detailUrl: String) {
        viewModelScope.launch {
            _detailState.value = DrugDetailYabState.Loading
            val result = drugDetailUseCase(detailUrl)
            _detailState.value = when {
                result.isSuccess -> DrugDetailYabState.Success(result.getOrNull()!!)
                else -> DrugDetailYabState.Error(result.exceptionOrNull().toUserMessage())
            }
        }
    }

    fun reset() {
        _detailState.value = DrugDetailYabState.Idle
    }
}

sealed class DrugDetailYabState {
    object Idle : DrugDetailYabState()
    object Loading : DrugDetailYabState()
    data class Success(val drugDetail: DrugDetail) : DrugDetailYabState()
    data class Error(val message: String) : DrugDetailYabState()
}
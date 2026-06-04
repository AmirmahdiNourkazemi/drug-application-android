package com.approagency.drug.presentation.viewModel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.approagency.drug.domain.model.PharmacyItem
import com.approagency.drug.domain.usecase.GetPharmaciesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class PharmacyState {
    object Idle : PharmacyState()
    object Loading : PharmacyState()
    data class Success(val items: List<PharmacyItem>) : PharmacyState()
    data class Error(val message: String) : PharmacyState()
}

class PharmacyViewModel(
    private val getPharmaciesUseCase: GetPharmaciesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<PharmacyState>(PharmacyState.Idle)
    val state: StateFlow<PharmacyState> = _state.asStateFlow()

    private var currentGenericDrugId: String = ""
    private var currentProvinceId: String = "0"

    fun search(genericDrugId: String, provinceId: String) {
        if (genericDrugId.isBlank()) return

        currentGenericDrugId = genericDrugId
        currentProvinceId = provinceId

        viewModelScope.launch {
            _state.value = PharmacyState.Loading
            try {
                val result = getPharmaciesUseCase(genericDrugId, provinceId)
                _state.value = PharmacyState.Success(result)
            } catch (e: Exception) {
                _state.value = PharmacyState.Error(e.message ?: "خطا در دریافت اطلاعات")
            }
        }
    }

    fun retry() {
        if (currentGenericDrugId.isNotBlank()) {
            search(currentGenericDrugId, currentProvinceId)
        }
    }
}
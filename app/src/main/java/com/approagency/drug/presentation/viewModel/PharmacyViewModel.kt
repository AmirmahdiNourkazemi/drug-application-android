package com.approagency.drug.presentation.viewModel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.approagency.drug.domain.model.PharmacyDetail
import com.approagency.drug.domain.model.PharmacyItem
import com.approagency.drug.domain.usecase.GetPharmaciesUseCase
import com.approagency.drug.domain.usecase.GetPharmacyDetailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
sealed class PharmacyDetailState {
    object Idle : PharmacyDetailState()
    object Loading : PharmacyDetailState()
    data class Success(val detail: PharmacyDetail) : PharmacyDetailState()
    data class Error(val message: String) : PharmacyDetailState()
}
sealed class PharmacyState {
    object Idle : PharmacyState()
    object Loading : PharmacyState()
    data class Success(val items: List<PharmacyItem>) : PharmacyState()
    data class Error(val message: String) : PharmacyState()
}

class PharmacyViewModel(
    private val getPharmaciesUseCase: GetPharmaciesUseCase,
    private val getPharmacyDetailUseCase: GetPharmacyDetailUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<PharmacyState>(PharmacyState.Idle)
    val state: StateFlow<PharmacyState> = _state.asStateFlow()


    // استفاده از Map برای نگهداری State هر داروخانه به صورت جداگانه
    private val _pharmacyDetailStates = MutableStateFlow<Map<String, PharmacyDetailState>>(emptyMap())
    val pharmacyDetailStates: StateFlow<Map<String, PharmacyDetailState>> = _pharmacyDetailStates.asStateFlow()

    private var currentGenericDrugId: String = ""
    private var currentProvinceId: String = "0"

    fun loadPharmacyDetail(pharmacyUrl: String) {
        // اگر در حال حاضر لود می‌شود یا موفقیت آمیز بوده، دوباره درخواست نده
        val currentState = _pharmacyDetailStates.value[pharmacyUrl]
        if (currentState is PharmacyDetailState.Loading || currentState is PharmacyDetailState.Success) {
            return
        }

        viewModelScope.launch {
            // تنظیم Loading برای این داروخانه خاص
            _pharmacyDetailStates.value = _pharmacyDetailStates.value.toMutableMap().apply {
                put(pharmacyUrl, PharmacyDetailState.Loading)
            }

            try {
                val detail = getPharmacyDetailUseCase(pharmacyUrl)
                _pharmacyDetailStates.value = _pharmacyDetailStates.value.toMutableMap().apply {
                    put(pharmacyUrl, PharmacyDetailState.Success(detail))
                }
            } catch (e: Exception) {
                _pharmacyDetailStates.value = _pharmacyDetailStates.value.toMutableMap().apply {
                    put(pharmacyUrl, PharmacyDetailState.Error(e.message ?: "خطا در دریافت اطلاعات"))
                }
            }
        }
    }


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
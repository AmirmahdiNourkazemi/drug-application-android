package com.approagency.drug.presentation.viewModel

import android.net.http.HttpException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.approagency.drug.data.dto.DrugListResponse
import com.approagency.drug.data.dto.DrugModels
import com.approagency.drug.domain.model.TestGroup
import com.approagency.drug.domain.model.TestItem
import com.approagency.drug.domain.usecase.GetTestGroupUseCase
import com.approagency.drug.domain.usecase.GetTestItemByGroupId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LabViewModel (
    private val getTestGroupUseCase: GetTestGroupUseCase,
    private val getTestItemByGroupId: GetTestItemByGroupId
) : ViewModel(){
    private val _testGroups = MutableStateFlow(TestGroupItem())
    val testGroups: StateFlow<TestGroupItem> = _testGroups.asStateFlow()

    private val _testItems = MutableStateFlow(TestItemState())
    val testItems: StateFlow<TestItemState> = _testItems.asStateFlow()

    init {
        loadTestGroups()
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


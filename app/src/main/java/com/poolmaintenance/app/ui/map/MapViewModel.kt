package com.poolmaintenance.app.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poolmaintenance.app.data.MaintenanceRecord
import com.poolmaintenance.app.data.VillaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VillaMapUiState(
    val selectedVilla: Int? = null,
    val showDialog: Boolean = false,
    val existingRecords: List<MaintenanceRecord> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: VillaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VillaMapUiState())
    val uiState: StateFlow<VillaMapUiState> = _uiState.asStateFlow()

    fun selectVilla(villaNumber: Int) {
        _uiState.value = _uiState.value.copy(
            selectedVilla = villaNumber,
            showDialog = true,
            isLoading = true
        )
        viewModelScope.launch {
            val records = repository.getRecordsForVilla(villaNumber)
            _uiState.value = _uiState.value.copy(
                existingRecords = records,
                isLoading = false
            )
        }
    }

    fun dismissDialog() {
        _uiState.value = _uiState.value.copy(
            showDialog = false,
            selectedVilla = null,
            existingRecords = emptyList()
        )
    }

    fun scheduleMaintenance(
        villaNumber: Int,
        scheduledDate: Long,
        obatAmount: Double,
        hclAmount: Double,
        checkStatus: String
    ) {
        viewModelScope.launch {
            val record = MaintenanceRecord(
                villaNumber = villaNumber,
                date = System.currentTimeMillis(),
                scheduledDate = scheduledDate,
                obatAmount = obatAmount,
                hclAmount = hclAmount,
                checkStatus = checkStatus,
                isCompleted = false
            )
            repository.insertRecord(record)
            // Refresh records
            val updatedRecords = repository.getRecordsForVilla(villaNumber)
            _uiState.value = _uiState.value.copy(existingRecords = updatedRecords)
        }
    }

    fun deleteRecord(id: Long) {
        viewModelScope.launch {
            repository.deleteRecord(id)
            val currentVilla = _uiState.value.selectedVilla
            if (currentVilla != null) {
                val updatedRecords = repository.getRecordsForVilla(currentVilla)
                _uiState.value = _uiState.value.copy(existingRecords = updatedRecords)
            }
        }
    }
}

package com.poolmaintenance.app.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poolmaintenance.app.data.MaintenanceRecord
import com.poolmaintenance.app.data.RecurrenceRule
import com.poolmaintenance.app.data.Schedule
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
    val existingSchedules: List<Schedule> = emptyList(),
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
            val schedules = repository.getSchedulesForVilla(villaNumber)
            _uiState.value = _uiState.value.copy(
                existingRecords = records,
                existingSchedules = schedules,
                isLoading = false
            )
        }
    }

    fun dismissDialog() {
        _uiState.value = _uiState.value.copy(
            showDialog = false,
            selectedVilla = null,
            existingRecords = emptyList(),
            existingSchedules = emptyList()
        )
    }

    fun scheduleMaintenance(
        villaNumber: Int,
        scheduledDate: Long,
        scheduleType: String,
        granular: Double,
        tablet: Double,
        hcl: Double,
        trusi: Double,
        sodaAsh: Double,
        pac: Double,
        tesPh: Double,
        tesChlorine: Double,
        vakum: Double,
        brushing: Double,
        kurasBalancing: Double,
        checkStatus: String,
        scheduledHour: Int = -1,
        scheduledMinute: Int = -1
    ) {
        viewModelScope.launch {
            // Create the recurring schedule
            val recurrenceRule = repository.getRecurrenceRule(scheduleType)
            val nextDueDate = repository.calculateNextDueDate(scheduledDate, recurrenceRule)

            val schedule = Schedule(
                villaNumber = villaNumber,
                scheduleType = scheduleType,
                startDate = scheduledDate,
                nextDueDate = nextDueDate,
                recurrenceRule = recurrenceRule,
                scheduledHour = scheduledHour,
                scheduledMinute = scheduledMinute
            )
            val scheduleId = repository.insertSchedule(schedule)

            // Create the first MaintenanceRecord with user's data — NOT completed by default
            val record = MaintenanceRecord(
                villaNumber = villaNumber,
                date = System.currentTimeMillis(),
                scheduledDate = scheduledDate,
                scheduleType = scheduleType,
                scheduleId = scheduleId,
                granular = granular,
                tablet = tablet,
                hcl = hcl,
                trusi = trusi,
                sodaAsh = sodaAsh,
                pac = pac,
                tesPh = tesPh,
                tesChlorine = tesChlorine,
                vakum = vakum,
                brushing = brushing,
                kurasBalancing = kurasBalancing,
                checkStatus = checkStatus,
                isCompleted = false
            )
            repository.insertRecord(record)

            val updatedRecords = repository.getRecordsForVilla(villaNumber)
            val updatedSchedules = repository.getSchedulesForVilla(villaNumber)
            _uiState.value = _uiState.value.copy(
                existingRecords = updatedRecords,
                existingSchedules = updatedSchedules
            )
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

    fun deleteSchedule(id: Long) {
        viewModelScope.launch {
            repository.deleteSchedule(id)
            val currentVilla = _uiState.value.selectedVilla
            if (currentVilla != null) {
                val updatedSchedules = repository.getSchedulesForVilla(currentVilla)
                _uiState.value = _uiState.value.copy(existingSchedules = updatedSchedules)
            }
        }
    }

    fun editScheduleTime(scheduleId: Long, hour: Int, minute: Int) {
        viewModelScope.launch {
            repository.updateScheduleTime(scheduleId, hour, minute)
            val currentVilla = _uiState.value.selectedVilla
            if (currentVilla != null) {
                val updatedSchedules = repository.getSchedulesForVilla(currentVilla)
                _uiState.value = _uiState.value.copy(existingSchedules = updatedSchedules)
            }
        }
    }
}

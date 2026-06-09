package com.poolmaintenance.app.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poolmaintenance.app.data.MaintenanceRecord
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
    val isLoading: Boolean = false,
    // Edit schedule state
    val editingSchedule: Schedule? = null,
    val editingRecord: MaintenanceRecord? = null,
    val showEditDialog: Boolean = false
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
            existingSchedules = emptyList(),
            editingSchedule = null,
            editingRecord = null,
            showEditDialog = false
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

    /**
     * Open the edit dialog for a schedule — loads the latest record for the schedule.
     */
    fun openEditSchedule(schedule: Schedule) {
        viewModelScope.launch {
            val latestRecord = repository.getLatestRecordForSchedule(schedule.id)
            _uiState.value = _uiState.value.copy(
                editingSchedule = schedule,
                editingRecord = latestRecord,
                showEditDialog = true
            )
        }
    }

    /**
     * Open the edit dialog for a record — loads the associated schedule if available.
     */
    fun openEditRecord(record: MaintenanceRecord) {
        viewModelScope.launch {
            val schedule = if (record.scheduleId > 0) {
                repository.getScheduleById(record.scheduleId)
            } else null
            _uiState.value = _uiState.value.copy(
                editingSchedule = schedule,
                editingRecord = record,
                showEditDialog = true
            )
        }
    }

    fun dismissEditDialog() {
        _uiState.value = _uiState.value.copy(
            editingSchedule = null,
            editingRecord = null,
            showEditDialog = false
        )
    }

    /**
     * Edit a standalone record — updates only the record data (no schedule metadata).
     */
    fun editRecord(
        recordId: Long,
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
        checkStatus: String
    ) {
        viewModelScope.launch {
            repository.updateRecordData(
                recordId,
                granular, tablet, hcl, trusi, sodaAsh, pac, tesPh, tesChlorine,
                vakum, brushing, kurasBalancing, checkStatus
            )
            // Refresh data
            val currentVilla = _uiState.value.selectedVilla
            if (currentVilla != null) {
                val updatedRecords = repository.getRecordsForVilla(currentVilla)
                _uiState.value = _uiState.value.copy(
                    existingRecords = updatedRecords,
                    editingSchedule = null,
                    editingRecord = null,
                    showEditDialog = false
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    editingSchedule = null,
                    editingRecord = null,
                    showEditDialog = false
                )
            }
        }
    }

    /**
     * Edit a schedule — updates schedule metadata (startDate, time) and latest record data.
     * If startDate changed, recalculate nextDueDate.
     */
    fun editSchedule(
        scheduleId: Long,
        startDate: Long,
        scheduledHour: Int,
        scheduledMinute: Int,
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
        checkStatus: String
    ) {
        viewModelScope.launch {
            val schedule = repository.getScheduleById(scheduleId) ?: return@launch
            val recurrenceRule = schedule.recurrenceRule
            val nextDueDate = repository.calculateNextDueDate(startDate, recurrenceRule)

            // Update schedule metadata
            repository.updateSchedule(scheduleId, startDate, nextDueDate, scheduledHour, scheduledMinute)

            // Update latest record data if exists
            val recordId = _uiState.value.editingRecord?.id
            if (recordId != null && recordId > 0) {
                repository.updateRecordData(
                    recordId,
                    granular, tablet, hcl, trusi, sodaAsh, pac, tesPh, tesChlorine,
                    vakum, brushing, kurasBalancing, checkStatus
                )
            }

            // Refresh data
            val currentVilla = _uiState.value.selectedVilla
            if (currentVilla != null) {
                val updatedRecords = repository.getRecordsForVilla(currentVilla)
                val updatedSchedules = repository.getSchedulesForVilla(currentVilla)
                _uiState.value = _uiState.value.copy(
                    existingRecords = updatedRecords,
                    existingSchedules = updatedSchedules,
                    editingSchedule = null,
                    editingRecord = null,
                    showEditDialog = false
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    editingSchedule = null,
                    editingRecord = null,
                    showEditDialog = false
                )
            }
        }
    }
}

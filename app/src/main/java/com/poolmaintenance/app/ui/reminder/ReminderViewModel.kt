package com.poolmaintenance.app.ui.reminder

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

data class ReminderUiState(
    val todayReminders: List<MaintenanceRecord> = emptyList(),
    val isLoading: Boolean = false,
    val completedCount: Int = 0,
    val pendingCount: Int = 0,
    // Detail dialog state
    val selectedRecord: MaintenanceRecord? = null,
    val previousRecord: MaintenanceRecord? = null,
    val showDetailDialog: Boolean = false
)

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val repository: VillaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReminderUiState())
    val uiState: StateFlow<ReminderUiState> = _uiState.asStateFlow()

    init {
        loadTodayReminders()
    }

    fun loadTodayReminders() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            // Ensure records exist for all due schedules today
            repository.ensureTodayRecords()
            val reminders = repository.getTodayReminders()
            val completed = reminders.count { it.isCompleted }
            val pending = reminders.count { !it.isCompleted }
            _uiState.value = _uiState.value.copy(
                todayReminders = reminders,
                isLoading = false,
                completedCount = completed,
                pendingCount = pending
            )
        }
    }

    fun toggleCompleted(record: MaintenanceRecord) {
        viewModelScope.launch {
            if (record.isCompleted) {
                repository.markIncomplete(record.id)
            } else {
                repository.markCompleted(record.id)
            }
            loadTodayReminders()
        }
    }

    fun deleteRecord(id: Long) {
        viewModelScope.launch {
            repository.deleteRecord(id)
            loadTodayReminders()
        }
    }

    /**
     * Open detail dialog for a reminder record.
     * Loads the previous completed record for the same schedule/villa/type.
     */
    fun openDetail(record: MaintenanceRecord) {
        viewModelScope.launch {
            var previous: MaintenanceRecord? = null

            // First try to find by scheduleId
            if (record.scheduleId > 0) {
                previous = repository.getPreviousCompletedRecord(record.scheduleId, record.scheduledDate)
            }

            // Fallback: find by villa + type
            if (previous == null) {
                previous = repository.getPreviousRecordByType(
                    record.villaNumber,
                    record.scheduleType,
                    record.scheduledDate
                )
            }

            _uiState.value = _uiState.value.copy(
                selectedRecord = record,
                previousRecord = previous,
                showDetailDialog = true
            )
        }
    }

    fun dismissDetail() {
        _uiState.value = _uiState.value.copy(
            selectedRecord = null,
            previousRecord = null,
            showDetailDialog = false
        )
    }

    /**
     * Update a record with new data from the detail dialog.
     */
    fun updateRecordData(
        recordId: Long,
        granular: Double, tablet: Double, hcl: Double, trusi: Double,
        sodaAsh: Double, pac: Double, tesPh: Double, tesChlorine: Double,
        vakum: Double, brushing: Double, kurasBalancing: Double,
        checkStatus: String
    ) {
        viewModelScope.launch {
            repository.updateRecordData(
                recordId, granular, tablet, hcl, trusi, sodaAsh, pac, tesPh, tesChlorine,
                vakum, brushing, kurasBalancing, checkStatus
            )
            dismissDetail()
            loadTodayReminders()
        }
    }
}

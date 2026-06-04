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
    val pendingCount: Int = 0
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
}

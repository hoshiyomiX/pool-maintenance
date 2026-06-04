package com.poolmaintenance.app.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poolmaintenance.app.data.AggregatedStats
import com.poolmaintenance.app.data.VillaRepository
import com.poolmaintenance.app.data.VillaStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

enum class TimeFilter(val label: String) {
    WEEKLY("Mingguan"),
    MONTHLY("Bulanan"),
    YEARLY("Tahunan")
}

data class StatsUiState(
    val currentFilter: TimeFilter = TimeFilter.MONTHLY,
    val aggregatedStats: AggregatedStats = AggregatedStats(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0),
    val perVillaStats: List<VillaStats> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val repository: VillaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init { loadStats() }

    fun setFilter(filter: TimeFilter) {
        _uiState.value = _uiState.value.copy(currentFilter = filter)
        loadStats()
    }

    private fun loadStats() {
        val filter = _uiState.value.currentFilter
        val (startMs, endMs) = getTimeRange(filter)
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            val stats = repository.getStatsInRange(startMs, endMs)
            val perVilla = repository.getPerVillaStatsInRange(startMs, endMs)
            _uiState.value = _uiState.value.copy(aggregatedStats = stats, perVillaStats = perVilla, isLoading = false)
        }
    }

    private fun getTimeRange(filter: TimeFilter): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        val endMs = calendar.timeInMillis
        when (filter) {
            TimeFilter.WEEKLY -> calendar.add(Calendar.DAY_OF_YEAR, -7)
            TimeFilter.MONTHLY -> calendar.add(Calendar.MONTH, -1)
            TimeFilter.YEARLY -> calendar.add(Calendar.YEAR, -1)
        }
        return Pair(calendar.timeInMillis, endMs)
    }

    fun refresh() { loadStats() }
}

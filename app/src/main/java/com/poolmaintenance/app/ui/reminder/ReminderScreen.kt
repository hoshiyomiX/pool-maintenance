package com.poolmaintenance.app.ui.reminder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.poolmaintenance.app.data.MaintenanceRecord
import com.poolmaintenance.app.data.ScheduleType
import com.poolmaintenance.app.ui.icons.AppIcons
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ReminderScreen(
    viewModel: ReminderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale("id", "ID")) }

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Reminder Hari Ini", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimary)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(text = "Selesai: ${uiState.completedCount}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f))
                    Text(text = "Pending: ${uiState.pendingCount}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else if (uiState.todayReminders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Filled.Notifications, contentDescription = null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Tidak ada jadwal hari ini", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Tambah jadwal dari tab Denah", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(items = uiState.todayReminders, key = { it.id }) { record ->
                    ReminderCard(record = record, onToggleCompleted = { viewModel.toggleCompleted(record) }, onDelete = { viewModel.deleteRecord(record.id) })
                }
            }
        }
    }
}

@Composable
fun ReminderCard(
    record: MaintenanceRecord,
    onToggleCompleted: () -> Unit,
    onDelete: () -> Unit
) {
    val isCompleted = record.isCompleted
    val cardColor = if (isCompleted) Color(0xFFE8F5E9) else Color(0xFFFFF8E1)
    val statusIconTint = if (isCompleted) Color(0xFF2E7D32) else Color(0xFFF57F17)
    val textDeco = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None

    val typeColor = when (record.scheduleType) {
        ScheduleType.MONITORING -> Color(0xFF1565C0)
        ScheduleType.TREATMENT_MINGGUAN -> Color(0xFF6A1B9A)
        ScheduleType.DEEP_TREATMENT -> Color(0xFFBF360C)
        else -> Color(0xFF757575)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onToggleCompleted, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = if (isCompleted) AppIcons.CheckCircle else AppIcons.CircleOutlined,
                    contentDescription = if (isCompleted) "Selesai" else "Belum selesai",
                    tint = statusIconTint,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = AppIcons.Pool, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Villa ${String.format("%02d", record.villaNumber)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, textDecoration = textDeco)
                    Spacer(modifier = Modifier.width(6.dp))
                    // Schedule type badge
                    Surface(shape = RoundedCornerShape(4.dp), color = typeColor.copy(alpha = 0.15f)) {
                        Text(
                            text = record.scheduleType,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = typeColor,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))

                // Show fields based on schedule type
                when (record.scheduleType) {
                    ScheduleType.MONITORING -> {
                        Row {
                            Text(text = "Granular: ${record.granular} kg", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(end = 6.dp), textDecoration = textDeco)
                            Text(text = "Tablet: ${record.tablet.toInt()}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(end = 6.dp), textDecoration = textDeco)
                            Text(text = "HCL: ${record.hcl} L", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textDecoration = textDeco)
                        }
                        Row {
                            Text(text = "Trusi: ${record.trusi} kg", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(end = 6.dp), textDecoration = textDeco)
                            Text(text = "Soda Ash: ${record.sodaAsh} kg", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(end = 6.dp), textDecoration = textDeco)
                            Text(text = "PAC: ${record.pac} L", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textDecoration = textDeco)
                        }
                        Row {
                            Text(text = "pH: ${record.tesPh}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(end = 6.dp), textDecoration = textDeco)
                            Text(text = "Chlorine: ${record.tesChlorine} ppm", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textDecoration = textDeco)
                        }
                    }
                    ScheduleType.TREATMENT_MINGGUAN -> {
                        Row {
                            Text(text = "Vakum: ${if (record.vakum > 0.0) "Ya" else "\u2014"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(end = 8.dp), textDecoration = textDeco)
                            Text(text = "Brushing: ${if (record.brushing > 0.0) "Ya" else "\u2014"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textDecoration = textDeco)
                        }
                    }
                    ScheduleType.DEEP_TREATMENT -> {
                        Text(text = "Kuras Balancing: ${if (record.kurasBalancing > 0.0) "Ya" else "\u2014"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textDecoration = textDeco)
                    }
                }
                Text(
                    text = record.checkStatus,
                    style = MaterialTheme.typography.labelSmall,
                    color = when (record.checkStatus) { "Sudah Dicek" -> Color(0xFF2E7D32); else -> Color(0xFFC62828) }
                )
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(imageVector = AppIcons.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
            }
        }
    }
}

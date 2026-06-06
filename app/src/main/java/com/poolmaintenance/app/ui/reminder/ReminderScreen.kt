package com.poolmaintenance.app.ui.reminder

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
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
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")) }

    // Request notification permission on API 33+
    val context = LocalContext.current
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { /* permission result — we don't block functionality */ }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

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
                    ReminderCard(
                        record = record,
                        onToggleCompleted = { viewModel.toggleCompleted(record) },
                        onDelete = { viewModel.deleteRecord(record.id) },
                        onClick = { viewModel.openDetail(record) }
                    )
                }
            }
        }
    }

    // Detail dialog
    if (uiState.showDetailDialog && uiState.selectedRecord != null) {
        ReminderDetailDialog(
            record = uiState.selectedRecord!!,
            previousRecord = uiState.previousRecord,
            dateFormatter = dateFormatter,
            onDismiss = { viewModel.dismissDetail() },
            onUpdate = { id, granular, tablet, hcl, trusi, sodaAsh, pac, tesPh, tesChlorine, vakum, brushing, kurasBalancing, status ->
                viewModel.updateRecordData(id, granular, tablet, hcl, trusi, sodaAsh, pac, tesPh, tesChlorine, vakum, brushing, kurasBalancing, status)
            },
            onToggleStatus = { viewModel.toggleCompleted(uiState.selectedRecord!!); viewModel.dismissDetail() }
        )
    }
}

@Composable
fun ReminderCard(
    record: MaintenanceRecord,
    onToggleCompleted: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
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

            Column(
                modifier = Modifier.weight(1f).clickable(onClick = onClick)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = AppIcons.Pool, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Villa ${String.format("%02d", record.villaNumber)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, textDecoration = textDeco)
                    Spacer(modifier = Modifier.width(6.dp))
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
                    text = if (record.isCompleted) "Selesai" else "Pending",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (record.isCompleted) Color(0xFF2E7D32) else Color(0xFFF57F17)
                )
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(imageVector = AppIcons.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
            }
        }
    }
}

// ========== Detail Dialog: previous data + input fields + status toggle ==========

@Composable
fun ReminderDetailDialog(
    record: MaintenanceRecord,
    previousRecord: MaintenanceRecord?,
    dateFormatter: SimpleDateFormat,
    onDismiss: () -> Unit,
    onUpdate: (Long, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, String) -> Unit,
    onToggleStatus: () -> Unit
) {
    // Input state
    var granularInput by remember { mutableStateOf(if (record.granular > 0.0) record.granular.toString() else "") }
    var tabletInput by remember { mutableStateOf(if (record.tablet > 0.0) record.tablet.toString() else "") }
    var hclInput by remember { mutableStateOf(if (record.hcl > 0.0) record.hcl.toString() else "") }
    var trusiInput by remember { mutableStateOf(if (record.trusi > 0.0) record.trusi.toString() else "") }
    var sodaAshInput by remember { mutableStateOf(if (record.sodaAsh > 0.0) record.sodaAsh.toString() else "") }
    var pacInput by remember { mutableStateOf(if (record.pac > 0.0) record.pac.toString() else "") }
    var tesPhInput by remember { mutableStateOf(if (record.tesPh > 0.0) record.tesPh.toString() else "") }
    var tesChlorineInput by remember { mutableStateOf(if (record.tesChlorine > 0.0) record.tesChlorine.toString() else "") }
    var vakumInput by remember { mutableStateOf(record.vakum > 0.0) }
    var brushingInput by remember { mutableStateOf(record.brushing > 0.0) }
    var kurasBalancingInput by remember { mutableStateOf(record.kurasBalancing > 0.0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = AppIcons.Pool, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Villa ${String.format("%02d", record.villaNumber)}")
                Spacer(modifier = Modifier.weight(1f))
                // Status toggle button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (record.isCompleted) Color(0xFFE8F5E9) else Color(0xFFFFF8E1)
                ) {
                    TextButton(onClick = onToggleStatus) {
                        Text(
                            text = if (record.isCompleted) "Selesai" else "Pending",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (record.isCompleted) Color(0xFF2E7D32) else Color(0xFFF57F17),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                // Type badge + date
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "${record.scheduleType} - ${dateFormatter.format(java.util.Date(record.scheduledDate))}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Previous data section
                if (previousRecord != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Data Sebelumnya",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = dateFormatter.format(java.util.Date(previousRecord.scheduledDate)),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            PreviousDataMarkdown(previousRecord)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                // Input fields for editing current record
                Text(
                    text = "Edit Data",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))

                when (record.scheduleType) {
                    ScheduleType.MONITORING -> {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = granularInput, onValueChange = { granularInput = it }, label = { Text("Granular (kg)") }, modifier = Modifier.weight(1f), singleLine = true)
                            OutlinedTextField(value = tabletInput, onValueChange = { tabletInput = it }, label = { Text("Tablet (pcs)") }, modifier = Modifier.weight(1f), singleLine = true)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = hclInput, onValueChange = { hclInput = it }, label = { Text("HCL (liter)") }, modifier = Modifier.weight(1f), singleLine = true)
                            OutlinedTextField(value = trusiInput, onValueChange = { trusiInput = it }, label = { Text("Trusi (kg)") }, modifier = Modifier.weight(1f), singleLine = true)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = sodaAshInput, onValueChange = { sodaAshInput = it }, label = { Text("Soda Ash (kg)") }, modifier = Modifier.weight(1f), singleLine = true)
                            OutlinedTextField(value = pacInput, onValueChange = { pacInput = it }, label = { Text("PAC (liter)") }, modifier = Modifier.weight(1f), singleLine = true)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = tesPhInput, onValueChange = { tesPhInput = it }, label = { Text("Tes pH") }, modifier = Modifier.weight(1f), singleLine = true)
                            OutlinedTextField(value = tesChlorineInput, onValueChange = { tesChlorineInput = it }, label = { Text("Tes Chlorine (ppm)") }, modifier = Modifier.weight(1f), singleLine = true)
                        }
                    }
                    ScheduleType.TREATMENT_MINGGUAN -> {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = vakumInput, onCheckedChange = { vakumInput = it })
                            Text("Vakum", style = MaterialTheme.typography.bodyLarge)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = brushingInput, onCheckedChange = { brushingInput = it })
                            Text("Brushing", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                    ScheduleType.DEEP_TREATMENT -> {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = kurasBalancingInput, onCheckedChange = { kurasBalancingInput = it })
                            Text("Kuras Balancing", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onUpdate(
                    record.id,
                    granularInput.toDoubleOrNull() ?: 0.0,
                    tabletInput.toDoubleOrNull() ?: 0.0,
                    hclInput.toDoubleOrNull() ?: 0.0,
                    trusiInput.toDoubleOrNull() ?: 0.0,
                    sodaAshInput.toDoubleOrNull() ?: 0.0,
                    pacInput.toDoubleOrNull() ?: 0.0,
                    tesPhInput.toDoubleOrNull() ?: 0.0,
                    tesChlorineInput.toDoubleOrNull() ?: 0.0,
                    if (vakumInput) 1.0 else 0.0,
                    if (brushingInput) 1.0 else 0.0,
                    if (kurasBalancingInput) 1.0 else 0.0,
                    "Sudah Dicek"
                )
            }) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Tutup") }
        }
    )
}

/**
 * Render previous record data as formatted markdown-like text.
 */
@Composable
fun PreviousDataMarkdown(record: MaintenanceRecord) {
    val monoStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)

    when (record.scheduleType) {
        ScheduleType.MONITORING -> {
            Text(text = "Granular   : ${record.granular} kg", style = monoStyle, color = MaterialTheme.colorScheme.onSurface)
            Text(text = "Tablet     : ${record.tablet.toInt()} pcs", style = monoStyle, color = MaterialTheme.colorScheme.onSurface)
            Text(text = "HCL        : ${record.hcl} L", style = monoStyle, color = MaterialTheme.colorScheme.onSurface)
            Text(text = "Trusi      : ${record.trusi} kg", style = monoStyle, color = MaterialTheme.colorScheme.onSurface)
            Text(text = "Soda Ash   : ${record.sodaAsh} kg", style = monoStyle, color = MaterialTheme.colorScheme.onSurface)
            Text(text = "PAC        : ${record.pac} L", style = monoStyle, color = MaterialTheme.colorScheme.onSurface)
            Text(text = "pH         : ${record.tesPh}", style = monoStyle, color = MaterialTheme.colorScheme.onSurface)
            Text(text = "Chlorine   : ${record.tesChlorine} ppm", style = monoStyle, color = MaterialTheme.colorScheme.onSurface)
        }
        ScheduleType.TREATMENT_MINGGUAN -> {
            Text(text = "Vakum      : ${if (record.vakum > 0.0) "Ya" else "Tidak"}", style = monoStyle, color = MaterialTheme.colorScheme.onSurface)
            Text(text = "Brushing   : ${if (record.brushing > 0.0) "Ya" else "Tidak"}", style = monoStyle, color = MaterialTheme.colorScheme.onSurface)
        }
        ScheduleType.DEEP_TREATMENT -> {
            Text(text = "Kuras Bal. : ${if (record.kurasBalancing > 0.0) "Ya" else "Tidak"}", style = monoStyle, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

package com.poolmaintenance.app.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.poolmaintenance.app.R
import com.poolmaintenance.app.data.MaintenanceRecord
import com.poolmaintenance.app.data.VillaRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Denah Villa",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = "Tap villa untuk jadwalkan maintenance",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Denah area — scrollable horizontally for many villas
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Line A label
            Text(
                text = "Line A (Villa 02 – 26)",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )

            // Line A — Villa row (pools face DOWN toward partition)
            VillaLineRow(
                villaNumbers = VillaRepository.LINE_A,
                poolDirection = PoolDirection.FACING_DOWN,
                onVillaClick = { viewModel.selectVilla(it) }
            )

            // Partition divider between Line A and Line B
            PartitionDivider()

            // Line B label
            Text(
                text = "Line B (Villa 28 – 49)",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp, bottom = 4.dp)
            )

            // Line B — Villa row (pools face UP toward partition)
            VillaLineRow(
                villaNumbers = VillaRepository.LINE_B,
                poolDirection = PoolDirection.FACING_UP,
                onVillaClick = { viewModel.selectVilla(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Legend
            LegendSection()
        }
    }

    // Schedule dialog
    if (uiState.showDialog && uiState.selectedVilla != null) {
        ScheduleDialog(
            villaNumber = uiState.selectedVilla!!,
            existingRecords = uiState.existingRecords,
            isLoading = uiState.isLoading,
            onDismiss = { viewModel.dismissDialog() },
            onSchedule = { villaNum, date, obat, hcl, status ->
                viewModel.scheduleMaintenance(villaNum, date, obat, hcl, status)
            },
            onDeleteRecord = { id -> viewModel.deleteRecord(id) }
        )
    }
}

enum class PoolDirection { FACING_DOWN, FACING_UP }

@Composable
fun VillaLineRow(
    villaNumbers: List<Int>,
    poolDirection: PoolDirection,
    onVillaClick: (Int) -> Unit
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        villaNumbers.forEach { villaNum ->
            VillaCell(
                villaNumber = villaNum,
                poolDirection = poolDirection,
                onClick = { onVillaClick(villaNum) }
            )
        }
    }
}

@Composable
fun VillaCell(
    villaNumber: Int,
    poolDirection: PoolDirection,
    onClick: () -> Unit
) {
    val isEven = villaNumber % 2 == 0
    val villaBg = if (isEven) {
        Color(0xFFE3F2FD)
    } else {
        Color(0xFFE0F2F1)
    }
    val poolBg = Color(0xFF4FC3F7).copy(alpha = 0.4f)

    Column(
        modifier = Modifier
            .width(52.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (poolDirection == PoolDirection.FACING_DOWN) {
            // Villa unit on top, pool below facing the partition
            // Villa unit
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .background(villaBg, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = String.format("%02d", villaNumber),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }
            // Pool (facing down toward partition)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .background(poolBg, RoundedCornerShape(bottomStart = 2.dp, bottomEnd = 2.dp))
                    .border(
                        width = 1.dp,
                        color = Color(0xFF0288D1).copy(alpha = 0.3f),
                        shape = RoundedCornerShape(bottomStart = 2.dp, bottomEnd = 2.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Pool,
                    contentDescription = null,
                    tint = Color(0xFF01579B).copy(alpha = 0.6f),
                    modifier = Modifier.size(10.dp)
                )
            }
        } else {
            // Pool on top facing up toward partition, villa unit below
            // Pool (facing up toward partition)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .background(poolBg, RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                    .border(
                        width = 1.dp,
                        color = Color(0xFF0288D1).copy(alpha = 0.3f),
                        shape = RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Pool,
                    contentDescription = null,
                    tint = Color(0xFF01579B).copy(alpha = 0.6f),
                    modifier = Modifier.size(10.dp)
                )
            }
            // Villa unit
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .background(villaBg, RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = String.format("%02d", villaNumber),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun PartitionDivider() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(
                    color = Color(0xFF795548).copy(alpha = 0.6f),
                    shape = RoundedCornerShape(2.dp)
                )
        )
        Text(
            text = "━━━ Partisi ━━━",
            fontSize = 8.sp,
            color = Color(0xFF795548).copy(alpha = 0.8f),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(
                    color = Color(0xFF795548).copy(alpha = 0.6f),
                    shape = RoundedCornerShape(2.dp)
                )
        )
    }
}

@Composable
fun LegendSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "Keterangan",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(Color(0xFF4FC3F7).copy(alpha = 0.4f), RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Private Pool", style = MaterialTheme.typography.labelSmall)
            }
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(Color(0xFF795548).copy(alpha = 0.6f), RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Partisi pembatas pool", style = MaterialTheme.typography.labelSmall)
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Pool Line A & B saling berhadapan",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleDialog(
    villaNumber: Int,
    existingRecords: List<MaintenanceRecord>,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onSchedule: (Int, Long, Double, Double, String) -> Unit,
    onDeleteRecord: (Long) -> Unit
) {
    var showAddForm by remember { mutableStateOf(false) }
    var obatInput by remember { mutableStateOf("") }
    var hclInput by remember { mutableStateOf("") }
    var checkStatus by remember { mutableStateOf("Sudah Dicek") }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Pool,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Villa ${String.format("%02d", villaNumber)}")
            }
        },
        text = {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.CircularProgressIndicator()
                }
            } else {
                Column {
                    // Add schedule form
                    if (showAddForm) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Jadwalkan Maintenance",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = obatInput,
                                    onValueChange = { obatInput = it },
                                    label = { Text(stringResource(R.string.obat_label)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = hclInput,
                                    onValueChange = { hclInput = it },
                                    label = { Text(stringResource(R.string.hcl_label)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                TextButton(onClick = { showDatePicker = true }) {
                                    Text("Jadwal: ${dateFormatter.format(Date(selectedDate))}")
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    TextButton(onClick = { showAddForm = false }) {
                                        Text(stringResource(R.string.cancel))
                                    }
                                    TextButton(onClick = {
                                        val obat = obatInput.toDoubleOrNull() ?: 0.0
                                        val hcl = hclInput.toDoubleOrNull() ?: 0.0
                                        onSchedule(villaNumber, selectedDate, obat, hcl, checkStatus)
                                        showAddForm = false
                                        obatInput = ""
                                        hclInput = ""
                                    }) {
                                        Text(stringResource(R.string.save))
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Existing records
                    if (existingRecords.isEmpty() && !showAddForm) {
                        Text(
                            text = "Belum ada catatan untuk villa ini",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else if (existingRecords.isNotEmpty()) {
                        Text(
                            text = "Riwayat Catatan",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyColumn(modifier = Modifier.height(200.dp)) {
                            items(existingRecords) { record ->
                                ScheduleRecordItem(
                                    record = record,
                                    dateFormatter = dateFormatter,
                                    onDelete = { onDeleteRecord(record.id) }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (!showAddForm) {
                TextButton(onClick = { showAddForm = true }) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Jadwalkan")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup")
            }
        }
    )

    // Date picker
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        selectedDate = it
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun ScheduleRecordItem(
    record: MaintenanceRecord,
    dateFormatter: SimpleDateFormat,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (record.isCompleted) Color(0xFFE8F5E9) else Color(0xFFFFF8E1)
        ),
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Jadwal: ${dateFormatter.format(Date(record.scheduledDate))}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row {
                    Text(
                        text = "Obat: ${record.obatAmount}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Text(
                        text = "HCL: ${record.hclAmount}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = record.checkStatus,
                        style = MaterialTheme.typography.labelSmall,
                        color = when (record.checkStatus) {
                            "Sudah Dicek" -> Color(0xFF2E7D32)
                            else -> Color(0xFFC62828)
                        }
                    )
                    if (record.isCompleted) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "✓ Selesai",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Hapus",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

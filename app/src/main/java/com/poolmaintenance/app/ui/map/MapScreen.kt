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
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.HorizontalDivider
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

// Denah layout constants
private val CELL_WIDTH = 52.dp
private val CELL_SPACING = 3.dp
private val SLOT_WIDTH = CELL_WIDTH + CELL_SPACING // 55.dp per villa slot

@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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

        Spacer(modifier = Modifier.height(8.dp))

        // Denah — unified single scroll, Card fits content (not stretched)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .horizontalScroll(rememberScrollState())
                    .padding(8.dp)
            ) {
                // Line A — pools face DOWN toward partition
                VillaLineRow(
                    villaNumbers = VillaRepository.LINE_A,
                    poolDirection = PoolDirection.FACING_DOWN,
                    startOffset = 0,
                    onVillaClick = { viewModel.selectVilla(it) }
                )

                // Net partition between Line A and Line B
                NetPartition()

                // Line B — pools face UP, offset 3 so V49 aligns with V26
                VillaLineRow(
                    villaNumbers = VillaRepository.LINE_B,
                    poolDirection = PoolDirection.FACING_UP,
                    startOffset = 3,
                    onVillaClick = { viewModel.selectVilla(it) }
                )

                // Separator between Line B and Line C
                Spacer(modifier = Modifier.height(6.dp))
                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color(0xFF795548).copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Line C — pools face DOWN, offset 5 so V50 aligns with V30
                VillaLineRow(
                    villaNumbers = VillaRepository.LINE_C,
                    poolDirection = PoolDirection.FACING_DOWN,
                    startOffset = 5,
                    onVillaClick = { viewModel.selectVilla(it) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Legend
                LegendSection()
            }
        }
    }

    // Schedule dialog
    if (uiState.showDialog && uiState.selectedVilla != null) {
        ScheduleDialog(
            villaNumber = uiState.selectedVilla!!,
            existingRecords = uiState.existingRecords,
            isLoading = uiState.isLoading,
            onDismiss = { viewModel.dismissDialog() },
            onSchedule = { villaNum, date, granular, tablet, hcl, trusi, sodaAsh, pac, status ->
                viewModel.scheduleMaintenance(villaNum, date, granular, tablet, hcl, trusi, sodaAsh, pac, status)
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
    startOffset: Int = 0,
    onVillaClick: (Int) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(CELL_SPACING)
    ) {
        if (startOffset > 0) {
            Spacer(modifier = Modifier.width(SLOT_WIDTH * startOffset - CELL_SPACING))
        }
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
            .width(CELL_WIDTH)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (poolDirection == PoolDirection.FACING_DOWN) {
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
fun NetPartition() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFF795548).copy(alpha = 0.5f))
    )
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
                        .background(Color(0xFF795548).copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Sekat net", style = MaterialTheme.typography.labelSmall)
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

// ========== Schedule Dialog with 6 chemical fields ==========

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleDialog(
    villaNumber: Int,
    existingRecords: List<MaintenanceRecord>,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onSchedule: (Int, Long, Double, Double, Double, Double, Double, Double, String) -> Unit,
    onDeleteRecord: (Long) -> Unit
) {
    var showAddForm by remember { mutableStateOf(false) }
    var granularInput by remember { mutableStateOf("") }
    var tabletInput by remember { mutableStateOf("") }
    var hclInput by remember { mutableStateOf("") }
    var trusiInput by remember { mutableStateOf("") }
    var sodaAshInput by remember { mutableStateOf("") }
    var pacInput by remember { mutableStateOf("") }
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

                                // Chemical inputs in 2-column grid layout
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = granularInput,
                                        onValueChange = { granularInput = it },
                                        label = { Text("Granular (kg)") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )
                                    OutlinedTextField(
                                        value = tabletInput,
                                        onValueChange = { tabletInput = it },
                                        label = { Text("Tablet") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = hclInput,
                                        onValueChange = { hclInput = it },
                                        label = { Text("HCL (liter)") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )
                                    OutlinedTextField(
                                        value = trusiInput,
                                        onValueChange = { trusiInput = it },
                                        label = { Text("Trusi (kg)") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = sodaAshInput,
                                        onValueChange = { sodaAshInput = it },
                                        label = { Text("Soda Ash (kg)") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )
                                    OutlinedTextField(
                                        value = pacInput,
                                        onValueChange = { pacInput = it },
                                        label = { Text("PAC (liter)") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )
                                }
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
                                        val granular = granularInput.toDoubleOrNull() ?: 0.0
                                        val tablet = tabletInput.toDoubleOrNull() ?: 0.0
                                        val hcl = hclInput.toDoubleOrNull() ?: 0.0
                                        val trusi = trusiInput.toDoubleOrNull() ?: 0.0
                                        val sodaAsh = sodaAshInput.toDoubleOrNull() ?: 0.0
                                        val pac = pacInput.toDoubleOrNull() ?: 0.0
                                        onSchedule(villaNumber, selectedDate, granular, tablet, hcl, trusi, sodaAsh, pac, checkStatus)
                                        showAddForm = false
                                        granularInput = ""
                                        tabletInput = ""
                                        hclInput = ""
                                        trusiInput = ""
                                        sodaAshInput = ""
                                        pacInput = ""
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Jadwal: ${dateFormatter.format(Date(record.scheduledDate))}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "✓",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(onClick = onDelete, modifier = Modifier.size(20.dp)) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Hapus",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            // Chemical summary — compact 2-row grid
            Row(modifier = Modifier.fillMaxWidth()) {
                ChemLabel("Granular", "${record.granular} kg")
                Spacer(modifier = Modifier.width(8.dp))
                ChemLabel("Tablet", "${record.tablet.toInt()}")
                Spacer(modifier = Modifier.width(8.dp))
                ChemLabel("HCL", "${record.hcl} L")
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                ChemLabel("Trusi", "${record.trusi} kg")
                Spacer(modifier = Modifier.width(8.dp))
                ChemLabel("Soda Ash", "${record.sodaAsh} kg")
                Spacer(modifier = Modifier.width(8.dp))
                ChemLabel("PAC", "${record.pac} L")
            }
        }
    }
}

@Composable
fun ChemLabel(name: String, value: String) {
    Text(
        text = "$name: $value",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textDecoration = TextDecoration.None
    )
}

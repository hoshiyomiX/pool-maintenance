package com.poolmaintenance.app.ui.stats

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.poolmaintenance.app.data.VillaStats

@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel()
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
                    text = "Statistik",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = "Akumulasi penggunaan bahan kimia",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Filter chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TimeFilter.entries.forEach { filter ->
                FilterChip(
                    selected = uiState.currentFilter == filter,
                    onClick = { viewModel.setFilter(filter) },
                    label = { Text(filter.label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Summary cards — 2 rows of 3
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                StatCard(
                    title = "Granular (kg)",
                    value = "${uiState.aggregatedStats.totalGranular}",
                    icon = { Icon(Icons.Filled.Science, contentDescription = "Granular", tint = Color(0xFF1565C0)) },
                    backgroundColor = Color(0xFFE3F2FD),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Tablet",
                    value = "${uiState.aggregatedStats.totalTablet.toInt()}",
                    icon = { Icon(Icons.Filled.Science, contentDescription = "Tablet", tint = Color(0xFF6A1B9A)) },
                    backgroundColor = Color(0xFFF3E5F5),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "HCL (L)",
                    value = "${uiState.aggregatedStats.totalHcl}",
                    icon = { Icon(Icons.Filled.WaterDrop, contentDescription = "HCL", tint = Color(0xFF00695C)) },
                    backgroundColor = Color(0xFFE0F2F1),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                StatCard(
                    title = "Trusi (kg)",
                    value = "${uiState.aggregatedStats.totalTrusi}",
                    icon = { Icon(Icons.Filled.Science, contentDescription = "Trusi", tint = Color(0xFFBF360C)) },
                    backgroundColor = Color(0xFFFBE9E7),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Soda Ash (kg)",
                    value = "${uiState.aggregatedStats.totalSodaAsh}",
                    icon = { Icon(Icons.Filled.Science, contentDescription = "Soda Ash", tint = Color(0xFFE65100)) },
                    backgroundColor = Color(0xFFFFF3E0),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "PAC (L)",
                    value = "${uiState.aggregatedStats.totalPac}",
                    icon = { Icon(Icons.Filled.WaterDrop, contentDescription = "PAC", tint = Color(0xFF0277BD)) },
                    backgroundColor = Color(0xFFE1F5FE),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Per-villa breakdown
            if (uiState.perVillaStats.isNotEmpty()) {
                Text(
                    text = "Rincian per Villa",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(uiState.perVillaStats) { villaStat ->
                        VillaStatRow(villaStat)
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tidak ada data untuk periode ini",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: @Composable () -> Unit,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            icon()
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun VillaStatRow(stat: VillaStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = "Villa ${stat.villaNumber}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ChemStat("Granular", "${stat.totalGranular} kg", Color(0xFF1565C0))
                ChemStat("Tablet", "${stat.totalTablet.toInt()}", Color(0xFF6A1B9A))
                ChemStat("HCL", "${stat.totalHcl} L", Color(0xFF00695C))
            }
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ChemStat("Trusi", "${stat.totalTrusi} kg", Color(0xFFBF360C))
                ChemStat("Soda Ash", "${stat.totalSodaAsh} kg", Color(0xFFE65100))
                ChemStat("PAC", "${stat.totalPac} L", Color(0xFF0277BD))
            }
        }
    }
}

@Composable
fun ChemStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = color)
    }
}

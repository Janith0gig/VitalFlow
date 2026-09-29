package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.SyncStatus
import com.example.model.BloodBag
import com.example.model.BloodType
import com.example.ui.components.BloodBagCard
import com.example.ui.components.BloodTypeBadge
import com.example.ui.components.StatCard
import com.example.ui.theme.BloodRed
import com.example.ui.theme.CreamContainer
import com.example.ui.theme.DeepBlood
import com.example.ui.theme.OutlineColor
import com.example.ui.theme.RareBloodAccent
import com.example.ui.theme.RareBloodContainer
import com.example.ui.theme.SoftRose
import com.example.ui.theme.StatusExpired
import com.example.ui.theme.StatusExpiring
import com.example.ui.theme.StatusFresh
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmSand
import com.example.ui.theme.WarmSandDark
import com.example.ui.viewmodel.BloodBankViewModel

@Composable
fun DashboardScreen(
    viewModel: BloodBankViewModel,
    onNavigateToAdd: () -> Unit,
    onNavigateToInventory: (BloodType?) -> Unit,
    onNavigateToCrossMatch: () -> Unit,
    onNavigateToAlerts: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allBags by viewModel.allBloodBags.collectAsState()
    val expiringSoon by viewModel.expiringWithin7Days.collectAsState()
    val rareShortages by viewModel.rareBloodShortages.collectAsState()
    val rareExpiring by viewModel.rareExpiringBags.collectAsState()
    val syncStatus by viewModel.syncStatus.collectAsState()

    val totalVolumeMl = allBags.filter { it.getDaysUntilExpiration() >= 0 }.sumOf { it.quantityMl }
    val volumeLiters = String.format("%.2f", totalVolumeMl / 1000.0)
    val validUnits = allBags.count { it.getDaysUntilExpiration() >= 0 }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Clinical Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Cross-Match Tracker",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = BloodRed
                    )
                    Text(
                        text = "Blood Bank Vault & Usability Monitor",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Supabase Sync Status Badge
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(CreamContainer)
                        .border(1.dp, OutlineColor, RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .clickable { viewModel.syncNow() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when (syncStatus) {
                        is SyncStatus.Syncing -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                strokeWidth = 2.dp,
                                color = SoftRose
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Syncing...", fontSize = 11.sp, color = TextSecondary)
                        }
                        is SyncStatus.Success -> {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(StatusFresh))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Supabase Connected", fontSize = 11.sp, color = StatusFresh, fontWeight = FontWeight.Bold)
                        }
                        is SyncStatus.Error -> {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(StatusExpiring))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Local Sync", fontSize = 11.sp, color = DeepBlood, fontWeight = FontWeight.SemiBold)
                        }
                        SyncStatus.Idle -> {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(StatusFresh))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Supabase Active", fontSize = 11.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                        }
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Sync",
                        tint = TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        // Critical Alerts Banner (If any rare blood is expiring or shortages exist)
        if (rareExpiring.isNotEmpty() || rareShortages.isNotEmpty() || expiringSoon.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp))
                        .clickable { onNavigateToAlerts() }
                        .testTag("critical_alert_banner"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BloodRed)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "CRITICAL INVENTORY ALERT",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            val alertMsg = when {
                                rareExpiring.isNotEmpty() ->
                                    "${rareExpiring.size} Rare blood unit(s) nearing 7-day expiration limit!"
                                rareShortages.isNotEmpty() ->
                                    "Rare blood shortage: ${rareShortages.first().bloodType.label} below safety reserve!"
                                else ->
                                    "${expiringSoon.size} blood unit(s) expiring within 7 days."
                            }
                            Text(
                                text = alertMsg,
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 12.sp
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "View alerts",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Key Metrics 2x2 Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        title = "Active Units",
                        value = "$validUnits Bags",
                        subtitle = "Ready for match",
                        icon = Icons.Default.Opacity,
                        iconTint = BloodRed,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Available Volume",
                        value = "$volumeLiters L",
                        subtitle = "$totalVolumeMl mL cold stock",
                        icon = Icons.Default.MedicalServices,
                        iconTint = SoftRose,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        title = "Expiring (≤7d)",
                        value = "${expiringSoon.size} Bags",
                        subtitle = "Usability limit warning",
                        icon = Icons.Default.Schedule,
                        iconTint = StatusExpiring,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Rare Reserves",
                        value = "${rareShortages.size} Critical",
                        subtitle = "O-, AB- stock alert",
                        icon = Icons.Default.Diamond,
                        iconTint = RareBloodAccent,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Blood Type Distribution Grid (8 types)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(16.dp))
                    .border(1.dp, OutlineColor.copy(alpha = 0.6f), RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CreamContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Blood Group Distribution",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Target: 4 units/type",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val byType = allBags.filter { it.getDaysUntilExpiration() >= 0 }.groupBy { it.bloodType }

                    // Display all 8 blood types
                    BloodType.entries.chunked(4).forEach { rowTypes ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowTypes.forEach { type ->
                                val count = byType[type]?.size ?: 0
                                val isLow = count < 2

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isLow && type.isRare) RareBloodContainer else WarmSand.copy(alpha = 0.5f))
                                        .border(
                                            1.dp,
                                            if (isLow && type.isRare) SoftRose else OutlineColor.copy(alpha = 0.5f),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable { onNavigateToInventory(type) }
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = type.label,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = if (type.isRare) BloodRed else DeepBlood
                                        )
                                        Text(
                                            text = "$count units",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isLow) StatusExpired else TextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick Clinical Action Hub
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onNavigateToAdd,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("quick_add_blood_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = BloodRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Log Blood Bag", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }

                Button(
                    onClick = onNavigateToCrossMatch,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("quick_cross_match_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = SoftRose),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.MedicalServices, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Cross-Match", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }
        }

        // Section: Urgent Usability Warnings (Within 7 Days)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Urgent Usability Watch",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "View All (${expiringSoon.size})",
                    fontSize = 13.sp,
                    color = BloodRed,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onNavigateToAlerts() }
                )
            }
        }

        if (expiringSoon.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CreamContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusFresh)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "All blood units are safely within stability margins (>7 days remaining).",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        } else {
            items(expiringSoon.take(3), key = { it.id }) { bag ->
                BloodBagCard(
                    bag = bag,
                    onRemove = { viewModel.removeBloodBag(it) },
                    onMarkTransfused = { viewModel.markAsTransfused(it) }
                )
            }
        }
    }
}

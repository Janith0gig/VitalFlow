package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BloodBag
import com.example.model.BloodType
import com.example.ui.components.BloodBagCard
import com.example.ui.components.BloodTypeBadge
import com.example.ui.components.RareIndicator
import com.example.ui.theme.BloodRed
import com.example.ui.theme.CreamContainer
import com.example.ui.theme.DeepBlood
import com.example.ui.theme.OutlineColor
import com.example.ui.theme.RareBloodAccent
import com.example.ui.theme.RareBloodContainer
import com.example.ui.theme.SoftRose
import com.example.ui.theme.StatusExpired
import com.example.ui.theme.StatusExpiredContainer
import com.example.ui.theme.StatusExpiring
import com.example.ui.theme.StatusExpiringContainer
import com.example.ui.theme.StatusFresh
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmSand
import com.example.ui.viewmodel.BloodBankViewModel
import com.example.ui.viewmodel.RareShortageAlert
import kotlinx.coroutines.launch

@Composable
fun AlertsScreen(
    viewModel: BloodBankViewModel,
    modifier: Modifier = Modifier
) {
    val expiringSoon by viewModel.expiringWithin7Days.collectAsState()
    val expiredBags by viewModel.expiredBags.collectAsState()
    val rareShortages by viewModel.rareBloodShortages.collectAsState()
    val rareExpiring by viewModel.rareExpiringBags.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.NotificationImportant,
                            contentDescription = null,
                            tint = BloodRed,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Alerts & Usability Limits",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = BloodRed
                        )
                    }
                    Text(
                        text = "Automated watchdog for 7-day expiration and rare blood critical thresholds",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }

            // Rare Blood Shortage Section (O- and AB-)
            item {
                Text(
                    text = "Rare Blood Group Shortage Warnings",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            if (rareShortages.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CreamContainer)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusFresh)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "All rare blood groups (O-, AB-, B-) meet minimum safety inventory levels.",
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            } else {
                items(rareShortages, key = { it.bloodType.name }) { alert ->
                    RareShortageCard(
                        alert = alert,
                        onInitiateDonorDrive = {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    "Automated donor recall dispatched for ${alert.bloodType.label} donors!"
                                )
                            }
                        }
                    )
                }
            }

            // Section: Rare Units Approaching Expiration (≤ 7 Days)
            if (rareExpiring.isNotEmpty()) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = BloodRed, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Priority: Rare Blood Units Nearing Expiration",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = BloodRed
                        )
                    }
                    Text(
                        text = "Critical: Match immediately to avoid discarding rare universal/specialized units.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                items(rareExpiring, key = { "rare_${it.id}" }) { bag ->
                    BloodBagCard(
                        bag = bag,
                        onRemove = { viewModel.removeBloodBag(it) },
                        onMarkTransfused = { viewModel.markAsTransfused(it) }
                    )
                }
            }

            // Section: General Stock Expiring within 7 Days
            item {
                Text(
                    text = "Standard Units Expiring Within 7 Days (${expiringSoon.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            if (expiringSoon.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CreamContainer)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusFresh)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "No inventory units are within 7 days of expiration.",
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            } else {
                items(expiringSoon.filter { !it.isRare }, key = { "std_${it.id}" }) { bag ->
                    BloodBagCard(
                        bag = bag,
                        onRemove = { viewModel.removeBloodBag(it) },
                        onMarkTransfused = { viewModel.markAsTransfused(it) }
                    )
                }
            }

            // Section: Expired Units (Ready for Biomedical Disposal)
            if (expiredBags.isNotEmpty()) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = StatusExpired, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Expired Stock (${expiredBags.size} Units)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = StatusExpired
                        )
                    }
                    Text(
                        text = "Units past 42-day viability. Deduct from inventory to log disposal.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                items(expiredBags, key = { "exp_${it.id}" }) { bag ->
                    BloodBagCard(
                        bag = bag,
                        onRemove = { viewModel.removeBloodBag(it) },
                        onMarkTransfused = { viewModel.markAsTransfused(it) }
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun RareShortageCard(
    alert: RareShortageAlert,
    onInitiateDonorDrive: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(16.dp))
            .border(1.5.dp, SoftRose, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CreamContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BloodTypeBadge(bloodType = alert.bloodType, size = 44)

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${alert.bloodType.label} Shortage",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = BloodRed
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        RareIndicator(label = "Critical")
                    }
                    Text(
                        text = alert.bloodType.rarityDescription,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(StatusExpiredContainer)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${alert.unitsAvailable} / ${alert.criticalThreshold} min",
                        color = BloodRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = alert.message,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onInitiateDonorDrive,
                    colors = ButtonDefaults.buttonColors(containerColor = BloodRed),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("initiate_donor_drive_${alert.bloodType.label}")
                ) {
                    Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Recall Donors", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

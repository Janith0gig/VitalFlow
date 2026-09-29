package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.ui.theme.BloodRed
import com.example.ui.theme.CreamContainer
import com.example.ui.theme.DeepBlood
import com.example.ui.theme.OutlineColor
import com.example.ui.theme.SoftRose
import com.example.ui.theme.StatusFresh
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmSand
import com.example.ui.viewmodel.BloodBankViewModel
import kotlinx.coroutines.launch

@Composable
fun CrossMatchScreen(
    viewModel: BloodBankViewModel,
    modifier: Modifier = Modifier
) {
    val selectedRecipient by viewModel.selectedRecipientType.collectAsState()
    val crossMatchResult by viewModel.crossMatchResult.collectAsState()
    val (compatibleTypes, matchingBags) = crossMatchResult

    val totalMatchingVolume = matchingBags.sumOf { it.quantityMl }
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
            // Screen Header
            item {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Science,
                            contentDescription = null,
                            tint = BloodRed,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cross-Match Compatibility",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = BloodRed
                        )
                    }
                    Text(
                        text = "Real-time recipient-to-donor cross-matching engine",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }

            // Recipient Blood Group Selector Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(3.dp, RoundedCornerShape(16.dp))
                        .border(1.dp, OutlineColor.copy(alpha = 0.6f), RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CreamContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "1. Select Recipient / Patient Blood Group",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Touch a blood type to simulate emergency or elective cross-match:",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Grid of 8 Blood Types
                        BloodType.entries.chunked(4).forEach { row ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                row.forEach { type ->
                                    val isSelected = selectedRecipient == type
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isSelected) BloodRed else WarmSand.copy(alpha = 0.5f))
                                            .border(
                                                1.5.dp,
                                                if (isSelected) DeepBlood else OutlineColor,
                                                RoundedCornerShape(12.dp)
                                            )
                                            .clickable { viewModel.selectedRecipientType.value = type }
                                            .padding(vertical = 10.dp)
                                            .testTag("cross_match_recipient_${type.label}"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = type.label,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = if (isSelected) Color.White else TextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Compatibility Rules Result Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(16.dp))
                        .border(1.dp, SoftRose.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
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
                                text = "Compatible RBC Donor Types:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "For Patient: ${selectedRecipient.label}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = BloodRed
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Compatible Types Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            compatibleTypes.forEach { type ->
                                BloodTypeBadge(bloodType = type, size = 36)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Special notes
                        val clinicalNote = when (selectedRecipient) {
                            BloodType.AB_POS -> "Universal RBC Recipient: Patient can safely receive red blood cells from any blood group."
                            BloodType.O_NEG -> "Universal RBC Donor ONLY: Patient can strictly receive O- blood units. No other type is compatible."
                            BloodType.O_POS -> "Patient can receive O+ and emergency O- units."
                            BloodType.AB_NEG -> "Extremely rare type: Can receive O-, A-, B-, AB-."
                            else -> "Patient can safely receive identical ABO Rh-type, or universal O- units."
                        }

                        Text(
                            text = clinicalNote,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Real-Time Inventory Stock Ready for Transfusion
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Available In Bank Right Now",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "${matchingBags.size} compatible units (${totalMatchingVolume} mL)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = StatusFresh
                        )
                    }
                    Text(
                        text = "Sorted by soonest expiry (FEFO)",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            if (matchingBags.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CreamContainer)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = BloodRed, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Zero Compatible Units in Stock",
                                fontWeight = FontWeight.Bold,
                                color = BloodRed,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "There are no unexpired compatible bags in cold storage for ${selectedRecipient.label}. Immediate blood drive required.",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(matchingBags, key = { "cross_${it.id}" }) { bag ->
                    BloodBagCard(
                        bag = bag,
                        onRemove = { viewModel.removeBloodBag(it) },
                        onMarkTransfused = {
                            viewModel.markAsTransfused(it)
                            scope.launch {
                                snackbarHostState.showSnackbar("Blood Unit ${bag.id} marked as transfused to ${selectedRecipient.label} patient.")
                            }
                        }
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

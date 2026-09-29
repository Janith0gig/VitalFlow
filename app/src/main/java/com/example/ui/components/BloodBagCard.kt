package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.model.BloodStatus
import com.example.ui.theme.BloodRed
import com.example.ui.theme.CreamContainer
import com.example.ui.theme.DeepBlood
import com.example.ui.theme.OutlineColor
import com.example.ui.theme.SoftRose
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmSandDark

@Composable
fun BloodBagCard(
    bag: BloodBag,
    onRemove: (String) -> Unit,
    onMarkTransfused: (BloodBag) -> Unit,
    onEdit: ((BloodBag) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val daysLeft = bag.getDaysUntilExpiration()
    val isUrgent = daysLeft in 0..7

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("blood_bag_card_${bag.id}")
            .shadow(
                elevation = if (isUrgent) 6.dp else 2.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = if (isUrgent) BloodRed.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.05f),
                spotColor = if (isUrgent) BloodRed.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.1f)
            )
            .border(
                width = if (bag.isRare) 1.5.dp else 1.dp,
                color = if (bag.isRare) SoftRose else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp)
            )
            .animateContentSize(animationSpec = spring()),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(
            modifier = Modifier
                .clickable { expanded = !expanded }
                .padding(16.dp)
        ) {
            // Top Row: Blood Type Badge + Bag ID + Expiration status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BloodTypeBadge(bloodType = bag.bloodType, size = 48)

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = bag.id,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (bag.isRare) {
                            Spacer(modifier = Modifier.width(6.dp))
                            RareIndicator(label = "Rare")
                        }
                    }
                    Text(
                        text = "${bag.quantityMl} mL • ${bag.component.displayName}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }

                ExpirationStatusBadge(bag = bag)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Middle Row: Donor summary & Location
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = bag.donorName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = " (${bag.donorId})",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = SoftRose,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = bag.storageLocation.take(16),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Expand toggle indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Show less" else "Show more",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Expandable details section
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), thickness = 1.dp)

                    Spacer(modifier = Modifier.height(10.dp))

                    DetailRow(label = "Collection Date:", value = bag.collectionDate)
                    DetailRow(label = "Expiration Date:", value = "${bag.expirationDate} (42-day cycle)")
                    DetailRow(label = "Storage Vault:", value = bag.storageLocation)
                    if (bag.donorPhone.isNotBlank()) {
                        DetailRow(label = "Donor Contact:", value = bag.donorPhone)
                    }
                    if (bag.notes.isNotBlank()) {
                        DetailRow(label = "Clinical Notes:", value = bag.notes)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (bag.status != BloodStatus.TRANSFUSED) {
                            OutlinedButton(
                                onClick = { onMarkTransfused(bag) },
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .testTag("transfuse_bag_${bag.id}"),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Transfuse", fontSize = 12.sp)
                            }
                        }

                        if (onEdit != null) {
                            OutlinedButton(
                                onClick = { onEdit(bag) },
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .testTag("edit_bag_${bag.id}"),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Edit Unit", fontSize = 12.sp)
                            }
                        }

                        TextButton(
                            onClick = { showDeleteConfirm = true },
                            modifier = Modifier.testTag("remove_bag_${bag.id}"),
                            colors = ButtonDefaults.textButtonColors(contentColor = BloodRed),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Deduct / Remove", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = {
                Text(
                    text = "Deduct Blood Bag?",
                    fontWeight = FontWeight.Bold,
                    color = BloodRed
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to deduct Bag ${bag.id} (${bag.bloodType.label}, ${bag.quantityMl} mL) from the inventory? This change will synchronize with Supabase PostgreSQL.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        onRemove(bag.id)
                    },
                    modifier = Modifier.testTag("confirm_delete_button")
                ) {
                    Text("Confirm Deduct", color = BloodRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
        Text(text = value, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
    }
}

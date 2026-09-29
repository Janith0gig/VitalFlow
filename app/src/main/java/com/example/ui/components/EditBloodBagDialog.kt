package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.BloodBag
import com.example.model.BloodComponent
import com.example.model.BloodStatus
import com.example.model.BloodType
import com.example.ui.theme.BloodRed
import com.example.ui.theme.HighContrastInputText
import com.example.ui.theme.OutlineColor
import com.example.ui.theme.SoftRose

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBloodBagDialog(
    bag: BloodBag,
    isLabDark: Boolean,
    onDismiss: () -> Unit,
    onSave: (BloodBag) -> Unit
) {
    var bloodType by remember { mutableStateOf(bag.bloodType) }
    var quantityMl by remember { mutableStateOf(bag.quantityMl.toString()) }
    var component by remember { mutableStateOf(bag.component) }
    var donorName by remember { mutableStateOf(bag.donorName) }
    var donorPhone by remember { mutableStateOf(bag.donorPhone) }
    var storageLocation by remember { mutableStateOf(bag.storageLocation) }
    var status by remember { mutableStateOf(bag.status) }
    var notes by remember { mutableStateOf(bag.notes) }

    var bloodTypeExpanded by remember { mutableStateOf(false) }
    var locationExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }

    val standardPresets = listOf("350", "450", "500")
    var isCustomQuantity by remember {
        mutableStateOf(quantityMl !in standardPresets)
    }

    val storageLocations = listOf(
        "Refrigerator 1 (4°C)",
        "Refrigerator 2 (4°C)",
        "Unit A - Emergency Vault (4°C)",
        "Unit B - Surgical Cold Stock",
        "Unit C - Trauma Floor Reserve",
        "Deep Freeze Unit (-20°C)"
    )

    // Dark contrast styling
    val inputContainerColor = if (isLabDark) Color(0xFF261D1E) else Color.White
    val typedTextColor = if (isLabDark) Color.White else HighContrastInputText // Sharp #000000 on light
    val fieldBorder = if (isLabDark) MaterialTheme.colorScheme.primary else BloodRed

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = typedTextColor,
        unfocusedTextColor = typedTextColor,
        focusedContainerColor = inputContainerColor,
        unfocusedContainerColor = inputContainerColor,
        focusedBorderColor = fieldBorder,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
        cursorColor = fieldBorder,
        focusedLabelColor = fieldBorder,
        unfocusedLabelColor = if (isLabDark) Color(0xFFC7B5B1) else Color(0xFF4E3D3A)
    )

    val typedTextStyle = LocalTextStyle.current.copy(
        color = typedTextColor,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                .testTag("edit_blood_bag_dialog"),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Dialog Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BloodTypeBadge(bloodType = bloodType, size = 36)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Edit Blood Bag",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = bag.id,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_edit_dialog_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))

                // Blood Group Dropdown
                ExposedDropdownMenuBox(
                    expanded = bloodTypeExpanded,
                    onExpandedChange = { bloodTypeExpanded = !bloodTypeExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = "${bloodType.label} (${if (bloodType.isRare) "Rare Group" else "Standard"})",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Blood Group") },
                        textStyle = typedTextStyle,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = bloodTypeExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .testTag("edit_blood_type_dropdown"),
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors
                    )

                    ExposedDropdownMenu(
                        expanded = bloodTypeExpanded,
                        onDismissRequest = { bloodTypeExpanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        BloodType.entries.forEach { type ->
                            DropdownMenuItem(
                                leadingIcon = {
                                    BloodTypeBadge(bloodType = type, size = 24)
                                },
                                text = {
                                    Text(
                                        text = "${type.label} ${if (type.isRare) "★ Rare" else ""}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                onClick = {
                                    bloodType = type
                                    bloodTypeExpanded = false
                                },
                                modifier = Modifier.testTag("edit_blood_type_option_${type.label}")
                            )
                        }
                    }
                }

                // Blood Component Selector
                Column {
                    Text(
                        text = "Blood Component",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        BloodComponent.entries.take(3).forEach { comp ->
                            FilterChip(
                                selected = component == comp,
                                onClick = { component = comp },
                                label = { Text(comp.displayName.take(14), fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        }
                    }
                }

                // Quantity / Volume (mL) with Custom option
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Volume / Quantity (mL)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (isCustomQuantity) {
                            Text(
                                text = "Custom Mode",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        standardPresets.forEach { preset ->
                            val isSel = !isCustomQuantity && quantityMl == preset
                            FilterChip(
                                selected = isSel,
                                onClick = {
                                    quantityMl = preset
                                    isCustomQuantity = false
                                },
                                label = { Text("$preset mL", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SoftRose,
                                    selectedLabelColor = Color.White,
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        }

                        FilterChip(
                            selected = isCustomQuantity,
                            onClick = { isCustomQuantity = true },
                            leadingIcon = {
                                Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(13.dp))
                            },
                            label = { Text("Custom", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier.testTag("edit_quantity_custom_chip")
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Text input for quantity (dark contrast text)
                    OutlinedTextField(
                        value = quantityMl,
                        onValueChange = {
                            quantityMl = it
                            if (it !in standardPresets && it.isNotBlank()) {
                                isCustomQuantity = true
                            }
                        },
                        label = { Text(if (isCustomQuantity) "Exact Custom Amount (mL)" else "Volume (mL)") },
                        textStyle = typedTextStyle.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isCustomQuantity) 16.sp else 14.sp
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("edit_quantity_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = fieldColors
                    )

                    // Fine-tune buttons for Custom amount
                    AnimatedVisibility(visible = isCustomQuantity) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("-50", "-10", "+10", "+50").forEach { deltaStr ->
                                val delta = deltaStr.toInt()
                                OutlinedButton(
                                    onClick = {
                                        val cur = quantityMl.toIntOrNull() ?: 450
                                        quantityMl = (cur + delta).coerceAtLeast(10).toString()
                                        isCustomQuantity = true
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(4.dp)
                                ) {
                                    Text(
                                        text = if (delta > 0) "+$delta" else "$delta",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isLabDark) Color.White else HighContrastInputText
                                    )
                                }
                            }
                        }
                    }
                }

                // Donor Name & Phone
                OutlinedTextField(
                    value = donorName,
                    onValueChange = { donorName = it },
                    label = { Text("Donor Name *") },
                    textStyle = typedTextStyle,
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_donor_name_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = fieldColors
                )

                OutlinedTextField(
                    value = donorPhone,
                    onValueChange = { donorPhone = it },
                    label = { Text("Donor Phone") },
                    textStyle = typedTextStyle,
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_donor_phone_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = fieldColors
                )

                // Storage Location Selector
                ExposedDropdownMenuBox(
                    expanded = locationExpanded,
                    onExpandedChange = { locationExpanded = !locationExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = storageLocation,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Storage Location Vault") },
                        textStyle = typedTextStyle,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = locationExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors
                    )

                    ExposedDropdownMenu(
                        expanded = locationExpanded,
                        onDismissRequest = { locationExpanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        storageLocations.forEach { loc ->
                            DropdownMenuItem(
                                text = { Text(loc, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface) },
                                onClick = {
                                    storageLocation = loc
                                    locationExpanded = false
                                }
                            )
                        }
                    }
                }

                // Status Selector
                ExposedDropdownMenuBox(
                    expanded = statusExpanded,
                    onExpandedChange = { statusExpanded = !statusExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = status.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Inventory Unit Status") },
                        textStyle = typedTextStyle,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors
                    )

                    ExposedDropdownMenu(
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        BloodStatus.entries.forEach { st ->
                            DropdownMenuItem(
                                text = { Text(st.displayName, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface) },
                                onClick = {
                                    status = st
                                    statusExpanded = false
                                }
                            )
                        }
                    }
                }

                // Clinical Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Clinical Notes / Priority") },
                    textStyle = typedTextStyle,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 2,
                    colors = fieldColors
                )

                // Dialog Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Button(
                        onClick = {
                            val parsedQty = quantityMl.toIntOrNull() ?: bag.quantityMl
                            val updatedBag = bag.copy(
                                bloodType = bloodType,
                                quantityMl = parsedQty,
                                component = component,
                                donorName = donorName.ifBlank { bag.donorName },
                                donorPhone = donorPhone,
                                storageLocation = storageLocation,
                                status = status,
                                isRare = bloodType.isRare,
                                notes = notes
                            )
                            onSave(updatedBag)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("save_edit_blood_bag_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Changes", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

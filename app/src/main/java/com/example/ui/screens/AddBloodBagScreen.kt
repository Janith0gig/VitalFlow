package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BloodBag
import com.example.model.BloodComponent
import com.example.model.BloodType
import com.example.ui.components.BloodTypeBadge
import com.example.ui.components.RareIndicator
import com.example.ui.theme.BloodRed
import com.example.ui.theme.HighContrastInputText
import com.example.ui.theme.LabDarkCard
import com.example.ui.theme.LabDarkOutline
import com.example.ui.theme.LabDarkPrimary
import com.example.ui.theme.OutlineColor
import com.example.ui.theme.SoftRose
import com.example.ui.theme.StatusFresh
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmSandDark
import com.example.ui.viewmodel.BloodBankViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBloodBagScreen(
    viewModel: BloodBankViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedBloodType by viewModel.formBloodType.collectAsState()
    val quantityMl by viewModel.formQuantityMl.collectAsState()
    val selectedComponent by viewModel.formComponent.collectAsState()
    val donorName by viewModel.formDonorName.collectAsState()
    val donorId by viewModel.formDonorId.collectAsState()
    val donorPhone by viewModel.formDonorPhone.collectAsState()
    val collectionDate by viewModel.formCollectionDate.collectAsState()
    val storageLocation by viewModel.formStorageLocation.collectAsState()
    val notes by viewModel.formNotes.collectAsState()
    val isSubmitting by viewModel.formIsSubmitting.collectAsState()
    val autoExpirationDate by viewModel.formAutoExpirationDate.collectAsState()
    val errorMessage by viewModel.formErrorMessage.collectAsState()
    val successMessage by viewModel.formSuccessMessage.collectAsState()
    val isLabDark by viewModel.isLabDarkMode.collectAsState()

    var bloodTypeExpanded by remember { mutableStateOf(false) }
    var locationExpanded by remember { mutableStateOf(false) }

    // Track whether Custom exact quantity option is active
    val standardPresets = listOf("350", "450", "500")
    var isCustomQuantitySelected by remember {
        mutableStateOf(quantityMl.isNotBlank() && quantityMl !in standardPresets)
    }

    val storageLocations = listOf(
        "Refrigerator 1 (4°C)",
        "Refrigerator 2 (4°C)",
        "Unit A - Emergency Vault (4°C)",
        "Unit B - Surgical Cold Stock",
        "Unit C - Trauma Floor Reserve",
        "Deep Freeze Unit (-20°C)"
    )

    // Reusable styling for high-contrast input fields where text that you type has pitch-clear dark contrast
    val inputContainerColor = if (isLabDark) Color(0xFF261D1E) else Color.White
    val typedTextColor = if (isLabDark) Color.White else HighContrastInputText // Pitch Black (#000000) for sharp dark contrast!
    val fieldBorderFocused = if (isLabDark) LabDarkPrimary else BloodRed
    val fieldBorderUnfocused = if (isLabDark) LabDarkOutline else OutlineColor
    val labelFocusedColor = if (isLabDark) LabDarkPrimary else BloodRed
    val labelUnfocusedColor = if (isLabDark) Color(0xFFC7B5B1) else Color(0xFF4E3D3A)
    val placeholderColor = if (isLabDark) Color(0xFF8D7B78) else Color(0xFF756764)

    val customTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = typedTextColor,
        unfocusedTextColor = typedTextColor,
        focusedContainerColor = inputContainerColor,
        unfocusedContainerColor = inputContainerColor,
        focusedBorderColor = fieldBorderFocused,
        unfocusedBorderColor = fieldBorderUnfocused,
        cursorColor = fieldBorderFocused,
        focusedLabelColor = labelFocusedColor,
        unfocusedLabelColor = labelUnfocusedColor,
        focusedPlaceholderColor = placeholderColor,
        unfocusedPlaceholderColor = placeholderColor
    )

    val typedTextStyle = LocalTextStyle.current.copy(
        color = typedTextColor,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Form Title
        Column {
            Text(
                text = "Log Blood Donation",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Register blood unit into the automated cross-match inventory",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Status Messages
        errorMessage?.let { err ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = BloodRed, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = err, color = BloodRed, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        successMessage?.let { success ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusFresh, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = success, color = StatusFresh, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Main Entry Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(16.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Section 1: Blood Specification
                Text(
                    text = "Blood Unit Specifications",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Blood Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = bloodTypeExpanded,
                    onExpandedChange = { bloodTypeExpanded = !bloodTypeExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = "${selectedBloodType.label} (${if (selectedBloodType.isRare) "Rare Universal/Special" else "Standard Group"})",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Blood Group *") },
                        textStyle = typedTextStyle,
                        leadingIcon = {
                            BloodTypeBadge(bloodType = selectedBloodType, size = 32)
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = bloodTypeExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .testTag("blood_type_dropdown"),
                        shape = RoundedCornerShape(12.dp),
                        colors = customTextFieldColors
                    )

                    ExposedDropdownMenu(
                        expanded = bloodTypeExpanded,
                        onDismissRequest = { bloodTypeExpanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        BloodType.entries.forEach { type ->
                            DropdownMenuItem(
                                leadingIcon = {
                                    BloodTypeBadge(bloodType = type, size = 28)
                                },
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = type.label,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        if (type.isRare) {
                                            RareIndicator(label = "Rare")
                                        }
                                    }
                                },
                                onClick = {
                                    viewModel.formBloodType.value = type
                                    bloodTypeExpanded = false
                                },
                                modifier = Modifier.testTag("blood_type_option_${type.label}")
                            )
                        }
                    }
                }

                // Component Type Selector Chips
                Column {
                    Text(
                        text = "Blood Component",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BloodComponent.entries.take(3).forEach { comp ->
                            FilterChip(
                                selected = selectedComponent == comp,
                                onClick = {
                                    viewModel.formComponent.value = comp
                                    if (comp == BloodComponent.PACKED_RBC && quantityMl == "450") {
                                        viewModel.formQuantityMl.value = "350"
                                        isCustomQuantitySelected = false
                                    } else if (comp == BloodComponent.WHOLE_BLOOD && quantityMl == "350") {
                                        viewModel.formQuantityMl.value = "450"
                                        isCustomQuantitySelected = false
                                    }
                                },
                                label = { Text(comp.displayName.take(15), fontSize = 11.sp) },
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
                            text = "Quantity / Volume Selection",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (isCustomQuantitySelected) {
                            Text(
                                text = "Custom Amount Mode Active",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Preset volume options + CUSTOM Option Chip
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        standardPresets.forEach { preset ->
                            val isSelected = !isCustomQuantitySelected && quantityMl == preset
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    viewModel.formQuantityMl.value = preset
                                    isCustomQuantitySelected = false
                                },
                                label = { Text("$preset mL", fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SoftRose,
                                    selectedLabelColor = Color.White,
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                modifier = Modifier.testTag("quantity_preset_$preset")
                            )
                        }

                        // Dedicated "Custom" Option
                        FilterChip(
                            selected = isCustomQuantitySelected,
                            onClick = {
                                isCustomQuantitySelected = true
                                if (quantityMl in standardPresets || quantityMl.isBlank()) {
                                    // Keep or let user edit custom exact
                                }
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = "Custom",
                                    fontSize = 11.sp,
                                    fontWeight = if (isCustomQuantitySelected) FontWeight.Bold else FontWeight.SemiBold
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier.testTag("quantity_custom_chip")
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Standard or Custom volume input with dark contrast typed text
                    OutlinedTextField(
                        value = quantityMl,
                        onValueChange = {
                            viewModel.formQuantityMl.value = it
                            if (it.isNotBlank() && it !in standardPresets) {
                                isCustomQuantitySelected = true
                            }
                        },
                        label = {
                            Text(if (isCustomQuantitySelected) "Exact Custom Amount (mL) *" else "Volume (mL) *")
                        },
                        placeholder = { Text("e.g. 425") },
                        textStyle = typedTextStyle.copy(
                            fontSize = if (isCustomQuantitySelected) 18.sp else 15.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("quantity_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = customTextFieldColors
                    )

                    // If Custom is selected, show exact measurement helpers
                    AnimatedVisibility(visible = isCustomQuantitySelected) {
                        Column(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "Fine-Tune Exact Amount:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("-50", "-10", "+10", "+50").forEach { deltaStr ->
                                    val delta = deltaStr.toInt()
                                    OutlinedButton(
                                        onClick = {
                                            val current = quantityMl.toIntOrNull() ?: 450
                                            val updated = (current + delta).coerceAtLeast(10)
                                            viewModel.formQuantityMl.value = updated.toString()
                                            isCustomQuantitySelected = true
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("quantity_step_${if (delta > 0) "plus" else "minus"}_${Math.abs(delta)}"),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 6.dp)
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
                            val currentInt = quantityMl.toIntOrNull()
                            if (currentInt != null && currentInt > 0) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Exact Tare Measurement: $currentInt mL (${String.format("%.3f", currentInt / 1000.0)} Liters)",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))

                // Section 2: Donor Details
                Text(
                    text = "Donor Information",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Donor Name (High dark contrast typed text)
                OutlinedTextField(
                    value = donorName,
                    onValueChange = { viewModel.formDonorName.value = it },
                    label = { Text("Donor Full Name *") },
                    placeholder = { Text("e.g. Jane Doe") },
                    textStyle = typedTextStyle,
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("donor_name_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = customTextFieldColors
                )

                // Donor ID & Contact Phone in 2 columns
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = donorId,
                        onValueChange = { viewModel.formDonorId.value = it },
                        label = { Text("Donor ID") },
                        placeholder = { Text("DNR-...") },
                        textStyle = typedTextStyle,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("donor_id_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = customTextFieldColors
                    )

                    OutlinedTextField(
                        value = donorPhone,
                        onValueChange = { viewModel.formDonorPhone.value = it },
                        label = { Text("Phone / Contact") },
                        placeholder = { Text("+1 (555)...") },
                        textStyle = typedTextStyle,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("donor_phone_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = customTextFieldColors
                    )
                }

                // Collection Date
                OutlinedTextField(
                    value = collectionDate,
                    onValueChange = { viewModel.formCollectionDate.value = it },
                    label = { Text("Collection Date (YYYY-MM-DD)") },
                    textStyle = typedTextStyle,
                    leadingIcon = {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("collection_date_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = customTextFieldColors
                )

                // Quick buttons for collection date
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val today = LocalDate.now()
                    listOf(
                        "Today" to today.format(BloodBag.DATE_FORMATTER),
                        "Yesterday" to today.minusDays(1).format(BloodBag.DATE_FORMATTER)
                    ).forEach { (label, dateVal) ->
                        FilterChip(
                            selected = collectionDate == dateVal,
                            onClick = { viewModel.formCollectionDate.value = dateVal },
                            label = { Text(label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    }
                }

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
                        label = { Text("Storage Vault Unit") },
                        textStyle = typedTextStyle,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = locationExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        shape = RoundedCornerShape(12.dp),
                        colors = customTextFieldColors
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
                                    viewModel.formStorageLocation.value = loc
                                    locationExpanded = false
                                }
                            )
                        }
                    }
                }

                // Optional Clinical Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { viewModel.formNotes.value = it },
                    label = { Text("Clinical Notes / Instructions") },
                    placeholder = { Text("e.g. Cross-match priority, CMV negative, etc.") },
                    textStyle = typedTextStyle,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 2,
                    colors = customTextFieldColors
                )
            }
        }

        // Auto-Calculated Expiration Date Notice Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(16.dp))
                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Auto-Calculated Expiration Date",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = autoExpirationDate,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Standard 42-day whole blood / RBC shelf-life validation",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Submit Button
        Button(
            onClick = {
                viewModel.submitAddBag {
                    onNavigateBack()
                }
            },
            enabled = !isSubmitting,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .shadow(4.dp, RoundedCornerShape(14.dp))
                .testTag("save_blood_bag_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Registering Blood Bag...", fontWeight = FontWeight.Bold)
            } else {
                Icon(
                    imageVector = Icons.Default.MedicalServices,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Commit Blood Unit to Inventory",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

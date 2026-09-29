package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BloodBag
import com.example.model.BloodStatus
import com.example.model.BloodType
import com.example.ui.components.BloodBagCard
import com.example.ui.components.EditBloodBagDialog
import com.example.ui.theme.BloodRed
import com.example.ui.theme.DeepBlood
import com.example.ui.theme.HighContrastInputText
import com.example.ui.theme.OutlineColor
import com.example.ui.theme.SoftRose
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.BloodBankViewModel
import com.example.ui.viewmodel.InventorySort
import com.example.ui.viewmodel.StockLevelFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    viewModel: BloodBankViewModel,
    modifier: Modifier = Modifier
) {
    val filteredBags by viewModel.filteredBloodBags.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedBloodType by viewModel.selectedBloodTypeFilter.collectAsState()
    val selectedStatus by viewModel.selectedStatusFilter.collectAsState()
    val selectedStockLevel by viewModel.selectedStockLevelFilter.collectAsState()
    val currentSort by viewModel.selectedSort.collectAsState()
    val isLabDark by viewModel.isLabDarkMode.collectAsState()

    var showSortMenu by remember { mutableStateOf(false) }
    var editingBag by remember { mutableStateOf<BloodBag?>(null) }

    val searchContainerColor = if (isLabDark) Color(0xFF261D1E) else Color.White
    val searchTextColor = if (isLabDark) Color.White else HighContrastInputText // Sharp #000000 dark contrast!

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search & Sort Header Bar at top of Inventory
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Main Search Bar with explicit Donor ID and Blood Type guidance
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.searchQuery.value = it },
                    placeholder = {
                        Text(
                            text = "Search Donor ID (DNR-...) or Blood Type (O-, AB+)...",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    textStyle = LocalTextStyle.current.copy(
                        color = searchTextColor,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    ),
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = "Clear search",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("inventory_search_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = searchTextColor,
                        unfocusedTextColor = searchTextColor,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        focusedContainerColor = searchContainerColor,
                        unfocusedContainerColor = searchContainerColor,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Sort Button & Dropdown
                Box {
                    IconButton(
                        onClick = { showSortMenu = true },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .testTag("sort_menu_button")
                    ) {
                        Icon(Icons.Default.Sort, contentDescription = "Sort Inventory", tint = MaterialTheme.colorScheme.primary)
                    }

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        InventorySort.entries.forEach { sortOption ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = sortOption.label,
                                        fontWeight = if (currentSort == sortOption) FontWeight.Bold else FontWeight.Normal,
                                        color = if (currentSort == sortOption) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                onClick = {
                                    viewModel.selectedSort.value = sortOption
                                    showSortMenu = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Blood Type Filters Row (All, O-, O+, A-, A+, B-, B+, AB-, AB+)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // "All Types" Chip
                FilterChip(
                    selected = selectedBloodType == null,
                    onClick = { viewModel.selectedBloodTypeFilter.value = null },
                    label = { Text("All Groups", fontSize = 12.sp, fontWeight = if (selectedBloodType == null) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.testTag("filter_chip_all_types")
                )

                // 8 Blood Group Chips
                BloodType.entries.forEach { type ->
                    val isSelected = selectedBloodType == type
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            viewModel.selectedBloodTypeFilter.value = if (isSelected) null else type
                        },
                        label = {
                            Text(
                                text = if (type.isRare) "${type.label} ★" else type.label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (type.isRare) SoftRose else MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.testTag("filter_chip_${type.label.lowercase().replace("+", "_pos").replace("-", "_neg")}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Usability Status Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val statusFilters: List<Pair<BloodStatus?, String>> = listOf(
                    null to "All Status",
                    BloodStatus.FRESH to "Fresh (>7d)",
                    BloodStatus.EXPIRING_SOON to "Expiring (≤7d)",
                    BloodStatus.EXPIRED to "Expired",
                    BloodStatus.TRANSFUSED to "Transfused"
                )
                statusFilters.forEach { (statusVal, label) ->
                    val isSelected = selectedStatus == statusVal
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectedStatusFilter.value = statusVal },
                        label = { Text(label, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                            selectedLabelColor = Color.White,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        )
                    )
                }
            }
        }

        // Inventory Count & Active Filters Indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filteredBags.size} blood units in vault",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (selectedBloodType != null || selectedStatus != null || selectedStockLevel != StockLevelFilter.ALL || searchQuery.isNotEmpty()) {
                TextButton(
                    onClick = {
                        viewModel.selectedBloodTypeFilter.value = null
                        viewModel.selectedStatusFilter.value = null
                        viewModel.selectedStockLevelFilter.value = StockLevelFilter.ALL
                        viewModel.searchQuery.value = ""
                    },
                    modifier = Modifier.testTag("reset_filters_button")
                ) {
                    Text("Clear Filters", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                }
            }
        }

        // Blood Bags List (LazyColumn with Edit & Swipe-To-Delete support)
        if (filteredBags.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.InvertColors,
                            contentDescription = null,
                            tint = SoftRose,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No matching blood stock found",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Try searching by Donor ID (e.g. DNR-), Blood Type (e.g. O-, AB+), or clear active filters.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredBags, key = { it.id }) { bag ->
                    SwipeableBloodBagItem(
                        bag = bag,
                        onRemove = { viewModel.removeBloodBag(it) },
                        onMarkTransfused = { viewModel.markAsTransfused(it) },
                        onEdit = { editingBag = it }
                    )
                }
            }
        }
    }

    // Edit Blood Bag Dialog when technician clicks a unit
    editingBag?.let { bagToEdit ->
        EditBloodBagDialog(
            bag = bagToEdit,
            isLabDark = isLabDark,
            onDismiss = { editingBag = null },
            onSave = { updatedBag ->
                viewModel.updateBloodBag(updatedBag)
                editingBag = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableBloodBagItem(
    bag: BloodBag,
    onRemove: (String) -> Unit,
    onMarkTransfused: (BloodBag) -> Unit,
    onEdit: (BloodBag) -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                onRemove(bag.id)
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(BloodRed)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = "Deduct / Delete",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    ) {
        BloodBagCard(
            bag = bag,
            onRemove = onRemove,
            onMarkTransfused = onMarkTransfused,
            onEdit = onEdit
        )
    }
}

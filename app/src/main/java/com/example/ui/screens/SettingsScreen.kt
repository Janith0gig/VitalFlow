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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.SyncStatus
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
import com.example.ui.viewmodel.BloodBankViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    viewModel: BloodBankViewModel,
    modifier: Modifier = Modifier
) {
    var urlInput by remember { mutableStateOf(viewModel.config.supabaseUrl) }
    var keyInput by remember { mutableStateOf(viewModel.config.supabaseAnonKey) }
    var autoSync by remember { mutableStateOf(viewModel.config.isAutoSyncEnabled) }

    val syncStatus by viewModel.syncStatus.collectAsState()
    val isTesting by viewModel.isTestingConnection.collectAsState()
    val testResult by viewModel.testConnectionResult.collectAsState()
    val allBags by viewModel.allBloodBags.collectAsState()
    val isLabDark by viewModel.isLabDarkMode.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // High dark contrast styling for text fields
    val inputContainerColor = if (isLabDark) Color(0xFF261D1E) else Color.White
    val typedTextColor = if (isLabDark) Color.White else HighContrastInputText
    val fieldBorderFocused = if (isLabDark) LabDarkPrimary else BloodRed
    val fieldBorderUnfocused = if (isLabDark) LabDarkOutline else OutlineColor

    val settingsTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = typedTextColor,
        unfocusedTextColor = typedTextColor,
        focusedContainerColor = inputContainerColor,
        unfocusedContainerColor = inputContainerColor,
        focusedBorderColor = fieldBorderFocused,
        unfocusedBorderColor = fieldBorderUnfocused,
        cursorColor = fieldBorderFocused,
        focusedLabelColor = fieldBorderFocused,
        unfocusedLabelColor = if (isLabDark) Color(0xFFC7B5B1) else Color(0xFF4E3D3A)
    )

    val typedTextStyle = LocalTextStyle.current.copy(
        color = typedTextColor,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    )

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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Settings & Configuration",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Display modes, PostgreSQL cloud backend and local storage",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Theme & Display Mode Toggle Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(16.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .testTag("theme_settings_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = if (isLabDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Laboratory Display Theme",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (isLabDark) "High-Contrast Lab Dark Mode Active" else "Warm Sand Mode Active",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Switch(
                                checked = isLabDark,
                                onCheckedChange = { viewModel.setLabDarkMode(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.testTag("settings_theme_switch")
                            )
                        }

                        Text(
                            text = if (isLabDark)
                                "High-Contrast Dark Mode is tailored for low-light blood bank workstations, cold storage rooms, and trauma centers. It reduces eye glare while accentuating critical expiration and rare blood alerts."
                            else
                                "Warm Sand Mode provides a natural clinical atmosphere using the warm sand, cream, and blood red palette.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.setLabDarkMode(false) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (!isLabDark) BloodRed else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (!isLabDark) Color.White else MaterialTheme.colorScheme.onSurface
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.LightMode, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Warm Sand", fontSize = 12.sp)
                            }

                            Button(
                                onClick = { viewModel.setLabDarkMode(true) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isLabDark) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (isLabDark) Color.White else MaterialTheme.colorScheme.onSurface
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.DarkMode, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Lab Dark Mode", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Supabase Credentials Card
            item {
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
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Supabase PostgreSQL Backend",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Enter your Supabase Project URL and Public Anon Key below to link your live PostgreSQL database:",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // URL input (High dark contrast typed text)
                        OutlinedTextField(
                            value = urlInput,
                            onValueChange = { urlInput = it },
                            label = { Text("Supabase URL") },
                            placeholder = { Text("https://your-project.supabase.co") },
                            textStyle = typedTextStyle,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("supabase_url_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = settingsTextFieldColors
                        )

                        // Anon key input (High dark contrast typed text)
                        OutlinedTextField(
                            value = keyInput,
                            onValueChange = { keyInput = it },
                            label = { Text("Supabase Anon Key") },
                            placeholder = { Text("eyJhbGciOiJIUz...") },
                            textStyle = typedTextStyle,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("supabase_key_input"),
                            shape = RoundedCornerShape(12.dp),
                            minLines = 2,
                            colors = settingsTextFieldColors
                        )

                        // Save & Test buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.updateSupabaseSettings(urlInput.trim(), keyInput.trim())
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Supabase credentials saved!")
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("save_supabase_settings_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Save Settings", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.updateSupabaseSettings(urlInput.trim(), keyInput.trim())
                                    viewModel.testSupabaseConnection()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("test_connection_button"),
                                shape = RoundedCornerShape(10.dp),
                                enabled = !isTesting
                            ) {
                                if (isTesting) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = MaterialTheme.colorScheme.primary)
                                } else {
                                    Text("Test Connection", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }

                        // Test Result Display
                        testResult?.let { (success, msg) ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (success) Color(0xFFE8F5E9) else Color(0xFFFFEBEE))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = msg,
                                    color = if (success) StatusFresh else BloodRed,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Sync Status & Trigger Card
            item {
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
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Cloud Synchronization",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Auto-Sync on Changes",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Sync immediately when blood bags are added or deducted",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = autoSync,
                                onCheckedChange = {
                                    autoSync = it
                                    viewModel.config.isAutoSyncEnabled = it
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))

                        Button(
                            onClick = { viewModel.syncNow() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("sync_now_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = SoftRose),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.CloudSync, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sync All Stock Now", fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        // Current sync status
                        when (syncStatus) {
                            is SyncStatus.Syncing -> {
                                Text("Sync in progress...", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            is SyncStatus.Success -> {
                                Text((syncStatus as SyncStatus.Success).message, fontSize = 12.sp, color = StatusFresh)
                            }
                            is SyncStatus.Error -> {
                                Text((syncStatus as SyncStatus.Error).message, fontSize = 12.sp, color = BloodRed)
                            }
                            SyncStatus.Idle -> {
                                Text("Sync engine ready.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            // Database Maintenance & Sample Data
            item {
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
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Local Storage & Quick Reset",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "Current Local Stock: ${allBags.size} blood units registered in Room SQLite database.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedButton(
                            onClick = {
                                viewModel.resetToDemoData()
                                scope.launch {
                                    snackbarHostState.showSnackbar("Inventory reset to clinical benchmark stock!")
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reset_benchmark_stock_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reset to Clinical Benchmark Stock", color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Supabase Table Schema Reference
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Supabase PostgreSQL Table Schema",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "CREATE TABLE blood_bags (\n" +
                                   "  id TEXT PRIMARY KEY,\n" +
                                   "  blood_type TEXT NOT NULL,\n" +
                                   "  quantity_ml INTEGER NOT NULL,\n" +
                                   "  component_type TEXT NOT NULL,\n" +
                                   "  donor_name TEXT NOT NULL,\n" +
                                   "  donor_id TEXT NOT NULL,\n" +
                                   "  donor_phone TEXT,\n" +
                                   "  collection_date TEXT NOT NULL,\n" +
                                   "  expiration_date TEXT NOT NULL,\n" +
                                   "  storage_location TEXT,\n" +
                                   "  status TEXT DEFAULT 'Fresh',\n" +
                                   "  is_rare BOOLEAN DEFAULT FALSE,\n" +
                                   "  notes TEXT,\n" +
                                   "  created_at BIGINT\n" +
                                   ");",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(10.dp)
                        )
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

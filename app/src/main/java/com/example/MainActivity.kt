package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AddBloodBagScreen
import com.example.ui.screens.AlertsScreen
import com.example.ui.screens.CrossMatchScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.InventoryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.BloodRed
import com.example.ui.theme.CrossMatchTheme
import com.example.ui.viewmodel.BloodBankViewModel

enum class Screen(val label: String, val icon: ImageVector) {
    DASHBOARD("Overview", Icons.Default.Dashboard),
    INVENTORY("Inventory", Icons.Default.Inventory2),
    ADD_BAG("Log Bag", Icons.Default.Add),
    ALERTS("Alerts", Icons.Default.NotificationsActive),
    CROSS_MATCH("Cross-Match", Icons.Default.Science),
    SETTINGS("Settings", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: BloodBankViewModel = viewModel()
            val isDarkTheme by viewModel.isLabDarkMode.collectAsState()

            CrossMatchTheme(darkTheme = isDarkTheme) {
                MainApp(viewModel = viewModel, isDarkTheme = isDarkTheme)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    viewModel: BloodBankViewModel = viewModel(),
    isDarkTheme: Boolean = false
) {
    var currentScreen by remember { mutableStateOf(Screen.DASHBOARD) }

    val expiringSoon by viewModel.expiringWithin7Days.collectAsState()
    val rareShortages by viewModel.rareBloodShortages.collectAsState()
    val totalAlerts = expiringSoon.size + rareShortages.size

    BackHandler(enabled = currentScreen != Screen.DASHBOARD) {
        currentScreen = Screen.DASHBOARD
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.MedicalServices,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Cross-Match Tracker",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isDarkTheme) "High-Contrast Lab Mode" else "Warm Sand Mode",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.toggleTheme() },
                        modifier = Modifier.testTag("theme_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = if (isDarkTheme) "Switch to Warm Sand Mode" else "Switch to High-Contrast Lab Dark Mode",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("main_bottom_nav"),
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                tonalElevation = 6.dp
            ) {
                listOf(
                    Screen.DASHBOARD,
                    Screen.INVENTORY,
                    Screen.ALERTS,
                    Screen.CROSS_MATCH,
                    Screen.SETTINGS
                ).forEach { screen ->
                    val isSelected = currentScreen == screen
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentScreen = screen },
                        icon = {
                            if (screen == Screen.ALERTS && totalAlerts > 0) {
                                BadgedBox(badge = {
                                    Badge(containerColor = BloodRed) {
                                        Text("$totalAlerts", color = Color.White)
                                    }
                                }) {
                                    Icon(
                                        imageVector = screen.icon,
                                        contentDescription = screen.label
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.label
                                )
                            }
                        },
                        label = {
                            Text(
                                text = screen.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_${screen.name.lowercase()}")
                    )
                }
            }
        },
        floatingActionButton = {
            if (currentScreen == Screen.DASHBOARD || currentScreen == Screen.INVENTORY) {
                FloatingActionButton(
                    onClick = { currentScreen = Screen.ADD_BAG },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("main_add_fab")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Log Blood Donation"
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { screen ->
                when (screen) {
                    Screen.DASHBOARD -> DashboardScreen(
                        viewModel = viewModel,
                        onNavigateToAdd = { currentScreen = Screen.ADD_BAG },
                        onNavigateToInventory = { bloodType ->
                            viewModel.selectedBloodTypeFilter.value = bloodType
                            currentScreen = Screen.INVENTORY
                        },
                        onNavigateToCrossMatch = { currentScreen = Screen.CROSS_MATCH },
                        onNavigateToAlerts = { currentScreen = Screen.ALERTS }
                    )
                    Screen.INVENTORY -> InventoryScreen(
                        viewModel = viewModel
                    )
                    Screen.ADD_BAG -> AddBloodBagScreen(
                        viewModel = viewModel,
                        onNavigateBack = { currentScreen = Screen.INVENTORY }
                    )
                    Screen.ALERTS -> AlertsScreen(
                        viewModel = viewModel
                    )
                    Screen.CROSS_MATCH -> CrossMatchScreen(
                        viewModel = viewModel
                    )
                    Screen.SETTINGS -> SettingsScreen(
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

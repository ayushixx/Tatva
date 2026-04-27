package com.tatva.app

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tatva.app.ai.GemmaManager
import com.tatva.app.bluetooth.TatvaBluetoothManager
import com.tatva.app.ui.tabs.*
import com.tatva.app.ui.theme.*
import com.tatva.app.utils.NotificationHelper
import kotlinx.coroutines.launch

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Desk : Screen("desk", "Desk", Icons.AutoMirrored.Filled.Chat)
    object Beacon : Screen("beacon", "Radar", Icons.Default.Sensors)
    object Vitals : Screen("vitals", "Vitals", Icons.Default.Favorite)
    object SOS : Screen("sos", "SOS", Icons.Default.Warning)
    object Guides : Screen("guides", "Guides", Icons.AutoMirrored.Filled.MenuBook)
    object Kit : Screen("kit", "Settings", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {
    private lateinit var gemmaManager: GemmaManager
    private lateinit var bluetoothManager: TatvaBluetoothManager
    private lateinit var notificationHelper: NotificationHelper

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        gemmaManager = GemmaManager(this)
        bluetoothManager = TatvaBluetoothManager(this)
        notificationHelper = NotificationHelper(this)

        lifecycleScope.launch {
            gemmaManager.initialize()
        }

        requestPermissions()
        enableEdgeToEdge()

        setContent {
            TatvaTheme {
                val navController = rememberNavController()
                val items = listOf(Screen.Desk, Screen.Beacon, Screen.Vitals, Screen.SOS, Screen.Guides, Screen.Kit)

                Scaffold(
                    bottomBar = {
                        NavigationBar(
                            containerColor = Surface,
                            contentColor = TextSecondary,
                            tonalElevation = 8.dp
                        ) {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentRoute = navBackStackEntry?.destination?.route
                            items.forEach { screen ->
                                NavigationBarItem(
                                    icon = { 
                                        Icon(
                                            screen.icon, 
                                            contentDescription = screen.title,
                                            tint = if (currentRoute == screen.route) {
                                                if (screen is Screen.SOS) EmergencyRed else TextPrimary
                                            } else TextSecondary
                                        ) 
                                    },
                                    label = { 
                                        Text(
                                            screen.title, 
                                            fontSize = 9.sp,
                                            color = if (currentRoute == screen.route) TextPrimary else TextSecondary
                                        ) 
                                    },
                                    selected = currentRoute == screen.route,
                                    onClick = {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.startDestinationId)
                                            launchSingleTop = true
                                        }
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = Border
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Desk.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Desk.route) { DeskTab(gemmaManager) }
                        composable(Screen.Beacon.route) { BeaconTab(bluetoothManager) }
                        composable(Screen.Vitals.route) { VitalsTab() }
                        composable(Screen.SOS.route) { SosTab() }
                        composable(Screen.Guides.route) { GuidesTab(gemmaManager) }
                        composable(Screen.Kit.route) { KitTab() }
                    }
                }

                // Handle notifications for victims in background if needed
                LaunchedEffect(Unit) {
                    bluetoothManager.detectedVictims.collect { victims ->
                        if (victims.isNotEmpty()) {
                            val closest = victims.values.minByOrNull { it.distance }
                            closest?.let {
                                notificationHelper.showVictimDetected(it.distance)
                            }
                        }
                    }
                }

                LaunchedEffect(Unit) {
                    bluetoothManager.isAdvertising.collect { isAdv ->
                        if (isAdv) notificationHelper.showBeaconActive()
                        else notificationHelper.cancelBeaconActive()
                    }
                }
            }
        }
    }

    private fun requestPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.BLUETOOTH_ADVERTISE)
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        permissionLauncher.launch(permissions.toTypedArray())
    }
}

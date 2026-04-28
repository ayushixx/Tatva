package com.tatva.app

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
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
                        LiquidGlassBottomBar(
                            items = items,
                            navController = navController
                        )
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

@Composable
private fun LiquidGlassBottomBar(
    items: List<Screen>,
    navController: androidx.navigation.NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val barShape = RoundedCornerShape(32.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .shadow(
                    elevation = 22.dp,
                    shape = barShape,
                    ambientColor = Color.Black.copy(alpha = 0.34f),
                    spotColor = Color.Black.copy(alpha = 0.42f)
                )
                .clip(barShape)
                .background(Color(0xEE2A2A2E))
                .border(
                    width = 1.dp,
                    color = White.copy(alpha = 0.16f),
                    shape = barShape
                )
                .padding(6.dp)
                .height(56.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { screen ->
                val selected = currentRoute == screen.route
                val itemWeight by animateFloatAsState(
                    targetValue = if (selected) 1.65f else 0.86f,
                    animationSpec = spring(),
                    label = "navItemWeight"
                )
                LiquidGlassNavItem(
                    screen = screen,
                    selected = selected,
                    modifier = Modifier.weight(itemWeight),
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun LiquidGlassNavItem(
    screen: Screen,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val itemShape = RoundedCornerShape(26.dp)
    val indicatorColor by animateColorAsState(
        targetValue = if (selected) {
            if (screen is Screen.SOS) EmergencyRed else Color(0xFF17171A)
        } else {
            Color.Transparent
        },
        animationSpec = spring(),
        label = "navIndicatorColor"
    )
    val contentColor by animateColorAsState(
        targetValue = when {
            selected -> White
            screen is Screen.SOS -> EmergencyRed.copy(alpha = 0.88f)
            else -> TextPrimary.copy(alpha = 0.68f)
        },
        animationSpec = spring(),
        label = "navContentColor"
    )
    val horizontalPadding by animateDpAsState(
        targetValue = if (selected) 12.dp else 4.dp,
        animationSpec = spring(),
        label = "navItemPadding"
    )

    Box(
        modifier = modifier
            .height(44.dp)
            .clip(itemShape)
            .background(indicatorColor, itemShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = horizontalPadding),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = screen.icon,
                contentDescription = screen.title,
                tint = contentColor,
                modifier = Modifier.size(if (selected) 16.dp else 18.dp)
            )
            AnimatedVisibility(
                visible = selected,
                enter = fadeIn(animationSpec = spring()) + expandHorizontally(
                    animationSpec = spring(),
                    expandFrom = Alignment.Start
                ),
                exit = fadeOut(animationSpec = spring()) + shrinkHorizontally(
                    animationSpec = spring(),
                    shrinkTowards = Alignment.Start
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = screen.title,
                        color = contentColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

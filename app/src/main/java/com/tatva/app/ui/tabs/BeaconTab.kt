package com.tatva.app.ui.tabs

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tatva.app.bluetooth.TatvaBluetoothManager
import com.tatva.app.ui.components.RadarView
import com.tatva.app.ui.theme.*

@Composable
fun BeaconTab(bluetoothManager: TatvaBluetoothManager) {
    val detectedVictims by bluetoothManager.detectedVictims.collectAsState()
    val isScanning by bluetoothManager.isScanning.collectAsState()
    val isAdvertising by bluetoothManager.isAdvertising.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "RESCUE RADAR",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Radar View Container
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Surface)
                .border(1.dp, Border, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            RadarView(
                modifier = Modifier.fillMaxSize(0.9f),
                victims = detectedVictims.values.toList()
            )
            
            // Stats Overlay
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(24.dp)
            ) {
                Text(
                    detectedVictims.size.toString(),
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    color = if (detectedVictims.isNotEmpty()) EmergencyRed else TextSecondary
                )
                Text(
                    "VICTIMS DETECTED",
                    fontSize = 10.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Device List for extra interactivity
        if (detectedVictims.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(detectedVictims.values.toList()) { victim ->
                    Surface(
                        modifier = Modifier
                            .width(160.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Surface)
                            .border(1.dp, if (victim.distance < 10) EmergencyRed else Border, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        color = Color.Transparent
                    ) {
                        Column {
                            Text(victim.address, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("${String.format("%.1f", victim.distance)}m away", color = TextSecondary, fontSize = 10.sp)
                            LinearProgressIndicator(
                                progress = { (1f - (victim.distance.toFloat() / 30f)).coerceIn(0f, 1f) },
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                color = if (victim.distance < 10) EmergencyRed else PulseColor,
                                trackColor = Border
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Toggle Mode
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(Surface)
                .border(1.dp, Border, RoundedCornerShape(32.dp))
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ModeToggleItem(
                label = "SEARCH",
                isActive = isScanning,
                activeColor = ActionBlue,
                modifier = Modifier.weight(1f),
                onClick = {
                    if (isScanning) bluetoothManager.stopScanning()
                    else bluetoothManager.startScanning()
                }
            )
            
            ModeToggleItem(
                label = "BROADCAST",
                isActive = isAdvertising,
                activeColor = EmergencyRed,
                modifier = Modifier.weight(1f),
                onClick = {
                    if (isAdvertising) bluetoothManager.stopAdvertising()
                    else bluetoothManager.startAdvertising()
                }
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            if (isAdvertising) "BROADCASTING EMERGENCY SIGNAL..." else "SCANNING FOR SIGNALS...",
            fontSize = 10.sp,
            color = if (isAdvertising || isScanning) TextPrimary else TextSecondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}

@Composable
fun ModeToggleItem(
    label: String, 
    isActive: Boolean, 
    activeColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(28.dp))
            .background(if (isActive) activeColor else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isActive) Color.White else TextSecondary,
            fontWeight = FontWeight.Black,
            fontSize = 11.sp,
            letterSpacing = 1.sp
        )
    }
}

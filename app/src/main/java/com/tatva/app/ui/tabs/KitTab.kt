package com.tatva.app.ui.tabs

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.tatva.app.ui.theme.Border
import com.tatva.app.ui.theme.Surface
import com.tatva.app.ui.theme.TextPrimary
import com.tatva.app.ui.theme.TextSecondary

@Composable
fun KitTab() {
    var role by remember { mutableStateOf("Off") }
    var calibrationOffset by remember { mutableFloatStateOf(0.0f) }
    val context = LocalContext.current

    val permissions = listOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_ADVERTISE,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.POST_NOTIFICATIONS
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Device Profile Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, Border)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("DEVICE PROFILE", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Tatva Node Alpha", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Text("UID: TATVA-2024-X1", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }

        // Emergency Role Selector
        Column {
            Text("EMERGENCY ROLE", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Border, RoundedCornerShape(8.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("Off", "Victim", "Rescuer").forEach { item ->
                    Button(
                        onClick = { role = item },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (role == item) Border else Color.Transparent,
                            contentColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(item, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        // Calibration Slider
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("CALIBRATION OFFSET", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                Text("${String.format("%.2f", calibrationOffset)}m", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
            }
            Slider(
                value = calibrationOffset,
                onValueChange = { calibrationOffset = it },
                valueRange = 0f..5f,
                colors = SliderDefaults.colors(
                    thumbColor = TextPrimary,
                    activeTrackColor = TextPrimary,
                    inactiveTrackColor = Border
                )
            )
        }

        // Permissions List
        Column {
            Text("PERMISSIONS", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            permissions.forEach { permission ->
                val isGranted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(permission.split(".").last(), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Text(
                        if (isGranted) "GRANTED" else "REQUIRED",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isGranted) Color.Green else Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Save Button
        Button(
            onClick = { /* Save calibration */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = TextPrimary, contentColor = Surface),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("SAVE CALIBRATION", fontWeight = FontWeight.Bold)
        }
    }
}

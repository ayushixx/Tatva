package com.tatva.app.ui.tabs

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.tatva.app.ui.theme.ActionBlue
import com.tatva.app.ui.theme.Background
import com.tatva.app.ui.theme.EmergencyRed
import com.tatva.app.ui.theme.SuccessGreen
import com.tatva.app.ui.theme.Surface
import com.tatva.app.ui.theme.TextPrimary
import com.tatva.app.ui.theme.TextSecondary
import com.tatva.app.ui.theme.WarningOrange
import com.tatva.app.ui.theme.White

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
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SettingsHeader()

        GlassPanel {
            Row(verticalAlignment = Alignment.CenterVertically) {
                GlassIcon(Icons.Default.Memory, ActionBlue)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Tatva Node Alpha", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.height(3.dp))
                    Text("UID: TATVA-2024-X1", color = TextSecondary, fontSize = 12.sp)
                }
                StatusBadge("LOCAL", SuccessGreen)
            }
        }

        GlassPanel {
            Text("Emergency role", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color(0xB3222227))
                    .border(1.dp, White.copy(alpha = 0.09f), RoundedCornerShape(26.dp))
                    .padding(5.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                listOf("Off", "Victim", "Rescuer").forEach { item ->
                    RoleSegment(
                        label = item,
                        selected = role == item,
                        modifier = Modifier.weight(1f),
                        onClick = { role = item }
                    )
                }
            }
        }

        GlassPanel {
            Row(verticalAlignment = Alignment.CenterVertically) {
                GlassIcon(Icons.Default.Tune, WarningOrange)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Calibration offset", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Distance correction", color = TextSecondary, fontSize = 12.sp)
                }
                Text(
                    "${String.format("%.2f", calibrationOffset)}m",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Slider(
                value = calibrationOffset,
                onValueChange = { calibrationOffset = it },
                valueRange = 0f..5f,
                colors = SliderDefaults.colors(
                    thumbColor = TextPrimary,
                    activeTrackColor = ActionBlue,
                    inactiveTrackColor = White.copy(alpha = 0.12f)
                )
            )
        }

        GlassPanel {
            Row(verticalAlignment = Alignment.CenterVertically) {
                GlassIcon(Icons.Default.Bluetooth, ActionBlue)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Permissions", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Required for discovery and alerts", color = TextSecondary, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            permissions.forEach { permission ->
                val isGranted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
                PermissionRow(
                    name = permission.split(".").last().replace("_", " "),
                    granted = isGranted
                )
            }
        }

        Button(
            onClick = { /* Save calibration */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TextPrimary, contentColor = Surface),
            shape = RoundedCornerShape(28.dp),
            contentPadding = PaddingValues(horizontal = 18.dp)
        ) {
            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Save calibration", fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun SettingsHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xCC202025))
            .border(1.dp, White.copy(alpha = 0.11f), RoundedCornerShape(28.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlassIcon(Icons.Default.Settings, TextPrimary.copy(alpha = 0.9f))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("Kit Settings", color = TextPrimary, fontSize = 21.sp, fontWeight = FontWeight.Black)
            Text("Device role and rescue calibration", color = TextSecondary, fontSize = 12.sp)
        }
    }
}

@Composable
private fun GlassPanel(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xA61C1C21))
            .border(1.dp, White.copy(alpha = 0.10f), RoundedCornerShape(28.dp))
            .padding(14.dp),
        content = content
    )
}

@Composable
private fun GlassIcon(icon: ImageVector, color: Color) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(White.copy(alpha = 0.08f))
            .border(1.dp, White.copy(alpha = 0.11f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
    }
}

@Composable
private fun StatusBadge(text: String, color: Color) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.24f), RoundedCornerShape(18.dp))
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun RoleSegment(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(42.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) TextPrimary else Color.Transparent,
            contentColor = if (selected) Background else TextPrimary.copy(alpha = 0.74f)
        ),
        shape = RoundedCornerShape(22.dp),
        contentPadding = PaddingValues(horizontal = 6.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp)
    ) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}

@Composable
private fun PermissionRow(name: String, granted: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (granted) Icons.Default.Check else Icons.Default.Warning,
            contentDescription = null,
            tint = if (granted) SuccessGreen else EmergencyRed,
            modifier = Modifier.size(17.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            name,
            modifier = Modifier.weight(1f),
            color = TextSecondary,
            fontSize = 12.sp,
            maxLines = 1
        )
        Text(
            if (granted) "Granted" else "Required",
            color = if (granted) SuccessGreen else EmergencyRed,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

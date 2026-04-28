package com.tatva.app.ui.tabs

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tatva.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun VitalsTab() {
    var heartRate by remember { mutableIntStateOf(72) }
    var spo2 by remember { mutableIntStateOf(98) }
    var temp by remember { mutableFloatStateOf(36.6f) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            heartRate = (70..75).random()
            spo2 = (97..99).random()
            temp = 36.5f + (0..3).random() * 0.1f
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 16.dp, vertical = 18.dp)
    ) {
        VitalsHeader()

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(214.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(Color(0xA61C1C21))
                .border(1.dp, White.copy(alpha = 0.10f), RoundedCornerShape(30.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    GlassVitalIcon(Icons.Default.Favorite, EmergencyRed)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Heart rate", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Live rhythm", color = TextSecondary, fontSize = 12.sp)
                    }
                }
                Text(
                    "$heartRate BPM",
                    color = TextPrimary,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0x8C111116))
                    .border(1.dp, White.copy(alpha = 0.08f), RoundedCornerShape(22.dp))
                    .padding(12.dp)
            ) {
                ECGGraph(modifier = Modifier.fillMaxSize(), color = PulseColor)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            VitalCard(
                label = "SpO2",
                value = "$spo2%",
                icon = Icons.Default.WaterDrop,
                color = ActionBlue,
                modifier = Modifier.weight(1f)
            )
            VitalCard(
                label = "TEMP",
                value = String.format("%.1f°C", temp),
                icon = Icons.Default.Thermostat,
                color = WarningOrange,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0xA61C1C21))
                .border(1.dp, White.copy(alpha = 0.10f), RoundedCornerShape(28.dp))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusDot(SuccessGreen)
                Spacer(modifier = Modifier.width(8.dp))
                Text("AI preliminary assessment", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Patient vitals are currently stable. Normal sinus rhythm detected. Oxygen saturation is within optimal range.",
                color = TextPrimary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun VitalsHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xCC202025))
            .border(1.dp, White.copy(alpha = 0.11f), RoundedCornerShape(28.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlassVitalIcon(Icons.Default.MonitorHeart, PulseColor)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("Patient Vitals", color = TextPrimary, fontSize = 21.sp, fontWeight = FontWeight.Black)
            Text("Live patient monitoring", color = TextSecondary, fontSize = 12.sp)
        }
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .background(SuccessGreen.copy(alpha = 0.12f))
                .border(1.dp, SuccessGreen.copy(alpha = 0.24f), RoundedCornerShape(18.dp))
                .padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatusDot(SuccessGreen)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Stable", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun VitalCard(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .height(86.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xA61C1C21))
            .border(1.dp, White.copy(alpha = 0.10f), RoundedCornerShape(28.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlassVitalIcon(icon, color)
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(label, color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Spacer(modifier = Modifier.height(3.dp))
            Text(value, color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 20.sp, maxLines = 1)
        }
    }
}

@Composable
private fun GlassVitalIcon(icon: ImageVector, color: Color) {
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
private fun StatusDot(color: Color) {
    Box(
        modifier = Modifier
            .size(7.dp)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
fun ECGGraph(modifier: Modifier = Modifier, color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "ecg")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val path = Path()
        
        path.moveTo(0f, height / 2)
        
        val points = 100
        for (i in 0..points) {
            val x = (i.toFloat() / points) * width
            // Simulate ECG pattern: Flat line with spikes
            val normalizedX = (i.toFloat() / points + phase) % 1f
            val y = if (normalizedX in 0.45f..0.55f) {
                val spike = when {
                    normalizedX < 0.48f -> (normalizedX - 0.45f) / 0.03f * -20f
                    normalizedX < 0.52f -> -20f + (normalizedX - 0.48f) / 0.04f * 60f
                    else -> 40f - (normalizedX - 0.52f) / 0.03f * 40f
                }
                height / 2 + spike
            } else {
                height / 2 + (Math.random().toFloat() - 0.5f) * 2f // Noise
            }
            path.lineTo(x, y)
        }
        
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

package com.tatva.app.ui.tabs

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tatva.app.ui.theme.*

@Composable
fun SosTab() {
    var isActivated by remember { mutableStateOf(false) }
    val infiniteTransition = rememberInfiniteTransition(label = "sos")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "EMERGENCY SOS",
            style = MaterialTheme.typography.headlineMedium,
            color = if (isActivated) EmergencyRed else TextPrimary,
            fontWeight = FontWeight.Black
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            if (isActivated) "EMERGENCY SIGNAL ACTIVE" else "HOLD TO SEND EMERGENCY SIGNAL",
            color = TextSecondary,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(64.dp))

        // SOS Button
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(if (isActivated) EmergencyRed.copy(alpha = 0.2f) else Surface)
                .border(2.dp, if (isActivated) EmergencyRed else Border, CircleShape)
                .clickable { isActivated = !isActivated },
            contentAlignment = Alignment.Center
        ) {
            // Pulsing circles when active
            if (isActivated) {
                Box(
                    modifier = Modifier
                        .size(200.dp * scale)
                        .clip(CircleShape)
                        .background(EmergencyRed.copy(alpha = 0.1f))
                )
            }

            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(if (isActivated) EmergencyRed else Surface)
                    .border(4.dp, if (isActivated) Color.White.copy(alpha = 0.5f) else Border, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "SOS",
                    color = if (isActivated) Color.White else EmergencyRed,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(64.dp))

        // Quick Contacts
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            EmergencyActionCard("POLICE", "100", Modifier.weight(1f))
            EmergencyActionCard("AMBULANCE", "102", Modifier.weight(1f))
            EmergencyActionCard("FIRE", "101", Modifier.weight(1f))
        }
    }
}

@Composable
fun EmergencyActionCard(label: String, number: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Surface)
            .border(1.dp, Border, RoundedCornerShape(12.dp))
            .clickable { /* Call */ }
            .padding(12.dp),
        color = Color.Transparent
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
            Text(number, fontSize = 18.sp, color = TextPrimary, fontWeight = FontWeight.Black)
        }
    }
}

package com.tatva.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.tatva.app.bluetooth.VictimDevice
import com.tatva.app.ui.theme.Border
import com.tatva.app.ui.theme.TextPrimary
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RadarView(
    modifier: Modifier = Modifier,
    victims: List<VictimDevice> = emptyList()
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val scanPulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse"
    )

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val maxRadius = size.width.coerceAtMost(size.height) / 2 * 0.85f
            
            // Draw background circles
            val rings = listOf(0.2f, 0.4f, 0.6f, 0.8f, 1.0f)
            rings.forEach { ratio ->
                drawCircle(
                    color = Border.copy(alpha = 0.5f),
                    radius = maxRadius * ratio,
                    center = center,
                    style = Stroke(width = 1f)
                )
            }

            // Pulsing ring
            drawCircle(
                color = TextPrimary.copy(alpha = 1f - scanPulse),
                radius = maxRadius * scanPulse,
                center = center,
                style = Stroke(width = 2f)
            )

            // Crosshairs
            drawLine(
                color = Border,
                start = Offset(center.x - maxRadius, center.y),
                end = Offset(center.x + maxRadius, center.y),
                strokeWidth = 1f
            )
            drawLine(
                color = Border,
                start = Offset(center.x, center.y - maxRadius),
                end = Offset(center.x, center.y + maxRadius),
                strokeWidth = 1f
            )

            // Rotating sweep gradient
            rotate(rotation, center) {
                drawArc(
                    brush = Brush.sweepGradient(
                        0f to Color.Transparent,
                        0.25f to TextPrimary.copy(alpha = 0.4f),
                        0.5f to Color.Transparent,
                        center = center
                    ),
                    startAngle = 0f,
                    sweepAngle = 90f,
                    useCenter = true,
                    topLeft = Offset(center.x - maxRadius, center.y - maxRadius),
                    size = androidx.compose.ui.geometry.Size(maxRadius * 2, maxRadius * 2)
                )
                
                // Sweep leading line
                drawLine(
                    color = TextPrimary,
                    start = center,
                    end = Offset(center.x + maxRadius, center.y),
                    strokeWidth = 2f
                )
            }

            // Victims
            victims.forEach { victim ->
                val angle = (victim.address.hashCode() % 360).toDouble()
                val rad = Math.toRadians(angle)
                val distanceScale = (victim.distance.coerceAtMost(30.0) / 30.0).toFloat()
                val victimRadius = maxRadius * distanceScale
                
                val victimOffset = Offset(
                    center.x + victimRadius * cos(rad).toFloat(),
                    center.y + victimRadius * sin(rad).toFloat()
                )

                // Victim Glow
                drawCircle(
                    color = Color.White.copy(alpha = 0.3f),
                    radius = 15f + 5f * sin(System.currentTimeMillis() / 200.0).toFloat(),
                    center = victimOffset
                )
                
                drawCircle(
                    color = Color.White,
                    radius = 6f,
                    center = victimOffset
                )
            }
        }
    }
}

package com.tatva.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = White,
    secondary = TextSecondary,
    background = Background,
    surface = Surface,
    onPrimary = Background,
    onSecondary = White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = Border
)

@Composable
fun TatvaTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

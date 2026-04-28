package com.tatva.app.ui.tabs

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.animation.core.spring
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tatva.app.ai.GemmaManager
import com.tatva.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun GuidesTab(gemmaManager: GemmaManager) {
    var searchQuery by remember { mutableStateOf("") }
    var guideResponse by remember { mutableStateOf("") }
    var isThinking by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val categories = listOf("Bleeding", "Burns", "Fractures", "Shock", "CPR")
    val curatedPrompts = listOf(
        "How to treat a burn?",
        "How to stop bleeding?",
        "Signs of a fracture?",
        "What to do in shock?",
        "How to perform CPR?"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 16.dp, vertical = 18.dp)
    ) {
        GuidesHeader()

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(30.dp))
                .background(Color(0xE6222227))
                .border(1.dp, White.copy(alpha = 0.12f), RoundedCornerShape(30.dp))
                .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ask a medical question", color = TextSecondary, fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = TextPrimary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(24.dp),
                singleLine = true
            )
            if (searchQuery.isNotEmpty()) {
                FilledIconButton(
                    onClick = {
                        if (!isThinking) {
                            scope.launch {
                                isThinking = true
                                guideResponse = ""
                                gemmaManager.ask(searchQuery).collect { chunk ->
                                    isThinking = false
                                    guideResponse += chunk
                                }
                            }
                        }
                    },
                    modifier = Modifier.size(48.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = TextPrimary,
                        contentColor = Background
                    ),
                    enabled = !isThinking
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                val isSelected = searchQuery == category
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(22.dp))
                        .background(if (isSelected) TextPrimary else Color(0xB3222227))
                        .border(
                            1.dp,
                            if (isSelected) TextPrimary else White.copy(alpha = 0.09f),
                            RoundedCornerShape(22.dp)
                        )
                        .clickable { searchQuery = if (isSelected) "" else category }
                        .padding(horizontal = 16.dp, vertical = 9.dp)
                ) {
                    Text(
                        category,
                        color = if (isSelected) Background else TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        AnimatedVisibility(
            visible = guideResponse.isNotEmpty() || isThinking,
            enter = expandVertically(animationSpec = spring()) + fadeIn(),
            exit = shrinkVertically(animationSpec = spring()) + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xA61C1C21))
                    .border(
                        1.dp,
                        if (isThinking) PulseColor.copy(alpha = 0.45f) else White.copy(alpha = 0.10f),
                        RoundedCornerShape(28.dp)
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        GuideGlassIcon(Icons.Default.LocalHospital, ActionBlue)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Tatva Guidance", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text(if (isThinking) "Preparing response" else "Offline answer", color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                    if (!isThinking) {
                        IconButton(onClick = { guideResponse = "" }, modifier = Modifier.size(34.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                if (isThinking) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .clip(CircleShape),
                        color = PulseColor,
                        trackColor = White.copy(alpha = 0.10f)
                    )
                } else {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        guideResponse,
                        color = TextPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 24.sp
                    )
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(WarningOrange)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Common emergencies", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 4.dp)
        ) {
            items(curatedPrompts.filter { it.contains(searchQuery, ignoreCase = true) }) { prompt ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xA61C1C21))
                        .border(1.dp, White.copy(alpha = 0.10f), RoundedCornerShape(24.dp))
                        .clickable {
                            if (!isThinking) {
                                scope.launch {
                                    isThinking = true
                                    guideResponse = ""
                                    gemmaManager.ask(prompt).collect { chunk ->
                                        isThinking = false
                                        guideResponse += chunk
                                    }
                                }
                            }
                        }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GuideGlassIcon(Icons.AutoMirrored.Filled.MenuBook, ActionBlue)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        prompt,
                        color = TextPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun GuidesHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xCC202025))
            .border(1.dp, White.copy(alpha = 0.11f), RoundedCornerShape(28.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GuideGlassIcon(Icons.AutoMirrored.Filled.MenuBook, ActionBlue)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("Medical Guides", color = TextPrimary, fontSize = 21.sp, fontWeight = FontWeight.Black)
            Text("Search offline emergency instructions", color = TextSecondary, fontSize = 12.sp)
        }
    }
}

@Composable
private fun GuideGlassIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
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

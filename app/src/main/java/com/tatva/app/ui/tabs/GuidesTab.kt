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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.animation.core.spring
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Close
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
            .padding(16.dp)
    ) {
        Text(
            "MEDICAL GUIDES",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            letterSpacing = 2.sp
        )
        
        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Ask anything...", color = TextSecondary) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(
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
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = TextPrimary)
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Surface,
                unfocusedContainerColor = Surface,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = TextPrimary,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Categories
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                val isSelected = searchQuery == category
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) TextPrimary else Surface)
                        .border(1.dp, if (isSelected) TextPrimary else Border, RoundedCornerShape(20.dp))
                        .clickable { searchQuery = if (isSelected) "" else category }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        category, 
                        color = if (isSelected) Background else TextPrimary, 
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Response Area
        AnimatedVisibility(
            visible = guideResponse.isNotEmpty() || isThinking,
            enter = expandVertically(animationSpec = spring()) + fadeIn(),
            exit = shrinkVertically(animationSpec = spring()) + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isThinking) PulseColor else Border),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("TATVA GUIDANCE", style = MaterialTheme.typography.labelSmall, color = ActionBlue, fontWeight = FontWeight.Black)
                        if (!isThinking) {
                            IconButton(onClick = { guideResponse = "" }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                    
                    if (isThinking) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp).clip(CircleShape),
                            color = PulseColor,
                            trackColor = Border
                        )
                    } else {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            guideResponse, 
                            color = TextPrimary, 
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 24.sp
                        )
                    }
                }
            }
        }

        // Curated Prompts
        Text("COMMON EMERGENCIES", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(curatedPrompts.filter { it.contains(searchQuery, ignoreCase = true) }) { prompt ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
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
                        },
                    color = Surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Border),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
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
}

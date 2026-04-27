package com.tatva.app.ui.tabs

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tatva.app.ai.GemmaManager
import com.tatva.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DeskTab(gemmaManager: GemmaManager) {
    var inputText by remember { mutableStateOf("") }
    val chatMessages = remember { mutableStateListOf<Pair<String, Boolean>>() }
    val scope = rememberCoroutineScope()
    val isAiReady by gemmaManager.isReady.collectAsState()
    val aiStatus by gemmaManager.status.collectAsState()
    val listState = rememberLazyListState()
    var isTyping by remember { mutableStateOf(false) }

    LaunchedEffect(chatMessages.size, isTyping) {
        if (chatMessages.isNotEmpty() || isTyping) {
            listState.animateScrollToItem(if (isTyping) chatMessages.size else chatMessages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        // Status Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatusCard("HEART RATE", "72 BPM", PulseColor, Modifier.weight(1f))
            StatusCard("SIGNAL", "CONNECTED", SuccessGreen, Modifier.weight(1f))
            StatusCard("AI STATUS", aiStatus, ActionBlue, Modifier.weight(1.2f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Chat Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Surface)
                .border(1.dp, Border, RoundedCornerShape(16.dp))
        ) {
            // Background Glow
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, PulseColor.copy(alpha = 0.05f))
                        )
                    )
            )
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(chatMessages) { (text, isUser) ->
                    ChatBubble(text, isUser)
                }
                
                if (isTyping) {
                    item {
                        TypingIndicator()
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Input Area
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ask anything...", color = TextSecondary, fontSize = 14.sp) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Surface,
                    unfocusedContainerColor = Surface,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = TextPrimary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(24.dp),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            FilledIconButton(
                onClick = {
                    if (inputText.isNotBlank() && isAiReady && !isTyping) {
                        val question = inputText
                        chatMessages.add(question to true)
                        inputText = ""
                        isTyping = true
                        scope.launch {
                            var fullResponse = ""
                            chatMessages.add("" to false) // Add empty message for bot
                            val botMessageIndex = chatMessages.size - 1
                            
                            gemmaManager.ask(question).collect { chunk ->
                                isTyping = false
                                fullResponse += chunk
                                chatMessages[botMessageIndex] = fullResponse to false
                            }
                        }
                    }
                },
                modifier = Modifier.size(48.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (inputText.isNotBlank() && isAiReady) TextPrimary else Surface,
                    contentColor = if (inputText.isNotBlank() && isAiReady) Background else TextSecondary
                ),
                enabled = isAiReady && !isTyping
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
            }
        }
    }
}

@Composable
fun StatusCard(label: String, value: String, accentColor: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Surface)
            .border(1.dp, Border, RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontSize = 8.sp, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.bodySmall, color = accentColor, fontWeight = FontWeight.Black)
    }
}

@Composable
fun ChatBubble(text: String, isUser: Boolean) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = expandVertically() + fadeIn()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            Surface(
                color = if (isUser) TextPrimary else Border,
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isUser) 16.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 16.dp
                ),
                modifier = Modifier.widthIn(max = 280.dp)
            ) {
                Text(
                    text = text,
                    modifier = Modifier.padding(12.dp),
                    color = if (isUser) Background else TextPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Border)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(3) { index ->
                    val infiniteTransition = rememberInfiniteTransition(label = "dot")
                    val alpha by infiniteTransition.animateFloat(
                        initialValue = 0.2f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(600, delayMillis = index * 200),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "alpha"
                    )
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(TextPrimary.copy(alpha = alpha))
                    )
                }
            }
        }
    }
}

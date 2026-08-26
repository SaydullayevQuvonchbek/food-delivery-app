package com.fooddelivery.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fooddelivery.components.AppHeaderBar
import com.fooddelivery.data.repository.FoodDeliveryRepository
import com.fooddelivery.domain.models.ChatMessage
import com.fooddelivery.theme.*

@Composable
fun ChatListScreen(
    repository: FoodDeliveryRepository,
    onNavigateToChat: (Long) -> Unit
) {
    val courier = repository.currentCourier

    val chatList = listOf(
        Triple("Geopart Etdsien", "Your Order Just Arrived!", "13.47"),
        Triple("Stevano Clirover", "Your Order Just Arrived!", "11.23"),
        Triple("Elisia Justin", "Your Order Just Arrived!", "11.23"),
        Triple("Geopart Etdsien", "Your Order Just Arrived!", "13.47"),
        Triple("Stevano Clirover", "Your Order Just Arrived!", "11.23"),
        Triple("Elisia Justin", "Your Order Just Arrived!", "11.23")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        AppHeaderBar(title = "Chat List")

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = "All Message",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(Modifier.height(14.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                items(chatList) { item ->
                    ChatListItemRow(
                        name = item.first,
                        message = item.second,
                        time = item.third,
                        onClick = { onNavigateToChat(courier.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ChatListItemRow(
    name: String,
    message: String,
    time: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Color(0xFFDDD5CA)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "ðŸ‘¨â€ðŸ³", fontSize = 24.sp)
        }

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = time,
                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
            )
            Spacer(Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(PrimaryOrange, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "3",
                    style = TextStyle(color = TextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
fun ChatScreen(
    courierId: Long,
    repository: FoodDeliveryRepository,
    onBackClick: () -> Unit,
    onNavigateToCall: (Long) -> Unit
) {
    val courier = repository.currentCourier
    val messages by repository.chatMessages.collectAsState()
    var inputText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        // Chat Header with Avatar, Name, Call button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(SurfaceLight, CircleShape)
                    .border(1.dp, BorderLight, CircleShape)
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "â€¹", style = MaterialTheme.typography.headlineMedium)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE5DDD5)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "ðŸ‘¨â€ðŸ³", fontSize = 18.sp)
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Stevano Clirover",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(SurfaceLight, CircleShape)
                    .border(1.dp, BorderLight, CircleShape)
                    .clickable { onNavigateToCall(courier.id) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Call,
                    contentDescription = "Call",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        HorizontalDivider(color = BorderLight)

        // Chat Message List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(messages) { message ->
                MessageBubble(message = message)
            }
        }

        // Bottom Input Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .background(SurfaceLight, RoundedCornerShape(26.dp))
                    .border(1.dp, BorderLight, RoundedCornerShape(26.dp))
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "ðŸ˜Š", fontSize = 20.sp, modifier = Modifier.clickable { /* Emoji */ })
                Spacer(Modifier.width(10.dp))

                Box(modifier = Modifier.weight(1f)) {
                    if (inputText.isEmpty()) {
                        Text(
                            text = "Type something...",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted)
                        )
                    }
                    androidx.compose.foundation.text.BasicTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary)
                    )
                }

                Text(text = "ðŸ“Ž", fontSize = 18.sp, modifier = Modifier.clickable { /* Attachment */ })
            }

            Spacer(Modifier.width(10.dp))

            // Send Button (Orange)
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(PrimaryOrange, CircleShape)
                    .clickable {
                        if (inputText.isNotBlank()) {
                            repository.sendChatMessage(inputText)
                            inputText = ""
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Send,
                    contentDescription = "Send",
                    tint = TextWhite,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
fun MessageBubble(message: ChatMessage) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isFromMe) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (message.isFromMe) 18.dp else 4.dp,
                        bottomEnd = if (message.isFromMe) 4.dp else 18.dp
                    )
                )
                .background(if (message.isFromMe) PrimaryOrange else SurfaceLight)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = if (message.isFromMe) TextWhite else TextPrimary
                )
            )
        }

        Spacer(Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = message.timestamp,
                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
            )
            if (message.isFromMe) {
                Spacer(Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Delivered",
                    tint = PrimaryOrange,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun AudioCallScreen(
    courierId: Long,
    onEndCall: () -> Unit
) {
    var isMuted by remember { mutableStateOf(false) }
    var isSpeakerOn by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF323639), Color(0xFF141618))
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(100.dp))

        Text(
            text = "Stevano Clirover",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(Modifier.height(14.dp))

        // Timer badge
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White.copy(alpha = 0.15f)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(DangerRed, CircleShape)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "03:45",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Spacer(Modifier.weight(1f))

        // Action Buttons Row (Mute, End Call, Speaker)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mute Button
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    .clickable { isMuted = !isMuted },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isMuted) Icons.Filled.MicOff else Icons.Filled.Mic,
                    contentDescription = "Mute",
                    tint = TextWhite,
                    modifier = Modifier.size(26.dp)
                )
            }

            // End Call Red Button
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .background(DangerRed, CircleShape)
                    .clickable { onEndCall() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.CallEnd,
                    contentDescription = "End Call",
                    tint = TextWhite,
                    modifier = Modifier.size(36.dp)
                )
            }

            // Speaker Button
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    .clickable { isSpeakerOn = !isSpeakerOn },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.VolumeUp,
                    contentDescription = "Speaker",
                    tint = TextWhite,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        Spacer(Modifier.height(50.dp))
    }
}
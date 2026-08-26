package com.fooddelivery.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fooddelivery.components.AppHeaderBar
import com.fooddelivery.data.repository.FoodDeliveryRepository
import com.fooddelivery.domain.models.ChatMessage
import com.fooddelivery.theme.*
import kotlinx.coroutines.launch

@Composable
fun ChatListScreen(
    repository: FoodDeliveryRepository,
    onNavigateToChat: (Long) -> Unit
) {
    val conversations by repository.conversations.collectAsState()

    // Suhbatlar ro'yxati serverdan olinadi (ilgari qattiq yozilgan ikkita element edi)
    LaunchedEffect(Unit) { repository.loadConversations() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .statusBarsPadding()
    ) {
        AppHeaderBar(title = "Chat List")

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = "Muloqotlar",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(Modifier.height(14.dp))

            if (conversations.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "💬", fontSize = 44.sp)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "Hozircha suhbatlar yo'q",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 90.dp)
                ) {
                    items(conversations, key = { it.id }) { chat ->
                        ChatListItemRow(
                            name = chat.title,
                            message = chat.lastMessage,
                            time = chat.lastMessageAt,
                            unreadCount = chat.unreadCount,
                            onClick = { onNavigateToChat(chat.id) }
                        )
                    }
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
    unreadCount: Int = 0,
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
                .background(PrimaryOrangeSoft),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                tint = PrimaryOrange,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 1
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                maxLines = 1
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = time,
                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
            )
            if (unreadCount > 0) {
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(PrimaryOrange, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (unreadCount > 9) "9+" else "$unreadCount",
                        style = TextStyle(color = TextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatScreen(
    chatId: Long,
    repository: FoodDeliveryRepository,
    onBackClick: () -> Unit,
    onNavigateToCall: (String) -> Unit
) {
    val messages by repository.chatMessages.collectAsState()
    val conversations by repository.conversations.collectAsState()
    var inputText by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val conversation = conversations.find { it.id == chatId }
    val title = conversation?.title ?: "Suhbat"

    LaunchedEffect(chatId) { repository.openChat(chatId) }

    // Yangi xabar kelganda oxirgi xabarga tushamiz
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.lastIndex)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .statusBarsPadding()
            .imePadding()  // Klaviatura ochilganda kiritish maydoni berkilib qolmaydi
            .navigationBarsPadding()
    ) {
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
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f).padding(horizontal = 10.dp)) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(PrimaryOrangeSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        tint = PrimaryOrange,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1
                )
            }

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(SurfaceLight, CircleShape)
                    .border(1.dp, BorderLight, CircleShape)
                    .clickable { onNavigateToCall(title) },
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

        if (messages.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Suhbatni boshlang 👋",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                )
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(messages, key = { it.id }) { message ->
                    MessageBubble(message = message)
                }
            }
        }

        errorMessage?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall.copy(color = DangerRed),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 52.dp)
                    .background(SurfaceLight, RoundedCornerShape(26.dp))
                    .border(1.dp, BorderLight, RoundedCornerShape(26.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (inputText.isEmpty()) {
                        Text(
                            text = "Xabar yozing...",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted)
                        )
                    }
                    androidx.compose.foundation.text.BasicTextField(
                        value = inputText,
                        onValueChange = {
                            inputText = it
                            errorMessage = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary)
                    )
                }
            }

            Spacer(Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        if (inputText.isBlank() || isSending) PrimaryOrange.copy(alpha = 0.5f) else PrimaryOrange,
                        CircleShape
                    )
                    .clickable(enabled = inputText.isNotBlank() && !isSending) {
                        val text = inputText
                        inputText = ""
                        scope.launch {
                            isSending = true
                            repository.sendChatMessage(text)
                                .onFailure {
                                    errorMessage = it.message
                                    inputText = text  // Yuborilmagan xabar yo'qolmaydi
                                }
                            isSending = false
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isSending) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = TextWhite,
                        strokeWidth = 2.dp
                    )
                } else {
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
                    contentDescription = if (message.isRead) "O'qildi" else "Yuborildi",
                    tint = if (message.isRead) PrimaryOrange else TextMuted,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun AudioCallScreen(
    contactName: String,
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
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(100.dp))

        Text(
            text = contactName,
            style = MaterialTheme.typography.headlineMedium.copy(
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(Modifier.height(14.dp))

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
                    text = "Ulanmoqda...",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
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

            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    .clickable { isSpeakerOn = !isSpeakerOn },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isSpeakerOn) Icons.Filled.VolumeUp else Icons.Filled.VolumeUp,
                    contentDescription = "Speaker",
                    tint = if (isSpeakerOn) TextWhite else TextMuted,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        Spacer(Modifier.height(50.dp))
    }
}

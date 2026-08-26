package com.fooddelivery.presentation.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fooddelivery.components.AppHeaderBar
import com.fooddelivery.data.repository.FoodDeliveryRepository
import com.fooddelivery.domain.models.AppNotificationItem
import com.fooddelivery.theme.*

@Composable
fun NotificationScreen(
    repository: FoodDeliveryRepository,
    onBackClick: () -> Unit
) {
    val notifications by repository.notifications.collectAsState()

    // Bildirishnomalar serverdan olinadi va ochilganda o'qilgan deb belgilanadi
    LaunchedEffect(Unit) {
        repository.loadNotifications()
        repository.markNotificationsRead()
    }

    // Guruhlash sana bo'yicha (ilgari "Today"/"Yesterday" matni bilan solishtirilardi va
    // serverdan kelgan sanalar hech qachon mos kelmasdi - ekran bo'sh qolardi)
    val grouped = remember(notifications) { notifications.groupBy { it.timeAgo.ifBlank { "Boshqa" } } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .statusBarsPadding()
    ) {
        AppHeaderBar(
            title = "Notification",
            onBackClick = onBackClick
        )

        if (notifications.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(top = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "🔔", fontSize = 44.sp)
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Hozircha bildirishnomalar yo'q",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                )
            }
            return@Column
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 30.dp)
        ) {
            grouped.forEach { (dateLabel, group) ->
                item(key = "header_$dateLabel") {
                    Text(
                        text = dateLabel,
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }

                items(group, key = { it.id }) { notification ->
                    NotificationItemCard(item = notification)
                }
            }
        }
    }
}

@Composable
fun NotificationItemCard(item: AppNotificationItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (item.isRead) SurfaceLight else PrimaryOrangeSoft)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(
                    when (item.type) {
                        "DISCOUNT", "SPECIAL_OFFER" -> Color(0xFFFFECEB)
                        "ORDER_TAKEN", "ORDER_DELIVERED" -> Color(0xFFE8F8EE)
                        "ORDER_CANCELED" -> Color(0xFFFFECEB)
                        "ACCOUNT" -> Color(0xFFEFF6FF)
                        else -> Color(0xFFF3E8FF)
                    },
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = when (item.type) {
                    "DISCOUNT" -> "🏷️"
                    "ORDER_TAKEN" -> "✅"
                    "ORDER_DELIVERED" -> "📦"
                    "ORDER_CANCELED" -> "❌"
                    "ACCOUNT" -> "👤"
                    "SPECIAL_OFFER" -> "🎁"
                    "CARD" -> "💳"
                    else -> "🔔"
                },
                fontSize = 20.sp
            )
        }

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = item.message,
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
            )
        }
    }
}

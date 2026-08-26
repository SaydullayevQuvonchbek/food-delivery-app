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
    val todayNotifications = notifications.filter { it.timeAgo == "Today" }
    val yesterdayNotifications = notifications.filter { it.timeAgo == "Yesterday" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        AppHeaderBar(
            title = "Notification",
            onBackClick = onBackClick
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 30.dp)
        ) {
            if (todayNotifications.isNotEmpty()) {
                item {
                    Text(
                        text = "Today",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }

                items(todayNotifications) { item ->
                    NotificationItemCard(item = item)
                }
            }

            if (yesterdayNotifications.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Yesterday",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }

                items(yesterdayNotifications) { item ->
                    NotificationItemCard(item = item)
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
            .background(SurfaceLight)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Distinct Badge Icon based on Type
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(
                    when (item.type) {
                        "DISCOUNT" -> Color(0xFFFFECEB)
                        "ORDER_TAKEN" -> Color(0xFFE8F8EE)
                        "ORDER_CANCELED" -> Color(0xFFFFECEB)
                        "ACCOUNT" -> Color(0xFFEFF6FF)
                        "SPECIAL_OFFER" -> Color(0xFFFFF7ED)
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
                    "ORDER_CANCELED" -> "❌"
                    "ACCOUNT" -> "👤"
                    "SPECIAL_OFFER" -> "🎁"
                    else -> "💳"
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
package com.fooddelivery.presentation.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TwoWheeler
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
import com.fooddelivery.domain.models.Order
import com.fooddelivery.domain.models.OrderStatus
import com.fooddelivery.theme.*
import com.fooddelivery.util.formatPrice

@Composable
fun OrdersHistoryScreen(
    repository: FoodDeliveryRepository,
    onBackClick: () -> Unit,
    onNavigateToTracking: () -> Unit
) {
    val orders by repository.orders.collectAsState()
    var selectedFilter by remember { mutableStateOf("all") } // all, active, delivered, canceled

    LaunchedEffect(Unit) {
        repository.loadOrders()
    }

    val filteredOrders = remember(orders, selectedFilter) {
        when (selectedFilter) {
            "active" -> orders.filter { it.status == OrderStatus.PENDING || it.status == OrderStatus.PREPARING || it.status == OrderStatus.ON_THE_WAY }
            "delivered" -> orders.filter { it.status == OrderStatus.DELIVERED }
            "canceled" -> orders.filter { it.status == OrderStatus.CANCELED }
            else -> orders
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .statusBarsPadding()
    ) {
        AppHeaderBar(
            title = "Buyurtmalar tarixi",
            onBackClick = onBackClick
        )

        // Filter Tabs
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val tabs = listOf(
                "all" to "Barchasi",
                "active" to "Faol (Jarayonda)",
                "delivered" to "Yetkazilgan",
                "canceled" to "Bekor qilingan"
            )
            items(tabs) { (key, label) ->
                val isSelected = selectedFilter == key
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) PrimaryOrange else SurfaceLight)
                        .border(1.dp, if (isSelected) PrimaryOrange else BorderLight, RoundedCornerShape(14.dp))
                        .clickable { selectedFilter = key }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) TextWhite else TextSecondary
                        )
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        if (filteredOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "📦", fontSize = 48.sp)
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = "Buyurtmalar topilmadi",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Hozircha ushbu bo'limda buyurtmalar mavjud emas",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 30.dp, top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredOrders, key = { it.id }) { order ->
                    OrderHistoryCard(
                        order = order,
                        onTrackClick = onNavigateToTracking
                    )
                }
            }
        }
    }
}

@Composable
fun OrderHistoryCard(
    order: Order,
    onTrackClick: () -> Unit
) {
    val isActive = order.status == OrderStatus.PENDING || order.status == OrderStatus.PREPARING || order.status == OrderStatus.ON_THE_WAY
    val (statusLabel, statusBg, statusColor) = when (order.status) {
        OrderStatus.PENDING -> Triple("Kutilmoqda", Color(0xFFFFF7ED), Color(0xFFEA580C))
        OrderStatus.PREPARING -> Triple("Tayyorlanmoqda", Color(0xFFFEF3C7), Color(0xFFD97706))
        OrderStatus.ON_THE_WAY -> Triple("Kuryerda (Yo'lda)", Color(0xFFEDE9FE), Color(0xFF7C3AED))
        OrderStatus.DELIVERED -> Triple("Yetkazildi", Color(0xFFECFDF5), Color(0xFF059669))
        OrderStatus.CANCELED -> Triple("Bekor qilindi", Color(0xFFFEF2F2), Color(0xFFDC2626))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Number + Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = order.orderNumber,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black)
                    )
                    Text(
                        text = order.createdAt,
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(statusBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusLabel,
                        style = MaterialTheme.typography.bodySmall.copy(color = statusColor, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = BorderLight)
            Spacer(Modifier.height(10.dp))

            // Address
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = PrimaryOrange,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = order.deliveryAddress.ifBlank { "Toshkent sh." },
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                    maxLines = 1
                )
            }

            Spacer(Modifier.height(12.dp))

            // Price & Action Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Jami summa:",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp)
                    )
                    Text(
                        text = formatPrice(order.total),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = TextPrimary)
                    )
                }

                if (isActive) {
                    Button(
                        onClick = onTrackClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.TwoWheeler, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(text = "Kuzatish", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
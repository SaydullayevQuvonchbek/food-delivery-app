package com.fooddelivery.presentation.tracking

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fooddelivery.components.AppHeaderBar
import com.fooddelivery.data.repository.FoodDeliveryRepository
import com.fooddelivery.theme.*

@Composable
fun DeliveryTrackingScreen(
    repository: FoodDeliveryRepository,
    onBackClick: () -> Unit,
    onNavigateToChat: (Long) -> Unit,
    onNavigateToCall: (Long) -> Unit
) {
    val courier = repository.currentCourier
    val lastOrder by repository.lastCreatedOrder.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8ECEF))
    ) {
        // Map Simulation Background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Simulated map roads (Light Gray Lines)
            drawLine(
                color = Color.White,
                start = Offset(0f, height * 0.35f),
                end = Offset(width, height * 0.35f),
                strokeWidth = 24.dp.toPx()
            )
            drawLine(
                color = Color.White,
                start = Offset(width * 0.4f, 0f),
                end = Offset(width * 0.4f, height),
                strokeWidth = 28.dp.toPx()
            )
            drawLine(
                color = Color.White,
                start = Offset(0f, height * 0.6f),
                end = Offset(width, height * 0.6f),
                strokeWidth = 20.dp.toPx()
            )
            drawLine(
                color = Color.White,
                start = Offset(width * 0.75f, 0f),
                end = Offset(width * 0.75f, height),
                strokeWidth = 18.dp.toPx()
            )

            // Orange Route Line (Restaurant -> Courier -> User)
            val pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 10f), 0f)
            drawLine(
                color = PrimaryOrange,
                start = Offset(width * 0.25f, height * 0.28f),
                end = Offset(width * 0.4f, height * 0.35f),
                strokeWidth = 6.dp.toPx(),
                pathEffect = pathEffect
            )
            drawLine(
                color = PrimaryOrange,
                start = Offset(width * 0.4f, height * 0.35f),
                end = Offset(width * 0.65f, height * 0.48f),
                strokeWidth = 6.dp.toPx()
            )
        }

        // Restaurant Marker (Source)
        Box(
            modifier = Modifier
                .offset(x = 80.dp, y = 180.dp)
                .size(44.dp)
                .background(SurfaceLight, CircleShape)
                .border(2.dp, PrimaryOrange, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🏪", fontSize = 20.sp)
        }

        // Destination Marker (User)
        Box(
            modifier = Modifier
                .offset(x = 240.dp, y = 300.dp)
                .size(48.dp)
                .background(DangerRed, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = null,
                tint = TextWhite,
                modifier = Modifier.size(28.dp)
            )
        }

        // Courier Scooter Marker (Live Animated/Placed on map)
        Box(
            modifier = Modifier
                .offset(x = 150.dp, y = 220.dp)
                .size(52.dp)
                .background(PrimaryOrange, CircleShape)
                .border(3.dp, BackgroundWhite, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🛵", fontSize = 26.sp)
        }

        // Top Navigation and Title
        AppHeaderBar(
            title = "Track Order",
            onBackClick = onBackClick,
            modifier = Modifier.align(Alignment.TopCenter).statusBarsPadding()
        )

        // Estimated Time Capsule Badge
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(BackgroundWhite)
                .border(1.dp, BorderLight, RoundedCornerShape(30.dp))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Navigation,
                    contentDescription = null,
                    tint = PrimaryOrange,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "10-15 min remaining",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                )
            }
        }

        // Bottom Delivery Info Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .clip(BottomSheetShape)
                .background(BackgroundWhite),
            shape = BottomSheetShape,
            colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
            elevation = CardDefaults.cardElevation(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Courier Info Card (Dark Capsule)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFF1B1B1B)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Color.Gray),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "👨‍🍳", fontSize = 24.sp)
                            }

                            Spacer(Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = courier.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = TextWhite,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = courier.badgeId,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextWhite.copy(alpha = 0.6f)
                                    )
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Chat Button
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(PrimaryOrange, CircleShape)
                                    .clickable { onNavigateToChat(courier.id) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ChatBubble,
                                    contentDescription = "Chat",
                                    tint = TextWhite,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Call Button
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(PrimaryOrange, CircleShape)
                                    .clickable { onNavigateToCall(courier.id) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Call,
                                    contentDescription = "Call",
                                    tint = TextWhite,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(18.dp))

                // Delivery Time
                Text(
                    text = "Your Delivery Time",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Estimated 8:30 - 9:15 PM",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(Modifier.height(16.dp))

                // Delivery Stepper (4 steps)
                DeliveryStepperView()

                Spacer(Modifier.height(18.dp))

                // Order summary snippet
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Order",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = lastOrder?.items?.firstOrNull()?.let { "${it.quantity} ${it.food.name}" } ?: "2 Burger With Meat",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Text(
                        text = "$ ${lastOrder?.total?.toInt() ?: 283}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = PrimaryOrange,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun DeliveryStepperView() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Step 1: Placed
        StepIcon(icon = "📋", isDone = true)
        StepLine(isDone = true, modifier = Modifier.weight(1f))

        // Step 2: Preparing
        StepIcon(icon = "🍲", isDone = true)
        StepLine(isDone = true, modifier = Modifier.weight(1f))

        // Step 3: On the way (Active)
        StepIcon(icon = "🛵", isDone = true, isCurrent = true)
        StepLine(isDone = false, modifier = Modifier.weight(1f))

        // Step 4: Delivered
        StepIcon(icon = "✓", isDone = false)
    }
}

@Composable
private fun StepIcon(icon: String, isDone: Boolean, isCurrent: Boolean = false) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .background(if (isDone) PrimaryOrangeSoft else SurfaceLight, CircleShape)
            .border(
                1.5.dp,
                if (isCurrent) PrimaryOrange else if (isDone) PrimaryOrangeLight else BorderLight,
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(text = icon, fontSize = 14.sp)
    }
}

@Composable
private fun StepLine(isDone: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(2.dp)
            .background(if (isDone) PrimaryOrange else BorderLight)
    )
}
package com.fooddelivery.presentation.tracking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    Box(modifier = Modifier.fillMaxSize()) {
        // Map Mock Background with Route and Pins
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE5E3DF))
        ) {
            // Map Roads simulation
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 80.dp),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                for (i in 0..6) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(Color.White.copy(alpha = 0.7f))
                    )
                }
            }

            // Simulated Route Line (Orange)
            Box(
                modifier = Modifier
                    .offset(x = 60.dp, y = 200.dp)
                    .width(180.dp)
                    .height(8.dp)
                    .background(PrimaryOrange, RoundedCornerShape(4.dp))
            )
            Box(
                modifier = Modifier
                    .offset(x = 232.dp, y = 200.dp)
                    .width(8.dp)
                    .height(140.dp)
                    .background(PrimaryOrange, RoundedCornerShape(4.dp))
            )

            // Pin Courier
            Box(
                modifier = Modifier
                    .offset(x = 220.dp, y = 250.dp)
                    .size(32.dp)
                    .background(PrimaryOrange, CircleShape)
                    .border(3.dp, TextWhite, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🛵", fontSize = 16.sp)
            }

            // Pin Destination
            Box(
                modifier = Modifier
                    .offset(x = 50.dp, y = 190.dp)
                    .size(28.dp)
                    .background(TextPrimary, CircleShape)
                    .border(2.dp, TextWhite, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "📍", fontSize = 14.sp)
            }

            // Floating Location Button
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp, bottom = 120.dp)
                    .size(44.dp)
                    .background(BackgroundWhite, CircleShape)
                    .shadow(4.dp, CircleShape)
                    .clickable { /* Re-center */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.MyLocation,
                    contentDescription = "Center Location",
                    tint = TextPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // Top Back Button & Title Card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(PrimaryOrange, CircleShape)
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "‹",
                    style = MaterialTheme.typography.headlineMedium.copy(color = TextWhite)
                )
            }

            Spacer(Modifier.width(16.dp))

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = BackgroundWhite,
                shadowElevation = 4.dp
            ) {
                Text(
                    text = "Delivered your order",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
                )
            }
        }

        // Bottom Delivery Info Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
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
                // Courier Info Card (Black / Dark Capsule)
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

                Spacer(Modifier.height(20.dp))

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

                Spacer(Modifier.height(18.dp))

                // Delivery Stepper (4 steps)
                DeliveryStepperView()

                Spacer(Modifier.height(20.dp))

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
                            text = "2 Burger With Meat",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Text(
                        text = "$283",
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
package com.fooddelivery.presentation.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fooddelivery.theme.*

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    var currentPage by remember { mutableStateOf(0) }

    val slides = listOf(
        Pair("We serve\nincomparable\ndelicacies", "All the best restaurants with their top menu waiting for you, they can't wait for your order!!"),
        Pair("Fast & Safe\nDelivery\nService", "Fast and safe delivery right to your doorsteps with live courier tracking anytime!!"),
        Pair("Delicious Meals\n& Special\nDiscounts", "Enjoy delicious meals and special discounts tailored just for your taste!!")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
    ) {
        // Hero Image Background with Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.62f)
                .statusBarsPadding()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF4A3525), Color(0xFF1E1E1E))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 40.dp)
            ) {
                Text(
                    text = when(currentPage) {
                        0 -> "🍔"
                        1 -> "🛵"
                        else -> "🍕"
                    },
                    fontSize = 110.sp
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Premium Fast Food",
                    style = MaterialTheme.typography.titleMedium.copy(color = TextWhite.copy(alpha = 0.85f))
                )
            }
        }

        // Bottom Rounded Orange Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            shape = RoundedCornerShape(36.dp),
            colors = CardDefaults.cardColors(containerColor = PrimaryOrange),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedContent(targetState = currentPage, label = "slide_text") { page ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = slides[page].first,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = TextWhite,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center,
                                lineHeight = 28.sp,
                                fontSize = 24.sp
                            )
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = slides[page].second,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextWhite.copy(alpha = 0.9f),
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp,
                                fontSize = 14.sp
                            )
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Dots indicator
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0..2) {
                        Box(
                            modifier = Modifier
                                .height(6.dp)
                                .width(if (i == currentPage) 24.dp else 8.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (i == currentPage) TextWhite else TextWhite.copy(alpha = 0.4f))
                        )
                    }
                }

                Spacer(Modifier.height(22.dp))

                // Navigation Row
                if (currentPage < 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Skip",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = TextWhite.copy(alpha = 0.85f),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            ),
                            modifier = Modifier.clickable { onFinish() }
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { currentPage++ }
                        ) {
                            Text(
                                text = "Next",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            )
                            Spacer(Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next",
                                tint = TextWhite,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(TextWhite, CircleShape)
                            .border(3.dp, TextWhite.copy(alpha = 0.3f), CircleShape)
                            .clickable { onFinish() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Start",
                            tint = PrimaryOrange,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }
    }
}
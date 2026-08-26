package com.fooddelivery.presentation.food_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fooddelivery.components.*
import com.fooddelivery.data.repository.FoodDeliveryRepository
import com.fooddelivery.domain.models.Food
import com.fooddelivery.theme.*

@Composable
fun FoodDetailScreen(
    foodId: Long,
    repository: FoodDeliveryRepository,
    onBackClick: () -> Unit,
    onNavigateToCart: () -> Unit
) {
    val foods by repository.foods.collectAsState()
    val food = foods.find { it.id == foodId } ?: foods.first()
    var quantity by remember { mutableStateOf(4) }
    var currentSlide by remember { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundWhite)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 90.dp)
        ) {
            // Image Slider with Top Navigation Icons
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .background(Color(0xFF382D24)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🍔",
                    fontSize = 110.sp
                )

                // Top Floating Back & Favorite / Share buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                            .clickable { onBackClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "‹",
                            style = MaterialTheme.typography.headlineMedium.copy(color = TextWhite)
                        )
                    }

                    Text(
                        text = "About This Menu",
                        style = MaterialTheme.typography.titleLarge.copy(color = TextWhite, fontWeight = FontWeight.Bold)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                                .clickable { repository.toggleFavorite(food.id) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (food.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (food.isFavorite) DangerRed else TextWhite,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Slider Dots
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (i in 0..2) {
                        Box(
                            modifier = Modifier
                                .height(5.dp)
                                .width(if (i == 0) 24.dp else 8.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (i == 0) PrimaryOrange else TextWhite.copy(alpha = 0.5f))
                        )
                    }
                }
            }

            // Food Details Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = food.name,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "$ ${food.price.toInt()}",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = PrimaryOrange,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(Modifier.height(16.dp))

                // Attributes Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "💲", fontSize = 14.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Free Delivery",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "⏱", fontSize = 14.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = food.deliveryTime,
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = StarYellow,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(3.dp))
                        Text(
                            text = "${food.rating}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = food.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondary,
                        lineHeight = 22.sp
                    )
                )

                Spacer(Modifier.height(28.dp))

                // Recommended For You
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recomended For You",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "See All",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = PrimaryOrange,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                Spacer(Modifier.height(14.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(foods.filterNot { it.id == food.id }) { recFood ->
                        Box(
                            modifier = Modifier
                                .width(150.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(SurfaceLight)
                                .padding(10.dp)
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFE8E0D4)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "🍔", fontSize = 36.sp)
                                }
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = recFood.name,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    maxLines = 1
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = "$ ${recFood.price.toInt()}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = PrimaryOrange,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom Sticky Action Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            color = BackgroundWhite,
            shadowElevation = 16.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QuantityCounter(
                    quantity = quantity,
                    onIncrease = { quantity++ },
                    onDecrease = { if (quantity > 1) quantity-- }
                )

                Spacer(Modifier.width(16.dp))

                AppPrimaryButton(
                    text = "Add to Cart",
                    icon = Icons.Filled.ShoppingCart,
                    onClick = {
                        repository.addToCart(food, quantity)
                        onNavigateToCart()
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
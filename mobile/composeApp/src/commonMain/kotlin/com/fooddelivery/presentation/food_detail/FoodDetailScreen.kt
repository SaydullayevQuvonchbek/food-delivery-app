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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.fooddelivery.components.*
import com.fooddelivery.data.repository.FoodDeliveryRepository
import com.fooddelivery.domain.models.Food
import com.fooddelivery.util.formatPrice
import com.fooddelivery.theme.*

@Composable
fun FoodDetailScreen(
    foodId: Long,
    repository: FoodDeliveryRepository,
    onBackClick: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToFood: (Long) -> Unit = {}
) {
    val allFoods by repository.allFoods.collectAsState()
    // Ilgari faqat joriy kategoriya ro'yxatidan qidirilardi va topilmasa BOSHQA taom ko'rsatilardi
    val food = allFoods.find { it.id == foodId }
    var quantity by remember(foodId) { mutableStateOf(1) }

    if (food == null) {
        Column(
            modifier = Modifier.fillMaxSize().background(BackgroundWhite).statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppHeaderBar(title = "Taom", onBackClick = onBackClick)
            Spacer(Modifier.height(60.dp))
            Text(text = "🍽", fontSize = 44.sp)
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Bu taom topilmadi yoki mavjud emas",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
            )
        }
        return
    }

    val recommended = remember(food.id, allFoods) { repository.recommendedFor(food) }

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
                if (food.imageUrl.isNotBlank() && food.imageUrl.startsWith("http")) {
                    AsyncImage(
                        model = food.imageUrl,
                        contentDescription = food.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = categoryEmoji(food.categoryId),
                        fontSize = 110.sp
                    )
                }

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
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = "Taom haqida",
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
                    text = formatPrice(food.price),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = PrimaryOrange,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(Modifier.height(16.dp))

                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🎯", fontSize = 14.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = food.distance,
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
                    text = "Taom tavsifi",
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
                        text = "Sizga tavsiya qilamiz",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Barchasi",
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
                    items(recommended, key = { it.id }) { recFood ->
                        Box(
                            modifier = Modifier
                                .width(150.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(SurfaceLight)
                                .clickable { onNavigateToFood(recFood.id) }
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
                                    if (recFood.imageUrl.isNotBlank() && recFood.imageUrl.startsWith("http")) {
                                        AsyncImage(
                                            model = recFood.imageUrl,
                                            contentDescription = recFood.name,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Text(text = categoryEmoji(recFood.categoryId), fontSize = 36.sp)
                                    }
                                }
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = recFood.name,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    maxLines = 1
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = formatPrice(recFood.price),
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
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(),
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
                    text = "Savatga qo'shish",
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

/** Rasm hali yuklanmagan taomlar uchun kategoriya emojisi */
fun categoryEmoji(categoryId: Long): String = when (categoryId) {
    1L -> "🍔"
    2L -> "🌮"
    3L -> "🥤"
    4L -> "🍕"
    else -> "🍽"
}

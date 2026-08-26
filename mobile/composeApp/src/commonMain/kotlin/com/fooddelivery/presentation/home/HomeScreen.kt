package com.fooddelivery.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fooddelivery.data.repository.FoodDeliveryRepository
import com.fooddelivery.domain.models.Category
import com.fooddelivery.domain.models.Food
import com.fooddelivery.theme.*

@Composable
fun HomeScreen(
    repository: FoodDeliveryRepository,
    onNavigateToSearch: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToFoodDetail: (Long) -> Unit
) {
    val categories by repository.categories.collectAsState()
    val foods by repository.foods.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(10.dp))

        // Top Location and Action Icons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Your Location",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "📍 ",
                        fontSize = 14.sp
                    )
                    Text(
                        text = "New York City",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(SurfaceLight, CircleShape)
                        .border(1.dp, BorderLight, CircleShape)
                        .clickable { onNavigateToSearch() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(SurfaceLight, CircleShape)
                        .border(1.dp, BorderLight, CircleShape)
                        .clickable { onNavigateToNotifications() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    // Notification red dot badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(8.dp)
                            .background(DangerRed, CircleShape)
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Hero Promo Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF2C2523), Color(0xFF191615))
                    )
                )
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Provide the best\nfood for you",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 24.sp
                        )
                    )
                }
                Text(
                    text = "🍔🌮",
                    fontSize = 46.sp
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        // Categories Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Find by Category",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "See All",
                modifier = Modifier.clickable { repository.selectCategory(0L) },
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PrimaryOrange,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        Spacer(Modifier.height(12.dp))

        // Category Horizontal Bar
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { category ->
                CategoryChip(
                    category = category,
                    onClick = { repository.selectCategory(category.id) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // 2-Column Food Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            items(foods) { food ->
                FoodCard(
                    food = food,
                    onItemClick = { onNavigateToFoodDetail(food.id) },
                    onFavoriteClick = { repository.toggleFavorite(food.id) }
                )
            }
        }
    }
}

@Composable
fun CategoryChip(
    category: Category,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (category.isSelected) PrimaryOrange else SurfaceLight)
            .border(
                1.dp,
                if (category.isSelected) PrimaryOrange else BorderLight,
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = category.icon,
                fontSize = 24.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = category.name,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (category.isSelected) TextWhite else TextPrimary
                )
            )
        }
    }
}

@Composable
fun FoodCard(
    food: Food,
    onItemClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Food Image Placeholder with Favorite Icon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFEBE3D5)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (food.categoryId == 1L) "🍔" else if (food.categoryId == 2L) "🌮" else if (food.categoryId == 3L) "🥤" else "🍕",
                    fontSize = 52.sp
                )

                // Favorite Heart Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .background(BackgroundWhite, CircleShape)
                        .clickable { onFavoriteClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (food.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (food.isFavorite) DangerRed else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = food.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "🎯 ${food.distance}",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "$ ${food.price.toInt()}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PrimaryOrange
                )
            )
        }
    }
}
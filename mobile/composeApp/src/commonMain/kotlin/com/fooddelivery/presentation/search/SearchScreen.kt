package com.fooddelivery.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fooddelivery.components.AppHeaderBar
import com.fooddelivery.components.AppInputField
import com.fooddelivery.data.repository.FoodDeliveryRepository
import com.fooddelivery.presentation.home.CategoryChip
import com.fooddelivery.theme.*

@Composable
fun SearchScreen(
    repository: FoodDeliveryRepository,
    onBackClick: () -> Unit,
    onNavigateToFoodDetail: (Long) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val categories by repository.categories.collectAsState()
    val recentSearches by repository.recentSearches.collectAsState()
    val foods by repository.foods.collectAsState()

    val filteredFoods = remember(searchQuery, foods) {
        if (searchQuery.isBlank()) emptyList()
        else foods.filter { it.name.contains(searchQuery, ignoreCase = true) || it.description.contains(searchQuery, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        AppHeaderBar(
            title = "Search Food",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            // Search Input with Filter Icon
            AppInputField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    if (it.isNotBlank()) repository.addRecentSearch(it)
                },
                placeholder = "Search Food",
                leadingIcon = Icons.Outlined.Search,
                trailingIcon = Icons.Outlined.Tune
            )

            Spacer(Modifier.height(18.dp))

            if (searchQuery.isNotBlank() && filteredFoods.isEmpty()) {
                // Empty State
                EmptySearchResultView(query = searchQuery)
            } else if (searchQuery.isNotBlank()) {
                // Live Search Results
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredFoods) { food ->
                        RecentOrderItemRow(
                            food = food,
                            onClick = { onNavigateToFoodDetail(food.id) }
                        )
                    }
                }
            } else {
                // Categories
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { category ->
                        CategoryChip(
                            category = category,
                            onClick = {
                                searchQuery = category.name
                            }
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Recent Searches Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent searches",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Delete",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = PrimaryOrange,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.clickable { repository.clearRecentSearches() }
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Recent Search Items
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    recentSearches.forEach { searchItem ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { searchQuery = searchItem }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Search,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text = searchItem,
                                    style = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary)
                                )
                            }
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Remove",
                                tint = TextMuted,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable { repository.removeRecentSearch(searchItem) }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(26.dp))

                // My Recent Orders Section
                Text(
                    text = "My recent orders",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(Modifier.height(14.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(foods.take(3)) { food ->
                        RecentOrderItemRow(
                            food = food,
                            onClick = { onNavigateToFoodDetail(food.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecentOrderItemRow(
    food: com.fooddelivery.domain.models.Food,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceLight)
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFEDE3D7)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🍔", fontSize = 28.sp)
        }

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = food.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "Burger Restaurant",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
            )
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = StarYellow,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "${food.rating}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "🎯 ${food.distance}",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                )
            }
        }
    }
}

@Composable
fun EmptySearchResultView(query: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .background(PrimaryOrangeSoft, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = PrimaryOrange,
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "We couldn't find any result!",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Please check your search for any typos or spelling errors, or try a different search term.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextSecondary,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}
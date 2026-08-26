package com.fooddelivery.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fooddelivery.components.AppHeaderBar
import com.fooddelivery.components.AppInputField
import com.fooddelivery.data.repository.FoodDeliveryRepository
import com.fooddelivery.domain.models.Food
import com.fooddelivery.presentation.food_detail.categoryEmoji
import com.fooddelivery.presentation.home.CategoryChip
import com.fooddelivery.theme.*
import com.fooddelivery.util.formatPrice

@Composable
fun SearchScreen(
    repository: FoodDeliveryRepository,
    onBackClick: () -> Unit,
    onNavigateToFoodDetail: (Long) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val categories by repository.categories.collectAsState()
    val recentSearches by repository.recentSearches.collectAsState()
    val allFoods by repository.allFoods.collectAsState()
    val keyboard = LocalSoftwareKeyboardController.current

    // Qidiruv butun katalog bo'yicha (ilgari faqat tanlangan kategoriya ichidan qidirardi)
    val filteredFoods = remember(searchQuery, allFoods) { repository.searchFoods(searchQuery) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .statusBarsPadding()
            .imePadding()
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
            AppInputField(
                value = searchQuery,
                // Tarixga faqat qidiruv tasdiqlanganda yoziladi (ilgari har bir harfda yozilardi)
                onValueChange = { searchQuery = it },
                placeholder = "Search Food",
                leadingIcon = Icons.Outlined.Search,
                trailingIcon = if (searchQuery.isNotEmpty()) Icons.Filled.Close else null,
                onTrailingIconClick = { searchQuery = "" },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        repository.addRecentSearch(searchQuery)
                        keyboard?.hide()
                    }
                )
            )

            Spacer(Modifier.height(18.dp))

            if (searchQuery.isNotBlank() && filteredFoods.isEmpty()) {
                EmptySearchResultView(query = searchQuery)
            } else if (searchQuery.isNotBlank()) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(filteredFoods, key = { it.id }) { food ->
                        FoodResultRow(
                            food = food,
                            onClick = {
                                repository.addRecentSearch(searchQuery)
                                onNavigateToFoodDetail(food.id)
                            }
                        )
                    }
                }
            } else {
                // Butun bo'sh holat bitta ro'yxatda - kichik ekranlarda ham to'liq aylanadi
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(categories, key = { it.id }) { category ->
                                CategoryChip(
                                    category = category,
                                    onClick = { searchQuery = category.name }
                                )
                            }
                        }
                        Spacer(Modifier.height(24.dp))
                    }

                    if (recentSearches.isNotEmpty()) {
                        item {
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
                        }

                        items(recentSearches) { searchItem ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { searchQuery = searchItem }
                                    .padding(vertical = 8.dp),
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

                    item {
                        Spacer(Modifier.height(20.dp))
                        Text(
                            text = "Mashhur taomlar",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(Modifier.height(14.dp))
                    }

                    items(allFoods.take(5), key = { it.id }) { food ->
                        FoodResultRow(
                            food = food,
                            onClick = { onNavigateToFoodDetail(food.id) }
                        )
                        Spacer(Modifier.height(14.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun FoodResultRow(
    food: Food,
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
            Text(text = categoryEmoji(food.categoryId), fontSize = 28.sp)
        }

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = food.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 1
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = formatPrice(food.price),
                style = MaterialTheme.typography.bodySmall.copy(color = PrimaryOrange, fontWeight = FontWeight.Bold)
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
            text = "\"$query\" bo'yicha hech narsa topilmadi",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Yozuvda xatolik bormi tekshiring yoki boshqa so'z bilan qidirib ko'ring.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextSecondary,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

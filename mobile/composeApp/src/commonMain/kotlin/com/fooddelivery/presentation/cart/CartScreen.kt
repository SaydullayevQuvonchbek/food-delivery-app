package com.fooddelivery.presentation.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
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
import com.fooddelivery.components.*
import com.fooddelivery.data.repository.FoodDeliveryRepository
import com.fooddelivery.domain.models.CartItem
import com.fooddelivery.theme.*

@Composable
fun CartScreen(
    repository: FoodDeliveryRepository,
    onNavigateToHome: () -> Unit,
    onNavigateToCheckout: () -> Unit
) {
    val cartItems by repository.cartItems.collectAsState()
    var promoCode by remember { mutableStateOf("") }
    var promoApplied by remember { mutableStateOf(false) }

    val subtotal = remember(cartItems) { cartItems.sumOf { it.totalPrice } }
    val deliveryFee = if (subtotal > 0) 0.0 else 0.0
    val discount = if (promoApplied) 10900.0 else 0.0
    val total = (subtotal + deliveryFee - discount).coerceAtLeast(0.0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        AppHeaderBar(
            title = "My Cart",
            actionIcon = Icons.Filled.MoreVert,
            onActionClick = { /* More actions */ }
        )

        if (cartItems.isEmpty()) {
            EmptyCartView(onFindFoods = onNavigateToHome)
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // Delivery Location Bar
                item {
                    DeliveryLocationCard()
                }

                // Promo Code Section
                item {
                    PromoCodeCard(
                        promoCode = promoCode,
                        onPromoCodeChange = { promoCode = it },
                        isApplied = promoApplied,
                        onApply = { promoApplied = true }
                    )
                }

                // Cart Items List
                items(cartItems) { item ->
                    CartItemRow(
                        item = item,
                        onIncrease = { repository.updateCartItemQuantity(item.food.id, 1) },
                        onDecrease = { repository.updateCartItemQuantity(item.food.id, -1) },
                        onRemove = { repository.removeCartItem(item.food.id) }
                    )
                }

                // Payment Summary Section
                item {
                    PaymentSummaryCard(
                        itemCount = cartItems.sumOf { it.quantity },
                        subtotal = subtotal,
                        deliveryFee = deliveryFee,
                        discount = discount,
                        total = total
                    )
                }

                // Order Now Button
                item {
                    Spacer(Modifier.height(10.dp))
                    AppPrimaryButton(
                        text = "Order Now",
                        onClick = onNavigateToCheckout
                    )
                    Spacer(Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun DeliveryLocationCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceLight)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Delivery Location",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "Home (New York City)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(PrimaryOrangeSoft)
                .clickable { /* Change Location */ }
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "Change Location",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = PrimaryOrange,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun PromoCodeCard(
    promoCode: String,
    onPromoCodeChange: (String) -> Unit,
    isApplied: Boolean,
    onApply: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .background(InputBackground, RoundedCornerShape(16.dp))
            .border(1.dp, BorderLight, RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "🎟 ", fontSize = 18.sp)

        Box(modifier = Modifier.weight(1f)) {
            if (promoCode.isEmpty()) {
                Text(
                    text = "Promo Code...",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted)
                )
            }
            androidx.compose.foundation.text.BasicTextField(
                value = promoCode,
                onValueChange = onPromoCodeChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary)
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(if (isApplied) SuccessGreen else PrimaryOrange)
                .clickable { onApply() }
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = if (isApplied) "Applied ✓" else "Apply",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    var isChecked by remember { mutableStateOf(item.isSelected) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceLight)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Checkbox
        Box(
            modifier = Modifier
                .size(22.dp)
                .background(if (isChecked) PrimaryOrange else Color.Transparent, RoundedCornerShape(6.dp))
                .border(1.5.dp, if (isChecked) PrimaryOrange else BorderLight, RoundedCornerShape(6.dp))
                .clickable { isChecked = !isChecked },
            contentAlignment = Alignment.Center
        ) {
            if (isChecked) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = TextWhite, modifier = Modifier.size(16.dp))
            }
        }

        Spacer(Modifier.width(12.dp))

        // Food Thumbnail
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE9E0D4)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🍔", fontSize = 32.sp)
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.food.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 1
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "$ ${item.food.price.toInt()}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PrimaryOrange,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuantityCounter(
                    quantity = item.quantity,
                    onIncrease = onIncrease,
                    onDecrease = onDecrease
                )

                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Remove",
                    tint = DangerRed.copy(alpha = 0.8f),
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onRemove() }
                )
            }
        }
    }
}

@Composable
fun PaymentSummaryCard(
    itemCount: Int,
    subtotal: Double,
    deliveryFee: Double,
    discount: Double,
    total: Double
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceLight)
            .padding(18.dp)
    ) {
        Text(
            text = "Payment Summary",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(Modifier.height(14.dp))

        SummaryRow(label = "Total Items ($itemCount)", value = "$ ${subtotal.toInt()}")
        Spacer(Modifier.height(8.dp))
        SummaryRow(label = "Delivery Fee", value = if (deliveryFee == 0.0) "Free" else "$ ${deliveryFee.toInt()}")
        Spacer(Modifier.height(8.dp))
        SummaryRow(
            label = "Discount",
            value = if (discount > 0) "-$ ${discount.toInt()}" else "$ 0",
            valueColor = if (discount > 0) DangerRed else TextPrimary
        )

        Spacer(Modifier.height(12.dp))
        HorizontalDivider(color = BorderLight)
        Spacer(Modifier.height(12.dp))

        SummaryRow(
            label = "Total",
            value = "$ ${total.toInt()}",
            isTotal = true
        )
    }
}

@Composable
fun SummaryRow(
    label: String,
    value: String,
    valueColor: Color = TextPrimary,
    isTotal: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (isTotal) MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            else MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
        )
        Text(
            text = value,
            style = if (isTotal) MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = PrimaryOrange)
            else MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold, color = valueColor)
        )
    }
}

@Composable
fun EmptyCartView(onFindFoods: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .background(PrimaryOrangeSoft, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🛒", fontSize = 70.sp)
        }

        Spacer(Modifier.height(28.dp))

        Text(
            text = "Ouch! Hungry",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Seems like you have not ordered\nany food yet",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        )

        Spacer(Modifier.height(32.dp))

        AppPrimaryButton(
            text = "Find Foods",
            onClick = onFindFoods
        )
    }
}
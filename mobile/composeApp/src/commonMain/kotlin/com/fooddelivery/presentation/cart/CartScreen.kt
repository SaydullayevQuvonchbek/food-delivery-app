package com.fooddelivery.presentation.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.fooddelivery.components.*
import com.fooddelivery.data.repository.FoodDeliveryRepository
import com.fooddelivery.domain.models.CartItem
import com.fooddelivery.presentation.food_detail.categoryEmoji
import com.fooddelivery.theme.*
import com.fooddelivery.util.formatPrice
import kotlinx.coroutines.launch

/** Savat va to'lov ekranlari uchun yagona hisob-kitob (server formulasi bilan bir xil) */
const val TAX_RATE = 0.10
const val DELIVERY_FEE = 0.0

@Composable
fun CartScreen(
    repository: FoodDeliveryRepository,
    onNavigateToHome: () -> Unit,
    onNavigateToCheckout: () -> Unit
) {
    val cartItems by repository.cartItems.collectAsState()
    val appliedPromo by repository.appliedPromo.collectAsState()
    var promoCode by remember { mutableStateOf("") }
    var promoError by remember { mutableStateOf<String?>(null) }
    var isApplyingPromo by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val selectedItems = cartItems.filter { it.isSelected }
    val subtotal = selectedItems.sumOf { it.totalPrice }
    val discount = appliedPromo?.discountAmount ?: 0.0
    val tax = subtotal * TAX_RATE
    val total = (subtotal + DELIVERY_FEE + tax - discount).coerceAtLeast(0.0)

    // Savat o'zgarsa, tasdiqlangan promo kod endi to'g'ri kelmasligi mumkin
    LaunchedEffect(subtotal) {
        if (appliedPromo != null && subtotal == 0.0) repository.clearPromoCode()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .statusBarsPadding()
            .imePadding()
    ) {
        AppHeaderBar(title = "Xarid Savati")

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
                item {
                    DeliveryLocationCard(repository)
                }

                item {
                    PromoCodeCard(
                        promoCode = promoCode,
                        onPromoCodeChange = {
                            promoCode = it
                            promoError = null
                        },
                        appliedCode = appliedPromo?.code,
                        isLoading = isApplyingPromo,
                        errorMessage = promoError,
                        onApply = {
                            scope.launch {
                                if (appliedPromo != null) {
                                    repository.clearPromoCode()
                                    promoCode = ""
                                    return@launch
                                }
                                isApplyingPromo = true
                                promoError = null
                                // Chegirma endi server tomonidan tasdiqlanadi (ilgari har qanday
                                // matn kiritilsa ham 10 900 chegirma "berilardi")
                                repository.applyPromoCode(promoCode, subtotal)
                                    .onFailure { promoError = it.message }
                                isApplyingPromo = false
                            }
                        }
                    )
                }

                items(cartItems, key = { it.food.id }) { item ->
                    CartItemRow(
                        item = item,
                        onToggleSelected = { repository.setCartItemSelected(item.food.id, !item.isSelected) },
                        onIncrease = { repository.updateCartItemQuantity(item.food.id, 1) },
                        onDecrease = { repository.updateCartItemQuantity(item.food.id, -1) },
                        onRemove = { repository.removeCartItem(item.food.id) }
                    )
                }

                item {
                    PaymentSummaryCard(
                        itemCount = selectedItems.sumOf { it.quantity },
                        subtotal = subtotal,
                        deliveryFee = DELIVERY_FEE,
                        tax = tax,
                        discount = discount,
                        total = total
                    )
                }

                item {
                    Spacer(Modifier.height(10.dp))
                    AppPrimaryButton(
                        text = if (selectedItems.isEmpty()) "Taom tanlang" else "Buyurtma berish",
                        enabled = selectedItems.isNotEmpty(),
                        onClick = onNavigateToCheckout
                    )
                    Spacer(Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun DeliveryLocationCard(repository: FoodDeliveryRepository) {
    val addresses by repository.addresses.collectAsState()
    val defaultAddress = addresses.firstOrNull { it.isDefault } ?: addresses.firstOrNull()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceLight)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Yetkazish manzili",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = defaultAddress?.let { "${it.label} (${it.addressLine})" } ?: "Toshkent sh., Chilonzor",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 1
            )
        }
    }
}

@Composable
fun PromoCodeCard(
    promoCode: String,
    onPromoCodeChange: (String) -> Unit,
    appliedCode: String?,
    isLoading: Boolean,
    errorMessage: String?,
    onApply: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .background(InputBackground, RoundedCornerShape(16.dp))
                .border(
                    1.dp,
                    if (errorMessage != null) DangerRed else BorderLight,
                    RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "🎟 ", fontSize = 18.sp)

            Box(modifier = Modifier.weight(1f)) {
                if (promoCode.isEmpty() && appliedCode == null) {
                    Text(
                        text = "Promokodni kiriting...",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted)
                    )
                }
                androidx.compose.foundation.text.BasicTextField(
                    value = appliedCode ?: promoCode,
                    onValueChange = { if (appliedCode == null) onPromoCodeChange(it.uppercase()) },
                    enabled = appliedCode == null,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary)
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (appliedCode != null) SuccessGreen else PrimaryOrange)
                    .clickable(enabled = !isLoading) { onApply() }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = TextWhite,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = if (appliedCode != null) "Bekor qilish" else "Qo'llash",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextWhite,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        errorMessage?.let {
            Spacer(Modifier.height(6.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall.copy(color = DangerRed),
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onToggleSelected: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceLight)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Belgilash holati endi savat holatida saqlanadi va yakuniy summaga ta'sir qiladi
        Box(
            modifier = Modifier
                .size(22.dp)
                .background(if (item.isSelected) PrimaryOrange else Color.Transparent, RoundedCornerShape(6.dp))
                .border(1.5.dp, if (item.isSelected) PrimaryOrange else BorderLight, RoundedCornerShape(6.dp))
                .clickable { onToggleSelected() },
            contentAlignment = Alignment.Center
        ) {
            if (item.isSelected) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = TextWhite, modifier = Modifier.size(16.dp))
            }
        }

        Spacer(Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE9E0D4)),
            contentAlignment = Alignment.Center
        ) {
            if (item.food.imageUrl.isNotBlank() && item.food.imageUrl.startsWith("http")) {
                AsyncImage(
                    model = item.food.imageUrl,
                    contentDescription = item.food.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(text = categoryEmoji(item.food.categoryId), fontSize = 32.sp)
            }
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
                text = formatPrice(item.totalPrice),
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
                    contentDescription = "O'chirish",
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
    tax: Double,
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
            text = "To'lov hisob-kitobi",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(Modifier.height(14.dp))

        SummaryRow(label = "Mahsulotlar ($itemCount ta)", value = formatPrice(subtotal))
        Spacer(Modifier.height(8.dp))
        SummaryRow(label = "Yetkazib berish", value = if (deliveryFee == 0.0) "Bepul" else formatPrice(deliveryFee))
        Spacer(Modifier.height(8.dp))
        SummaryRow(label = "Soliq (10%)", value = formatPrice(tax))
        Spacer(Modifier.height(8.dp))
        SummaryRow(
            label = "Chegirma",
            value = if (discount > 0) "-${formatPrice(discount)}" else formatPrice(0.0),
            valueColor = if (discount > 0) SuccessGreen else TextPrimary
        )

        Spacer(Modifier.height(12.dp))
        HorizontalDivider(color = BorderLight)
        Spacer(Modifier.height(12.dp))

        SummaryRow(
            label = "Jami to'lov",
            value = formatPrice(total),
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
            style = if (isTotal) MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = PrimaryOrange)
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
            text = "Savat bo'sh",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Hali hech qanday taom tanlamadingiz",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        )

        Spacer(Modifier.height(32.dp))

        AppPrimaryButton(
            text = "Taomlarni ko'rish",
            onClick = onFindFoods
        )
    }
}

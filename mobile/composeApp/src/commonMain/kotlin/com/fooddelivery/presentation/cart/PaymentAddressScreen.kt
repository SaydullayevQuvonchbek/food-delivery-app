package com.fooddelivery.presentation.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fooddelivery.components.AppHeaderBar
import com.fooddelivery.components.AppInputField
import com.fooddelivery.components.AppPrimaryButton
import com.fooddelivery.data.repository.FoodDeliveryRepository
import com.fooddelivery.presentation.food_detail.categoryEmoji
import com.fooddelivery.theme.*
import com.fooddelivery.util.formatPrice
import kotlinx.coroutines.launch

@Composable
fun PaymentAddressScreen(
    repository: FoodDeliveryRepository,
    onBackClick: () -> Unit,
    onOrderSuccess: () -> Unit
) {
    val user by repository.currentUser.collectAsState()
    val cartItems by repository.cartItems.collectAsState()
    val addresses by repository.addresses.collectAsState()
    val appliedPromo by repository.appliedPromo.collectAsState()
    val cards by repository.savedCards.collectAsState()

    val selectedItems = cartItems.filter { it.isSelected }
    val subtotal = selectedItems.sumOf { it.totalPrice }
    val tax = subtotal * TAX_RATE
    val discount = appliedPromo?.discountAmount ?: 0.0
    val total = (subtotal + DELIVERY_FEE + tax - discount).coerceAtLeast(0.0)

    val defaultAddress = addresses.firstOrNull { it.isDefault } ?: addresses.firstOrNull()

    var paymentMethod by remember { mutableStateOf("card") }
    var notes by remember { mutableStateOf("") }
    var isPlacingOrder by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Yangi manzil kiritish (server'da manzil bo'lmasa buyurtma manzilsiz ketardi)
    var newAddressLine by remember { mutableStateOf("") }
    var newHouseNumber by remember { mutableStateOf("") }
    var newCity by remember { mutableStateOf("Tashkent") }
    var isSavingAddress by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .statusBarsPadding()
            .imePadding()
    ) {
        AppHeaderBar(
            title = "Rasmiylashtirish",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
        ) {
            Text(
                text = "Mazali va issiq taomlar",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Tanlangan Taomlar",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(Modifier.height(14.dp))

            if (selectedItems.isEmpty()) {
                Text(
                    text = "Savatda tanlangan taom yo'q",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                )
            }

            selectedItems.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceLight)
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFEBE0D2)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = categoryEmoji(item.food.categoryId), fontSize = 28.sp)
                    }

                    Spacer(Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.food.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = formatPrice(item.food.price),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = PrimaryOrange,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Text(
                        text = "${item.quantity} ta",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontWeight = FontWeight.Bold)
                    )
                }
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "To'lov Tafsilotlari",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceLight)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SummaryRow("Mahsulotlar summasi", formatPrice(subtotal))
                SummaryRow("Yetkazib berish", if (DELIVERY_FEE == 0.0) "Bepul" else formatPrice(DELIVERY_FEE))
                SummaryRow("Soliq (10%)", formatPrice(tax))
                if (discount > 0) {
                    SummaryRow("Chegirma (${appliedPromo?.code})", "-${formatPrice(discount)}", valueColor = SuccessGreen)
                }
                HorizontalDivider(color = BorderLight)
                SummaryRow("Jami to'lov", formatPrice(total), isTotal = true)
            }

            Spacer(Modifier.height(24.dp))

            // To'lov usuli
            Text(
                text = "To'lov Usuli",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                PaymentMethodChip(
                    label = if (cards.isEmpty()) "Karta (qo'shilmagan)" else "Karta ••${cards.first().lastFour}",
                    isSelected = paymentMethod == "card",
                    modifier = Modifier.weight(1f),
                    onClick = { paymentMethod = "card" }
                )
                PaymentMethodChip(
                    label = "Naqd pul",
                    isSelected = paymentMethod == "cash",
                    modifier = Modifier.weight(1f),
                    onClick = { paymentMethod = "cash" }
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Yetkazish Manzili:",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceLight)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SummaryRow("Mijoz", user.fullName.ifEmpty { "-" })
                SummaryRow("Telefon", user.phone.orEmpty().ifEmpty { "-" })

                if (defaultAddress != null) {
                    SummaryRow("Manzil", defaultAddress.addressLine)
                    SummaryRow("Uy/Kvartira", defaultAddress.houseNumber.ifEmpty { "-" })
                    SummaryRow("Shahar", defaultAddress.city)
                } else {
                    Text(
                        text = "Yetkazib berish manzilini kiriting:",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                    AppInputField(
                        value = newAddressLine,
                        onValueChange = { newAddressLine = it },
                        placeholder = "Ko'cha, mahalla"
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        AppInputField(
                            value = newHouseNumber,
                            onValueChange = { newHouseNumber = it },
                            placeholder = "Uy / kvartira",
                            modifier = Modifier.weight(1f)
                        )
                        AppInputField(
                            value = newCity,
                            onValueChange = { newCity = it },
                            placeholder = "Shahar",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    AppPrimaryButton(
                        text = "Manzilni saqlash",
                        isLoading = isSavingAddress,
                        enabled = newAddressLine.isNotBlank(),
                        onClick = {
                            scope.launch {
                                isSavingAddress = true
                                errorMessage = null
                                repository.addAddress(newAddressLine, newHouseNumber, newCity)
                                    .onFailure { errorMessage = it.message }
                                isSavingAddress = false
                            }
                        }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            AppInputField(
                value = notes,
                onValueChange = { notes = it },
                label = "Kuryerga izoh (ixtiyoriy)",
                placeholder = "Masalan: eshik oldiga qoldiring"
            )

            errorMessage?.let {
                Spacer(Modifier.height(16.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = DangerRed.copy(alpha = 0.1f)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.ErrorOutline,
                            contentDescription = null,
                            tint = DangerRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(text = it, style = MaterialTheme.typography.bodySmall.copy(color = DangerRed))
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            AppPrimaryButton(
                text = "Buyurtmani Tasdiqlash",
                isLoading = isPlacingOrder,
                enabled = selectedItems.isNotEmpty(),
                onClick = {
                    scope.launch {
                        isPlacingOrder = true
                        errorMessage = null
                        // Buyurtma faqat server tasdiqlagandan keyin muvaffaqiyatli hisoblanadi
                        val result = repository.placeOrder(
                            paymentMethod = paymentMethod,
                            notes = notes
                        )
                        isPlacingOrder = false
                        result.onSuccess {
                            onOrderSuccess()
                        }.onFailure {
                            errorMessage = it.message ?: "Buyurtmani rasmiylashtirib bo'lmadi"
                        }
                    }
                }
            )

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun PaymentMethodChip(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) PrimaryOrangeSoft else SurfaceLight)
            .border(
                1.5.dp,
                if (isSelected) PrimaryOrange else BorderLight,
                RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                color = if (isSelected) PrimaryOrange else TextPrimary
            ),
            maxLines = 1
        )
    }
}

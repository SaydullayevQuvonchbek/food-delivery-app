package com.fooddelivery.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fooddelivery.components.*
import com.fooddelivery.data.repository.FoodDeliveryRepository
import com.fooddelivery.domain.models.SavedPaymentCard
import com.fooddelivery.theme.*
import com.fooddelivery.util.formatCardNumber
import com.fooddelivery.util.formatExpiry
import com.fooddelivery.util.isValidExpiry
import kotlinx.coroutines.launch

@Composable
fun CardsScreen(
    repository: FoodDeliveryRepository,
    onBackClick: () -> Unit,
    onNavigateToAddCard: () -> Unit
) {
    val cards by repository.savedCards.collectAsState()
    var cardToDelete by remember { mutableStateOf<SavedPaymentCard?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Kartalar serverdan olinadi (ilgari ekranda uchta soxta karta ko'rinardi)
    LaunchedEffect(Unit) { repository.loadCards() }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundWhite)
                .statusBarsPadding()
        ) {
            AppHeaderBar(
                title = "Mening kartalarim",
                onBackClick = onBackClick
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                VirtualCardPreview(cards.firstOrNull { it.isDefault } ?: cards.firstOrNull())

                Spacer(Modifier.height(26.dp))

                Text(
                    text = "Saqlangan kartalar",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(Modifier.height(14.dp))

                if (cards.isEmpty()) {
                    Text(
                        text = "Hali karta qo'shilmagan",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        cards.forEach { card ->
                            SavedCardItem(
                                card = card,
                                // Har bir kartaning o'z o'chirish tugmasi bor
                                onDelete = { cardToDelete = card }
                            )
                        }
                    }
                }

                errorMessage?.let {
                    Spacer(Modifier.height(12.dp))
                    Text(text = it, style = MaterialTheme.typography.bodySmall.copy(color = DangerRed))
                }

                Spacer(Modifier.height(30.dp))

                AppPrimaryButton(
                    text = "Yangi karta qo'shish",
                    onClick = onNavigateToAddCard
                )

                Spacer(Modifier.height(30.dp))
            }
        }

        cardToDelete?.let { card ->
            ConfirmDeleteCardDialog(
                lastFour = card.lastFour,
                onDismiss = { cardToDelete = null },
                onConfirm = {
                    scope.launch {
                        repository.removePaymentCard(card.id)
                            .onFailure { errorMessage = it.message }
                        cardToDelete = null
                    }
                }
            )
        }
    }
}

@Composable
fun VirtualCardPreview(card: SavedPaymentCard? = null) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFFFFA055), Color(0xFFFA5A00))
                )
            )
            .padding(22.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Food Delivery",
                    style = MaterialTheme.typography.titleLarge.copy(color = TextWhite, fontWeight = FontWeight.Bold)
                )
                Text(
                    text = card?.cardType?.uppercase() ?: "CARD",
                    style = MaterialTheme.typography.titleMedium.copy(color = TextWhite, fontWeight = FontWeight.ExtraBold)
                )
            }

            Text(
                text = "••••  ••••  ••••  ${card?.lastFour ?: "----"}",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Card holder name",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextWhite.copy(alpha = 0.7f))
                    )
                    Text(
                        text = card?.cardHolderName?.ifBlank { "-" } ?: "-",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextWhite, fontWeight = FontWeight.Bold),
                        maxLines = 1
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Expiry date",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextWhite.copy(alpha = 0.7f))
                    )
                    Text(
                        text = card?.expiryDate?.ifBlank { "--/--" } ?: "--/--",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextWhite, fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
fun SavedCardItem(card: SavedPaymentCard, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceLight)
            .border(1.dp, BorderLight, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(BackgroundWhite, RoundedCornerShape(10.dp))
                    .border(1.dp, BorderLight, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (card.cardType) {
                        "Paypal" -> "🅿️"
                        "Apple Pay" -> "🍎"
                        else -> "💳"
                    },
                    fontSize = 20.sp
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = card.cardType,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    if (card.isDefault) {
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(PrimaryOrangeSoft)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Asosiy",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = PrimaryOrange,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "**** **** **** ${card.lastFour}  •  ${card.expiryDate}",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                )
            }
        }

        Icon(
            imageVector = Icons.Filled.Delete,
            contentDescription = "O'chirish",
            tint = DangerRed.copy(alpha = 0.8f),
            modifier = Modifier
                .size(22.dp)
                .clickable { onDelete() }
        )
    }
}

@Composable
fun ConfirmDeleteCardDialog(
    lastFour: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Kartani o'chirish",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "**** $lastFour kartasini o'chirmoqchimisiz?",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                )

                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = ButtonShape
                    ) {
                        Text(text = "Bekor qilish", style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary))
                    }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = ButtonShape,
                        colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                    ) {
                        Text(text = "O'chirish", style = MaterialTheme.typography.bodyMedium.copy(color = TextWhite, fontWeight = FontWeight.Bold))
                    }
                }
            }
        }
    }
}

@Composable
fun AddCardScreen(
    repository: FoodDeliveryRepository,
    onBackClick: () -> Unit
) {
    // Maydonlar bo'sh boshlanadi (ilgari soxta karta ma'lumotlari oldindan to'ldirilgan edi)
    var cardName by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val digitsOnly = cardNumber.filter { it.isDigit() }
    val isFormValid = cardName.trim().length >= 3 && digitsOnly.length in 12..19 && isValidExpiry(expiryDate)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .statusBarsPadding()
            .imePadding()
    ) {
        AppHeaderBar(
            title = "Karta qo'shish",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            VirtualCardPreview(
                SavedPaymentCard(
                    cardHolderName = cardName,
                    lastFour = digitsOnly.takeLast(4),
                    expiryDate = expiryDate
                )
            )

            Spacer(Modifier.height(24.dp))

            AppInputField(
                value = cardName,
                onValueChange = {
                    cardName = it
                    errorMessage = null
                },
                label = "Kartadagi ism",
                placeholder = "ISM FAMILIYA"
            )

            Spacer(Modifier.height(16.dp))

            AppInputField(
                value = cardNumber,
                onValueChange = {
                    cardNumber = formatCardNumber(it)
                    errorMessage = null
                },
                label = "Karta raqami",
                placeholder = "8600 1234 5678 9012",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = digitsOnly.isNotEmpty() && digitsOnly.length < 12,
                errorMessage = "Karta raqami to'liq emas"
            )

            Spacer(Modifier.height(16.dp))

            AppInputField(
                value = expiryDate,
                onValueChange = {
                    expiryDate = formatExpiry(it)
                    errorMessage = null
                },
                label = "Amal qilish muddati (MM/YY)",
                placeholder = "12/28",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = expiryDate.length == 5 && !isValidExpiry(expiryDate),
                errorMessage = "Muddat MM/YY ko'rinishida bo'lsin"
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "🔒 Xavfsizlik uchun karta raqami va CVV saqlanmaydi - faqat oxirgi 4 raqam.",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
            )

            errorMessage?.let {
                Spacer(Modifier.height(12.dp))
                Text(text = it, style = MaterialTheme.typography.bodySmall.copy(color = DangerRed))
            }

            Spacer(Modifier.height(30.dp))

            AppPrimaryButton(
                text = "Kartani saqlash",
                enabled = isFormValid,
                isLoading = isSaving,
                onClick = {
                    scope.launch {
                        isSaving = true
                        errorMessage = null
                        val result = repository.addPaymentCard(
                            holderName = cardName,
                            cardNumber = digitsOnly,
                            expiryDate = expiryDate
                        )
                        isSaving = false
                        result.onSuccess { onBackClick() }
                            .onFailure { errorMessage = it.message }
                    }
                }
            )

            Spacer(Modifier.height(30.dp))
        }
    }
}

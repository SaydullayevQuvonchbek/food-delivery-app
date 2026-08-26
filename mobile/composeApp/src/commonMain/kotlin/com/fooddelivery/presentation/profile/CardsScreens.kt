package com.fooddelivery.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fooddelivery.components.*
import com.fooddelivery.data.repository.FoodDeliveryRepository
import com.fooddelivery.domain.models.SavedPaymentCard
import com.fooddelivery.theme.*

@Composable
fun CardsScreen(
    repository: FoodDeliveryRepository,
    onBackClick: () -> Unit,
    onNavigateToAddCard: () -> Unit
) {
    val cards by repository.savedCards.collectAsState()
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var selectedCardIdToDelete by remember { mutableStateOf<Long?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundWhite)
        ) {
            AppHeaderBar(
                title = "Extra Card",
                onBackClick = onBackClick,
                actionIcon = Icons.Filled.Delete,
                onActionClick = {
                    cards.firstOrNull()?.let {
                        selectedCardIdToDelete = it.id
                        showDeleteConfirmDialog = true
                    }
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Virtual Orange Gradient Card Preview
                VirtualCardPreview()

                Spacer(Modifier.height(26.dp))

                Text(
                    text = "Credit card",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(Modifier.height(14.dp))

                // Saved Cards List
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    cards.forEach { card ->
                        SavedCardItem(card = card)
                    }
                }

                Spacer(Modifier.height(30.dp))

                AppPrimaryButton(
                    text = "Add New Card",
                    onClick = onNavigateToAddCard
                )

                Spacer(Modifier.height(30.dp))
            }
        }

        if (showDeleteConfirmDialog) {
            ConfirmDeleteCardDialog(
                onDismiss = { showDeleteConfirmDialog = false },
                onConfirm = {
                    selectedCardIdToDelete?.let { repository.removePaymentCard(it) }
                    showDeleteConfirmDialog = false
                }
            )
        }
    }
}

@Composable
fun VirtualCardPreview() {
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
                    text = "SoCard",
                    style = MaterialTheme.typography.titleLarge.copy(color = TextWhite, fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "VISA",
                    style = MaterialTheme.typography.titleLarge.copy(color = TextWhite, fontWeight = FontWeight.ExtraBold)
                )
            }

            Text(
                text = "••••  ••••  ••••  8374",
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
                Column {
                    Text(
                        text = "Card holder name",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextWhite.copy(alpha = 0.7f))
                    )
                    Text(
                        text = "Albert Stevano Bajefski",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextWhite, fontWeight = FontWeight.Bold)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Expiry date",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextWhite.copy(alpha = 0.7f))
                    )
                    Text(
                        text = "11/24",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextWhite, fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
fun SavedCardItem(card: SavedPaymentCard) {
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(BackgroundWhite, RoundedCornerShape(10.dp))
                    .border(1.dp, BorderLight, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (card.cardType == "MasterCard") "💳" else if (card.cardType == "Paypal") "🅿️" else "",
                    fontSize = 20.sp
                )
            }

            Spacer(Modifier.width(14.dp))

            Column {
                Text(
                    text = card.cardType,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "**** **** ${card.lastFour}",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                )
            }
        }
    }
}

@Composable
fun ConfirmDeleteCardDialog(
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
                    text = "Confirm Delete",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Are you sure to delete this card?",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                )

                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = ButtonShape,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
                    ) {
                        Text(text = "No, I won't", style = MaterialTheme.typography.bodyMedium.copy(color = TextWhite, fontWeight = FontWeight.Bold))
                    }

                    OutlinedButton(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = ButtonShape
                    ) {
                        Text(text = "Yes, Of course", style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary))
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
    var cardName by remember { mutableStateOf("Albert Stevano Bajefski") }
    var cardNumber by remember { mutableStateOf("3822 8293 8292 2356") }
    var expiryDate by remember { mutableStateOf("11/24") }
    var cvv by remember { mutableStateOf("531") }
    var billingAddress by remember { mutableStateOf("New York City") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        AppHeaderBar(
            title = "Extra Card",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            VirtualCardPreview()

            Spacer(Modifier.height(24.dp))

            AppInputField(
                value = cardName,
                onValueChange = { cardName = it },
                label = "Name on Card"
            )

            Spacer(Modifier.height(16.dp))

            AppInputField(
                value = cardNumber,
                onValueChange = { cardNumber = it },
                label = "Card Number"
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppInputField(
                    value = expiryDate,
                    onValueChange = { expiryDate = it },
                    label = "Expiry Date",
                    modifier = Modifier.weight(1f)
                )

                AppInputField(
                    value = cvv,
                    onValueChange = { cvv = it },
                    label = "3-Digit CVV",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(16.dp))

            AppInputField(
                value = billingAddress,
                onValueChange = { billingAddress = it },
                label = "Billing Address"
            )

            Spacer(Modifier.height(30.dp))

            AppPrimaryButton(
                text = "Save Card",
                onClick = {
                    repository.addPaymentCard(
                        SavedPaymentCard(
                            cardHolderName = cardName,
                            cardNumber = cardNumber,
                            lastFour = if (cardNumber.length >= 4) cardNumber.takeLast(4) else "1234",
                            expiryDate = expiryDate,
                            cvv = cvv
                        )
                    )
                    onBackClick()
                }
            )

            Spacer(Modifier.height(30.dp))
        }
    }
}
package com.fooddelivery.presentation.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.fooddelivery.components.AppPrimaryButton
import com.fooddelivery.data.repository.FoodDeliveryRepository
import com.fooddelivery.theme.*

@Composable
fun PaymentAddressScreen(
    repository: FoodDeliveryRepository,
    onBackClick: () -> Unit,
    onOrderSuccess: () -> Unit
) {
    val user by repository.currentUser.collectAsState()
    val cartItems by repository.cartItems.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        AppHeaderBar(
            title = "Payment",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "You deserve better meal",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Item Ordered",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(Modifier.height(14.dp))

            // Ordered Item Snippet Card
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
                    Text(text = "🍔", fontSize = 28.sp)
                }

                Spacer(Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = cartItems.firstOrNull()?.food?.name ?: "Burger With Meat",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "$ 12,230",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = PrimaryOrange,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Text(
                    text = "${cartItems.sumOf { it.quantity }} Items",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                )
            }

            Spacer(Modifier.height(24.dp))

            // Details Transaction Section
            Text(
                text = "Details Transaction",
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
                SummaryRow("Cherry Healthy", "$ 180.000")
                SummaryRow("Driver", "$ 50.000")
                SummaryRow("Tax 10%", "$ 80.390")
                HorizontalDivider(color = BorderLight)
                SummaryRow("Total Price", "$ 100.000", isTotal = true)
            }

            Spacer(Modifier.height(24.dp))

            // Deliver to Section
            Text(
                text = "Deliver to :",
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
                SummaryRow("Name", user.fullName)
                SummaryRow("Phone No.", user.phone)
                SummaryRow("Address", "New York")
                SummaryRow("House No.", "BC54 Berlin")
                SummaryRow("City", "New York City")
            }

            Spacer(Modifier.height(30.dp))

            AppPrimaryButton(
                text = "Checkout Now",
                onClick = {
                    repository.clearCart()
                    onOrderSuccess()
                }
            )

            Spacer(Modifier.height(30.dp))
        }
    }
}
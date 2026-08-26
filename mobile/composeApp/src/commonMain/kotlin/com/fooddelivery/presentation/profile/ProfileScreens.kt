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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fooddelivery.components.*
import com.fooddelivery.data.repository.FoodDeliveryRepository
import com.fooddelivery.theme.*

@Composable
fun ProfileScreen(
    repository: FoodDeliveryRepository,
    onNavigateToPersonalData: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToCards: () -> Unit,
    onNavigateToHelpCenter: () -> Unit,
    onSignOut: () -> Unit
) {
    val user by repository.currentUser.collectAsState()
    var showSignOutModal by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundWhite)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Text(
                text = "Profile Settings",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(20.dp))

            // Avatar with camera badge
            Box(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Box(
                    modifier = Modifier
                        .size(86.dp)
                        .clip(CircleShape)
                        .background(PrimaryOrangeSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (user.fullName.take(1).ifEmpty { "U" }).uppercase(),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryOrange
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(28.dp)
                        .background(PrimaryOrange, CircleShape)
                        .border(2.dp, BackgroundWhite, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PhotoCamera,
                        contentDescription = "Change photo",
                        tint = TextWhite,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = user.fullName.ifEmpty { "User" },
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = user.email.ifEmpty { "user@example.com" },
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(24.dp))

            // My Orders Widget
            val lastOrder by repository.lastCreatedOrder.collectAsState()
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = SurfaceLight,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "My Orders",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = if (lastOrder != null) "Active" else "Empty",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (lastOrder != null) PrimaryOrange else TextSecondary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    if (lastOrder != null) {
                        val order = lastOrder!!
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Order ID ${order.orderNumber}",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PrimaryOrange)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "In Delivery",
                                    style = TextStyle(color = TextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                )
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(PrimaryOrangeSoft),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🍔", fontSize = 22.sp)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = order.items.firstOrNull()?.food?.name ?: "Food Order",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "$ ${order.total.toInt()}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = PrimaryOrange, fontWeight = FontWeight.Bold)
                                )
                            }
                            Text(
                                text = "${order.items.sumOf { it.quantity }} Items",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                        }
                    } else {
                        Text(
                            text = "Hozircha faol buyurtmalaringiz yo'q",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Profile Menu List
            Text(
                text = "Profile",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontWeight = FontWeight.Bold)
            )

            Spacer(Modifier.height(10.dp))

            ProfileMenuItem(
                icon = Icons.Outlined.Person,
                title = "Personal Data",
                onClick = onNavigateToPersonalData
            )
            ProfileMenuItem(
                icon = Icons.Outlined.Settings,
                title = "Settings",
                onClick = onNavigateToSettings
            )
            ProfileMenuItem(
                icon = Icons.Outlined.CreditCard,
                title = "Extra Card",
                onClick = onNavigateToCards
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Support",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontWeight = FontWeight.Bold)
            )

            Spacer(Modifier.height(10.dp))

            ProfileMenuItem(
                icon = Icons.Outlined.HelpOutline,
                title = "Help Center",
                onClick = onNavigateToHelpCenter
            )
            ProfileMenuItem(
                icon = Icons.Outlined.Delete,
                title = "Request Account Deletion",
                onClick = { /* Account deletion */ }
            )
            ProfileMenuItem(
                icon = Icons.Outlined.PersonAdd,
                title = "Add another account",
                onClick = { /* Add account */ }
            )

            Spacer(Modifier.height(24.dp))

            // Sign Out Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(ButtonShape)
                    .background(PrimaryOrangeSoft)
                    .clickable { showSignOutModal = true },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Sign Out",
                        tint = DangerRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Sign Out",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = DangerRed,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(Modifier.height(80.dp))
        }

        if (showSignOutModal) {
            SignOutDialog(
                onDismiss = { showSignOutModal = false },
                onConfirm = {
                    showSignOutModal = false
                    onSignOut()
                }
            )
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextPrimary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(14.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun PersonalDataScreen(
    repository: FoodDeliveryRepository,
    onBackClick: () -> Unit
) {
    val user by repository.currentUser.collectAsState()
    var fullName by remember { mutableStateOf(user.fullName) }
    var dob by remember { mutableStateOf(user.dateOfBirth ?: "") }
    var gender by remember { mutableStateOf(user.gender) }
    var phone by remember { mutableStateOf(user.phone ?: "") }
    var email by remember { mutableStateOf(user.email) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        AppHeaderBar(
            title = "Personal Data",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(PrimaryOrangeSoft),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = PrimaryOrange,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            AppInputField(
                value = fullName,
                onValueChange = { fullName = it },
                label = "Full Name"
            )

            Spacer(Modifier.height(16.dp))

            AppInputField(
                value = dob,
                onValueChange = { dob = it },
                label = "Date of birth"
            )

            Spacer(Modifier.height(16.dp))

            AppInputField(
                value = gender,
                onValueChange = { gender = it },
                label = "Gender",
                trailingIcon = Icons.Filled.KeyboardArrowDown
            )

            Spacer(Modifier.height(16.dp))

            AppInputField(
                value = phone,
                onValueChange = { phone = it },
                label = "Phone"
            )

            Spacer(Modifier.height(16.dp))

            AppInputField(
                value = email,
                onValueChange = { email = it },
                label = "Email"
            )

            Spacer(Modifier.height(30.dp))

            AppPrimaryButton(
                text = "Save",
                onClick = {
                    repository.updateUserProfile(fullName, phone, email, dob, gender)
                    onBackClick()
                }
            )

            Spacer(Modifier.height(30.dp))
        }
    }
}

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit
) {
    var pushEnabled by remember { mutableStateOf(true) }
    var locationEnabled by remember { mutableStateOf(true) }
    var showLanguageModal by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("English (US)") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundWhite)
        ) {
            AppHeaderBar(
                title = "Settings",
                onBackClick = onBackClick
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "PROFILE",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontWeight = FontWeight.Bold)
                )

                Spacer(Modifier.height(12.dp))

                // Push Notification Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Push Notification", style = MaterialTheme.typography.bodyLarge)
                    Switch(
                        checked = pushEnabled,
                        onCheckedChange = { pushEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = BackgroundWhite, checkedTrackColor = PrimaryOrange)
                    )
                }

                // Location Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Location", style = MaterialTheme.typography.bodyLarge)
                    Switch(
                        checked = locationEnabled,
                        onCheckedChange = { locationEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = BackgroundWhite, checkedTrackColor = PrimaryOrange)
                    )
                }

                // Language Select Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showLanguageModal = true }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Language", style = MaterialTheme.typography.bodyLarge)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = selectedLanguage,
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                        )
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "OTHER",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontWeight = FontWeight.Bold)
                )

                Spacer(Modifier.height(12.dp))

                ProfileMenuItem(icon = Icons.Outlined.Info, title = "About Ticketis", onClick = {})
                ProfileMenuItem(icon = Icons.Outlined.Lock, title = "Privacy Policy", onClick = {})
                ProfileMenuItem(icon = Icons.Outlined.Description, title = "Terms and Conditions", onClick = {})
            }
        }

        if (showLanguageModal) {
            LanguageSelectModal(
                currentLanguage = selectedLanguage,
                onSelectLanguage = {
                    selectedLanguage = it
                    showLanguageModal = false
                },
                onDismiss = { showLanguageModal = false }
            )
        }
    }
}

@Composable
fun LanguageSelectModal(
    currentLanguage: String,
    onSelectLanguage: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val languages = listOf("Indonesia", "English (US)", "Thailand", "Chinese")
    var selected by remember { mutableStateOf(currentLanguage) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(BottomSheetShape)
                .background(BackgroundWhite)
                .padding(24.dp),
            shape = BottomSheetShape,
            colors = CardDefaults.cardColors(containerColor = BackgroundWhite)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(40.dp, 4.dp)
                        .background(BorderLight, CircleShape)
                )
                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Select Language",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(Modifier.height(18.dp))

                languages.forEach { lang ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selected = lang }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = lang,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                        )
                        if (selected == lang) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Selected",
                                tint = PrimaryOrange,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                AppPrimaryButton(
                    text = "Select",
                    onClick = { onSelectLanguage(selected) }
                )
            }
        }
    }
}

@Composable
fun HelpCenterScreen(onBackClick: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        AppHeaderBar(
            title = "Help Center",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = "Hi, how we can help you?",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(Modifier.height(16.dp))

            AppInputField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = "Search",
                leadingIcon = Icons.Outlined.Search
            )

            Spacer(Modifier.height(24.dp))

            TopicCard(icon = "💡", title = "General", subtitle = "General questions about delivery")
            Spacer(Modifier.height(12.dp))
            TopicCard(icon = "💳", title = "Payment", subtitle = "Payment methods and refunds")
            Spacer(Modifier.height(12.dp))
            TopicCard(icon = "🛒", title = "Orders", subtitle = "Tracking and order cancellation")
            Spacer(Modifier.height(12.dp))
            TopicCard(icon = "🛵", title = "Delivery", subtitle = "Delivery times and courier information")
        }
    }
}

@Composable
private fun TopicCard(icon: String, title: String, subtitle: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SurfaceLight,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, fontSize = 24.sp)
            Spacer(Modifier.width(14.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(Modifier.height(2.dp))
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
            }
        }
    }
}

@Composable
fun SignOutDialog(
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
                    text = "Sign Out",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Do you want to log out?",
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
                        Text(text = "Cancel", style = MaterialTheme.typography.titleMedium)
                    }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = ButtonShape,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
                    ) {
                        Text(text = "Log Out", style = MaterialTheme.typography.titleMedium.copy(color = TextWhite))
                    }
                }
            }
        }
    }
}
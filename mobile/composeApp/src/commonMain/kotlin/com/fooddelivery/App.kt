package com.fooddelivery

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.fooddelivery.components.AppBottomBar
import com.fooddelivery.data.repository.FoodDeliveryRepository
import com.fooddelivery.presentation.auth.*
import com.fooddelivery.presentation.cart.CartScreen
import com.fooddelivery.presentation.cart.PaymentAddressScreen
import com.fooddelivery.presentation.chat.AudioCallScreen
import com.fooddelivery.presentation.chat.ChatListScreen
import com.fooddelivery.presentation.chat.ChatScreen
import com.fooddelivery.presentation.food_detail.FoodDetailScreen
import com.fooddelivery.presentation.home.HomeScreen
import com.fooddelivery.presentation.navigation.Screen
import com.fooddelivery.presentation.notifications.NotificationScreen
import com.fooddelivery.presentation.onboarding.OnboardingScreen
import com.fooddelivery.presentation.profile.*
import com.fooddelivery.presentation.search.SearchScreen
import com.fooddelivery.presentation.tracking.DeliveryTrackingScreen
import com.fooddelivery.theme.FoodDeliveryTheme

@Composable
fun App() {
    val repository = remember { FoodDeliveryRepository() }
    val cartItems by repository.cartItems.collectAsState()
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Onboarding) }

    FoodDeliveryTheme {
        val showBottomBar = when (currentScreen) {
            is Screen.Home, is Screen.Cart, is Screen.ChatList, is Screen.Profile -> true
            else -> false
        }

        val currentRoute = when (currentScreen) {
            is Screen.Home -> "home"
            is Screen.Cart -> "cart"
            is Screen.ChatList -> "chat"
            is Screen.Profile -> "profile"
            else -> ""
        }

        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    AppBottomBar(
                        currentRoute = currentRoute,
                        cartItemCount = cartItems.sumOf { it.quantity },
                        onNavigate = { route ->
                            currentScreen = when (route) {
                                "home" -> Screen.Home
                                "cart" -> Screen.Cart
                                "chat" -> Screen.ChatList
                                "profile" -> Screen.Profile
                                else -> Screen.Home
                            }
                        }
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(if (showBottomBar) paddingValues else androidx.compose.foundation.layout.PaddingValues())
            ) {
                when (val screen = currentScreen) {
                    is Screen.Onboarding -> {
                        OnboardingScreen(
                            onFinish = { currentScreen = Screen.Login }
                        )
                    }
                    is Screen.Login -> {
                        LoginScreen(
                            onLoginSuccess = { currentScreen = Screen.Home },
                            onNavigateToRegister = { currentScreen = Screen.Register },
                            onNavigateToForgotPassword = { currentScreen = Screen.ForgotPassword }
                        )
                    }
                    is Screen.Register -> {
                        RegisterScreen(
                            onRegisterSuccess = { currentScreen = Screen.Home },
                            onNavigateToLogin = { currentScreen = Screen.Login }
                        )
                    }
                    is Screen.ForgotPassword -> {
                        ForgotPasswordScreen(
                            onBackClick = { currentScreen = Screen.Login },
                            onContinueToOtp = { currentScreen = Screen.OtpVerification }
                        )
                    }
                    is Screen.OtpVerification -> {
                        OtpVerificationScreen(
                            onBackClick = { currentScreen = Screen.ForgotPassword },
                            onVerified = { currentScreen = Screen.ResetPassword }
                        )
                    }
                    is Screen.ResetPassword -> {
                        ResetPasswordScreen(
                            onBackClick = { currentScreen = Screen.OtpVerification },
                            onSuccess = { currentScreen = Screen.Login }
                        )
                    }
                    is Screen.Home -> {
                        HomeScreen(
                            repository = repository,
                            onNavigateToSearch = { currentScreen = Screen.Search },
                            onNavigateToNotifications = { currentScreen = Screen.Notifications },
                            onNavigateToFoodDetail = { foodId -> currentScreen = Screen.FoodDetail(foodId) }
                        )
                    }
                    is Screen.FoodDetail -> {
                        FoodDetailScreen(
                            foodId = screen.foodId,
                            repository = repository,
                            onBackClick = { currentScreen = Screen.Home },
                            onNavigateToCart = { currentScreen = Screen.Cart }
                        )
                    }
                    is Screen.Search -> {
                        SearchScreen(
                            repository = repository,
                            onBackClick = { currentScreen = Screen.Home },
                            onNavigateToFoodDetail = { foodId -> currentScreen = Screen.FoodDetail(foodId) }
                        )
                    }
                    is Screen.Cart -> {
                        CartScreen(
                            repository = repository,
                            onNavigateToHome = { currentScreen = Screen.Home },
                            onNavigateToCheckout = { currentScreen = Screen.Checkout }
                        )
                    }
                    is Screen.Checkout -> {
                        PaymentAddressScreen(
                            repository = repository,
                            onBackClick = { currentScreen = Screen.Cart },
                            onOrderSuccess = { currentScreen = Screen.DeliveryTracking }
                        )
                    }
                    is Screen.DeliveryTracking -> {
                        DeliveryTrackingScreen(
                            repository = repository,
                            onBackClick = { currentScreen = Screen.Home },
                            onNavigateToChat = { courierId: Long -> currentScreen = Screen.Chat(courierId) },
                            onNavigateToCall = { courierId: Long -> currentScreen = Screen.AudioCall(courierId) }
                        )
                    }
                    is Screen.ChatList -> {
                        ChatListScreen(
                            repository = repository,
                            onNavigateToChat = { courierId: Long -> currentScreen = Screen.Chat(courierId) }
                        )
                    }
                    is Screen.Chat -> {
                        ChatScreen(
                            courierId = screen.courierId,
                            repository = repository,
                            onBackClick = { currentScreen = Screen.ChatList },
                            onNavigateToCall = { courierId: Long -> currentScreen = Screen.AudioCall(courierId) }
                        )
                    }
                    is Screen.AudioCall -> {
                        AudioCallScreen(
                            courierId = screen.courierId,
                            onEndCall = { currentScreen = Screen.Chat(screen.courierId) }
                        )
                    }
                    is Screen.Notifications -> {
                        NotificationScreen(
                            repository = repository,
                            onBackClick = { currentScreen = Screen.Home }
                        )
                    }
                    is Screen.Profile -> {
                        ProfileScreen(
                            repository = repository,
                            onNavigateToPersonalData = { currentScreen = Screen.PersonalData },
                            onNavigateToSettings = { currentScreen = Screen.Settings },
                            onNavigateToCards = { currentScreen = Screen.Cards },
                            onNavigateToHelpCenter = { currentScreen = Screen.HelpCenter },
                            onSignOut = { currentScreen = Screen.Login }
                        )
                    }
                    is Screen.PersonalData -> {
                        PersonalDataScreen(
                            repository = repository,
                            onBackClick = { currentScreen = Screen.Profile }
                        )
                    }
                    is Screen.Settings -> {
                        SettingsScreen(
                            onBackClick = { currentScreen = Screen.Profile }
                        )
                    }
                    is Screen.Cards -> {
                        CardsScreen(
                            repository = repository,
                            onBackClick = { currentScreen = Screen.Profile },
                            onNavigateToAddCard = { currentScreen = Screen.AddCard }
                        )
                    }
                    is Screen.AddCard -> {
                        AddCardScreen(
                            repository = repository,
                            onBackClick = { currentScreen = Screen.Cards }
                        )
                    }
                    is Screen.HelpCenter -> {
                        HelpCenterScreen(
                            onBackClick = { currentScreen = Screen.Profile }
                        )
                    }
                }
            }
        }
    }
}
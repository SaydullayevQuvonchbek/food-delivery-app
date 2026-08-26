package com.fooddelivery

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.fooddelivery.components.AppBottomBar
import com.fooddelivery.components.BackHandler
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
import com.fooddelivery.presentation.orders.OrdersHistoryScreen
import com.fooddelivery.presentation.profile.*
import com.fooddelivery.presentation.search.SearchScreen
import com.fooddelivery.presentation.tracking.DeliveryTrackingScreen
import com.fooddelivery.theme.FoodDeliveryTheme

@Composable
fun App() {
    val repository = remember { FoodDeliveryRepository() }
    val cartItems by repository.cartItems.collectAsState()
    val isLoggedIn by repository.isLoggedIn.collectAsState()
    val hasSeenOnboarding by repository.hasSeenOnboarding.collectAsState()

    val backStack = remember {
        mutableStateListOf<Screen>(
            when {
                isLoggedIn -> Screen.Home
                hasSeenOnboarding -> Screen.Login
                else -> Screen.Onboarding
            }
        )
    }

    // Sessiya tugasa (masalan token eskirsa) foydalanuvchi kirish ekraniga qaytariladi
    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn && backStack.lastOrNull() !is Screen.Login &&
            backStack.lastOrNull() !is Screen.Onboarding &&
            backStack.lastOrNull() !is Screen.Register &&
            backStack.lastOrNull() !is Screen.ForgotPassword &&
            backStack.lastOrNull() !is Screen.OtpVerification &&
            backStack.lastOrNull() !is Screen.ResetPassword
        ) {
            backStack.clear()
            backStack.add(if (hasSeenOnboarding) Screen.Login else Screen.Onboarding)
        }
    }

    val currentScreen = backStack.lastOrNull() ?: Screen.Home

    fun navigateTo(screen: Screen, clearStack: Boolean = false) {
        if (clearStack) {
            backStack.clear()
        }
        backStack.add(screen)
    }

    fun popBack(): Boolean {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
            return true
        }
        return false
    }

    BackHandler(enabled = backStack.size > 1) {
        popBack()
    }

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
                            val targetScreen = when (route) {
                                "home" -> Screen.Home
                                "cart" -> Screen.Cart
                                "chat" -> Screen.ChatList
                                "profile" -> Screen.Profile
                                else -> Screen.Home
                            }
                            if (currentScreen != targetScreen) {
                                backStack.clear()
                                backStack.add(targetScreen)
                            }
                        }
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(if (showBottomBar) paddingValues else PaddingValues())
            ) {
                when (val screen = currentScreen) {
                    is Screen.Onboarding -> {
                        OnboardingScreen(
                            onFinish = {
                                repository.completeOnboarding()
                                navigateTo(Screen.Login, clearStack = true)
                            }
                        )
                    }
                    is Screen.Login -> {
                        LoginScreen(
                            repository = repository,
                            onLoginSuccess = { navigateTo(Screen.Home, clearStack = true) },
                            onNavigateToRegister = { navigateTo(Screen.Register) },
                            onNavigateToForgotPassword = { navigateTo(Screen.ForgotPassword) }
                        )
                    }
                    is Screen.Register -> {
                        RegisterScreen(
                            repository = repository,
                            onRegisterSuccess = { navigateTo(Screen.Home, clearStack = true) },
                            onNavigateToLogin = { popBack() }
                        )
                    }
                    is Screen.ForgotPassword -> {
                        ForgotPasswordScreen(
                            repository = repository,
                            onBackClick = { popBack() },
                            onContinueToOtp = { navigateTo(Screen.OtpVerification) }
                        )
                    }
                    is Screen.OtpVerification -> {
                        OtpVerificationScreen(
                            repository = repository,
                            onBackClick = { popBack() },
                            onVerified = { navigateTo(Screen.ResetPassword) }
                        )
                    }
                    is Screen.ResetPassword -> {
                        ResetPasswordScreen(
                            repository = repository,
                            onBackClick = { popBack() },
                            onSuccess = { navigateTo(Screen.Login, clearStack = true) }
                        )
                    }
                    is Screen.Home -> {
                        HomeScreen(
                            repository = repository,
                            onNavigateToSearch = { navigateTo(Screen.Search) },
                            onNavigateToNotifications = { navigateTo(Screen.Notifications) },
                            onNavigateToFoodDetail = { foodId -> navigateTo(Screen.FoodDetail(foodId)) }
                        )
                    }
                    is Screen.FoodDetail -> {
                        FoodDetailScreen(
                            foodId = screen.foodId,
                            repository = repository,
                            onBackClick = { popBack() },
                            onNavigateToCart = { navigateTo(Screen.Cart) },
                            onNavigateToFood = { foodId -> navigateTo(Screen.FoodDetail(foodId)) }
                        )
                    }
                    is Screen.Search -> {
                        SearchScreen(
                            repository = repository,
                            onBackClick = { popBack() },
                            onNavigateToFoodDetail = { foodId -> navigateTo(Screen.FoodDetail(foodId)) }
                        )
                    }
                    is Screen.Cart -> {
                        CartScreen(
                            repository = repository,
                            onNavigateToHome = { navigateTo(Screen.Home, clearStack = true) },
                            onNavigateToCheckout = { navigateTo(Screen.Checkout) }
                        )
                    }
                    is Screen.Checkout -> {
                        PaymentAddressScreen(
                            repository = repository,
                            onBackClick = { popBack() },
                            onOrderSuccess = { navigateTo(Screen.DeliveryTracking, clearStack = true) }
                        )
                    }
                    is Screen.DeliveryTracking -> {
                        DeliveryTrackingScreen(
                            repository = repository,
                            onBackClick = { navigateTo(Screen.Home, clearStack = true) },
                            onNavigateToChat = { navigateTo(Screen.ChatList) },
                            onNavigateToCall = { name -> navigateTo(Screen.AudioCall(name)) }
                        )
                    }
                    is Screen.ChatList -> {
                        ChatListScreen(
                            repository = repository,
                            onNavigateToChat = { chatId -> navigateTo(Screen.Chat(chatId)) }
                        )
                    }
                    is Screen.Chat -> {
                        ChatScreen(
                            chatId = screen.chatId,
                            repository = repository,
                            onBackClick = { popBack() },
                            onNavigateToCall = { name -> navigateTo(Screen.AudioCall(name)) }
                        )
                    }
                    is Screen.AudioCall -> {
                        AudioCallScreen(
                            contactName = screen.contactName,
                            onEndCall = { popBack() }
                        )
                    }
                    is Screen.Notifications -> {
                        NotificationScreen(
                            repository = repository,
                            onBackClick = { popBack() }
                        )
                    }
                    is Screen.Profile -> {
                        ProfileScreen(
                            repository = repository,
                            onNavigateToPersonalData = { navigateTo(Screen.PersonalData) },
                            onNavigateToSettings = { navigateTo(Screen.Settings) },
                            onNavigateToCards = { navigateTo(Screen.Cards) },
                            onNavigateToHelpCenter = { navigateTo(Screen.HelpCenter) },
                            onNavigateToOrdersHistory = { navigateTo(Screen.OrdersHistory) },
                            onNavigateToTracking = { navigateTo(Screen.DeliveryTracking) },
                            onSignOut = {
                                repository.logout()
                                navigateTo(Screen.Login, clearStack = true)
                            }
                        )
                    }
                    is Screen.OrdersHistory -> {
                        OrdersHistoryScreen(
                            repository = repository,
                            onBackClick = { popBack() },
                            onNavigateToTracking = { navigateTo(Screen.DeliveryTracking) }
                        )
                    }
                    is Screen.PersonalData -> {
                        PersonalDataScreen(
                            repository = repository,
                            onBackClick = { popBack() }
                        )
                    }
                    is Screen.Settings -> {
                        SettingsScreen(
                            onBackClick = { popBack() }
                        )
                    }
                    is Screen.Cards -> {
                        CardsScreen(
                            repository = repository,
                            onBackClick = { popBack() },
                            onNavigateToAddCard = { navigateTo(Screen.AddCard) }
                        )
                    }
                    is Screen.AddCard -> {
                        AddCardScreen(
                            repository = repository,
                            onBackClick = { popBack() }
                        )
                    }
                    is Screen.HelpCenter -> {
                        HelpCenterScreen(
                            onBackClick = { popBack() }
                        )
                    }
                }
            }
        }
    }
}

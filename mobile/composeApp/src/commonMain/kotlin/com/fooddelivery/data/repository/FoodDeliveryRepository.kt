package com.fooddelivery.data.repository

import com.fooddelivery.data.network.ApiConfig
import com.fooddelivery.data.network.createHttpClient
import com.fooddelivery.domain.models.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class FoodDeliveryRepository {

    private val httpClient = createHttpClient()
    private val scope = CoroutineScope(Dispatchers.Default)
    private val jsonParser = Json { ignoreUnknownKeys = true; isLenient = true }

    private val defaultCategories = listOf(
        Category(1, "Burger", "🍔", isSelected = true),
        Category(2, "Taco", "🌮"),
        Category(3, "Drink", "🥤"),
        Category(4, "Pizza", "🍕")
    )

    private val defaultFoods = listOf(
        Food(
            id = 1,
            categoryId = 1,
            name = "Burger With Meat 🍔",
            description = "Burger With Meat is a typical food from our restaurant that is much in demand by many people, this is very recommended for you.",
            price = 12230.0,
            originalPrice = 15000.0,
            rating = 4.5,
            reviewCount = 240,
            distance = "190m",
            deliveryTime = "20 - 30 min",
            isFreeDelivery = true,
            imageUrl = "burger1.png",
            isFavorite = true
        ),
        Food(
            id = 2,
            categoryId = 1,
            name = "Ordinary Burgers",
            description = "Classic beef burger with fresh crisp lettuce, juicy tomatoes, and our signature secret sauce.",
            price = 17230.0,
            rating = 4.9,
            reviewCount = 180,
            distance = "190m",
            deliveryTime = "15 - 25 min",
            isFreeDelivery = true,
            imageUrl = "burger2.png",
            isFavorite = false
        ),
        Food(
            id = 3,
            categoryId = 1,
            name = "Double Cheese Burger",
            description = "Double grilled patties with double melted cheddar cheese, caramelized onions and pickles.",
            price = 19500.0,
            rating = 4.8,
            reviewCount = 310,
            distance = "250m",
            deliveryTime = "20 - 35 min",
            isFreeDelivery = false,
            imageUrl = "burger3.png",
            isFavorite = true
        ),
        Food(
            id = 4,
            categoryId = 2,
            name = "Crispy Beef Taco",
            description = "Crispy corn taco shell packed with seasoned ground beef, shredded cheddar, lettuce and salsa.",
            price = 9800.0,
            rating = 4.7,
            reviewCount = 95,
            distance = "300m",
            deliveryTime = "15 - 20 min",
            isFreeDelivery = true,
            imageUrl = "taco1.png",
            isFavorite = false
        ),
        Food(
            id = 5,
            categoryId = 3,
            name = "Fresh Citrus Cooler",
            description = "Chilled freshly squeezed orange and lime juice with crushed ice and mint.",
            price = 4500.0,
            rating = 4.9,
            reviewCount = 75,
            distance = "190m",
            deliveryTime = "10 - 15 min",
            isFreeDelivery = true,
            imageUrl = "drink1.png",
            isFavorite = false
        ),
        Food(
            id = 6,
            categoryId = 4,
            name = "Pepperoni Supreme Pizza",
            description = "Hand-tossed pizza crust loaded with rich tomato sauce, mozzarella and spicy pepperoni.",
            price = 28000.0,
            rating = 4.9,
            reviewCount = 420,
            distance = "400m",
            deliveryTime = "25 - 40 min",
            isFreeDelivery = true,
            imageUrl = "pizza1.png",
            isFavorite = true
        )
    )

    private var allFoodsCache = defaultFoods
    private var currentSelectedCategoryId: Long = 1L

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _hasSeenOnboarding = MutableStateFlow(false)
    val hasSeenOnboarding: StateFlow<Boolean> = _hasSeenOnboarding.asStateFlow()

    private val _currentUser = MutableStateFlow(
        User(
            id = 1,
            fullName = "Albert Stevano Bajefski",
            email = "Albertstevano@gmail.com",
            phone = "+1 325-433-7656",
            dateOfBirth = "19/06/1999",
            gender = "Male",
            avatarUrl = ""
        )
    )
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    private val _categories = MutableStateFlow(defaultCategories)
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _foods = MutableStateFlow(defaultFoods.filter { it.categoryId == 1L })
    val foods: StateFlow<List<Food>> = _foods.asStateFlow()

    private val _cartItems = MutableStateFlow(
        listOf(
            CartItem(defaultFoods[0], quantity = 2, isSelected = true),
            CartItem(defaultFoods[1], quantity = 1, isSelected = true)
        )
    )
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _savedCards = MutableStateFlow(
        listOf(
            SavedPaymentCard(
                id = 1,
                cardHolderName = "Albert Stevano Bajefski",
                cardNumber = "3822 8293 8292 2356",
                lastFour = "8374",
                expiryDate = "11/24",
                cvv = "531",
                cardType = "MasterCard",
                isDefault = true
            ),
            SavedPaymentCard(
                id = 2,
                cardHolderName = "Albert Stevano",
                cardNumber = "**** **** 0783 7873",
                lastFour = "7873",
                cardType = "MasterCard",
                isDefault = false
            ),
            SavedPaymentCard(
                id = 3,
                cardHolderName = "Albert Stevano",
                cardNumber = "**** **** 0582 4672",
                lastFour = "4672",
                cardType = "Paypal",
                isDefault = false
            )
        )
    )
    val savedCards: StateFlow<List<SavedPaymentCard>> = _savedCards.asStateFlow()

    private val _notifications = MutableStateFlow(
        listOf(
            AppNotificationItem(1, "30% Special Discount!", "Special promotion only valid today", "Today", "DISCOUNT"),
            AppNotificationItem(2, "Your Order Has Been Taken by the Driver", "Recently", "Today", "ORDER_TAKEN"),
            AppNotificationItem(3, "Your Order Has Been Canceled", "19 Jun 2023", "Today", "ORDER_CANCELED"),
            AppNotificationItem(4, "35% Special Discount!", "Special promotion only valid today", "Yesterday", "DISCOUNT"),
            AppNotificationItem(5, "Account Setup Successfull!", "Special promotion only valid today", "Yesterday", "ACCOUNT"),
            AppNotificationItem(6, "Special Offer! 60% Off", "Special offer for new account, valid until 20 Nov 2022", "Yesterday", "SPECIAL_OFFER"),
            AppNotificationItem(7, "Credit Card Connected", "Special promotion only valid today", "Yesterday", "CARD")
        )
    )
    val notifications: StateFlow<List<AppNotificationItem>> = _notifications.asStateFlow()

    private val _recentSearches = MutableStateFlow(
        listOf("Burgers", "Fast food", "Dessert", "French", "Pastry")
    )
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

    val currentCourier = Courier(
        id = 101,
        name = "Cristopert Dastin",
        badgeId = "ID 213752",
        phone = "+1 234 567 8900",
        avatarUrl = "",
        rating = 4.9
    )

    private val _chatMessages = MutableStateFlow(
        listOf(
            ChatMessage(1, 101, "Just to order", "09.00", isFromMe = false),
            ChatMessage(2, 1, "Okay, for what level of spiciness?", "09.15", isFromMe = true),
            ChatMessage(3, 101, "Okay, Wait a minute 🙏", "09.00", isFromMe = false),
            ChatMessage(4, 1, "Okay, I'm waiting 🙌", "09.15", isFromMe = true)
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _lastCreatedOrder = MutableStateFlow<Order?>(null)
    val lastCreatedOrder: StateFlow<Order?> = _lastCreatedOrder.asStateFlow()

    init {
        fetchRemoteData()
    }

    fun completeOnboarding() {
        _hasSeenOnboarding.value = true
    }

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response: HttpResponse = httpClient.post("${ApiConfig.BASE_URL}/auth/login") {
                contentType(ContentType.Application.Json)
                headers { append(HttpHeaders.Accept, "application/json") }
                setBody(LoginRequest(email = email, password = password))
            }
            val responseText = response.bodyAsText()
            val apiResp: ApiResponse<AuthData> = jsonParser.decodeFromString(responseText)

            if (apiResp.success && apiResp.data?.user != null) {
                val token = apiResp.data.token ?: ""
                val userWithToken = apiResp.data.user.copy(token = token)
                ApiConfig.AUTH_TOKEN = token
                _currentUser.value = userWithToken
                _isLoggedIn.value = true
                _hasSeenOnboarding.value = true
                Result.success(userWithToken)
            } else {
                val errorMsg = apiResp.message ?: apiResp.errors?.values?.firstOrNull()?.firstOrNull() ?: "Email yoki parol xato"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Tarmoqqa ulanishda xatolik yuz berdi"))
        }
    }

    suspend fun register(fullName: String, email: String, password: String, phone: String? = null): Result<User> {
        return try {
            val response: HttpResponse = httpClient.post("${ApiConfig.BASE_URL}/auth/register") {
                contentType(ContentType.Application.Json)
                headers { append(HttpHeaders.Accept, "application/json") }
                setBody(
                    RegisterRequest(
                        fullName = fullName,
                        email = email,
                        password = password,
                        phone = phone
                    )
                )
            }
            val responseText = response.bodyAsText()
            val apiResp: ApiResponse<AuthData> = jsonParser.decodeFromString(responseText)

            if (apiResp.success && apiResp.data?.user != null) {
                val token = apiResp.data.token ?: ""
                val userWithToken = apiResp.data.user.copy(token = token)
                ApiConfig.AUTH_TOKEN = token
                _currentUser.value = userWithToken
                _isLoggedIn.value = true
                _hasSeenOnboarding.value = true
                Result.success(userWithToken)
            } else {
                val errorMsg = apiResp.message ?: apiResp.errors?.values?.firstOrNull()?.firstOrNull() ?: "Ro'yxatdan o'tishda xatolik"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Serverga ulanib bo'lmadi"))
        }
    }

    suspend fun forgotPassword(email: String): Result<String> {
        return try {
            val response: HttpResponse = httpClient.post("${ApiConfig.BASE_URL}/auth/forgot-password") {
                contentType(ContentType.Application.Json)
                headers { append(HttpHeaders.Accept, "application/json") }
                setBody(ForgotPasswordRequest(email = email))
            }
            val responseText = response.bodyAsText()
            val apiResp: ApiResponse<Map<String, String>> = jsonParser.decodeFromString(responseText)
            Result.success(apiResp.message ?: "Tasdiqlash kodi yuborildi")
        } catch (e: Exception) {
            Result.success("Tasdiqlash kodi 9627 yuborildi")
        }
    }

    suspend fun verifyOtp(email: String, otp: String): Result<String> {
        return try {
            val response: HttpResponse = httpClient.post("${ApiConfig.BASE_URL}/auth/verify-otp") {
                contentType(ContentType.Application.Json)
                headers { append(HttpHeaders.Accept, "application/json") }
                setBody(VerifyOtpRequest(email = email, otp = otp))
            }
            val responseText = response.bodyAsText()
            val apiResp: ApiResponse<String> = jsonParser.decodeFromString(responseText)
            if (apiResp.success) {
                Result.success("Kod tasdiqlandi")
            } else {
                Result.failure(Exception(apiResp.message ?: "Tasdiqlash kodi noto'g'ri"))
            }
        } catch (e: Exception) {
            if (otp == "9627") {
                Result.success("Kod tasdiqlandi")
            } else {
                Result.failure(Exception("Tasdiqlash kodi noto'g'ri (Demo kod: 9627)"))
            }
        }
    }

    suspend fun resetPassword(email: String, password: String): Result<String> {
        return try {
            val response: HttpResponse = httpClient.post("${ApiConfig.BASE_URL}/auth/reset-password") {
                contentType(ContentType.Application.Json)
                headers { append(HttpHeaders.Accept, "application/json") }
                setBody(
                    ResetPasswordRequest(
                        email = email,
                        password = password,
                        passwordConfirmation = password
                    )
                )
            }
            val responseText = response.bodyAsText()
            val apiResp: ApiResponse<String> = jsonParser.decodeFromString(responseText)
            Result.success(apiResp.message ?: "Parol muvaffaqiyatli yangilandi")
        } catch (e: Exception) {
            Result.success("Parol muvaffaqiyatli yangilandi")
        }
    }

    fun logout() {
        ApiConfig.AUTH_TOKEN = null
        _isLoggedIn.value = false
        _currentUser.value = User()
    }

    fun fetchRemoteData() {
        scope.launch {
            try {
                val catResponse: ApiResponse<List<Category>> = httpClient.get("${ApiConfig.BASE_URL}/categories") {
                    headers { append(HttpHeaders.Accept, "application/json") }
                }.body()
                if (catResponse.success && !catResponse.data.isNullOrEmpty()) {
                    _categories.value = catResponse.data.mapIndexed { index, cat ->
                        cat.copy(isSelected = if (currentSelectedCategoryId == 0L) index == 0 else cat.id == currentSelectedCategoryId)
                    }
                }
            } catch (e: Exception) {
                // Keep default categories
            }

            try {
                val foodResponse: ApiResponse<List<Food>> = httpClient.get("${ApiConfig.BASE_URL}/foods") {
                    headers { append(HttpHeaders.Accept, "application/json") }
                }.body()
                if (foodResponse.success && !foodResponse.data.isNullOrEmpty()) {
                    allFoodsCache = foodResponse.data
                    applyFoodFilter(currentSelectedCategoryId)
                }
            } catch (e: Exception) {
                // Keep default foods
            }
        }
    }

    fun selectCategory(categoryId: Long) {
        currentSelectedCategoryId = categoryId
        _categories.update { list ->
            list.map { it.copy(isSelected = it.id == categoryId) }
        }
        applyFoodFilter(categoryId)
    }

    private fun applyFoodFilter(categoryId: Long) {
        _foods.update {
            if (categoryId == 0L) {
                allFoodsCache
            } else {
                val filtered = allFoodsCache.filter { it.categoryId == categoryId }
                if (filtered.isNotEmpty()) filtered else allFoodsCache
            }
        }
    }

    fun searchFoods(query: String) {
        if (query.isBlank()) {
            applyFoodFilter(currentSelectedCategoryId)
        } else {
            _foods.update {
                allFoodsCache.filter {
                    it.name.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true)
                }
            }
        }
    }

    fun toggleFavorite(foodId: Long) {
        allFoodsCache = allFoodsCache.map { if (it.id == foodId) it.copy(isFavorite = !it.isFavorite) else it }
        _foods.update { list ->
            list.map { if (it.id == foodId) it.copy(isFavorite = !it.isFavorite) else it }
        }
    }

    fun addToCart(food: Food, quantity: Int = 1) {
        _cartItems.update { list ->
            val existing = list.find { it.food.id == food.id }
            if (existing != null) {
                list.map { if (it.food.id == food.id) it.copy(quantity = it.quantity + quantity) else it }
            } else {
                list + CartItem(food = food, quantity = quantity)
            }
        }
    }

    fun updateCartItemQuantity(foodId: Long, delta: Int) {
        _cartItems.update { list ->
            list.mapNotNull { item ->
                if (item.food.id == foodId) {
                    val newQty = item.quantity + delta
                    if (newQty > 0) item.copy(quantity = newQty) else null
                } else {
                    item
                }
            }
        }
    }

    fun removeCartItem(foodId: Long) {
        _cartItems.update { list -> list.filterNot { it.food.id == foodId } }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun addRecentSearch(query: String) {
        if (query.isNotBlank()) {
            _recentSearches.update { list -> (listOf(query) + list.filterNot { it.equals(query, ignoreCase = true) }).take(10) }
        }
    }

    fun removeRecentSearch(query: String) {
        _recentSearches.update { list -> list.filterNot { it == query } }
    }

    fun clearRecentSearches() {
        _recentSearches.value = emptyList()
    }

    fun sendChatMessage(text: String) {
        if (text.isNotBlank()) {
            _chatMessages.update { list ->
                list + ChatMessage(
                    id = list.size.toLong() + 1,
                    senderId = _currentUser.value.id,
                    text = text,
                    timestamp = "Now",
                    isFromMe = true
                )
            }
        }
    }

    fun updateUserProfile(name: String, phone: String, email: String, dob: String, gender: String) {
        _currentUser.update {
            it.copy(
                fullName = name,
                phone = phone,
                email = email,
                dateOfBirth = dob,
                gender = gender
            )
        }
    }

    fun addPaymentCard(card: SavedPaymentCard) {
        _savedCards.update { list -> list + card.copy(id = list.size.toLong() + 1) }
    }

    fun removePaymentCard(cardId: Long) {
        _savedCards.update { list -> list.filterNot { it.id == cardId } }
    }

    fun placeOrder(address: String = "New York City, BC54 Berlin", paymentMethod: String = "card", notes: String = ""): Order {
        val currentCart = _cartItems.value
        val subtotal = currentCart.sumOf { it.totalPrice }
        val deliveryFee = 2000.0
        val discount = 3000.0
        val total = (subtotal + deliveryFee - discount).coerceAtLeast(0.0)

        val newOrder = Order(
            id = (1000..9999).random().toLong(),
            orderNumber = "ORD-${(100000..999999).random()}",
            items = currentCart,
            status = OrderStatus.PREPARING,
            deliveryAddress = address,
            subtotal = subtotal,
            deliveryFee = deliveryFee,
            discount = discount,
            tax = 0.0,
            total = total,
            courier = currentCourier,
            createdAt = "Just now"
        )

        _lastCreatedOrder.value = newOrder
        clearCart()

        // Asynchronously post to backend with typed body
        scope.launch {
            try {
                httpClient.post("${ApiConfig.BASE_URL}/orders") {
                    contentType(ContentType.Application.Json)
                    headers {
                        append(HttpHeaders.Accept, "application/json")
                        ApiConfig.AUTH_TOKEN?.let { token ->
                            append(HttpHeaders.Authorization, "Bearer $token")
                        }
                    }
                    setBody(
                        CreateOrderRequest(
                            addressId = 1,
                            paymentMethod = paymentMethod,
                            items = currentCart.map {
                                CreateOrderItemRequest(foodId = it.food.id, quantity = it.quantity)
                            }
                        )
                    )
                }
            } catch (e: Exception) {
                // Keep local order active even if offline
            }
        }

        return newOrder
    }
}
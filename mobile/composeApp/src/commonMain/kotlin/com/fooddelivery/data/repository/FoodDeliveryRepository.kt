package com.fooddelivery.data.repository

import com.fooddelivery.data.network.ApiConfig
import com.fooddelivery.data.network.AppJson
import com.fooddelivery.data.network.createHttpClient
import com.fooddelivery.data.storage.getLocalStorage
import com.fooddelivery.domain.models.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString

class FoodDeliveryRepository {

    private val httpClient = createHttpClient()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val jsonParser = AppJson

    private val storage by lazy {
        try {
            getLocalStorage()
        } catch (e: Exception) {
            null
        }
    }

    // ---------------- Offline uchun zaxira katalog ----------------

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
            imageUrl = "burger1.png"
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
            imageUrl = "burger2.png"
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
            imageUrl = "burger3.png"
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
            imageUrl = "taco1.png"
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
            imageUrl = "drink1.png"
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
            imageUrl = "pizza1.png"
        )
    )

    // ---------------- Holat (State) ----------------

    private val _allFoods = MutableStateFlow(defaultFoods)
    val allFoods: StateFlow<List<Food>> = _allFoods.asStateFlow()

    private var currentSelectedCategoryId: Long = 0L

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _hasSeenOnboarding = MutableStateFlow(false)
    val hasSeenOnboarding: StateFlow<Boolean> = _hasSeenOnboarding.asStateFlow()

    private val _currentUser = MutableStateFlow(User())
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    private val _categories = MutableStateFlow(defaultCategories)
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _foods = MutableStateFlow(defaultFoods)
    val foods: StateFlow<List<Food>> = _foods.asStateFlow()

    private val _isCatalogLoading = MutableStateFlow(false)
    val isCatalogLoading: StateFlow<Boolean> = _isCatalogLoading.asStateFlow()

    private val _catalogError = MutableStateFlow<String?>(null)
    val catalogError: StateFlow<String?> = _catalogError.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _savedCards = MutableStateFlow<List<SavedPaymentCard>>(emptyList())
    val savedCards: StateFlow<List<SavedPaymentCard>> = _savedCards.asStateFlow()

    private val _addresses = MutableStateFlow<List<Address>>(emptyList())
    val addresses: StateFlow<List<Address>> = _addresses.asStateFlow()

    private val _notifications = MutableStateFlow<List<AppNotificationItem>>(emptyList())
    val notifications: StateFlow<List<AppNotificationItem>> = _notifications.asStateFlow()

    private val _unreadNotifications = MutableStateFlow(0)
    val unreadNotifications: StateFlow<Int> = _unreadNotifications.asStateFlow()

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

    private val _conversations = MutableStateFlow<List<ChatConversation>>(emptyList())
    val conversations: StateFlow<List<ChatConversation>> = _conversations.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _activeChatId = MutableStateFlow<Long?>(null)
    val activeChatId: StateFlow<Long?> = _activeChatId.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _activeOrder = MutableStateFlow<Order?>(null)
    val activeOrder: StateFlow<Order?> = _activeOrder.asStateFlow()

    /** Parolni tiklash oqimi uchun (email + bir martalik token) */
    private var pendingResetEmail: String = ""
    private var pendingResetToken: String = ""
    val resetEmail: String get() = pendingResetEmail

    /** Savatga qo'llanilgan promo kod */
    private val _appliedPromo = MutableStateFlow<PromoValidationData?>(null)
    val appliedPromo: StateFlow<PromoValidationData?> = _appliedPromo.asStateFlow()

    init {
        restoreSession()
        refreshCatalog()
        if (_isLoggedIn.value) refreshUserData()
    }

    // ---------------- Sessiya ----------------

    private fun restoreSession() {
        try {
            val s = storage ?: return
            val savedToken = s.getString(KEY_TOKEN)
            val isLogged = s.getBoolean(KEY_LOGGED_IN, false)
            val onboardingDone = s.getBoolean(KEY_ONBOARDING, false)
            val userJson = s.getString(KEY_USER)

            if (onboardingDone) _hasSeenOnboarding.value = true

            if (!savedToken.isNullOrBlank() && isLogged) {
                ApiConfig.AUTH_TOKEN = savedToken
                _isLoggedIn.value = true
                _hasSeenOnboarding.value = true
                if (!userJson.isNullOrBlank()) {
                    try {
                        _currentUser.value = jsonParser.decodeFromString(userJson)
                    } catch (e: Exception) {
                        // Eski formatdagi ma'lumot - e'tiborsiz qoldiramiz
                    }
                }
            }

            s.getString(KEY_RECENT_SEARCHES)?.let { raw ->
                _recentSearches.value = raw.split(SEARCH_SEPARATOR).filter { it.isNotBlank() }.take(10)
            }
        } catch (e: Exception) {
            // Xotira mavjud bo'lmasa ham ilova ishlashda davom etadi
        }
    }

    private fun persistSession(user: User, token: String) {
        try {
            storage?.let { s ->
                s.setString(KEY_TOKEN, token)
                s.setBoolean(KEY_LOGGED_IN, true)
                s.setBoolean(KEY_ONBOARDING, true)
                s.setString(KEY_USER, jsonParser.encodeToString(user))
            }
        } catch (e: Exception) {
        }
    }

    fun completeOnboarding() {
        _hasSeenOnboarding.value = true
        try {
            storage?.setBoolean(KEY_ONBOARDING, true)
        } catch (e: Exception) {
        }
    }

    // ---------------- Auth ----------------

    suspend fun login(email: String, password: String): Result<User> = runCatchingNetwork(
        fallbackMessage = "Serverga ulanib bo'lmadi. Internetni tekshiring."
    ) {
        val response = httpClient.post("${ApiConfig.BASE_URL}/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(email = email, password = password))
        }
        val apiResp: ApiResponse<AuthData> = decode(response)

        val user = apiResp.data?.user
        if (!response.status.isSuccess() || !apiResp.success || user == null) {
            return@runCatchingNetwork Result.failure(Exception(apiResp.firstError("Email yoki parol xato")))
        }

        val token = apiResp.data?.token.orEmpty()
        applyAuthenticatedUser(user.copy(token = token), token)
        Result.success(user)
    }

    suspend fun register(fullName: String, email: String, password: String, phone: String? = null): Result<User> =
        runCatchingNetwork(fallbackMessage = "Serverga ulanib bo'lmadi. Internetni tekshiring.") {
            val response = httpClient.post("${ApiConfig.BASE_URL}/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(
                    RegisterRequest(
                        fullName = fullName,
                        email = email,
                        password = password,
                        phone = phone?.takeIf { it.isNotBlank() }
                    )
                )
            }
            val apiResp: ApiResponse<AuthData> = decode(response)

            val user = apiResp.data?.user
            if (!response.status.isSuccess() || !apiResp.success || user == null) {
                return@runCatchingNetwork Result.failure(Exception(apiResp.firstError("Ro'yxatdan o'tishda xatolik")))
            }

            val token = apiResp.data?.token.orEmpty()
            applyAuthenticatedUser(user.copy(token = token), token)
            Result.success(user)
        }

    private fun applyAuthenticatedUser(user: User, token: String) {
        ApiConfig.AUTH_TOKEN = token
        _currentUser.value = user
        _isLoggedIn.value = true
        _hasSeenOnboarding.value = true
        persistSession(user, token)
        refreshCatalog()
        refreshUserData()
    }

    suspend fun forgotPassword(email: String): Result<String> = runCatchingNetwork(
        fallbackMessage = "Tasdiqlash kodini yuborib bo'lmadi. Internetni tekshiring."
    ) {
        val response = httpClient.post("${ApiConfig.BASE_URL}/auth/forgot-password") {
            contentType(ContentType.Application.Json)
            setBody(ForgotPasswordRequest(email = email.trim()))
        }
        val apiResp: ApiResponse<OtpRequestData> = decode(response)

        if (!response.status.isSuccess() || !apiResp.success) {
            return@runCatchingNetwork Result.failure(Exception(apiResp.firstError("Bunday email topilmadi")))
        }

        pendingResetEmail = email.trim()
        pendingResetToken = ""
        Result.success(apiResp.message ?: "Tasdiqlash kodi yuborildi")
    }

    suspend fun verifyOtp(otp: String): Result<String> = runCatchingNetwork(
        fallbackMessage = "Kodni tekshirib bo'lmadi. Internetni tekshiring."
    ) {
        if (pendingResetEmail.isBlank()) {
            return@runCatchingNetwork Result.failure(Exception("Avval email manzilingizni kiriting"))
        }

        val response = httpClient.post("${ApiConfig.BASE_URL}/auth/verify-otp") {
            contentType(ContentType.Application.Json)
            setBody(VerifyOtpRequest(email = pendingResetEmail, otp = otp))
        }
        val apiResp: ApiResponse<ResetTokenData> = decode(response)

        if (!response.status.isSuccess() || !apiResp.success) {
            return@runCatchingNetwork Result.failure(Exception(apiResp.firstError("Tasdiqlash kodi noto'g'ri")))
        }

        pendingResetToken = apiResp.data?.resetToken.orEmpty()
        Result.success("Kod tasdiqlandi")
    }

    suspend fun resetPassword(password: String): Result<String> = runCatchingNetwork(
        fallbackMessage = "Parolni yangilab bo'lmadi. Internetni tekshiring."
    ) {
        if (pendingResetEmail.isBlank() || pendingResetToken.isBlank()) {
            return@runCatchingNetwork Result.failure(Exception("Avval tasdiqlash kodini kiriting"))
        }

        val response = httpClient.post("${ApiConfig.BASE_URL}/auth/reset-password") {
            contentType(ContentType.Application.Json)
            setBody(
                ResetPasswordRequest(
                    email = pendingResetEmail,
                    resetToken = pendingResetToken,
                    password = password,
                    passwordConfirmation = password
                )
            )
        }
        val apiResp: ApiResponse<String> = decode(response)

        if (!response.status.isSuccess() || !apiResp.success) {
            return@runCatchingNetwork Result.failure(Exception(apiResp.firstError("Parolni yangilab bo'lmadi")))
        }

        pendingResetEmail = ""
        pendingResetToken = ""
        Result.success(apiResp.message ?: "Parol muvaffaqiyatli yangilandi")
    }

    fun logout() {
        val token = ApiConfig.AUTH_TOKEN
        // Avval lokal token tozalanadi - aks holda so'rovga ikkita Authorization sarlavhasi tushadi
        ApiConfig.AUTH_TOKEN = null

        scope.launch {
            try {
                if (!token.isNullOrBlank()) {
                    httpClient.post("${ApiConfig.BASE_URL}/auth/logout") {
                        header(HttpHeaders.Authorization, "Bearer $token")
                    }
                }
            } catch (e: Exception) {
                // Server javob bermasa ham lokal sessiya tozalanadi
            }
        }

        _isLoggedIn.value = false
        _currentUser.value = User()
        _cartItems.value = emptyList()
        _savedCards.value = emptyList()
        _addresses.value = emptyList()
        _notifications.value = emptyList()
        _unreadNotifications.value = 0
        _conversations.value = emptyList()
        _chatMessages.value = emptyList()
        _activeChatId.value = null
        _orders.value = emptyList()
        _activeOrder.value = null
        _appliedPromo.value = null

        try {
            storage?.let { s ->
                s.remove(KEY_TOKEN)
                s.setBoolean(KEY_LOGGED_IN, false)
                s.remove(KEY_USER)
            }
        } catch (e: Exception) {
        }
    }

    // ---------------- Katalog ----------------

    fun refreshCatalog() {
        scope.launch {
            _isCatalogLoading.value = true
            var failed = false

            try {
                val response = httpClient.get("${ApiConfig.BASE_URL}/categories")
                val apiResp: ApiResponse<List<Category>> = decode(response)
                if (apiResp.success && !apiResp.data.isNullOrEmpty()) {
                    _categories.value = apiResp.data.map { cat ->
                        cat.copy(
                            icon = cat.icon.ifBlank { iconForCategory(cat.name) },
                            isSelected = cat.id == currentSelectedCategoryId
                        )
                    }
                }
            } catch (e: Exception) {
                failed = true
            }

            try {
                val response = httpClient.get("${ApiConfig.BASE_URL}/foods")
                val apiResp: ApiResponse<List<Food>> = decode(response)
                if (apiResp.success && apiResp.data != null) {
                    _allFoods.value = apiResp.data
                    applyFoodFilter(currentSelectedCategoryId)
                }
            } catch (e: Exception) {
                failed = true
            }

            _catalogError.value = if (failed) "Ma'lumotlarni yangilab bo'lmadi. Oflayn ma'lumotlar ko'rsatilmoqda." else null
            _isCatalogLoading.value = false
        }
    }

    private fun iconForCategory(name: String): String = when (name.lowercase().trim()) {
        "burger" -> "🍔"
        "taco" -> "🌮"
        "drink", "ichimlik" -> "🥤"
        "pizza" -> "🍕"
        "salad", "salat" -> "🥗"
        "dessert", "shirinlik" -> "🍰"
        else -> "🍲"
    }

    fun selectCategory(categoryId: Long) {
        currentSelectedCategoryId = categoryId
        _categories.update { list -> list.map { it.copy(isSelected = it.id == categoryId) } }
        applyFoodFilter(categoryId)
    }

    private fun applyFoodFilter(categoryId: Long) {
        // Kategoriya bo'sh bo'lsa - bo'sh ro'yxat ko'rsatiladi (avval butun ro'yxat qaytarilardi va
        // foydalanuvchiga filtr ishlamayotgandek tuyulardi)
        _foods.value = if (categoryId == 0L) _allFoods.value else _allFoods.value.filter { it.categoryId == categoryId }
    }

    /** Qidiruv butun katalog bo'yicha ishlaydi, joriy kategoriya bilan cheklanmaydi */
    fun searchFoods(query: String): List<Food> {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return emptyList()
        return _allFoods.value.filter {
            it.name.contains(trimmed, ignoreCase = true) || it.description.contains(trimmed, ignoreCase = true)
        }
    }

    fun findFoodById(foodId: Long): Food? = _allFoods.value.find { it.id == foodId }

    fun recommendedFor(food: Food): List<Food> =
        _allFoods.value.filter { it.categoryId == food.categoryId && it.id != food.id }.ifEmpty {
            _allFoods.value.filter { it.id != food.id }
        }.take(6)

    fun toggleFavorite(foodId: Long) {
        _allFoods.update { list -> list.map { if (it.id == foodId) it.copy(isFavorite = !it.isFavorite) else it } }
        applyFoodFilter(currentSelectedCategoryId)
    }

    // ---------------- Qidiruv tarixi ----------------

    fun addRecentSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.length < 2) return
        _recentSearches.update { list ->
            (listOf(trimmed) + list.filterNot { it.equals(trimmed, ignoreCase = true) }).take(10)
        }
        persistRecentSearches()
    }

    fun removeRecentSearch(query: String) {
        _recentSearches.update { list -> list.filterNot { it == query } }
        persistRecentSearches()
    }

    fun clearRecentSearches() {
        _recentSearches.value = emptyList()
        persistRecentSearches()
    }

    private fun persistRecentSearches() {
        try {
            storage?.setString(KEY_RECENT_SEARCHES, _recentSearches.value.joinToString(SEARCH_SEPARATOR))
        } catch (e: Exception) {
        }
    }

    // ---------------- Savat ----------------

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

    fun setCartItemSelected(foodId: Long, selected: Boolean) {
        _cartItems.update { list ->
            list.map { if (it.food.id == foodId) it.copy(isSelected = selected) else it }
        }
    }

    fun removeCartItem(foodId: Long) {
        _cartItems.update { list -> list.filterNot { it.food.id == foodId } }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        _appliedPromo.value = null
    }

    fun selectedCartItems(): List<CartItem> = _cartItems.value.filter { it.isSelected }

    // ---------------- Promo kod ----------------

    suspend fun applyPromoCode(code: String, subtotal: Double): Result<PromoValidationData> = runCatchingNetwork(
        fallbackMessage = "Promo kodni tekshirib bo'lmadi. Internetni tekshiring."
    ) {
        if (code.isBlank()) {
            return@runCatchingNetwork Result.failure(Exception("Promo kodni kiriting"))
        }

        val response = httpClient.post("${ApiConfig.BASE_URL}/promotions/validate") {
            contentType(ContentType.Application.Json)
            setBody(ValidatePromoRequest(code = code.trim().uppercase(), subtotal = subtotal))
        }
        val apiResp: ApiResponse<PromoValidationData> = decode(response)

        val data = apiResp.data
        if (!response.status.isSuccess() || !apiResp.success || data == null) {
            _appliedPromo.value = null
            return@runCatchingNetwork Result.failure(Exception(apiResp.firstError("Promo kod amal qilmaydi")))
        }

        _appliedPromo.value = data
        Result.success(data)
    }

    fun clearPromoCode() {
        _appliedPromo.value = null
    }

    // ---------------- Foydalanuvchi ma'lumotlari ----------------

    fun refreshUserData() {
        if (!_isLoggedIn.value) return
        scope.launch { loadProfile() }
        scope.launch { loadCards() }
        scope.launch { loadAddresses() }
        scope.launch { loadNotifications() }
        scope.launch { loadConversations() }
        scope.launch { loadOrders() }
    }

    private suspend fun loadProfile() {
        try {
            val response = httpClient.get("${ApiConfig.BASE_URL}/auth/profile")
            if (response.status == HttpStatusCode.Unauthorized) {
                handleUnauthorized()
                return
            }
            val apiResp: ApiResponse<User> = decode(response)
            apiResp.data?.let { user ->
                val token = ApiConfig.AUTH_TOKEN.orEmpty()
                _currentUser.value = user.copy(token = token)
                persistSession(_currentUser.value, token)
            }
        } catch (e: Exception) {
        }
    }

    suspend fun loadCards() {
        try {
            val response = httpClient.get("${ApiConfig.BASE_URL}/cards")
            val apiResp: ApiResponse<List<SavedPaymentCard>> = decode(response)
            if (apiResp.success && apiResp.data != null) {
                _savedCards.value = apiResp.data
            }
        } catch (e: Exception) {
        }
    }

    suspend fun loadAddresses() {
        try {
            val response = httpClient.get("${ApiConfig.BASE_URL}/addresses")
            val apiResp: ApiResponse<List<Address>> = decode(response)
            if (apiResp.success && apiResp.data != null) {
                _addresses.value = apiResp.data
            }
        } catch (e: Exception) {
        }
    }

    suspend fun loadNotifications() {
        try {
            val response = httpClient.get("${ApiConfig.BASE_URL}/notifications")
            val apiResp: ApiResponse<List<ServerNotification>> = decode(response)
            if (apiResp.success && apiResp.data != null) {
                _notifications.value = apiResp.data.map { it.toDomain() }
                _unreadNotifications.value = apiResp.data.count { !it.isRead }
            }
        } catch (e: Exception) {
        }
    }

    fun markNotificationsRead() {
        if (_unreadNotifications.value == 0) return
        _notifications.update { list -> list.map { it.copy(isRead = true) } }
        _unreadNotifications.value = 0
        scope.launch {
            try {
                httpClient.post("${ApiConfig.BASE_URL}/notifications/read-all")
            } catch (e: Exception) {
            }
        }
    }

    suspend fun loadOrders() {
        try {
            val response = httpClient.get("${ApiConfig.BASE_URL}/orders")
            val apiResp: ApiResponse<List<ServerOrder>> = decode(response)
            if (apiResp.success && apiResp.data != null) {
                val mapped = apiResp.data.map { it.toDomain() }
                _orders.value = mapped
                _activeOrder.value = mapped.firstOrNull {
                    it.status == OrderStatus.PREPARING || it.status == OrderStatus.ON_THE_WAY || it.status == OrderStatus.PENDING
                }
            }
        } catch (e: Exception) {
        }
    }

    // ---------------- Profil ----------------

    suspend fun updateUserProfile(name: String, phone: String, dob: String, gender: String): Result<User> =
        runCatchingNetwork(fallbackMessage = "Profilni saqlab bo'lmadi. Internetni tekshiring.") {
            val response = httpClient.put("${ApiConfig.BASE_URL}/auth/profile") {
                contentType(ContentType.Application.Json)
                setBody(
                    UpdateProfileRequest(
                        fullName = name.trim(),
                        phone = phone.trim().takeIf { it.isNotBlank() },
                        dateOfBirth = dob.trim().takeIf { it.isNotBlank() },
                        gender = gender.takeIf { it in listOf("Male", "Female", "Other") }
                    )
                )
            }
            val apiResp: ApiResponse<User> = decode(response)

            val user = apiResp.data
            if (!response.status.isSuccess() || !apiResp.success || user == null) {
                return@runCatchingNetwork Result.failure(Exception(apiResp.firstError("Profilni saqlab bo'lmadi")))
            }

            val token = ApiConfig.AUTH_TOKEN.orEmpty()
            _currentUser.value = user.copy(token = token)
            persistSession(_currentUser.value, token)
            Result.success(user)
        }

    // ---------------- Kartalar ----------------

    suspend fun addPaymentCard(
        holderName: String,
        cardNumber: String,
        expiryDate: String,
        cardType: String? = null
    ): Result<SavedPaymentCard> = runCatchingNetwork(
        fallbackMessage = "Kartani saqlab bo'lmadi. Internetni tekshiring."
    ) {
        val response = httpClient.post("${ApiConfig.BASE_URL}/cards") {
            contentType(ContentType.Application.Json)
            setBody(
                CreateCardRequest(
                    cardHolderName = holderName.trim(),
                    cardNumber = cardNumber.filter { it.isDigit() },
                    expiryDate = expiryDate.trim(),
                    cardType = cardType
                )
            )
        }
        val apiResp: ApiResponse<SavedPaymentCard> = decode(response)

        val card = apiResp.data
        if (!response.status.isSuccess() || !apiResp.success || card == null) {
            return@runCatchingNetwork Result.failure(Exception(apiResp.firstError("Kartani saqlab bo'lmadi")))
        }

        // Server bergan haqiqiy ID bilan ishlaymiz (lokal indeks emas)
        _savedCards.update { list -> list + card }
        Result.success(card)
    }

    suspend fun removePaymentCard(cardId: Long): Result<Unit> = runCatchingNetwork(
        fallbackMessage = "Kartani o'chirib bo'lmadi. Internetni tekshiring."
    ) {
        val response = httpClient.delete("${ApiConfig.BASE_URL}/cards/$cardId")
        val apiResp: ApiResponse<String> = decode(response)

        if (!response.status.isSuccess() || !apiResp.success) {
            return@runCatchingNetwork Result.failure(Exception(apiResp.firstError("Kartani o'chirib bo'lmadi")))
        }

        _savedCards.update { list -> list.filterNot { it.id == cardId } }
        Result.success(Unit)
    }

    // ---------------- Manzillar ----------------

    suspend fun addAddress(addressLine: String, houseNumber: String, city: String, label: String = "Home"): Result<Address> =
        runCatchingNetwork(fallbackMessage = "Manzilni saqlab bo'lmadi. Internetni tekshiring.") {
            val response = httpClient.post("${ApiConfig.BASE_URL}/addresses") {
                contentType(ContentType.Application.Json)
                setBody(
                    CreateAddressRequest(
                        label = label,
                        addressLine = addressLine.trim(),
                        houseNumber = houseNumber.trim(),
                        city = city.trim(),
                        isDefault = _addresses.value.isEmpty()
                    )
                )
            }
            val apiResp: ApiResponse<Address> = decode(response)

            val address = apiResp.data
            if (!response.status.isSuccess() || !apiResp.success || address == null) {
                return@runCatchingNetwork Result.failure(Exception(apiResp.firstError("Manzilni saqlab bo'lmadi")))
            }

            _addresses.update { list -> list.map { it.copy(isDefault = false) } + address }
            Result.success(address)
        }

    fun defaultAddress(): Address? = _addresses.value.firstOrNull { it.isDefault } ?: _addresses.value.firstOrNull()

    // ---------------- Buyurtma ----------------

    /**
     * Buyurtma serverga yuboriladi va faqat server tasdiqlagach muvaffaqiyatli hisoblanadi.
     * Ilgari buyurtma lokal yaratilib, server xatosi foydalanuvchidan yashirilardi.
     */
    suspend fun placeOrder(
        paymentMethod: String = "card",
        notes: String = ""
    ): Result<Order> = runCatchingNetwork(fallbackMessage = "Buyurtmani yuborib bo'lmadi. Internetni tekshiring.") {
        val items = selectedCartItems()
        if (items.isEmpty()) {
            return@runCatchingNetwork Result.failure(Exception("Savat bo'sh. Avval taom tanlang."))
        }

        val response = httpClient.post("${ApiConfig.BASE_URL}/orders") {
            contentType(ContentType.Application.Json)
            setBody(
                CreateOrderRequest(
                    addressId = defaultAddress()?.id,
                    paymentMethod = paymentMethod,
                    promoCode = _appliedPromo.value?.code,
                    notes = notes.takeIf { it.isNotBlank() },
                    items = items.map { CreateOrderItemRequest(foodId = it.food.id, quantity = it.quantity) }
                )
            )
        }

        if (response.status == HttpStatusCode.Unauthorized) {
            handleUnauthorized()
            return@runCatchingNetwork Result.failure(Exception("Sessiya muddati tugagan. Qaytadan kiring."))
        }

        val apiResp: ApiResponse<ServerOrder> = decode(response)
        val serverOrder = apiResp.data
        if (!response.status.isSuccess() || !apiResp.success || serverOrder == null) {
            return@runCatchingNetwork Result.failure(Exception(apiResp.firstError("Buyurtmani rasmiylashtirib bo'lmadi")))
        }

        val order = serverOrder.toDomain(fallbackAddress = defaultAddress()?.addressLine.orEmpty())
        _activeOrder.value = order
        _orders.update { listOf(order) + it }

        // Faqat buyurtmaga kirgan mahsulotlar savatdan olib tashlanadi
        val orderedIds = items.map { it.food.id }.toSet()
        _cartItems.update { list -> list.filterNot { it.food.id in orderedIds } }
        _appliedPromo.value = null

        scope.launch { loadConversations() }
        Result.success(order)
    }

    // ---------------- Chat ----------------

    suspend fun loadConversations() {
        try {
            val response = httpClient.get("${ApiConfig.BASE_URL}/chats")
            val apiResp: ApiResponse<List<ServerChat>> = decode(response)
            if (apiResp.success && apiResp.data != null) {
                _conversations.value = apiResp.data.map { it.toDomain() }
                if (_activeChatId.value == null) {
                    _activeChatId.value = apiResp.data.firstOrNull()?.id
                }
            }
        } catch (e: Exception) {
        }
    }

    suspend fun openChat(chatId: Long) {
        _activeChatId.value = chatId
        loadMessages(chatId)
        _conversations.update { list -> list.map { if (it.id == chatId) it.copy(unreadCount = 0) else it } }
    }

    suspend fun loadMessages(chatId: Long) {
        try {
            val response = httpClient.get("${ApiConfig.BASE_URL}/chats/$chatId/messages")
            val apiResp: ApiResponse<List<ServerMessage>> = decode(response)
            if (apiResp.success && apiResp.data != null) {
                val myId = _currentUser.value.id
                _chatMessages.value = apiResp.data.map { it.toDomain(myId) }
            }
        } catch (e: Exception) {
        }
    }

    suspend fun sendChatMessage(text: String): Result<Unit> = runCatchingNetwork(
        fallbackMessage = "Xabar yuborilmadi. Internetni tekshiring."
    ) {
        val trimmed = text.trim()
        if (trimmed.isBlank()) return@runCatchingNetwork Result.success(Unit)

        if (_activeChatId.value == null) {
            loadConversations()
        }
        val targetChatId = _activeChatId.value
            ?: return@runCatchingNetwork Result.failure(Exception("Suhbat topilmadi"))

        // Optimistik ko'rsatish - xabar darhol ekranda paydo bo'ladi
        val optimistic = ChatMessage(
            id = -(_chatMessages.value.size + 1).toLong(),
            senderId = _currentUser.value.id,
            text = trimmed,
            timestamp = "",
            isFromMe = true
        )
        _chatMessages.update { it + optimistic }

        val response = httpClient.post("${ApiConfig.BASE_URL}/chats/$targetChatId/messages") {
            contentType(ContentType.Application.Json)
            setBody(SendMessageRequest(text = trimmed))
        }

        if (!response.status.isSuccess()) {
            _chatMessages.update { list -> list.filterNot { it.id == optimistic.id } }
            val apiResp: ApiResponse<ServerMessage> = decode(response)
            return@runCatchingNetwork Result.failure(Exception(apiResp.firstError("Xabar yuborilmadi")))
        }

        loadMessages(targetChatId)
        Result.success(Unit)
    }

    // ---------------- Yordamchilar ----------------

    private fun handleUnauthorized() {
        // Token eskirgan - foydalanuvchini qayta kirishga yo'naltiramiz
        logout()
    }

    private suspend inline fun <reified T> decode(response: HttpResponse): ApiResponse<T> {
        val text = response.bodyAsText()
        return try {
            jsonParser.decodeFromString(text)
        } catch (e: Exception) {
            ApiResponse(
                success = false,
                message = if (response.status.value >= 500) {
                    "Serverda xatolik (${response.status.value})"
                } else {
                    "Server javobini o'qib bo'lmadi"
                }
            )
        }
    }

    private inline fun <T> runCatchingNetwork(
        fallbackMessage: String,
        block: () -> Result<T>
    ): Result<T> = try {
        block()
    } catch (e: Exception) {
        Result.failure(Exception(e.message?.takeIf { it.isNotBlank() } ?: fallbackMessage))
    }

    private companion object {
        const val KEY_TOKEN = "auth_token"
        const val KEY_LOGGED_IN = "is_logged_in"
        const val KEY_ONBOARDING = "onboarding_completed"
        const val KEY_USER = "user_json"
        const val KEY_RECENT_SEARCHES = "recent_searches"
        const val SEARCH_SEPARATOR = "|~|"
    }
}

private fun <T> ApiResponse<T>.firstError(default: String): String =
    errors?.values?.firstOrNull()?.firstOrNull() ?: message?.takeIf { it.isNotBlank() } ?: default

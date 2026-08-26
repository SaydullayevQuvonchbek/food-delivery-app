package com.fooddelivery.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean = true,
    val message: String? = null,
    val data: T? = null
)

@Serializable
data class AuthData(
    val user: User? = null,
    val token: String? = null
)

@Serializable
data class User(
    val id: Long = 0,
    @SerialName("full_name")
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    @SerialName("date_of_birth")
    val dateOfBirth: String = "",
    val gender: String = "Male",
    @SerialName("avatar_url")
    val avatarUrl: String = "",
    val token: String = ""
)

@Serializable
data class Category(
    val id: Long,
    val name: String,
    val icon: String = "🍔",
    @SerialName("is_selected")
    val isSelected: Boolean = false
)

@Serializable
data class Food(
    val id: Long,
    @SerialName("category_id")
    val categoryId: Long = 1,
    val name: String,
    val description: String = "",
    val price: Double = 0.0,
    @SerialName("original_price")
    val originalPrice: Double? = null,
    val rating: Double = 4.9,
    @SerialName("review_count")
    val reviewCount: Int = 120,
    val distance: String = "190m",
    @SerialName("delivery_time")
    val deliveryTime: String = "20 - 30 min",
    @SerialName("is_free_delivery")
    val isFreeDelivery: Boolean = true,
    @SerialName("image_url")
    val imageUrl: String = "",
    @SerialName("is_favorite")
    val isFavorite: Boolean = false
)

@Serializable
data class CartItem(
    val food: Food,
    val quantity: Int = 1,
    val isSelected: Boolean = true,
    val specialNotes: String = ""
) {
    val totalPrice: Double get() = food.price * quantity
}

@Serializable
enum class OrderStatus {
    PENDING,
    PREPARING,
    ON_THE_WAY,
    DELIVERED,
    CANCELED
}

@Serializable
data class Order(
    val id: Long = 0,
    @SerialName("order_number")
    val orderNumber: String = "",
    val items: List<CartItem> = emptyList(),
    val status: OrderStatus = OrderStatus.PENDING,
    @SerialName("delivery_address")
    val deliveryAddress: String = "New York City, BC54 Berlin",
    val subtotal: Double = 0.0,
    @SerialName("delivery_fee")
    val deliveryFee: Double = 0.0,
    val discount: Double = 0.0,
    val tax: Double = 0.0,
    val total: Double = 0.0,
    val courier: Courier? = null,
    @SerialName("created_at")
    val createdAt: String = ""
)

@Serializable
data class Courier(
    val id: Long = 101,
    val name: String = "Cristopert Dastin",
    @SerialName("badge_id")
    val badgeId: String = "ID 213752",
    val phone: String = "+1 234 567 8900",
    @SerialName("avatar_url")
    val avatarUrl: String = "",
    val rating: Double = 4.9,
    val currentLat: Double = 37.7749,
    val currentLng: Double = -122.4194
)

@Serializable
data class Address(
    val id: Long = 0,
    val label: String = "Home",
    @SerialName("address_line")
    val addressLine: String = "New York City",
    @SerialName("house_number")
    val houseNumber: String = "BC54 Berlin",
    val city: String = "New York",
    @SerialName("is_default")
    val isDefault: Boolean = true
)

@Serializable
data class SavedPaymentCard(
    val id: Long = 0,
    @SerialName("card_holder_name")
    val cardHolderName: String = "",
    @SerialName("card_number")
    val cardNumber: String = "",
    @SerialName("last_four")
    val lastFour: String = "",
    @SerialName("expiry_date")
    val expiryDate: String = "",
    val cvv: String = "",
    @SerialName("card_type")
    val cardType: String = "MasterCard",
    @SerialName("is_default")
    val isDefault: Boolean = false
)

@Serializable
data class AppNotificationItem(
    val id: Long,
    val title: String,
    val message: String,
    @SerialName("time_ago")
    val timeAgo: String = "Today",
    val type: String = "DISCOUNT",
    @SerialName("is_read")
    val isRead: Boolean = false
)

@Serializable
data class ChatMessage(
    val id: Long = 0,
    @SerialName("sender_id")
    val senderId: Long = 0,
    val text: String = "",
    val timestamp: String = "Now",
    @SerialName("is_from_me")
    val isFromMe: Boolean = true,
    @SerialName("is_read")
    val isRead: Boolean = true
)
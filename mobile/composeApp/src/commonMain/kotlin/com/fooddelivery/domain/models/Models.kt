package com.fooddelivery.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long = 0,
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val dateOfBirth: String = "",
    val gender: String = "Male",
    val avatarUrl: String = "",
    val token: String = ""
)

@Serializable
data class Category(
    val id: Long,
    val name: String,
    val icon: String, // Emoji or asset name (e.g. "🍔", "🌮", "🥤", "🍕")
    val isSelected: Boolean = false
)

@Serializable
data class Food(
    val id: Long,
    val categoryId: Long,
    val name: String,
    val description: String,
    val price: Double,
    val originalPrice: Double? = null,
    val rating: Double = 4.9,
    val reviewCount: Int = 120,
    val distance: String = "190m",
    val deliveryTime: String = "20 - 30 min",
    val isFreeDelivery: Boolean = true,
    val imageUrl: String,
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
    val id: Long,
    val orderNumber: String,
    val items: List<CartItem>,
    val status: OrderStatus = OrderStatus.PENDING,
    val deliveryAddress: String = "",
    val subtotal: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val discount: Double = 0.0,
    val tax: Double = 0.0,
    val total: Double = 0.0,
    val courier: Courier? = null,
    val createdAt: String = ""
)

@Serializable
data class Courier(
    val id: Long,
    val name: String,
    val badgeId: String,
    val phone: String,
    val avatarUrl: String,
    val rating: Double = 4.9,
    val currentLat: Double = 37.7749,
    val currentLng: Double = -122.4194
)

@Serializable
data class Address(
    val id: Long = 0,
    val label: String = "Home",
    val addressLine: String = "New York City",
    val houseNumber: String = "BC54 Berlin",
    val city: String = "New York",
    val isDefault: Boolean = true
)

@Serializable
data class SavedPaymentCard(
    val id: Long = 0,
    val cardHolderName: String = "",
    val cardNumber: String = "",
    val lastFour: String = "",
    val expiryDate: String = "",
    val cvv: String = "",
    val cardType: String = "MasterCard",
    val isDefault: Boolean = false
)

@Serializable
data class AppNotificationItem(
    val id: Long,
    val title: String,
    val message: String,
    val timeAgo: String,
    val type: String, // "DISCOUNT", "ORDER_TAKEN", "ORDER_CANCELED", "ACCOUNT", "SPECIAL_OFFER", "CARD"
    val isRead: Boolean = false
)

@Serializable
data class ChatMessage(
    val id: Long,
    val senderId: Long,
    val text: String,
    val timestamp: String,
    val isFromMe: Boolean,
    val isRead: Boolean = true
)

@Serializable
data class ChatConversation(
    val id: Long,
    val courier: Courier,
    val lastMessage: String,
    val time: String,
    val unreadCount: Int = 0
)
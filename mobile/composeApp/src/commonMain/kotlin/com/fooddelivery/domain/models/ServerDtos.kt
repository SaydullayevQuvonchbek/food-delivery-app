package com.fooddelivery.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Server javoblari mobil modellardan farq qiladi (order_number, total_amount, body, sender_id ...).
 * Shuning uchun alohida DTO'lar + mapper funksiyalari ishlatiladi.
 */

@Serializable
data class ServerUserBrief(
    val id: Long = 0,
    @SerialName("full_name") val fullName: String = "",
    val phone: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null
)

@Serializable
data class ServerOrderItem(
    val id: Long = 0,
    @SerialName("food_id") val foodId: Long = 0,
    val quantity: Int = 1,
    @SerialName("unit_price")
    @Serializable(with = FlexibleDoubleSerializer::class)
    val unitPrice: Double = 0.0,
    val food: Food? = null
)

@Serializable
data class ServerDelivery(
    val id: Long = 0,
    val status: String = "on_the_way",
    @SerialName("current_lat")
    @Serializable(with = FlexibleDoubleSerializer::class)
    val currentLat: Double = 41.2995,
    @SerialName("current_lng")
    @Serializable(with = FlexibleDoubleSerializer::class)
    val currentLng: Double = 69.2401,
    @SerialName("estimated_delivery_time") val estimatedDeliveryTime: String? = null
)

@Serializable
data class ServerOrder(
    val id: Long = 0,
    @SerialName("order_number") val orderNumber: String = "",
    val status: String = "pending",
    @Serializable(with = FlexibleDoubleSerializer::class)
    val subtotal: Double = 0.0,
    @SerialName("delivery_fee")
    @Serializable(with = FlexibleDoubleSerializer::class)
    val deliveryFee: Double = 0.0,
    @SerialName("discount_amount")
    @Serializable(with = FlexibleDoubleSerializer::class)
    val discountAmount: Double = 0.0,
    @SerialName("tax_amount")
    @Serializable(with = FlexibleDoubleSerializer::class)
    val taxAmount: Double = 0.0,
    @SerialName("total_amount")
    @Serializable(with = FlexibleDoubleSerializer::class)
    val totalAmount: Double = 0.0,
    @SerialName("payment_method") val paymentMethod: String = "card",
    @SerialName("created_at") val createdAt: String? = null,
    val items: List<ServerOrderItem> = emptyList(),
    val courier: ServerUserBrief? = null,
    val address: Address? = null,
    val delivery: ServerDelivery? = null
)

@Serializable
data class ServerNotification(
    val id: Long = 0,
    val title: String = "",
    val body: String = "",
    val type: String = "GENERAL",
    @SerialName("is_read")
    @Serializable(with = FlexibleBooleanSerializer::class)
    val isRead: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class ServerChat(
    val id: Long = 0,
    @SerialName("order_id") val orderId: Long? = null,
    val courier: ServerUserBrief? = null,
    @SerialName("unread_count") val unreadCount: Int = 0,
    @SerialName("last_message") val lastMessage: String? = null,
    @SerialName("last_message_at") val lastMessageAt: String? = null
)

@Serializable
data class ServerMessage(
    val id: Long = 0,
    @SerialName("chat_id") val chatId: Long = 0,
    @SerialName("sender_id") val senderId: Long = 0,
    val text: String = "",
    @SerialName("is_read")
    @Serializable(with = FlexibleBooleanSerializer::class)
    val isRead: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class PromoValidationData(
    val code: String = "",
    val title: String? = null,
    @SerialName("discount_type") val discountType: String = "fixed",
    @SerialName("discount_amount")
    @Serializable(with = FlexibleDoubleSerializer::class)
    val discountAmount: Double = 0.0
)

@Serializable
data class ResetTokenData(
    @SerialName("reset_token") val resetToken: String = ""
)

@Serializable
data class OtpRequestData(
    @SerialName("otp_expires_in") val otpExpiresIn: Int = 600,
    @SerialName("demo_otp") val demoOtp: String? = null
)

@Serializable
data class CreateCardRequest(
    @SerialName("card_holder_name") val cardHolderName: String,
    @SerialName("card_number") val cardNumber: String,
    @SerialName("expiry_date") val expiryDate: String,
    @SerialName("card_type") val cardType: String? = null
)

@Serializable
data class SendMessageRequest(val text: String)

@Serializable
data class ValidatePromoRequest(
    val code: String,
    val subtotal: Double
)

@Serializable
data class UpdateProfileRequest(
    @SerialName("full_name") val fullName: String,
    val phone: String? = null,
    @SerialName("date_of_birth") val dateOfBirth: String? = null,
    val gender: String? = null
)

// ---------- Mapperlar ----------

fun String.toOrderStatus(): OrderStatus = when (lowercase()) {
    "pending" -> OrderStatus.PENDING
    "preparing" -> OrderStatus.PREPARING
    "on_the_way" -> OrderStatus.ON_THE_WAY
    "delivered" -> OrderStatus.DELIVERED
    "canceled", "cancelled" -> OrderStatus.CANCELED
    else -> OrderStatus.PENDING
}

fun ServerOrder.toDomain(fallbackAddress: String = ""): Order = Order(
    id = id,
    orderNumber = orderNumber,
    items = items.mapNotNull { item ->
        val food = item.food ?: return@mapNotNull null
        CartItem(food = food, quantity = item.quantity)
    },
    status = status.toOrderStatus(),
    deliveryAddress = address?.let { listOfNotNull(it.addressLine.ifBlank { null }, it.houseNumber.ifBlank { null }).joinToString(", ") }
        ?: fallbackAddress,
    subtotal = subtotal,
    deliveryFee = deliveryFee,
    discount = discountAmount,
    tax = taxAmount,
    total = totalAmount,
    courier = courier?.let {
        Courier(
            id = it.id,
            name = it.fullName,
            badgeId = "ID ${it.id}",
            phone = it.phone ?: "",
            avatarUrl = it.avatarUrl ?: "",
            currentLat = delivery?.currentLat ?: 41.2995,
            currentLng = delivery?.currentLng ?: 69.2401
        )
    },
    createdAt = createdAt?.take(10) ?: ""
)

fun ServerNotification.toDomain(): AppNotificationItem = AppNotificationItem(
    id = id,
    title = title,
    message = body,
    timeAgo = createdAt?.take(10) ?: "",
    type = type.uppercase(),
    isRead = isRead
)

fun ServerMessage.toDomain(currentUserId: Long): ChatMessage = ChatMessage(
    id = id,
    senderId = senderId,
    text = text,
    timestamp = createdAt?.let { raw ->
        // "2026-08-26T14:32:11.000000Z" -> "14:32"
        val timePart = raw.substringAfter('T', "").take(5)
        if (timePart.length == 5) timePart else raw.take(10)
    } ?: "",
    isFromMe = senderId == currentUserId,
    isRead = isRead
)

fun ServerChat.toDomain(): ChatConversation = ChatConversation(
    id = id,
    title = courier?.fullName?.ifBlank { null } ?: "Qo'llab-quvvatlash",
    lastMessage = lastMessage ?: "Suhbatni boshlang",
    lastMessageAt = lastMessageAt?.substringAfter('T', "")?.take(5) ?: "",
    unreadCount = unreadCount,
    courierId = courier?.id ?: 0
)

@Serializable
data class ChatConversation(
    val id: Long = 0,
    val title: String = "",
    val lastMessage: String = "",
    val lastMessageAt: String = "",
    val unreadCount: Int = 0,
    val courierId: Long = 0
)

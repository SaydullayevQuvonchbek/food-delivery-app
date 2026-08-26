package com.fooddelivery.domain.models

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

object FlexibleDoubleSerializer : KSerializer<Double> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("FlexibleDouble", PrimitiveKind.DOUBLE)
    override fun serialize(encoder: Encoder, value: Double) = encoder.encodeDouble(value)
    override fun deserialize(decoder: Decoder): Double {
        val jsonDecoder = decoder as? JsonDecoder ?: return try { decoder.decodeDouble() } catch (e: Exception) { 0.0 }
        val element = jsonDecoder.decodeJsonElement()
        return if (element is JsonPrimitive) {
            element.doubleOrNull ?: element.content.toDoubleOrNull() ?: 0.0
        } else {
            0.0
        }
    }
}

object FlexibleNullableDoubleSerializer : KSerializer<Double?> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("FlexibleNullableDouble", PrimitiveKind.DOUBLE)
    override fun serialize(encoder: Encoder, value: Double?) {
        if (value != null) encoder.encodeDouble(value) else encoder.encodeNull()
    }
    override fun deserialize(decoder: Decoder): Double? {
        val jsonDecoder = decoder as? JsonDecoder ?: return try { decoder.decodeDouble() } catch (e: Exception) { null }
        val element = jsonDecoder.decodeJsonElement()
        return when (element) {
            is JsonNull -> null
            is JsonPrimitive -> {
                if (element.content.isBlank() || element.content == "null") null
                else element.doubleOrNull ?: element.content.toDoubleOrNull()
            }
            else -> null
        }
    }
}

object FlexibleBooleanSerializer : KSerializer<Boolean> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("FlexibleBoolean", PrimitiveKind.BOOLEAN)
    override fun serialize(encoder: Encoder, value: Boolean) = encoder.encodeBoolean(value)
    override fun deserialize(decoder: Decoder): Boolean {
        val jsonDecoder = decoder as? JsonDecoder ?: return try { decoder.decodeBoolean() } catch (e: Exception) { false }
        val element = jsonDecoder.decodeJsonElement()
        return if (element is JsonPrimitive) {
            element.booleanOrNull ?: (element.intOrNull == 1) ?: (element.content == "1" || element.content.equals("true", ignoreCase = true))
        } else {
            false
        }
    }
}

@Serializable
data class ApiResponse<T>(
    val success: Boolean = true,
    val message: String? = null,
    val data: T? = null,
    val errors: Map<String, List<String>>? = null
)

@Serializable
data class AuthData(
    val user: User? = null,
    val token: String? = null
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    @SerialName("full_name") val fullName: String,
    val email: String,
    val password: String,
    val phone: String? = null
)

@Serializable
data class ForgotPasswordRequest(
    val email: String
)

@Serializable
data class VerifyOtpRequest(
    val email: String,
    val otp: String
)

@Serializable
data class ResetPasswordRequest(
    val email: String,
    val password: String,
    @SerialName("password_confirmation") val passwordConfirmation: String
)

@Serializable
data class CreateOrderRequest(
    @SerialName("address_id") val addressId: Long? = null,
    @SerialName("payment_method") val paymentMethod: String = "card",
    val items: List<CreateOrderItemRequest>
)

@Serializable
data class CreateOrderItemRequest(
    @SerialName("food_id") val foodId: Long,
    val quantity: Int
)

@Serializable
data class User(
    val id: Long = 0,
    @SerialName("full_name")
    val fullName: String = "",
    val email: String = "",
    val phone: String? = null,
    @SerialName("date_of_birth")
    val dateOfBirth: String? = null,
    val gender: String = "Male",
    @SerialName("avatar_url")
    val avatarUrl: String? = null,
    val role: String = "customer",
    val status: String = "active",
    val token: String = ""
)

@Serializable
data class Category(
    val id: Long = 0,
    val name: String = "",
    val icon: String = "🍔",
    @SerialName("is_selected")
    @Serializable(with = FlexibleBooleanSerializer::class)
    val isSelected: Boolean = false
)

@Serializable
data class Food(
    val id: Long = 0,
    @SerialName("category_id")
    val categoryId: Long = 1,
    val name: String = "",
    val description: String = "",
    @Serializable(with = FlexibleDoubleSerializer::class)
    val price: Double = 0.0,
    @SerialName("original_price")
    @Serializable(with = FlexibleNullableDoubleSerializer::class)
    val originalPrice: Double? = null,
    @Serializable(with = FlexibleDoubleSerializer::class)
    val rating: Double = 4.9,
    @SerialName("review_count")
    val reviewCount: Int = 120,
    val distance: String = "190m",
    @SerialName("delivery_time")
    val deliveryTime: String = "20 - 30 min",
    @SerialName("is_free_delivery")
    @Serializable(with = FlexibleBooleanSerializer::class)
    val isFreeDelivery: Boolean = true,
    @SerialName("image_url")
    val imageUrl: String = "",
    @SerialName("is_favorite")
    @Serializable(with = FlexibleBooleanSerializer::class)
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
    @Serializable(with = FlexibleDoubleSerializer::class)
    val subtotal: Double = 0.0,
    @SerialName("delivery_fee")
    @Serializable(with = FlexibleDoubleSerializer::class)
    val deliveryFee: Double = 0.0,
    @Serializable(with = FlexibleDoubleSerializer::class)
    val discount: Double = 0.0,
    @Serializable(with = FlexibleDoubleSerializer::class)
    val tax: Double = 0.0,
    @Serializable(with = FlexibleDoubleSerializer::class)
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
    @Serializable(with = FlexibleDoubleSerializer::class)
    val rating: Double = 4.9,
    @Serializable(with = FlexibleDoubleSerializer::class)
    val currentLat: Double = 37.7749,
    @Serializable(with = FlexibleDoubleSerializer::class)
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
    @Serializable(with = FlexibleBooleanSerializer::class)
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
    @Serializable(with = FlexibleBooleanSerializer::class)
    val isDefault: Boolean = false
)

@Serializable
data class AppNotificationItem(
    val id: Long = 0,
    val title: String = "",
    val message: String = "",
    @SerialName("time_ago")
    val timeAgo: String = "Today",
    val type: String = "DISCOUNT",
    @SerialName("is_read")
    @Serializable(with = FlexibleBooleanSerializer::class)
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
    @Serializable(with = FlexibleBooleanSerializer::class)
    val isFromMe: Boolean = true,
    @SerialName("is_read")
    @Serializable(with = FlexibleBooleanSerializer::class)
    val isRead: Boolean = true
)
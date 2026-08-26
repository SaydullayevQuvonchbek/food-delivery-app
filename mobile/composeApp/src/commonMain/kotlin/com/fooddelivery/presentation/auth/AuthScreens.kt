package com.fooddelivery.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fooddelivery.components.*
import com.fooddelivery.data.repository.FoodDeliveryRepository
import com.fooddelivery.theme.*
import com.fooddelivery.util.isValidEmail
import kotlinx.coroutines.launch

@Composable
private fun ErrorBanner(message: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = DangerRed.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.ErrorOutline,
                contentDescription = null,
                tint = DangerRed,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall.copy(color = DangerRed)
            )
        }
    }
}

@Composable
fun LoginScreen(
    repository: FoodDeliveryRepository,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(Modifier.height(30.dp))

        Text(
            text = "Login to your\naccount.",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                lineHeight = 36.sp
            )
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Please sign in to your account",
            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
        )

        errorMessage?.let {
            Spacer(Modifier.height(16.dp))
            ErrorBanner(it)
        }

        Spacer(Modifier.height(24.dp))

        AppInputField(
            value = email,
            onValueChange = {
                email = it
                errorMessage = null
            },
            label = "Email Address",
            placeholder = "Enter Email",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(Modifier.height(18.dp))

        AppInputField(
            value = password,
            onValueChange = {
                password = it
                errorMessage = null
            },
            label = "Password",
            placeholder = "Password",
            isPassword = true
        )

        Spacer(Modifier.height(12.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = "Forgot password?",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PrimaryOrange,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.clickable { onNavigateToForgotPassword() }
            )
        }

        Spacer(Modifier.height(24.dp))

        AppPrimaryButton(
            text = "Sign In",
            isLoading = isLoading,
            onClick = {
                scope.launch {
                    if (!isValidEmail(email)) {
                        errorMessage = "To'g'ri email manzil kiriting"
                        return@launch
                    }
                    if (password.isBlank()) {
                        errorMessage = "Parolni kiriting"
                        return@launch
                    }
                    isLoading = true
                    errorMessage = null
                    val result = repository.login(email.trim(), password)
                    isLoading = false
                    result.onSuccess {
                        onLoginSuccess()
                    }.onFailure { err ->
                        errorMessage = err.message ?: "Email yoki parol xato"
                    }
                }
            }
        )

        Spacer(Modifier.height(24.dp))

        SocialLoginSection()

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Don't have an account? ",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
            )
            Text(
                text = "Register",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PrimaryOrange,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.clickable { onNavigateToRegister() }
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun RegisterScreen(
    repository: FoodDeliveryRepository,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var termsAgreed by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(Modifier.height(30.dp))

        Text(
            text = "Create your new\naccount",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                lineHeight = 36.sp
            )
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Create an account to start looking for the food you like",
            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
        )

        errorMessage?.let {
            Spacer(Modifier.height(16.dp))
            ErrorBanner(it)
        }

        Spacer(Modifier.height(24.dp))

        AppInputField(
            value = email,
            onValueChange = {
                email = it
                errorMessage = null
            },
            label = "Email Address",
            placeholder = "Enter Email",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(Modifier.height(18.dp))

        AppInputField(
            value = userName,
            onValueChange = {
                userName = it
                errorMessage = null
            },
            label = "Full Name",
            placeholder = "Ism Familiya"
        )

        Spacer(Modifier.height(18.dp))

        AppInputField(
            value = phone,
            onValueChange = {
                phone = it
                errorMessage = null
            },
            label = "Phone (ixtiyoriy)",
            placeholder = "+998 90 123 45 67",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        Spacer(Modifier.height(18.dp))

        AppInputField(
            value = password,
            onValueChange = {
                password = it
                errorMessage = null
            },
            label = "Password",
            placeholder = "Kamida 8 ta belgi",
            isPassword = true
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { termsAgreed = !termsAgreed },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .background(if (termsAgreed) PrimaryOrange else Color.Transparent, RoundedCornerShape(6.dp))
                    .border(
                        1.5.dp,
                        if (termsAgreed) PrimaryOrange else BorderLight,
                        RoundedCornerShape(6.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (termsAgreed) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = TextWhite,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(Modifier.width(10.dp))

            Text(
                text = "I Agree with Terms of Service and Privacy Policy",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )
            )
        }

        Spacer(Modifier.height(24.dp))

        AppPrimaryButton(
            text = "Register",
            enabled = termsAgreed,
            isLoading = isLoading,
            onClick = {
                scope.launch {
                    if (!isValidEmail(email)) {
                        errorMessage = "To'g'ri email kiriting"
                        return@launch
                    }
                    if (userName.trim().length < 3) {
                        errorMessage = "To'liq ismingizni kiriting"
                        return@launch
                    }
                    // Server minimal 8 ta belgi talab qiladi - shuning uchun bu yerda ham 8
                    if (password.length < 8) {
                        errorMessage = "Parol kamida 8 ta belgidan iborat bo'lsin"
                        return@launch
                    }
                    isLoading = true
                    errorMessage = null
                    val result = repository.register(userName.trim(), email.trim(), password, phone.trim())
                    isLoading = false
                    result.onSuccess {
                        onRegisterSuccess()
                    }.onFailure { err ->
                        errorMessage = err.message ?: "Ro'yxatdan o'tishda xatolik"
                    }
                }
            }
        )

        Spacer(Modifier.height(24.dp))

        SocialLoginSection()

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Already have an account? ",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
            )
            Text(
                text = "Sign In",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PrimaryOrange,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.clickable { onNavigateToLogin() }
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun ForgotPasswordScreen(
    repository: FoodDeliveryRepository,
    onBackClick: () -> Unit,
    onContinueToOtp: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var selectedMethod by remember { mutableStateOf("email") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var infoMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        AppHeaderBar(
            title = "",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Forgot password?",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Hisobingiz email manzilini kiriting - tasdiqlash kodini yuboramiz",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
            )

            Spacer(Modifier.height(24.dp))

            // Email endi tahrirlanadi: ilgari kod qat'iy demo hisobga yuborilardi
            AppInputField(
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = null
                },
                label = "Email Address",
                placeholder = "Enter Email",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            errorMessage?.let {
                Spacer(Modifier.height(14.dp))
                ErrorBanner(it)
            }

            infoMessage?.let {
                Spacer(Modifier.height(14.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall.copy(color = SuccessGreen)
                )
            }

            Spacer(Modifier.height(24.dp))

            OptionCard(
                icon = Icons.Filled.Email,
                title = "Send via Email",
                value = email.ifBlank { "Email manzilingiz" },
                isSelected = selectedMethod == "email",
                onClick = { selectedMethod = "email" }
            )

            Spacer(Modifier.height(16.dp))

            OptionCard(
                icon = Icons.Filled.Phone,
                title = "Send via WhatsApp",
                value = "Tez orada",
                isSelected = selectedMethod == "whatsapp",
                onClick = { /* Hali qo'llab-quvvatlanmaydi */ }
            )

            Spacer(Modifier.height(40.dp))

            AppPrimaryButton(
                text = "Continue",
                isLoading = isLoading,
                onClick = {
                    scope.launch {
                        if (!isValidEmail(email)) {
                            errorMessage = "To'g'ri email manzil kiriting"
                            return@launch
                        }
                        isLoading = true
                        errorMessage = null
                        val result = repository.forgotPassword(email.trim())
                        isLoading = false
                        result.onSuccess {
                            infoMessage = it
                            onContinueToOtp()
                        }.onFailure { err ->
                            errorMessage = err.message
                        }
                    }
                }
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun OptionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) PrimaryOrangeSoft.copy(alpha = 0.4f) else SurfaceLight)
            .border(
                1.5.dp,
                if (isSelected) PrimaryOrange else BorderLight,
                RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(18.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(if (isSelected) PrimaryOrange else BorderLight, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) TextWhite else TextPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = PrimaryOrange,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun OtpVerificationScreen(
    repository: FoodDeliveryRepository,
    onBackClick: () -> Unit,
    onVerified: () -> Unit
) {
    var otpCode by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val maskedEmail = remember { maskEmail(repository.resetEmail) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        AppHeaderBar(
            title = "OTP",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))

            Text(
                text = "Email verification",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Tasdiqlash kodini kiriting:\n$maskedEmail",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            )

            errorMessage?.let {
                Spacer(Modifier.height(14.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall.copy(color = DangerRed)
                )
            }

            Spacer(Modifier.height(30.dp))

            // Endi kodni haqiqatan ham kiritish mumkin (ilgari faqat ko'rinish edi)
            OtpCodeInput(
                otpValue = otpCode,
                onOtpChange = {
                    otpCode = it.filter { ch -> ch.isDigit() }.take(4)
                    errorMessage = null
                }
            )

            Spacer(Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Didn't receive code? ",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                )
                Text(
                    text = "Resend",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = PrimaryOrange,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.clickable {
                        scope.launch {
                            repository.forgotPassword(repository.resetEmail)
                                .onFailure { errorMessage = it.message }
                        }
                    }
                )
            }

            Spacer(Modifier.weight(1f))

            AppPrimaryButton(
                text = "Continue",
                isLoading = isLoading,
                enabled = otpCode.length == 4,
                onClick = {
                    scope.launch {
                        isLoading = true
                        errorMessage = null
                        val res = repository.verifyOtp(otpCode)
                        isLoading = false
                        res.onSuccess {
                            onVerified()
                        }.onFailure { err ->
                            errorMessage = err.message
                        }
                    }
                }
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

private fun maskEmail(email: String): String {
    if (email.isBlank()) return "email manzilingiz"
    val at = email.indexOf('@')
    if (at <= 1) return email
    val visible = email.take(minOf(3, at))
    return visible + "*".repeat(maxOf(1, at - visible.length)) + email.substring(at)
}

@Composable
fun ResetPasswordScreen(
    repository: FoodDeliveryRepository,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundWhite)
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
        ) {
            AppHeaderBar(
                title = "Reset Password",
                onBackClick = onBackClick
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(Modifier.height(20.dp))

                Text(
                    text = "Reset Password",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Yangi parol avvalgisidan farq qilishi kerak",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                )

                errorMessage?.let {
                    Spacer(Modifier.height(16.dp))
                    ErrorBanner(it)
                }

                Spacer(Modifier.height(32.dp))

                AppInputField(
                    value = newPassword,
                    onValueChange = {
                        newPassword = it
                        errorMessage = null
                    },
                    label = "New Password",
                    placeholder = "Enter new password",
                    isPassword = true
                )
                Text(
                    text = "Kamida 8 ta belgi",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted),
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )

                Spacer(Modifier.height(18.dp))

                AppInputField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        errorMessage = null
                    },
                    label = "Confirm Password",
                    placeholder = "Re-enter new password",
                    isPassword = true,
                    isError = confirmPassword.isNotEmpty() && confirmPassword != newPassword,
                    errorMessage = "Parollar mos kelmadi"
                )

                Spacer(Modifier.height(40.dp))

                AppPrimaryButton(
                    text = "Verify Account",
                    isLoading = isLoading,
                    onClick = {
                        scope.launch {
                            if (newPassword.length < 8) {
                                errorMessage = "Parol kamida 8 ta belgidan iborat bo'lsin"
                                return@launch
                            }
                            if (newPassword != confirmPassword) {
                                errorMessage = "Parollar mos kelmadi"
                                return@launch
                            }
                            isLoading = true
                            errorMessage = null
                            val result = repository.resetPassword(newPassword)
                            isLoading = false
                            result.onSuccess {
                                showSuccessDialog = true
                            }.onFailure { err ->
                                errorMessage = err.message
                            }
                        }
                    }
                )

                Spacer(Modifier.height(24.dp))
            }
        }

        if (showSuccessDialog) {
            PasswordChangedModal(
                onConfirm = {
                    showSuccessDialog = false
                    onSuccess()
                }
            )
        }
    }
}

@Composable
fun PasswordChangedModal(
    onConfirm: () -> Unit
) {
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
                .padding(28.dp),
            shape = BottomSheetShape,
            colors = CardDefaults.cardColors(containerColor = BackgroundWhite)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp, 4.dp)
                        .background(BorderLight, CircleShape)
                )
                Spacer(Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(PrimaryOrangeSoft, CircleShape)
                        .border(4.dp, PrimaryOrange, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Success",
                        tint = PrimaryOrange,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    text = "Password Changed",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Parol muvaffaqiyatli o'zgartirildi. Endi yangi parol bilan kirishingiz mumkin.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(Modifier.height(28.dp))

                AppPrimaryButton(
                    text = "Sign In",
                    onClick = onConfirm
                )
            }
        }
    }
}

@Composable
private fun SocialLoginSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = BorderLight)
            Text(
                text = "  Or sign in with  ",
                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = BorderLight)
        }

        Spacer(Modifier.height(20.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SocialIconButton(text = "G", color = GoogleRed)
            SocialIconButton(text = "f", color = FacebookBlue)
            SocialIconButton(text = "", color = AppleBlack)
        }
    }
}

@Composable
private fun SocialIconButton(
    text: String,
    color: Color
) {
    var showHint by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(SurfaceLight, CircleShape)
                .border(1.dp, BorderLight, CircleShape)
                .clickable { showHint = true },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            )
        }
        if (showHint) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Tez orada",
                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 10.sp)
            )
        }
    }
}

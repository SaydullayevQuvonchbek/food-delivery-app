package com.fooddelivery.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fooddelivery.components.*
import com.fooddelivery.data.repository.FoodDeliveryRepository
import com.fooddelivery.theme.*
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    repository: FoodDeliveryRepository,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    var email by remember { mutableStateOf("Albertstevano@gmail.com") }
    var password by remember { mutableStateOf("password123") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .statusBarsPadding()
            .navigationBarsPadding()
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

        if (errorMessage != null) {
            Spacer(Modifier.height(16.dp))
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
                        text = errorMessage ?: "",
                        style = MaterialTheme.typography.bodySmall.copy(color = DangerRed)
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        AppInputField(
            value = email,
            onValueChange = {
                email = it
                errorMessage = null
            },
            label = "Email Address",
            placeholder = "Enter Email"
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
                    if (email.isBlank()) {
                        errorMessage = "Iltimos, email manzilini kiriting"
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

        Spacer(Modifier.height(14.dp))

        // Demo Fast-Track button
        OutlinedButton(
            onClick = {
                scope.launch {
                    isLoading = true
                    val result = repository.login("Albertstevano@gmail.com", "password123")
                    isLoading = false
                    result.onSuccess { onLoginSuccess() }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryOrangeLight)
        ) {
            Text(
                text = "⚡ Demo Fast-Track Sign In",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PrimaryOrange,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(Modifier.height(24.dp))

        // Social Logins
        SocialLoginSection()

        Spacer(Modifier.height(24.dp))

        // Footer Register Link
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
    var password by remember { mutableStateOf("") }
    var termsAgreed by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .statusBarsPadding()
            .navigationBarsPadding()
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

        if (errorMessage != null) {
            Spacer(Modifier.height(16.dp))
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
                        text = errorMessage ?: "",
                        style = MaterialTheme.typography.bodySmall.copy(color = DangerRed)
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        AppInputField(
            value = email,
            onValueChange = {
                email = it
                errorMessage = null
            },
            label = "Email Address",
            placeholder = "Enter Email"
        )

        Spacer(Modifier.height(18.dp))

        AppInputField(
            value = userName,
            onValueChange = {
                userName = it
                errorMessage = null
            },
            label = "User Name",
            placeholder = "User Name"
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

        Spacer(Modifier.height(16.dp))

        // Terms Checkbox
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
                    if (email.isBlank() || !email.contains("@")) {
                        errorMessage = "To'g'ri email kiriting"
                        return@launch
                    }
                    if (userName.isBlank()) {
                        errorMessage = "Ismingizni kiriting"
                        return@launch
                    }
                    if (password.length < 6) {
                        errorMessage = "Parol kamida 6 ta belgidan iborat bo'lsin"
                        return@launch
                    }
                    isLoading = true
                    errorMessage = null
                    val result = repository.register(userName.trim(), email.trim(), password)
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
    var email by remember { mutableStateOf("Albertstevano@gmail.com") }
    var selectedMethod by remember { mutableStateOf("email") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        AppHeaderBar(
            title = "",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Forgot password?",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Select which contact details should we use to reset your password",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
            )

            Spacer(Modifier.height(32.dp))

            // WhatsApp Option Card
            OptionCard(
                icon = Icons.Filled.Phone,
                title = "Send via WhatsApp",
                value = "+12 8347 2838 28",
                isSelected = selectedMethod == "whatsapp",
                onClick = { selectedMethod = "whatsapp" }
            )

            Spacer(Modifier.height(16.dp))

            // Email Option Card
            OptionCard(
                icon = Icons.Filled.Email,
                title = "Send via Email",
                value = "Albertstevano@gmail.com",
                isSelected = selectedMethod == "email",
                onClick = { selectedMethod = "email" }
            )

            Spacer(Modifier.weight(1f))

            AppPrimaryButton(
                text = "Continue",
                isLoading = isLoading,
                onClick = {
                    scope.launch {
                        isLoading = true
                        repository.forgotPassword(email)
                        isLoading = false
                        onContinueToOtp()
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
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
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
    var otpCode by remember { mutableStateOf("9627") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .statusBarsPadding()
            .navigationBarsPadding()
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
                text = "Enter the verification code we send you on:\nAlberts******@gmail.com\n(Demo Code: 9627)",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            )

            if (errorMessage != null) {
                Spacer(Modifier.height(14.dp))
                Text(
                    text = errorMessage ?: "",
                    style = MaterialTheme.typography.bodySmall.copy(color = DangerRed)
                )
            }

            Spacer(Modifier.height(30.dp))

            OtpCodeInput(
                otpValue = otpCode,
                onOtpChange = {
                    if (it.length <= 4) {
                        otpCode = it
                        errorMessage = null
                    }
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
                            repository.forgotPassword("Albertstevano@gmail.com")
                        }
                    }
                )
            }

            Spacer(Modifier.weight(1f))

            AppPrimaryButton(
                text = "Continue",
                isLoading = isLoading,
                onClick = {
                    scope.launch {
                        isLoading = true
                        errorMessage = null
                        val res = repository.verifyOtp("Albertstevano@gmail.com", otpCode)
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

@Composable
fun ResetPasswordScreen(
    repository: FoodDeliveryRepository,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit
) {
    var newPassword by remember { mutableStateOf("newpass123") }
    var confirmPassword by remember { mutableStateOf("newpass123") }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundWhite)
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            AppHeaderBar(
                title = "Reset Password",
                onBackClick = onBackClick
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(Modifier.height(20.dp))

                Text(
                    text = "Reset Password",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Your new password must be different from the previously used password",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                )

                Spacer(Modifier.height(32.dp))

                AppInputField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = "New Password",
                    placeholder = "Enter new password",
                    isPassword = true
                )
                Text(
                    text = "Must be at least 8 character",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted),
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )

                Spacer(Modifier.height(18.dp))

                AppInputField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = "Confirm Password",
                    placeholder = "Re-enter new password",
                    isPassword = true
                )
                Text(
                    text = "Both password must match",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted),
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )

                Spacer(Modifier.weight(1f))

                AppPrimaryButton(
                    text = "Verify Account",
                    isLoading = isLoading,
                    onClick = {
                        scope.launch {
                            isLoading = true
                            repository.resetPassword("Albertstevano@gmail.com", newPassword)
                            isLoading = false
                            showSuccessDialog = true
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

                // Success Icon with Shield
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
                    text = "Password changed successfully, you can login again with a new password",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(Modifier.height(28.dp))

                AppPrimaryButton(
                    text = "Verify Account",
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
            SocialIconButton(text = "", color = AppleBlack)
        }
    }
}

@Composable
private fun SocialIconButton(
    text: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .background(SurfaceLight, CircleShape)
            .border(1.dp, BorderLight, CircleShape)
            .clickable { /* Social Auth */ },
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
}
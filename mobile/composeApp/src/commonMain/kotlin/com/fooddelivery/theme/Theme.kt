package com.fooddelivery.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = PrimaryOrange,
    onPrimary = TextWhite,
    primaryContainer = PrimaryOrangeSoft,
    onPrimaryContainer = PrimaryOrangeDark,
    secondary = PrimaryOrangeLight,
    onSecondary = TextWhite,
    background = BackgroundWhite,
    onBackground = TextPrimary,
    surface = SurfaceLight,
    onSurface = TextPrimary,
    outline = BorderLight,
    error = DangerRed
)

@Composable
fun FoodDeliveryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val HomeWorkoutLightColorScheme = lightColorScheme(
    primary = RoyalBlue,
    onPrimary = PureWhiteCard,
    primaryContainer = RoyalBlueLight,
    onPrimaryContainer = RoyalBlueDark,
    secondary = EnergeticOrange,
    onSecondary = PureWhiteCard,
    secondaryContainer = EnergeticOrangeLight,
    onSecondaryContainer = TextPrimarySlate,
    tertiary = EmeraldSuccess,
    onTertiary = PureWhiteCard,
    background = HomeBackground,
    onBackground = TextPrimarySlate,
    surface = PureWhiteCard,
    onSurface = TextPrimarySlate,
    surfaceVariant = SoftCardSurface,
    onSurfaceVariant = TextSecondarySlate,
    outline = SubtleDivider,
    outlineVariant = SubtleDivider,
    error = CoralRed,
    onError = PureWhiteCard
)

val ApexModernShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

@Composable
fun ApexProtocolTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                @Suppress("DEPRECATION")
                window.statusBarColor = HomeBackground.toArgb()
                @Suppress("DEPRECATION")
                window.navigationBarColor = PureWhiteCard.toArgb()
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = true
                    isAppearanceLightNavigationBars = true
                }
            }
        }
    }

    MaterialTheme(
        colorScheme = HomeWorkoutLightColorScheme,
        typography = Typography,
        shapes = ApexModernShapes,
        content = content
    )
}

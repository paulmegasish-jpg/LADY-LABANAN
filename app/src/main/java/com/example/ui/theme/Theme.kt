package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = DeepRose,
    onPrimary = PureWhite,
    primaryContainer = BlushPetal,
    onPrimaryContainer = ObsidianBlack,
    secondary = ObsidianBlack,
    onSecondary = PureWhite,
    secondaryContainer = ChampagneLight,
    onSecondaryContainer = BronzeGold,
    tertiary = ChampagneGold,
    onTertiary = ObsidianBlack,
    tertiaryContainer = ChampagneLight,
    onTertiaryContainer = ObsidianBlack,
    background = BlushMist,
    onBackground = ObsidianBlack,
    surface = PureWhite,
    onSurface = ObsidianBlack,
    surfaceVariant = SoftIvory,
    onSurfaceVariant = MutedPlum,
    outline = HairlineBlush,
    outlineVariant = BlushPetal,
    error = CrimsonAlert,
    onError = PureWhite,
    errorContainer = CrimsonSurface,
    onErrorContainer = CrimsonAlert
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkRosePrimary,
    onPrimary = ObsidianBlack,
    primaryContainer = DeepRoseDark,
    onPrimaryContainer = PureWhite,
    secondary = DarkChampagne,
    onSecondary = ObsidianBlack,
    secondaryContainer = DarkCardSurface,
    onSecondaryContainer = DarkChampagne,
    tertiary = ChampagneGold,
    onTertiary = ObsidianBlack,
    tertiaryContainer = DarkCardSurface,
    onTertiaryContainer = DarkChampagne,
    background = DarkObsidianBg,
    onBackground = DarkTextPrimary,
    surface = DarkVelvetSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkCardSurface,
    onSurfaceVariant = DarkTextMuted,
    outline = ColorTokens.DarkOutline,
    outlineVariant = DarkCardSurface,
    error = CrimsonAlert,
    onError = PureWhite
)

private object ColorTokens {
    val DarkOutline = androidx.compose.ui.graphics.Color(0xFF3E333B)
}

val LadyLebananShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun LadyLebananTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = LadyLebananTypography,
        shapes = LadyLebananShapes,
        content = content
    )
}

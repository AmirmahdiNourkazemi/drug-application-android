package com.approagency.pharmacy.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.fabirt.podcastapp.ui.theme.Shapes
import com.vada.caller.ui.theme.Dime
import com.vada.caller.ui.theme.LocalDime
object MedicalColors {

    val Primary = Color(0xFF0F766E)
    val PrimaryContainer = Color(0xFFD7F3EF)

    val Secondary = Color(0xFF5B8E8A)
    val SecondaryContainer = Color(0xFFE3F3F1)

    val Tertiary = Color(0xFF8FBFB8)

    val Background = Color(0xFFF8FCFB)
    val Surface = Color(0xFFFFFFFF)

    val DarkBackground = Color(0xFF0F1720)
    val DarkSurface = Color(0xFF17232B)

    val OnDark = Color(0xFFF5FAF9)
    val OnLight = Color(0xFF102A2A)

    val Error = Color(0xFFD64545)

    val Outline = Color(0xFFD0E2DF)
}
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF5DD4C3),
    onPrimary = Color(0xFF003733),

    secondary = Color(0xFF8CC9C1),
    onSecondary = Color(0xFF0D2B29),

    tertiary = Color(0xFFA7DCD4),
    onTertiary = Color(0xFF102A2A),

    background = MedicalColors.DarkBackground,
    onBackground = MedicalColors.OnDark,

    surface = MedicalColors.DarkSurface,
    onSurface = MedicalColors.OnDark,

    primaryContainer = Color(0xFF124A45),
    secondaryContainer = Color(0xFF1D3D3A),

    error = Color(0xFFFF8A80)
)

private val LightColorScheme = lightColorScheme(
    primary = MedicalColors.Primary,
    onPrimary = Color.White,

    secondary = MedicalColors.Secondary,
    onSecondary = Color.White,

    tertiary = MedicalColors.Tertiary,
    onTertiary = MedicalColors.OnLight,

    background = MedicalColors.Background,
    onBackground = MedicalColors.OnLight,

    surface = MedicalColors.Surface,
    onSurface = MedicalColors.OnLight,

    primaryContainer = MedicalColors.PrimaryContainer,
    secondaryContainer = MedicalColors.SecondaryContainer,

    outline = MedicalColors.Outline,
    error = MedicalColors.Error
)

@Composable
fun DrugTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    CompositionLocalProvider(
        LocalDime provides Dime()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content,
            shapes = Shapes,
        )
    }
}
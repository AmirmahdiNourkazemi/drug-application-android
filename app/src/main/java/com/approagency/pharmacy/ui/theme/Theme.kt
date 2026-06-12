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
// پالت کامل و هماهنگ بر پایه‌ی سبز-آبی پزشکی (teal).
// همه‌ی نقش‌های surface/container/outline تعریف شده‌اند تا کانتینرها (کارت‌ها،
// تکست‌فیلدها، باتم‌شیت‌ها) رنگ یکدست و متناسب با برند داشته باشند و به مقادیر
// پیش‌فرض بنفش‌گون Material برنگردند.
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0F766E),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFC7ECE6),
    onPrimaryContainer = Color(0xFF00201D),

    secondary = Color(0xFF4C6360),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFCEE8E3),
    onSecondaryContainer = Color(0xFF09201D),

    tertiary = Color(0xFF3D6373),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFC0E8FB),
    onTertiaryContainer = Color(0xFF001F2A),

    background = Color(0xFFF5FBF9),
    onBackground = Color(0xFF171D1C),

    surface = Color(0xFFF5FBF9),
    onSurface = Color(0xFF171D1C),
    surfaceVariant = Color(0xFFDAE5E1),
    onSurfaceVariant = Color(0xFF3F4947),

    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFEFF6F3),
    surfaceContainer = Color(0xFFE9F1EE),
    surfaceContainerHigh = Color(0xFFE3ECE9),
    surfaceContainerHighest = Color(0xFFDEE7E4),

    outline = Color(0xFF6F7977),
    outlineVariant = Color(0xFFBEC9C5),

    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    inverseSurface = Color(0xFF2B3231),
    inverseOnSurface = Color(0xFFECF2EF),
    inversePrimary = Color(0xFF80D5CB),
    scrim = Color(0xFF000000),
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF5DD4C3),
    onPrimary = Color(0xFF00382F),
    primaryContainer = Color(0xFF005048),
    onPrimaryContainer = Color(0xFF7FF8E6),

    secondary = Color(0xFFB0CCC6),
    onSecondary = Color(0xFF1B3531),
    secondaryContainer = Color(0xFF324B47),
    onSecondaryContainer = Color(0xFFCCE8E2),

    tertiary = Color(0xFFA4CCDE),
    onTertiary = Color(0xFF053543),
    tertiaryContainer = Color(0xFF234C5B),
    onTertiaryContainer = Color(0xFFC0E8FB),

    background = Color(0xFF0E1514),
    onBackground = Color(0xFFDDE4E1),

    surface = Color(0xFF0E1514),
    onSurface = Color(0xFFDDE4E1),
    surfaceVariant = Color(0xFF3F4947),
    onSurfaceVariant = Color(0xFFBEC9C5),

    surfaceContainerLowest = Color(0xFF090F0E),
    surfaceContainerLow = Color(0xFF171D1C),
    surfaceContainer = Color(0xFF1B211F),
    surfaceContainerHigh = Color(0xFF252B2A),
    surfaceContainerHighest = Color(0xFF303635),

    outline = Color(0xFF899390),
    outlineVariant = Color(0xFF3F4947),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    inverseSurface = Color(0xFFDDE4E1),
    inverseOnSurface = Color(0xFF2B3231),
    inversePrimary = Color(0xFF0F766E),
    scrim = Color(0xFF000000),
)

@Composable
fun DrugTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }
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
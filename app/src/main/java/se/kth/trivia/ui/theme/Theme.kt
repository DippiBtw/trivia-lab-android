package se.kth.trivia.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFD32F2F),  // Warm Red
    secondary = Color(0xFFAFB42B),  // Olive
    tertiary = Color(0xFF7B1FA2),  // Deep Purple
    background = Color(0xFF121212),  // Dark Background
    surface = Color(0xFF1E1E1E),  // Surface Color
    error = Color(0xFFCF6679),  // Error Color
    onPrimary = Color(0xFFFFFFFF),  // Text/Icon on Primary
    onSecondary = Color(0xFF000000),  // Text/Icon on Secondary
    onBackground = Color(0xFFE0E0E0),  // Text on Background
    onSurface = Color(0xFFFFFFFF),  // Text on Surface
    onError = Color(0xFF000000)  // Text on Error
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1976D2),  // Vibrant Blue
    secondary = Color(0xFF6D4C41),  // Warm Brown for better contrast
    tertiary = Color(0xFF8E24AA),  // Deep Purple
    background = Color(0xFFFFFFFF),  // Pure White Background
    surface = Color(0xFFF5F5F5),  // Light Surface Color
    error = Color(0xFFD32F2F),  // Error Red
    onPrimary = Color(0xFFFFFFFF),  // White text/icons on Primary
    onSecondary = Color(0xFFFFFFFF),  // White text/icons on Secondary for readability
    onBackground = Color(0xFF424242),  // Dark Gray text for strong contrast on white
    onSurface = Color(0xFF212121),  // Nearly black text for surfaces
    onError = Color(0xFFFFFFFF)  // White text/icons on Error
)



@Composable
fun TriviaLabAndroidTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

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
    primary = Color(0xFFE57373),  // Soft Red
    secondary = Color(0xFFFCBF15),  // Lime Green
    tertiary = Color(0xFFBA68C8),  // Light Purple
    background = Color(0xFFFFFFFF),  // Light Background
    surface = Color(0xFFF5F5F5),  // Surface Color
    error = Color(0xFFD32F2F),  // Error Color
    onPrimary = Color(0xFFFFFFFF),  // Text/Icon on Primary
    onSecondary = Color(0xFF000000),  // Text/Icon on Secondary
    onBackground = Color(0xFF212121),  // Text on Background
    onSurface = Color(0xFF000000),  // Text on Surface
    onError = Color(0xFFFFFFFF)  // Text on Error
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

package appdev.studybuddy.ui.theme

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
    primary = Purple80,
    onPrimary = PurpleDarkText,
    secondary = Purple40,
    onSecondary = Color.White,
    tertiary = Pink80,
    onTertiary = Color.White,

    background = DarkGrey,
    onBackground = Color.White,

    surface = Grey,
    onSurface = Color.White,

    primaryContainer = Purple40,
    onPrimaryContainer = Beige,
    secondaryContainer = Grey,
    onSecondaryContainer = Beige,
    tertiaryContainer = Pink,
    onTertiaryContainer = DarkGrey,

    error = logOutRed,
    onError = Color.White,

    outline = PurpleDarkText,
    outlineVariant = PurpleBackground
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    onPrimary = Color.White,
    secondary = Purple80,
    onSecondary = Color.White,
    tertiary = Pink40,
    onTertiary = Color.White,

    background = Beige,
    onBackground = DarkGrey,

    surface = PurpleBackground,
    onSurface = PurpleDarkText,

    primaryContainer = PurpleBackground2,
    onPrimaryContainer = Color.White,
    secondaryContainer = Pink,
    onSecondaryContainer = DarkGrey,
    tertiaryContainer = Grey,
    onTertiaryContainer = Beige,

    error = logOutRed,
    onError = Color.White,

    outline = PurpleDarkText,
    outlineVariant = PurpleBackground
)

@Composable
fun StudyBuddyTheme(
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
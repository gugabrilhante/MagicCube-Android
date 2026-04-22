package gustavo.brilhante.magiccube2.activity.ui.theme

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
    primary = CyanAccent300,
    onPrimary = Navy900,
    primaryContainer = Navy600,
    onPrimaryContainer = CyanAccent300,
    secondary = AmberAccent,
    onSecondary = Navy900,
    secondaryContainer = AmberAccentContainer,
    onSecondaryContainer = AmberAccent,
    background = Navy900,
    onBackground = OnNavy,
    surface = SurfaceDark,
    onSurface = OnNavy,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnNavySecondary,
    outline = OutlineDark,
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = PrimaryBlueLight,
    onPrimaryContainer = Navy900,
    secondary = AmberAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFF3E0),
    onSecondaryContainer = Color(0xFF3D2A00),
    background = SurfaceLight,
    onBackground = NeutralGray900,
    surface = Color.White,
    onSurface = NeutralGray900,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = NeutralGray600,
    outline = OutlineLight,
)

@Composable
fun MagicCubeAndroidTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
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

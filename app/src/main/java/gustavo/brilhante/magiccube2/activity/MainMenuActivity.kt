package gustavo.brilhante.magiccube2.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import gustavo.brilhante.magiccube2.activity.ui.theme.MagicCubeAndroidTheme
import gustavo.brilhante.magiccube2.navigation.AppNavigation

/**
 * Single-Activity entry point for the entire app.
 * Navigation between screens is handled by Navigation3 inside [AppNavigation].
 */
class MainMenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MagicCubeAndroidTheme {
                AppNavigation()
            }
        }
    }
}

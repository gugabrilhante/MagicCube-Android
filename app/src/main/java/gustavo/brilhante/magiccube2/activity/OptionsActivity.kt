package gustavo.brilhante.magiccube2.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import gustavo.brilhante.magiccube2.activity.ui.theme.MagicCubeAndroidTheme
import gustavo.brilhante.magiccube2.compose.OptionsScreen

class OptionsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        setContent {
            MagicCubeAndroidTheme {
                OptionsScreen()
            }
        }
    }
}

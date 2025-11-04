package gustavo.brilhante.magiccube2.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import gustavo.brilhante.magiccube2.activity.ui.theme.MagicCubeAndroidTheme
import gustavo.brilhante.magiccube2.compose.MainMenuScreen

class MainMenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MagicCubeAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MagicCubeApp(modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),)
                }
            }
        }
    }
}

@Composable
fun MagicCubeApp(modifier: Modifier) {
    MaterialTheme {
        val context = LocalContext.current as Activity

        // Pode trocar por um NavController futuramente se tiver mais telas
        MainMenuScreen(
            modifier = modifier,
            onStartClick = {
                context.startActivity(Intent(context, MagicCubeActivity::class.java))
            },
            onOptionsClick = {
                 context.startActivity(Intent(context, OptionsActivity::class.java))
            },
            onQuitClick = {
                context.finish()
            }
        )
    }
}
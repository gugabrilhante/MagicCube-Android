package gustavo.brilhante.magiccube2.navigation

import android.app.Activity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import gustavo.brilhante.magiccube2.compose.CubeScreen
import gustavo.brilhante.magiccube2.compose.MainMenuScreen
import gustavo.brilhante.magiccube2.compose.OptionsScreen

// Uses remember + toMutableStateList instead of rememberNavBackStack to avoid
// the kotlinx-serialization plugin. Trade-off: back stack is not restored after process death.
@Composable
fun AppNavigation() {
    val backStack: SnapshotStateList<AppRoute> = remember {
        mutableListOf<AppRoute>(AppRoute.MainMenu).toMutableStateList()
    }
    val context = LocalContext.current

    val handleBack: () -> Unit = {
        if (backStack.size > 1) {
            backStack.removeLastOrNull()
        } else {
            (context as? Activity)?.finish()
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = { handleBack() },
        entryProvider = entryProvider {
            entry<AppRoute.MainMenu> {
                MainMenuScreen(
                    modifier = Modifier.fillMaxSize(),
                    onStartClick = { backStack.add(AppRoute.Cube) },
                    onOptionsClick = { backStack.add(AppRoute.Options) },
                    onQuitClick = { (context as? Activity)?.finish() },
                )
            }
            entry<AppRoute.Cube> {
                CubeScreen(onBack = handleBack)
            }
            entry<AppRoute.Options> {
                OptionsScreen()
            }
        },
    )
}

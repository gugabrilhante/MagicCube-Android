package gustavo.brilhante.magiccube2.navigation

import android.app.Activity
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import gustavo.brilhante.magiccube2.compose.CubeArrivalSpring
import gustavo.brilhante.magiccube2.compose.CubeTransitionOverlay
import gustavo.brilhante.magiccube2.compose.CubeTransitionState
import gustavo.brilhante.magiccube2.compose.CubeScreen
import gustavo.brilhante.magiccube2.compose.LocalCubeTransition
import gustavo.brilhante.magiccube2.compose.MainMenuScreen
import gustavo.brilhante.magiccube2.compose.OptionsScreen
import kotlinx.coroutines.launch

// Uses remember + toMutableStateList instead of rememberNavBackStack to avoid
// the kotlinx-serialization plugin. Trade-off: back stack is not restored after process death.
@Composable
fun AppNavigation() {
    val backStack: SnapshotStateList<AppRoute> = remember {
        mutableListOf<AppRoute>(AppRoute.MainMenu).toMutableStateList()
    }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val cubeTransitionState = remember { CubeTransitionState() }

    val handleBack: () -> Unit = {
        if (backStack.size > 1) {
            backStack.removeLastOrNull()
        } else {
            (context as? Activity)?.finish()
        }
    }

    CompositionLocalProvider(LocalCubeTransition provides cubeTransitionState) {
        Box(modifier = Modifier.fillMaxSize()) {

            NavDisplay(
                modifier = Modifier.fillMaxSize(),
                backStack = backStack,
                onBack = { handleBack() },
                transitionSpec = {
                    // Entry: fade + spring-scale from 90 % → 100 % with subtle overshoot.
                    // Exit:  fade + slight scale-up so the outgoing screen "recedes."
                    val enter = fadeIn(tween(300)) + scaleIn(
                        animationSpec = CubeArrivalSpring,
                        initialScale = 0.90f
                    )
                    val exit = fadeOut(tween(220)) + scaleOut(tween(220), targetScale = 1.06f)
                    enter togetherWith exit
                },
                entryProvider = entryProvider {
                    entry<AppRoute.MainMenu> {
                        MainMenuScreen(
                            modifier = Modifier.fillMaxSize(),
                            onStartClick = { backStack.add(AppRoute.Cube) },
                            onOptionsClick = {
                                // Navigate immediately — NavDisplay begins its own transition.
                                backStack.add(AppRoute.Options)
                                // Launch the cube fly-away overlay concurrently so it plays
                                // above the NavDisplay transition, drawing the eye upward.
                                coroutineScope.launch {
                                    cubeTransitionState.play(cubeTransitionState.cubeColors)
                                }
                            },
                            onQuitClick = { (context as? Activity)?.finish() },
                        )
                    }
                    entry<AppRoute.Cube> {
                        CubeScreen(onBack = handleBack)
                    }
                    entry<AppRoute.Options> {
                        OptionsScreen(onBack = handleBack)
                    }
                },
            )

            // Cube fly-away overlay — rendered above NavDisplay at zIndex 99.
            // Invisible until play() is called; cleans itself up automatically.
            CubeTransitionOverlay(state = cubeTransitionState)
        }
    }
}

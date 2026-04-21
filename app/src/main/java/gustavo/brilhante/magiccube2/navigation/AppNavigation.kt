package gustavo.brilhante.magiccube2.navigation

import android.app.Activity
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.ui.graphics.graphicsLayer
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

@Composable
fun AppNavigation() {
    val backStack: SnapshotStateList<AppRoute> = remember {
        mutableListOf<AppRoute>(AppRoute.MainMenu).toMutableStateList()
    }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val cubeTransitionState = remember { CubeTransitionState() }

    // Two independent Animatables so scale and fade can use different durations/easings.
    // scale: 1f → 0f over 360 ms (collapses to a point at centre)
    // alpha: 1f → 0f over 220 ms (fades out faster, making both effects clearly distinct)
    val mainMenuExitScale = remember { Animatable(1f) }
    val mainMenuExitAlpha = remember { Animatable(1f) }

    val handleBack: () -> Unit = {
        if (backStack.size > 1) backStack.removeLastOrNull()
        else (context as? Activity)?.finish()
    }

    val optionsBack: () -> Unit = {
        handleBack()
        scope.launch { cubeTransitionState.playReverse(cubeTransitionState.cubeColors) }
    }

    CompositionLocalProvider(LocalCubeTransition provides cubeTransitionState) {
        Box(modifier = Modifier.fillMaxSize()) {

            NavDisplay(
                modifier = Modifier.fillMaxSize(),
                backStack = backStack,
                onBack = {
                    if (backStack.lastOrNull() is AppRoute.Options) optionsBack()
                    else handleBack()
                },
                transitionSpec = {
                    val from = initialState.key
                    val to   = targetState.key
                    when {
                        // MainMenu → Cube: handled by the manual Animatables below.
                        // CubeScreen (AndroidView / GLSurface) appears after the exit animation.
                        from is AppRoute.MainMenu && to is AppRoute.Cube ->
                            EnterTransition.None togetherWith ExitTransition.None

                        // Cube → MainMenu: symmetric expansion with fade from the centre.
                        from is AppRoute.Cube && to is AppRoute.MainMenu ->
                            (fadeIn(tween(380)) + scaleIn(
                                animationSpec = tween(380, easing = FastOutSlowInEasing),
                                initialScale  = 0f
                            )) togetherWith ExitTransition.None

                        // All other routes: spring scale + fade.
                        else -> {
                            val enter = fadeIn(tween(300)) + scaleIn(CubeArrivalSpring, 0.90f)
                            val exit  = fadeOut(tween(220)) + scaleOut(tween(220), targetScale = 1.06f)
                            enter togetherWith exit
                        }
                    }
                },
                entryProvider = entryProvider {
                    entry<AppRoute.MainMenu> {
                        // Read in composable scope — both values are State under the hood,
                        // so graphicsLayer reacts on every animation frame.
                        val scale = mainMenuExitScale.value
                        val alpha = mainMenuExitAlpha.value

                        MainMenuScreen(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                    this.alpha = alpha
                                },
                            onStartClick = {
                                // Guard against double-tap while animation is running.
                                if (mainMenuExitScale.value == 1f) {
                                    scope.launch {
                                        // Fade and scale run concurrently with different durations.
                                        // Fade (220 ms) finishes first so the user sees the screen
                                        // grow transparent, then the collapsed ghost vanishes.
                                        launch {
                                            mainMenuExitAlpha.animateTo(
                                                targetValue   = 0f,
                                                animationSpec = tween(220, easing = FastOutSlowInEasing)
                                            )
                                        }
                                        val scaleJob = launch {
                                            mainMenuExitScale.animateTo(
                                                targetValue   = 0f,
                                                animationSpec = tween(360, easing = FastOutSlowInEasing)
                                            )
                                        }
                                        // Wait only for the slower animation.
                                        // fadeJob is guaranteed done before scaleJob finishes.
                                        scaleJob.join()

                                        backStack.add(AppRoute.Cube)

                                        // Reset so MainMenu is fully visible on the next visit.
                                        mainMenuExitScale.snapTo(1f)
                                        mainMenuExitAlpha.snapTo(1f)
                                    }
                                }
                            },
                            onOptionsClick = {
                                cubeTransitionState.prepareForTransition()
                                backStack.add(AppRoute.Options)
                                scope.launch { cubeTransitionState.play(cubeTransitionState.cubeColors) }
                            },
                            onQuitClick = { (context as? Activity)?.finish() },
                        )
                    }
                    entry<AppRoute.Cube> {
                        CubeScreen(onBack = handleBack)
                    }
                    entry<AppRoute.Options> {
                        OptionsScreen(onBack = optionsBack)
                    }
                },
            )

            CubeTransitionOverlay(state = cubeTransitionState)
        }
    }
}

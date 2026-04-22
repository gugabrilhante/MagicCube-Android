package gustavo.brilhante.magiccube2.compose

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import gustavo.brilhante.magiccube2.grafic.CubeRenderer
import gustavo.brilhante.magiccube2.grafic.CubeSurfaceView
import gustavo.brilhante.magiccube2.presentation.cube.CubeViewModel
import org.koin.androidx.compose.koinViewModel

// Matches AnimatedBackground's starting gradient colour so the overlay is
// indistinguishable from the MainMenu background at the moment of transition.
private val EntranceOverlayColor = Color(0xFF141E30)

@Composable
fun CubeScreen(
    modifier: Modifier = Modifier,
    viewModel: CubeViewModel = koinViewModel(),
    onBack: () -> Unit = {},
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val surfaceView = remember {
        CubeSurfaceView(context, viewModel).apply {
            setEGLContextClientVersion(3)
            setRenderer(CubeRenderer(viewModel))
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> surfaceView.onResume()
                Lifecycle.Event.ON_PAUSE -> surfaceView.onPause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            surfaceView.onPause()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Entrance overlay — a pure-Compose Box that sits on top of the GL surface.
    // GLSurfaceView renders on its own hardware layer and ignores graphicsLayer alpha,
    // so fading the view itself is impossible. Instead we place a dark overlay above it
    // and animate THAT from opaque → transparent, masking the background colour jump.
    val overlayAlpha = remember { Animatable(1f) }
    LaunchedEffect(Unit) {
        overlayAlpha.animateTo(
            targetValue   = 0f,
            animationSpec = tween(durationMillis = 480, easing = FastOutSlowInEasing)
        )
    }

    Box(modifier = modifier) {
        AndroidView(
            factory   = { surfaceView },
            modifier  = Modifier.fillMaxSize(),
        )

        // Pure-Compose overlay — drawn after AndroidView so it sits on top in Z order.
        // Once alpha reaches 0 the composition skips it entirely (no wasted draw calls).
        val alpha = overlayAlpha.value
        if (alpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { this.alpha = alpha }
                    .background(EntranceOverlayColor)
            )
        }
    }
}

package gustavo.brilhante.magiccube2.compose

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import gustavo.brilhante.magiccube2.grafic.CubeRenderer
import gustavo.brilhante.magiccube2.grafic.CubeSurfaceView
import gustavo.brilhante.magiccube2.presentation.cube.CubeViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * Full-screen Composable that hosts the OpenGL cube via [AndroidView].
 *
 * The [CubeSurfaceView] handles touch events internally and delegates to
 * [CubeViewModel]. Lifecycle events (pause/resume) are forwarded through
 * [LifecycleEventObserver] so the GL thread is properly suspended when the
 * app goes to the background.
 *
 * @param onBack Called when the user presses the system back button.
 */
@Composable
fun CubeScreen(
    modifier: Modifier = Modifier,
    viewModel: CubeViewModel = koinViewModel(),
    onBack: () -> Unit = {},
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Create the surface view once and reuse it across recompositions.
    val surfaceView = remember {
        CubeSurfaceView(context, viewModel).apply {
            setEGLContextClientVersion(3)
            setRenderer(CubeRenderer(viewModel))
        }
    }

    // Forward Activity lifecycle events to the GLSurfaceView.
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

    AndroidView(
        factory = { surfaceView },
        modifier = modifier,
    )
}

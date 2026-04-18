package gustavo.brilhante.magiccube2.compose

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt

// ---------------------------------------------------------------------------
// State
// ---------------------------------------------------------------------------

/**
 * Holds the runtime data needed to drive the cube fly-away transition.
 *
 * The source screen calls [updateSource] on every layout pass so the overlay
 * always knows where to start the animation from, even after rotation or
 * window resizing.
 *
 * [play] runs the Animatable; it is safe to call from any coroutine scope.
 * Re-entrant calls while an animation is already playing are silently ignored.
 */
class CubeTransitionState {

    // Position + size of the cube on the source screen, in root-relative pixels.
    var sourceBoundsLeft by mutableFloatStateOf(0f)
        internal set
    var sourceBoundsTop by mutableFloatStateOf(0f)
        internal set
    var sourceBoundsSizePx by mutableFloatStateOf(0f)
        internal set

    // A snapshot of the cube face colors taken just before the animation starts.
    var cubeColors by mutableStateOf<List<Long>>(emptyList())
        internal set

    var isActive by mutableStateOf(false)
        private set

    // progress drives every animated property; reading it inside @Composable
    // automatically subscribes the composable to its changes.
    private val progressAnim = Animatable(0f)
    val progress: Float get() = progressAnim.value

    fun updateSource(left: Float, top: Float, sizePx: Float) {
        sourceBoundsLeft = left
        sourceBoundsTop = top
        sourceBoundsSizePx = sizePx
    }

    suspend fun play(colors: List<Long>) {
        if (isActive) return
        cubeColors = colors
        isActive = true
        progressAnim.snapTo(0f)
        progressAnim.animateTo(
            targetValue = 1f,
            // 500 ms total: long enough to feel cinematic, short enough to feel snappy.
            animationSpec = tween(durationMillis = 500)
        )
        isActive = false
        progressAnim.snapTo(0f)
    }
}

/** Provides [CubeTransitionState] down the composition tree. Null outside [AppNavigation]. */
val LocalCubeTransition = staticCompositionLocalOf<CubeTransitionState?> { null }

// ---------------------------------------------------------------------------
// Overlay composable
// ---------------------------------------------------------------------------

/**
 * Renders an animated clone of the cube **above** every other UI layer.
 *
 * Animation design (progress 0 → 1, 500 ms):
 *  - 0 %  → 30 %  : cube scales 1.0 → 1.15  (it "jumps")
 *  - 30 % → 100 % : cube scales 1.15 → 0.2  (it shoots upward and shrinks)
 *  - 0 %  → 40 %  : alpha stays 1.0
 *  - 40 % → 85 %  : alpha 1.0 → 0.0         (fades while still moving)
 *  - 85 % → 100 % : alpha 0.0                (invisible, cleanup phase)
 *  - Y offset: linear 0 → -280 dp            (upward flight path)
 */
@Composable
fun CubeTransitionOverlay(state: CubeTransitionState) {
    val isVisible = state.isActive || state.progress > 0f
    if (!isVisible) return
    if (state.cubeColors.size < 9 || state.sourceBoundsSizePx == 0f) return

    val density = LocalDensity.current
    val liftPx = with(density) { 280f.dp.toPx() }
    val sizeDp = with(density) { state.sourceBoundsSizePx.toDp() }

    val p = state.progress

    val scale = when {
        p <= 0.30f -> lerpF(1.00f, 1.15f, p / 0.30f)
        else       -> lerpF(1.15f, 0.20f, (p - 0.30f) / 0.70f)
    }

    val alpha = when {
        p <= 0.40f -> 1f
        p <= 0.85f -> lerpF(1f, 0f, (p - 0.40f) / 0.45f)
        else       -> 0f
    }

    val yTranslationPx = -p * liftPx

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(99f)
    ) {
        Canvas(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = state.sourceBoundsLeft.roundToInt(),
                        y = state.sourceBoundsTop.roundToInt()
                    )
                }
                .size(sizeDp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationY = yTranslationPx
                    this.alpha = alpha
                    transformOrigin = TransformOrigin(0.5f, 0.5f)
                }
        ) {
            // Static render — no color animation, just a clean snapshot.
            val cellSize = size.minDimension / 3f
            state.cubeColors.take(9).forEachIndexed { index, argb ->
                val row = index / 3
                val col = index % 3
                drawRect(
                    color = Color(argb),
                    topLeft = Offset(col * cellSize, row * cellSize),
                    size = Size(cellSize - 4f, cellSize - 4f)
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Spring spec shared with NavDisplay for the "overshoot on arrival" feel
// ---------------------------------------------------------------------------

/**
 * Spring used by NavDisplay's enter transition to give the incoming screen a
 * subtle overshoot that makes it feel like it "lands" after the cube departs.
 */
val CubeArrivalSpring = spring<Float>(
    dampingRatio = 0.72f,       // slight overshoot without excessive bounce
    stiffness = Spring.StiffnessMedium
)

// ---------------------------------------------------------------------------
// Private helpers
// ---------------------------------------------------------------------------

private fun lerpF(a: Float, b: Float, t: Float) = a + (b - a) * t.coerceIn(0f, 1f)

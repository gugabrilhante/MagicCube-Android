package gustavo.brilhante.magiccube2.compose

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin

// ---------------------------------------------------------------------------
// Fallback colors — used when no live colors are available (direct navigation)
// ---------------------------------------------------------------------------

internal val DefaultCubeColors: List<Long> = listOf(
    0xFFB71C1CL, 0xFF1565C0L, 0xFFF9A825L,
    0xFF2E7D32L, 0xFFF5F5F5L, 0xFFE65100L,
    0xFF1565C0L, 0xFFB71C1CL, 0xFF2E7D32L
)

// ---------------------------------------------------------------------------
// State
// ---------------------------------------------------------------------------

/**
 * Drives the cube shared-element transition in **both directions**:
 *
 * - **Forward** (MainMenu → Options): large cube flies to the Options mini-cube.
 * - **Reverse** (Options → MainMenu): mini-cube flies back to the large cube.
 *
 * Lifecycle:
 *  1. [prepareForTransition] — resets the target before forward navigation.
 *  2. Source screen continuously updates [updateSource] via onGloballyPositioned.
 *  3. Destination screen calls [updateTarget] via onGloballyPositioned.
 *  4. [play] / [playReverse] starts the animation concurrently with navigation.
 */
class CubeTransitionState {

    // Large cube in MainMenuScreen, root-relative pixels.
    var sourceBoundsLeft by mutableFloatStateOf(0f)
        internal set
    var sourceBoundsTop by mutableFloatStateOf(0f)
        internal set
    var sourceBoundsSizePx by mutableFloatStateOf(0f)
        internal set

    // Mini-cube in OptionsScreen, root-relative pixels.
    var targetBoundsLeft by mutableFloatStateOf(0f)
        internal set
    var targetBoundsTop by mutableFloatStateOf(0f)
        internal set
    var targetBoundsSizePx by mutableFloatStateOf(0f)
        internal set

    // Color snapshot captured before each animation.
    var cubeColors by mutableStateOf<List<Long>>(emptyList())
        internal set

    var isActive by mutableStateOf(false)
        private set

    var isReverse by mutableStateOf(false)
        private set

    private val progressAnim = Animatable(0f)
    val progress: Float get() = progressAnim.value

    fun updateSource(left: Float, top: Float, sizePx: Float) {
        sourceBoundsLeft = left
        sourceBoundsTop = top
        sourceBoundsSizePx = sizePx
    }

    fun updateTarget(left: Float, top: Float, sizePx: Float) {
        targetBoundsLeft = left
        targetBoundsTop = top
        targetBoundsSizePx = sizePx
    }

    /** Call before forward navigation to discard stale target coordinates. */
    fun prepareForTransition() {
        targetBoundsLeft = 0f
        targetBoundsTop = 0f
        targetBoundsSizePx = 0f
    }

    /** Forward: large cube → Options mini-cube. Re-entrant calls are ignored. */
    suspend fun play(colors: List<Long>) {
        if (isActive) return
        cubeColors = colors
        isReverse = false
        isActive = true
        progressAnim.snapTo(0f)
        progressAnim.animateTo(1f, animationSpec = tween(560, easing = FastOutSlowInEasing))
        isActive = false
        progressAnim.snapTo(0f)
    }

    /** Reverse: Options mini-cube → large cube. Re-entrant calls are ignored. */
    suspend fun playReverse(colors: List<Long>) {
        if (isActive) return
        cubeColors = colors
        isReverse = true
        isActive = true
        progressAnim.snapTo(0f)
        progressAnim.animateTo(1f, animationSpec = tween(560, easing = FastOutSlowInEasing))
        isActive = false
        isReverse = false
        progressAnim.snapTo(0f)
    }
}

val LocalCubeTransition = staticCompositionLocalOf<CubeTransitionState?> { null }

// ---------------------------------------------------------------------------
// Alpha helpers consumed by the screens
// ---------------------------------------------------------------------------

/**
 * Alpha for the **large cube** in MainMenuScreen.
 *
 * During the **reverse** transition the large cube hides while the mini-cube
 * is flying toward it, then crossfades in at handoff (same window as the
 * overlay fades out). This makes the landing feel like a real materialisation.
 *
 * During the **forward** transition it stays fully visible (NavDisplay fades
 * the whole screen out anyway, no special handling needed).
 */
fun largeCubeAlpha(isActive: Boolean, isReverse: Boolean, progress: Float): Float {
    if (!isActive && progress == 0f) return 1f
    if (!isReverse) return 1f
    return when {
        progress <= 0.65f -> 0f
        progress <= 0.90f -> lerpF(0f, 1f, (progress - 0.65f) / 0.25f)
        else -> 1f
    }
}

/**
 * Alpha for the **mini-cube landing zone** in OptionsScreen.
 *
 * - Forward: hidden while the overlay is traveling, crossfades in at handoff.
 * - Reverse: immediately hidden (overlay takes over the role of the mini-cube).
 * - No active transition: always visible.
 */
fun miniCubeAlpha(isActive: Boolean, isReverse: Boolean, progress: Float): Float {
    if (!isActive && progress == 0f) return 1f
    if (isReverse) return 0f                    // overlay represents it; keep it hidden
    return when {
        progress <= 0.65f -> 0f
        progress <= 0.90f -> lerpF(0f, 1f, (progress - 0.65f) / 0.25f)
        else -> 1f
    }
}

/**
 * Alpha for the **cards content** in OptionsScreen.
 *
 * During the **reverse** transition the cards fade out quickly as the cube
 * departs, giving the impression it is "taking the settings with it."
 * Fully transparent by 50 % progress.
 */
fun optionsContentAlpha(isActive: Boolean, isReverse: Boolean, progress: Float): Float {
    if (!isActive && progress == 0f) return 1f
    if (!isReverse) return 1f
    return (1f - progress * 2f).coerceAtLeast(0f)
}

// ---------------------------------------------------------------------------
// Spring for NavDisplay screen entries
// ---------------------------------------------------------------------------

val CubeArrivalSpring = spring<Float>(
    dampingRatio = 0.72f,
    stiffness = Spring.StiffnessMedium
)

// ---------------------------------------------------------------------------
// Overlay
// ---------------------------------------------------------------------------

/**
 * Draws the animated cube clone **above all UI** (zIndex 99).
 *
 * Works for both directions by swapping the effective source / target endpoints
 * when [CubeTransitionState.isReverse] is true.
 *
 * ### Position — arc trajectory
 *  - Center linearly interpolated between endpoints.
 *  - Y shifted by `–sin(p · π) · 80dp`: an upward arc that peaks at the
 *    midpoint, creating a natural "thrown object" path in both directions.
 *
 * ### Size — constant-dp overshoot spring
 *  - 0 %  → 75 % : lerp from start-size → target-size + 12 dp (overshoot)
 *  - 75 % → 90 % : lerp target+12 dp → target–6 dp (undershoot / bounce)
 *  - 90 % → 100 %: lerp target–6 dp → exact target-size (settle)
 *
 * ### Crossfade handoff
 *  - 0 %  → 65 % : overlay alpha = 1, destination element alpha = 0
 *  - 65 % → 90 % : overlay fades 1 → 0, destination fades 0 → 1
 *  - 90 % → 100 %: overlay invisible, destination fully visible
 */
@Composable
fun CubeTransitionOverlay(state: CubeTransitionState) {
    if (!state.isActive && state.progress == 0f) return
    if (state.cubeColors.size < 9 || state.sourceBoundsSizePx == 0f) return

    val density = LocalDensity.current
    val p = state.progress

    // Resolve effective endpoints based on direction.
    // Forward:  source = large cube (MainMenu),  target = mini-cube (Options)
    // Reverse:  source = mini-cube (Options),    target = large cube (MainMenu)
    val hasTgt = state.targetBoundsSizePx > 0f

    val (eSrcLeft, eSrcTop, eSrcSz, eTgtLeft, eTgtTop, eTgtSz) = if (!state.isReverse) {
        // Forward
        val tgtL = if (hasTgt) state.targetBoundsLeft else state.sourceBoundsLeft
        val tgtT = if (hasTgt) state.targetBoundsTop  else state.sourceBoundsTop - with(density) { 260f.dp.toPx() }
        val tgtS = if (hasTgt) state.targetBoundsSizePx else state.sourceBoundsSizePx * 0.28f
        SixFloats(state.sourceBoundsLeft, state.sourceBoundsTop, state.sourceBoundsSizePx,
                  tgtL, tgtT, tgtS)
    } else {
        // Reverse — swap: Options mini-cube becomes the start, large cube becomes the end
        val tgtL = if (hasTgt) state.targetBoundsLeft else state.sourceBoundsLeft
        val tgtT = if (hasTgt) state.targetBoundsTop  else state.sourceBoundsTop
        val tgtS = if (hasTgt) state.targetBoundsSizePx else state.sourceBoundsSizePx * 0.28f
        SixFloats(tgtL, tgtT, tgtS,
                  state.sourceBoundsLeft, state.sourceBoundsTop, state.sourceBoundsSizePx)
    }

    // -- Trajectory --
    val srcCx = eSrcLeft + eSrcSz / 2f
    val srcCy = eSrcTop  + eSrcSz / 2f
    val tgtCx = eTgtLeft + eTgtSz / 2f
    val tgtCy = eTgtTop  + eTgtSz / 2f

    val arcPx = with(density) { 80f.dp.toPx() }
    val currentCx = lerpF(srcCx, tgtCx, p)
    val currentCy = lerpF(srcCy, tgtCy, p) - sin(p * PI.toFloat()) * arcPx

    // -- Size with constant-dp overshoot spring --
    val overshootPx = with(density) { 12f.dp.toPx() }
    val undershootPx = with(density) { 6f.dp.toPx() }
    val currentSizePx = when {
        p <= 0.75f -> lerpF(eSrcSz, eTgtSz + overshootPx, p / 0.75f)
        p <= 0.90f -> lerpF(eTgtSz + overshootPx, eTgtSz - undershootPx, (p - 0.75f) / 0.15f)
        else       -> lerpF(eTgtSz - undershootPx, eTgtSz,               (p - 0.90f) / 0.10f)
    }

    // -- Overlay alpha (crossfade handoff) --
    val overlayAlpha = when {
        p <= 0.65f -> 1f
        p <= 0.90f -> lerpF(1f, 0f, (p - 0.65f) / 0.25f)
        else       -> 0f
    }

    val sizeDp   = with(density) { currentSizePx.toDp() }
    val offsetX  = (currentCx - currentSizePx / 2f).roundToInt()
    val offsetY  = (currentCy - currentSizePx / 2f).roundToInt()

    Box(modifier = Modifier.fillMaxSize().zIndex(99f)) {
        Canvas(
            modifier = Modifier
                .offset { IntOffset(offsetX, offsetY) }
                .size(sizeDp)
                .graphicsLayer { this.alpha = overlayAlpha }
        ) {
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
// Internal helpers
// ---------------------------------------------------------------------------

private data class SixFloats(
    val a: Float, val b: Float, val c: Float,
    val d: Float, val e: Float, val f: Float
)

private operator fun SixFloats.component1() = a
private operator fun SixFloats.component2() = b
private operator fun SixFloats.component3() = c
private operator fun SixFloats.component4() = d
private operator fun SixFloats.component5() = e
private operator fun SixFloats.component6() = f

internal fun lerpF(a: Float, b: Float, t: Float) = a + (b - a) * t.coerceIn(0f, 1f)

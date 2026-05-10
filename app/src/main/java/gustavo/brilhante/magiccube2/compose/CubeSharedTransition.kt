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
import gustavo.brilhante.magiccube2.domain.math.AnimationMath
import kotlin.math.roundToInt

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
 * Drives the cube shared-element transition in **both directions**.
 */
class CubeTransitionState {

    var sourceBoundsLeft by mutableFloatStateOf(0f)
        internal set
    var sourceBoundsTop by mutableFloatStateOf(0f)
        internal set
    var sourceBoundsSizePx by mutableFloatStateOf(0f)
        internal set

    var targetBoundsLeft by mutableFloatStateOf(0f)
        internal set
    var targetBoundsTop by mutableFloatStateOf(0f)
        internal set
    var targetBoundsSizePx by mutableFloatStateOf(0f)
        internal set

    var cubeColors by mutableStateOf<List<Long>>(DefaultCubeColors)
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

    fun prepareForTransition() {
        targetBoundsLeft = 0f
        targetBoundsTop = 0f
        targetBoundsSizePx = 0f
    }

    suspend fun play(colors: List<Long>) {
        if (isActive) return
        cubeColors = colors.toList()
        isReverse = false
        isActive = true
        progressAnim.snapTo(0f)
        try {
            progressAnim.animateTo(1f, animationSpec = tween(560, easing = FastOutSlowInEasing))
        } finally {
            isActive = false
            cubeColors = DefaultCubeColors
            progressAnim.snapTo(0f)
        }
    }

    suspend fun playReverse(colors: List<Long>) {
        if (isActive) return
        cubeColors = colors.toList()
        isReverse = true
        isActive = true
        progressAnim.snapTo(0f)
        try {
            progressAnim.animateTo(1f, animationSpec = tween(560, easing = FastOutSlowInEasing))
        } finally {
            isActive = false
            isReverse = false
            cubeColors = DefaultCubeColors
            progressAnim.snapTo(0f)
        }
    }
}

val LocalCubeTransition = staticCompositionLocalOf<CubeTransitionState?> { null }

// ---------------------------------------------------------------------------
// Alpha helpers consumed by the screens
// ---------------------------------------------------------------------------

fun largeCubeAlpha(isActive: Boolean, isReverse: Boolean, progress: Float): Float =
    AnimationMath.calculateLargeCubeAlpha(isActive, isReverse, progress)

fun miniCubeAlpha(isActive: Boolean, isReverse: Boolean, progress: Float): Float =
    AnimationMath.calculateMiniCubeAlpha(isActive, isReverse, progress)

fun optionsContentAlpha(isActive: Boolean, isReverse: Boolean, progress: Float): Float =
    AnimationMath.calculateOptionsContentAlpha(isActive, isReverse, progress)

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
 */
@Composable
fun CubeTransitionOverlay(state: CubeTransitionState) {
    if (!state.isActive && state.progress == 0f) return
    if (state.cubeColors.size < 9 || state.sourceBoundsSizePx == 0f) return

    val density = LocalDensity.current
    val p = state.progress

    val hasTgt = state.targetBoundsSizePx > 0f

    val (eSrcLeft, eSrcTop, eSrcSz, eTgtLeft, eTgtTop, eTgtSz) = if (!state.isReverse) {
        val tgtL = if (hasTgt) state.targetBoundsLeft else state.sourceBoundsLeft
        val tgtT = if (hasTgt) state.targetBoundsTop  else state.sourceBoundsTop - with(density) { 260f.dp.toPx() }
        val tgtS = if (hasTgt) state.targetBoundsSizePx else state.sourceBoundsSizePx * 0.28f
        SixFloats(state.sourceBoundsLeft, state.sourceBoundsTop, state.sourceBoundsSizePx, tgtL, tgtT, tgtS)
    } else {
        val tgtL = if (hasTgt) state.targetBoundsLeft else state.sourceBoundsLeft
        val tgtT = if (hasTgt) state.targetBoundsTop  else state.sourceBoundsTop
        val tgtS = if (hasTgt) state.targetBoundsSizePx else state.sourceBoundsSizePx * 0.28f
        SixFloats(tgtL, tgtT, tgtS, state.sourceBoundsLeft, state.sourceBoundsTop, state.sourceBoundsSizePx)
    }

    // -- Trajectory --
    val srcCx = eSrcLeft + eSrcSz / 2f
    val srcCy = eSrcTop  + eSrcSz / 2f
    val tgtCx = eTgtLeft + eTgtSz / 2f
    val tgtCy = eTgtTop  + eTgtSz / 2f

    val arcPx = with(density) { 80f.dp.toPx() }
    val currentCx = AnimationMath.lerp(srcCx, tgtCx, p)
    val currentCy = AnimationMath.lerp(srcCy, tgtCy, p) - AnimationMath.calculateArcOffset(p, arcPx)

    // -- Size with constant-dp overshoot spring --
    val overshootPx = with(density) { 12f.dp.toPx() }
    val undershootPx = with(density) { 6f.dp.toPx() }
    val currentSizePx = AnimationMath.calculateOvershootSize(p, eSrcSz, eTgtSz, overshootPx, undershootPx)

    // -- Overlay alpha (crossfade handoff) --
    val overlayAlpha = AnimationMath.calculateOverlayAlpha(p)

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

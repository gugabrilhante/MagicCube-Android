package gustavo.brilhante.magiccube2.domain.math

import kotlin.math.sin

object AnimationMath {

    fun lerp(a: Float, b: Float, t: Float): Float = a + (b - a) * t.coerceIn(0f, 1f)

    fun calculateLargeCubeAlpha(isActive: Boolean, isReverse: Boolean, progress: Float): Float {
        if (!isActive && progress == 0f) return 1f
        if (!isReverse) return 1f
        return when {
            progress <= 0.65f -> 0f
            progress <= 0.90f -> lerp(0f, 1f, (progress - 0.65f) / 0.25f)
            else -> 1f
        }
    }

    fun calculateMiniCubeAlpha(isActive: Boolean, isReverse: Boolean, progress: Float): Float {
        if (!isActive && progress == 0f) return 1f
        if (isReverse) return 0f
        return when {
            progress <= 0.65f -> 0f
            progress <= 0.90f -> lerp(0f, 1f, (progress - 0.65f) / 0.25f)
            else -> 1f
        }
    }

    fun calculateOptionsContentAlpha(isActive: Boolean, isReverse: Boolean, progress: Float): Float {
        if (!isActive && progress == 0f) return 1f
        if (!isReverse) return 1f
        return (1f - progress * 2f).coerceAtLeast(0f)
    }

    fun calculateOverlayAlpha(progress: Float): Float = when {
        progress <= 0.65f -> 1f
        progress <= 0.90f -> lerp(1f, 0f, (progress - 0.65f) / 0.25f)
        else -> 0f
    }

    fun calculateArcOffset(progress: Float, arcPx: Float): Float =
        sin(progress * Math.PI.toFloat()) * arcPx

    fun calculateOvershootSize(progress: Float, srcSz: Float, tgtSz: Float, overshootPx: Float, undershootPx: Float): Float = when {
        progress <= 0.75f -> lerp(srcSz, tgtSz + overshootPx, progress / 0.75f)
        progress <= 0.90f -> lerp(tgtSz + overshootPx, tgtSz - undershootPx, (progress - 0.75f) / 0.15f)
        else -> lerp(tgtSz - undershootPx, tgtSz, (progress - 0.90f) / 0.10f)
    }
}

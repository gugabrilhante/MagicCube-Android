package gustavo.brilhante.magiccube2.domain.math

import org.junit.Assert.assertEquals
import org.junit.Test

class AnimationMathTest {

    @Test
    fun `lerp should interpolate correctly`() {
        assertEquals(5f, AnimationMath.lerp(0f, 10f, 0.5f), 0.001f)
        assertEquals(0f, AnimationMath.lerp(0f, 10f, 0f), 0.001f)
        assertEquals(10f, AnimationMath.lerp(0f, 10f, 1f), 0.001f)
        assertEquals(10f, AnimationMath.lerp(0f, 10f, 1.5f), 0.001f) // coerceIn
        assertEquals(0f, AnimationMath.lerp(0f, 10f, -0.5f), 0.001f) // coerceIn
    }

    @Test
    fun `calculateLargeCubeAlpha tests`() {
        // !isActive && progress == 0f -> 1f
        assertEquals(1f, AnimationMath.calculateLargeCubeAlpha(false, false, 0f), 0.001f)
        // !isReverse -> 1f
        assertEquals(1f, AnimationMath.calculateLargeCubeAlpha(true, false, 0.5f), 0.001f)
        // isReverse
        assertEquals(0f, AnimationMath.calculateLargeCubeAlpha(true, true, 0.5f), 0.001f)
        assertEquals(0.4f, AnimationMath.calculateLargeCubeAlpha(true, true, 0.75f), 0.001f) // (0.75-0.65)/0.25 = 0.1/0.25 = 0.4
        assertEquals(1f, AnimationMath.calculateLargeCubeAlpha(true, true, 0.95f), 0.001f)
    }

    @Test
    fun `calculateMiniCubeAlpha tests`() {
        // !isActive && progress == 0f -> 1f
        assertEquals(1f, AnimationMath.calculateMiniCubeAlpha(false, false, 0f), 0.001f)
        // isReverse -> 0f
        assertEquals(0f, AnimationMath.calculateMiniCubeAlpha(true, true, 0.5f), 0.001f)
        // !isReverse
        assertEquals(0f, AnimationMath.calculateMiniCubeAlpha(true, false, 0.5f), 0.001f)
        assertEquals(0.4f, AnimationMath.calculateMiniCubeAlpha(true, false, 0.75f), 0.001f)
        assertEquals(1f, AnimationMath.calculateMiniCubeAlpha(true, false, 0.95f), 0.001f)
    }

    @Test
    fun `calculateOptionsContentAlpha tests`() {
        assertEquals(1f, AnimationMath.calculateOptionsContentAlpha(false, false, 0f), 0.001f)
        assertEquals(1f, AnimationMath.calculateOptionsContentAlpha(true, false, 0.5f), 0.001f)
        assertEquals(0.2f, AnimationMath.calculateOptionsContentAlpha(true, true, 0.4f), 0.001f) // 1 - 0.4*2 = 0.2
        assertEquals(0f, AnimationMath.calculateOptionsContentAlpha(true, true, 0.6f), 0.001f) // 1 - 1.2 = -0.2 -> 0f
    }

    @Test
    fun `calculateOverlayAlpha tests`() {
        assertEquals(1f, AnimationMath.calculateOverlayAlpha(0.5f), 0.001f)
        assertEquals(0.6f, AnimationMath.calculateOverlayAlpha(0.75f), 0.001f) // lerp(1,0,0.4) = 1 + (0-1)*0.4 = 0.6
        assertEquals(0f, AnimationMath.calculateOverlayAlpha(0.95f), 0.001f)
    }

    @Test
    fun `calculateArcOffset tests`() {
        assertEquals(0f, AnimationMath.calculateArcOffset(0f, 100f), 0.001f)
        assertEquals(100f, AnimationMath.calculateArcOffset(0.5f, 100f), 0.001f)
        assertEquals(0f, AnimationMath.calculateArcOffset(1f, 100f), 0.001f)
    }

    @Test
    fun `calculateOvershootSize tests`() {
        // progress <= 0.75f: lerp(src, tgt+over, p/0.75)
        assertEquals(110f, AnimationMath.calculateOvershootSize(0.75f, 0f, 100f, 10f, 5f), 0.001f)
        // progress <= 0.90f: lerp(tgt+over, tgt-under, (p-0.75)/0.15)
        assertEquals(95f, AnimationMath.calculateOvershootSize(0.90f, 0f, 100f, 10f, 5f), 0.001f)
        // else: lerp(tgt-under, tgt, (p-0.90)/0.10)
        assertEquals(100f, AnimationMath.calculateOvershootSize(1.0f, 0f, 100f, 10f, 5f), 0.001f)
    }
}

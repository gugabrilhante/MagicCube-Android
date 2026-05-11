package gustavo.brilhante.magiccube2.domain.cube

import gustavo.brilhante.magiccube2.domain.model.Vector2
import gustavo.brilhante.magiccube2.domain.model.Vector3
import org.junit.Assert.assertEquals
import org.junit.Test

class CubeRotationMathTest {

    private val rotationMath = CubeRotationMath()

    @Test
    fun `given zero rotation when computeLocalDrag then returns same vector`() {
        val screenDelta = Vector2(10f, 20f)
        val result = rotationMath.computeLocalDrag(screenDelta, 0f, 0f)
        assertEquals(10f, result.x, 0.001f)
        assertEquals(20f, result.y, 0.001f)
    }

    @Test
    fun `given 90 degree Y rotation when computeLocalDrag then transforms correctly`() {
        val screenDelta = Vector2(10f, 20f)
        // cos(90)=0, sin(90)=1. 
        // x = 10*0 - 20*0*1 = 0
        // y = 20*1 = 20
        val result = rotationMath.computeLocalDrag(screenDelta, 90f, 0f)
        assertEquals(0f, result.x, 0.001f)
        assertEquals(20f, result.y, 0.001f)
    }

    @Test
    fun `given 180 degree rotation when computeLocalDrag then reverses direction`() {
        val screenDelta = Vector2(10f, 20f)
        val result = rotationMath.computeLocalDrag(screenDelta, 180f, 0f)
        assertEquals(-10f, result.x, 0.001f)
        // Y remains same in this specific projection formula
        assertEquals(20f, result.y, 0.001f)
    }

    @Test
    fun `given 90 degree Y rotation when localToScreenSpace then transforms correctly`() {
        // At 90 deg Y, local X becomes world Z, local Z becomes world -X
        val v = Vector3(1f, 0f, 0f)
        val result = rotationMath.localToScreenSpace(v, 90f, 0f)
        // x1 = v.x * cos(90) + v.z * sin(90) = 1 * 0 + 0 * 1 = 0
        // y1 = v.y = 0
        // z1 = -v.x * sin(90) + v.z * cos(90) = -1 * 1 + 0 * 0 = -1
        // x2 = x1 = 0
        // y2 = y1 * cos(0) - z1 * sin(0) = 0 * 1 - (-1) * 0 = 0
        // return Vector2(x2, -y2) = (0, 0)
        assertEquals(0f, result.x, 0.001f)
        assertEquals(0f, result.y, 0.001f)
    }

    @Test
    fun `given negative rotation when computeLocalDrag then works correctly`() {
        val screenDelta = Vector2(10f, 20f)
        val result = rotationMath.computeLocalDrag(screenDelta, -180f, 0f)
        assertEquals(-10f, result.x, 0.001f)
        assertEquals(20f, result.y, 0.001f)
    }

    @Test
    fun `given 270 degree rotation when computeLocalDrag then same as -90`() {
        val screenDelta = Vector2(10f, 20f)
        val result1 = rotationMath.computeLocalDrag(screenDelta, 270f, 0f)
        val result2 = rotationMath.computeLocalDrag(screenDelta, -90f, 0f)
        assertEquals(result1.x, result2.x, 0.001f)
        assertEquals(result1.y, result2.y, 0.001f)
    }
}

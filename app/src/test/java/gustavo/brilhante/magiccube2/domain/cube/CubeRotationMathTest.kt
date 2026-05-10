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
    fun `given 90 degree Y rotation when localToScreenSpace then transforms correctly`() {
        // At 90 deg Y, local X becomes world Z, local Z becomes world -X
        val v = Vector3(1f, 0f, 0f)
        val result = rotationMath.localToScreenSpace(v, 90f, 0f)
        // localToScreenSpace: x1 = v.x * cosY + v.z * sinY = 1 * 0 + 0 * 1 = 0
        assertEquals(0f, result.x, 0.001f)
    }

    @Test
    fun `given 180 degree rotation when computeLocalDrag then reverses direction`() {
        val screenDelta = Vector2(10f, 20f)
        val result = rotationMath.computeLocalDrag(screenDelta, 180f, 0f)
        assertEquals(-10f, result.x, 0.001f)
        // Y remains same in this specific projection formula
        assertEquals(20f, result.y, 0.001f)
    }
}

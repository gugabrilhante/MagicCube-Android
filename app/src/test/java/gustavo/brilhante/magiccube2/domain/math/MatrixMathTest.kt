package gustavo.brilhante.magiccube2.domain.math

import gustavo.brilhante.magiccube2.domain.model.Vector3
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.sqrt

class MatrixMathTest {

    @Test
    fun `setIdentityM should create identity matrix`() {
        val matrix = FloatArray(16)
        MatrixMath.setIdentityM(matrix, 0)
        
        val expected = floatArrayOf(
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f
        )
        assertArrayEquals(expected, matrix, 0f)
    }

    @Test
    fun `crossProduct should return correct vector`() {
        val v1 = Vector3(1f, 0f, 0f)
        val v2 = Vector3(0f, 1f, 0f)
        val result = MatrixMath.crossProduct(v1, v2)
        
        assertEquals(0f, result.x)
        assertEquals(0f, result.y)
        assertEquals(1f, result.z)
    }

    @Test
    fun `normalize should return unit vector`() {
        val v = Vector3(3f, 4f, 0f)
        val result = MatrixMath.normalize(v)
        
        val length = sqrt(result.x * result.x + result.y * result.y + result.z * result.z)
        assertEquals(1f, length, 1e-6f)
        assertEquals(0.6f, result.x, 1e-6f)
        assertEquals(0.8f, result.y, 1e-6f)
        assertEquals(0f, result.z, 1e-6f)
    }

    @Test
    fun `normalize of zero vector should return zero vector`() {
        val v = Vector3.Zero
        val result = MatrixMath.normalize(v)
        assertEquals(Vector3.Zero, result)
    }
}

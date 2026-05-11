package gustavo.brilhante.magiccube2.domain.math

import gustavo.brilhante.magiccube2.domain.model.Vector3
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MatrixMathTest {

    private lateinit var matrixMath: MatrixMath

    @Before
    fun setUp() {
        matrixMath = MatrixMath()
    }

    @Test
    fun `setIdentityM should set identity matrix`() {
        val matrix = FloatArray(16)
        matrixMath.setIdentityM(matrix, 0)

        val expected = floatArrayOf(
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f
        )
        assertArrayEquals(expected, matrix, 0.001f)
    }

    @Test
    fun `translateM should apply translation`() {
        val matrix = FloatArray(16)
        matrixMath.setIdentityM(matrix, 0)
        matrixMath.translateM(matrix, 0, 1f, 2f, 3f)

        val expected = floatArrayOf(
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            1f, 2f, 3f, 1f
        )
        assertArrayEquals(expected, matrix, 0.001f)
    }

    @Test
    fun `rotateM 90 degrees around X axis`() {
        val matrix = FloatArray(16)
        matrixMath.setIdentityM(matrix, 0)
        matrixMath.rotateM(matrix, 0, 90f, 1f, 0f, 0f)

        // Rotation around X by 90 deg:
        // [ 1  0  0  0 ]
        // [ 0  cos -sin 0 ] -> [ 0  0 -1  0 ]
        // [ 0  sin  cos 0 ] -> [ 0  1  0  0 ]
        // [ 0  0  0  1 ]
        val expected = floatArrayOf(
            1f, 0f, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, -1f, 0f, 0f,
            0f, 0f, 0f, 1f
        )
        // Wait, OpenGL matrices are column-major. 
        // My manual check:
        // row 0: 1 0 0 0
        // row 1: 0 0 -1 0
        // row 2: 0 1 0 0
        // row 3: 0 0 0 1
        // Column major:
        // col 0: 1 0 0 0
        // col 1: 0 0 1 0
        // col 2: 0 -1 0 0
        // col 3: 0 0 0 1
        assertArrayEquals(expected, matrix, 0.001f)
    }

    @Test
    fun `multiplyMM should multiply two matrices`() {
        val m1 = FloatArray(16)
        val m2 = FloatArray(16)
        matrixMath.setIdentityM(m1, 0)
        matrixMath.setIdentityM(m2, 0)
        
        m1[12] = 1f // translation x=1
        m2[13] = 2f // translation y=2

        val result = FloatArray(16)
        matrixMath.multiplyMM(result, 0, m1, 0, m2, 0)

        val expected = FloatArray(16)
        matrixMath.setIdentityM(expected, 0)
        expected[12] = 1f
        expected[13] = 2f
        
        assertArrayEquals(expected, result, 0.001f)
    }

    @Test
    fun `multiplyMV should multiply matrix and vector`() {
        val m = FloatArray(16)
        matrixMath.setIdentityM(m, 0)
        matrixMath.translateM(m, 0, 1f, 2f, 3f)

        val v = floatArrayOf(1f, 1f, 1f, 1f)
        val result = FloatArray(4)
        matrixMath.multiplyMV(result, 0, m, 0, v, 0)

        // (1,1,1,1) translated by (1,2,3) should be (2,3,4,1)
        val expected = floatArrayOf(2f, 3f, 4f, 1f)
        assertArrayEquals(expected, result, 0.001f)
    }

    @Test
    fun `invertM should invert matrix`() {
        val m = FloatArray(16)
        matrixMath.setIdentityM(m, 0)
        matrixMath.translateM(m, 0, 1f, 2f, 3f)
        matrixMath.rotateM(m, 0, 90f, 0f, 1f, 0f)

        val inv = FloatArray(16)
        val success = matrixMath.invertM(inv, 0, m, 0)
        assertTrue(success)

        val result = FloatArray(16)
        matrixMath.multiplyMM(result, 0, m, 0, inv, 0)

        val identity = FloatArray(16)
        matrixMath.setIdentityM(identity, 0)
        assertArrayEquals(identity, result, 0.001f)
    }

    @Test
    fun `invertM should return false for singular matrix`() {
        val m = FloatArray(16) // all zeros
        val inv = FloatArray(16)
        val success = matrixMath.invertM(inv, 0, m, 0)
        assertFalse(success)
    }

    @Test
    fun `crossProduct should return correct vector`() {
        val v1 = Vector3(1f, 0f, 0f)
        val v2 = Vector3(0f, 1f, 0f)
        val result = matrixMath.crossProduct(v1, v2)
        assertEquals(Vector3(0f, 0f, 1f), result)
    }

    @Test
    fun `normalize should return unit vector`() {
        val v = Vector3(3f, 0f, 0f)
        val result = matrixMath.normalize(v)
        assertEquals(Vector3(1f, 0f, 0f), result)
    }

    @Test
    fun `normalize zero vector should return zero vector`() {
        val v = Vector3.Zero
        val result = matrixMath.normalize(v)
        assertEquals(Vector3.Zero, result)
    }

    @Test
    fun `frustumM should generate projection matrix`() {
        val m = FloatArray(16)
        matrixMath.frustumM(m, 0, -1f, 1f, -1f, 1f, 1f, 10f)
        
        // rWidth = 1/2, rHeight = 1/2, rDepth = 1/-9
        // x = 2 * 1 * 1/2 = 1
        // y = 2 * 1 * 1/2 = 1
        // a = 0
        // b = 0
        // c = (10+1)/-9 = -11/9
        // d = 2*10*1/-9 = -20/9
        
        assertEquals(1f, m[0], 0.001f)
        assertEquals(1f, m[5], 0.001f)
        assertEquals(0f, m[8], 0.001f)
        assertEquals(0f, m[9], 0.001f)
        assertEquals(-11f/9f, m[10], 0.001f)
        assertEquals(-20f/9f, m[14], 0.001f)
        assertEquals(-1f, m[11], 0.001f)
    }
}

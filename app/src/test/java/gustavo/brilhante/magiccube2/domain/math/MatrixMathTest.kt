package gustavo.brilhante.magiccube2.domain.math

import gustavo.brilhante.magiccube2.domain.model.Vector3
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MatrixMathTest {

    private val matrixMath = MatrixMath()
    private val delta = 1e-6f

    @Test
    fun `setIdentityM should create identity matrix`() {
        val matrix = FloatArray(16)
        matrixMath.setIdentityM(matrix, 0)
        
        val expected = floatArrayOf(
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f
        )
        assertArrayEquals(expected, matrix, 0f)
    }

    @Test
    fun `translateM should translate matrix correctly`() {
        val matrix = FloatArray(16)
        matrixMath.setIdentityM(matrix, 0)
        matrixMath.translateM(matrix, 0, 1f, 2f, 3f)

        val expected = floatArrayOf(
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            1f, 2f, 3f, 1f
        )
        assertArrayEquals(expected, matrix, 0f)
    }

    @Test
    fun `rotateM should rotate matrix correctly around X axis`() {
        val matrix = FloatArray(16)
        matrixMath.setIdentityM(matrix, 0)
        val angle = 90f
        matrixMath.rotateM(matrix, 0, angle, 1f, 0f, 0f)

        // Rotation around X by 90 degrees
        // [ 1  0       0      0 ]
        // [ 0  cos(a) -sin(a) 0 ]
        // [ 0  sin(a)  cos(a) 0 ]
        // [ 0  0       0      1 ]
        // cos(90) = 0, sin(90) = 1
        val expected = floatArrayOf(
            1f, 0f, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, -1f, 0f, 0f,
            0f, 0f, 0f, 1f
        )
        assertArrayEquals(expected, matrix, delta)
    }

    @Test
    fun `rotateM should rotate matrix correctly around Z axis`() {
        val matrix = FloatArray(16)
        matrixMath.setIdentityM(matrix, 0)
        val angle = 90f
        matrixMath.rotateM(matrix, 0, angle, 0f, 0f, 1f)

        // Rotation around Z by 90 degrees
        // [ cos(a) -sin(a) 0 0 ]
        // [ sin(a)  cos(a) 0 0 ]
        // [ 0       0      1 0 ]
        // [ 0       0      0 1 ]
        // cos(90) = 0, sin(90) = 1
        val expected = floatArrayOf(
            0f, 1f, 0f, 0f,
            -1f, 0f, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f
        )
        assertArrayEquals(expected, matrix, delta)
    }

    @Test
    fun `rotateM should not change matrix when rotating around zero vector`() {
        val matrix = FloatArray(16)
        matrixMath.setIdentityM(matrix, 0)
        matrixMath.rotateM(matrix, 0, 90f, 0f, 0f, 0f)

        val expected = FloatArray(16)
        matrixMath.setIdentityM(expected, 0)
        assertArrayEquals(expected, matrix, delta)
    }

    @Test
    fun `multiplyMM should multiply matrices correctly`() {
        val lhs = floatArrayOf(
            1f, 2f, 3f, 4f,
            5f, 6f, 7f, 8f,
            9f, 10f, 11f, 12f,
            13f, 14f, 15f, 16f
        )
        val rhs = floatArrayOf(
            17f, 18f, 19f, 20f,
            21f, 22f, 23f, 24f,
            25f, 26f, 27f, 28f,
            29f, 30f, 31f, 32f
        )
        val result = FloatArray(16)
        matrixMath.multiplyMM(result, 0, lhs, 0, rhs, 0)

        val expected = floatArrayOf(
            538f, 612f, 686f, 760f,
            650f, 740f, 830f, 920f,
            762f, 868f, 974f, 1080f,
            874f, 996f, 1118f, 1240f
        )
        assertArrayEquals(expected, result, delta)
    }

    @Test
    fun `translateM should translate matrix correctly from non-identity`() {
        val matrix = floatArrayOf(
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            10f, 20f, 30f, 1f
        )
        matrixMath.translateM(matrix, 0, 1f, 2f, 3f)

        // Result[12] = 10 + 1*1 + 0*2 + 0*3 = 11
        // Result[13] = 20 + 0*1 + 1*2 + 0*3 = 22
        // Result[14] = 30 + 0*1 + 0*2 + 1*3 = 33
        assertEquals(11f, matrix[12], delta)
        assertEquals(22f, matrix[13], delta)
        assertEquals(33f, matrix[14], delta)
    }

    @Test
    fun `rotateM should rotate matrix correctly around Y axis`() {
        val matrix = FloatArray(16)
        matrixMath.setIdentityM(matrix, 0)
        val angle = 90f
        matrixMath.rotateM(matrix, 0, angle, 0f, 1f, 0f)

        // Rotation around Y by 90 degrees
        // [ cos(a)  0  sin(a) 0 ]
        // [ 0       1  0      0 ]
        // [ -sin(a) 0  cos(a) 0 ]
        // [ 0       0  0      1 ]
        // cos(90) = 0, sin(90) = 1
        val expected = floatArrayOf(
            0f, 0f, -1f, 0f,
            0f, 1f, 0f, 0f,
            1f, 0f, 0f, 0f,
            0f, 0f, 0f, 1f
        )
        assertArrayEquals(expected, matrix, delta)
    }

    @Test
    fun `multiplyMM with identity should not change matrix`() {
        val matrix = floatArrayOf(
            1f, 2f, 3f, 4f,
            5f, 6f, 7f, 8f,
            9f, 10f, 11f, 12f,
            13f, 14f, 15f, 16f
        )
        val identity = FloatArray(16)
        matrixMath.setIdentityM(identity, 0)
        
        val result = FloatArray(16)
        matrixMath.multiplyMM(result, 0, matrix, 0, identity, 0)
        assertArrayEquals(matrix, result, delta)

        matrixMath.multiplyMM(result, 0, identity, 0, matrix, 0)
        assertArrayEquals(matrix, result, delta)
    }

    @Test
    fun `multiplyMV should multiply matrix and vector correctly`() {
        val lhs = floatArrayOf(
            1f, 2f, 3f, 4f,
            5f, 6f, 7f, 8f,
            9f, 10f, 11f, 12f,
            13f, 14f, 15f, 16f
        )
        val rhs = floatArrayOf(1f, 2f, 3f, 4f)
        val result = FloatArray(4)
        matrixMath.multiplyMV(result, 0, lhs, 0, rhs, 0)

        // Result[0] = 1*1 + 5*2 + 9*3 + 13*4 = 1 + 10 + 27 + 52 = 90
        // Result[1] = 2*1 + 6*2 + 10*3 + 14*4 = 2 + 12 + 30 + 56 = 100
        // Result[2] = 3*1 + 7*2 + 11*3 + 15*4 = 3 + 14 + 33 + 60 = 110
        // Result[3] = 4*1 + 8*2 + 12*3 + 16*4 = 4 + 16 + 36 + 64 = 120
        val expected = floatArrayOf(90f, 100f, 110f, 120f)
        assertArrayEquals(expected, result, delta)
    }

    @Test
    fun `frustumM should create correct frustum matrix`() {
        val matrix = FloatArray(16)
        val left = -1f
        val right = 1f
        val bottom = -1f
        val top = 1f
        val near = 1f
        val far = 10f
        matrixMath.frustumM(matrix, 0, left, right, bottom, top, near, far)

        // rWidth = 1/2, rHeight = 1/2, rDepth = 1/-9
        // x = 2 * 1 * 1/2 = 1
        // y = 2 * 1 * 1/2 = 1
        // a = 0
        // b = 0
        // c = (10 + 1) / -9 = -11/9 = -1.222222
        // d = 2 * 10 * 1 / -9 = -20/9 = -2.222222
        
        assertEquals(1f, matrix[0], delta)
        assertEquals(1f, matrix[5], delta)
        assertEquals(0f, matrix[8], delta)
        assertEquals(0f, matrix[9], delta)
        assertEquals(-11f/9f, matrix[10], delta)
        assertEquals(-20f/9f, matrix[14], delta)
        assertEquals(-1f, matrix[11], delta)
        assertEquals(0f, matrix[15], delta)
    }

    @Test
    fun `invertM should invert matrix correctly`() {
        val matrix = floatArrayOf(
            1f, 0f, 0f, 0f,
            0f, 2f, 0f, 0f,
            0f, 0f, 4f, 0f,
            0f, 0f, 0f, 1f
        )
        val inverted = FloatArray(16)
        val success = matrixMath.invertM(inverted, 0, matrix, 0)

        assertTrue(success)
        val expected = floatArrayOf(
            1f, 0f, 0f, 0f,
            0f, 0.5f, 0f, 0f,
            0f, 0f, 0.25f, 0f,
            0f, 0f, 0f, 1f
        )
        assertArrayEquals(expected, inverted, delta)
    }

    @Test
    fun `invertM should return false for singular matrix`() {
        val matrix = FloatArray(16) // All zeros, determinant is 0
        val inverted = FloatArray(16)
        val success = matrixMath.invertM(inverted, 0, matrix, 0)
        assertFalse(success)
    }

    @Test
    fun `crossProduct should return correct vector`() {
        val v1 = Vector3(1f, 0f, 0f)
        val v2 = Vector3(0f, 1f, 0f)
        val result = matrixMath.crossProduct(v1, v2)
        
        assertEquals(0f, result.x, delta)
        assertEquals(0f, result.y, delta)
        assertEquals(1f, result.z, delta)
    }

    @Test
    fun `normalize should return unit vector`() {
        val v = Vector3(3f, 4f, 0f)
        val result = matrixMath.normalize(v)
        
        val length = sqrt(result.x * result.x + result.y * result.y + result.z * result.z)
        assertEquals(1f, length, delta)
        assertEquals(0.6f, result.x, delta)
        assertEquals(0.8f, result.y, delta)
        assertEquals(0f, result.z, delta)
    }

    @Test
    fun `normalize of zero vector should return zero vector`() {
        val v = Vector3.Zero
        val result = matrixMath.normalize(v)
        assertEquals(Vector3.Zero, result)
    }
}

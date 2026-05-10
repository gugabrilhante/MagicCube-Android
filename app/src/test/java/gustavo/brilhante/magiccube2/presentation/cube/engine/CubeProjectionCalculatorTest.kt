package gustavo.brilhante.magiccube2.presentation.cube.engine

import gustavo.brilhante.magiccube2.domain.math.MatrixMath
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CubeProjectionCalculatorTest {

    private lateinit var calculator: CubeProjectionCalculator

    @BeforeEach
    fun setUp() {
        calculator = CubeProjectionCalculator(MatrixMath())
    }

    @Test
    fun `onSurfaceChanged should calculate projection matrix`() {
        val initialMatrix = calculator.projectionMatrix.copyOf()
        
        calculator.onSurfaceChanged(1080, 1920)
        
        val newMatrix = calculator.projectionMatrix
        assertNotEquals(initialMatrix.toList(), newMatrix.toList())
        
        // Matrix should not be all zeros
        assertTrue(newMatrix.any { it != 0f })
    }

    @Test
    fun `onSurfaceChanged with zero dimensions should not update matrix`() {
        calculator.onSurfaceChanged(1080, 1920)
        val matrixBefore = calculator.projectionMatrix.copyOf()
        
        calculator.onSurfaceChanged(0, 1920)
        
        assertEqualsFloatArrays(matrixBefore, calculator.projectionMatrix)
    }

    private fun assertEqualsFloatArrays(expected: FloatArray, actual: FloatArray) {
        assertTrue(expected.contentEquals(actual), "Arrays are not equal")
    }
}

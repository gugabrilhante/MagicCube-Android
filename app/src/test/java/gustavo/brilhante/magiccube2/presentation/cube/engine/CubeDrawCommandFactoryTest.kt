package gustavo.brilhante.magiccube2.presentation.cube.engine

import gustavo.brilhante.magiccube2.domain.math.MatrixMath
import gustavo.brilhante.magiccube2.grafic.Cube
import gustavo.brilhante.magiccube2.grafic.IMatrixTracker
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class CubeDrawCommandFactoryTest {

    private val matrixMath = MatrixMath()
    private val factory = CubeDrawCommandFactory(matrixMath)

    @Test
    fun `createCommand should multiply matrices`() {
        val cube = mockk<Cube>()
        val projection = FloatArray(16) { index -> 1f }
        val tracker = mockk<IMatrixTracker>()
        val modelMatrix = FloatArray(16) { index -> 2f }
        
        every { tracker.getMatrix() } returns modelMatrix

        val command = factory.createCommand(cube, projection, tracker)
        
        assertEquals(cube, command.cube)
        // Check if matrix math was actually called by checking result
        // m1[0]*m2[0] + ... = 1*2 + 1*2 + 1*2 + 1*2 = 8
        assertEquals(8f, command.mvpMatrix[0], 0.001f)
    }
}

package gustavo.brilhante.magiccube2.presentation.cube.engine

import gustavo.brilhante.magiccube2.domain.CubeSettings
import gustavo.brilhante.magiccube2.domain.math.MatrixMath
import gustavo.brilhante.magiccube2.grafic.IMatrixTracker
import gustavo.brilhante.magiccube2.testutil.FakeCubeGameEngine
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CubeTraversalEngineTest {

    private val matrixTracker = mockk<IMatrixTracker>(relaxed = true)
    private val sliceResolver = CubeSliceResolver()
    private val matrixMath = MatrixMath()
    private val commandFactory = CubeDrawCommandFactory(matrixMath)
    private val engine = CubeTraversalEngine(matrixTracker, sliceResolver, commandFactory)
    private val fakeGameEngine = FakeCubeGameEngine()

    @Before
    fun setUp() {
        every { matrixTracker.getMatrix() } returns FloatArray(16)
    }

    @Test
    fun `buildFrame should generate 27 draw commands for 3x3x3 cube`() {
        val settings = CubeSettings(size = 3)
        val rotationState = CubeRotationState(angleX = 0f, angleY = 0f, isInertiaActive = false)
        val projectionMatrix = FloatArray(16)

        val commands = engine.buildFrame(fakeGameEngine, settings, rotationState, projectionMatrix)

        assertEquals(27, commands.size)
        verify { matrixTracker.reset() }
        verify { matrixTracker.rotate(0f, 0f, 1f, 0f) }
        verify { matrixTracker.rotate(0f, 1f, 0f, 0f) }
    }
}

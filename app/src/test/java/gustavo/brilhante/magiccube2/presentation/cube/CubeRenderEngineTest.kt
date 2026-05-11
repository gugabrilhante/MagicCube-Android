package gustavo.brilhante.magiccube2.presentation.cube

import gustavo.brilhante.magiccube2.domain.CubeSettings
import gustavo.brilhante.magiccube2.grafic.ICubeGameEngine
import gustavo.brilhante.magiccube2.presentation.cube.engine.CubeRotationState
import gustavo.brilhante.magiccube2.presentation.cube.engine.ICubeProjectionCalculator
import gustavo.brilhante.magiccube2.presentation.cube.engine.ICubeRotationEngine
import gustavo.brilhante.magiccube2.presentation.cube.engine.ICubeTraversalEngine
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CubeRenderEngineTest {

    private val rotationEngine = mockk<ICubeRotationEngine>(relaxed = true)
    private val projectionCalculator = mockk<ICubeProjectionCalculator>(relaxed = true)
    private val traversalEngine = mockk<ICubeTraversalEngine>(relaxed = true)
    private lateinit var renderEngine: CubeRenderEngine

    @BeforeEach
    fun setUp() {
        renderEngine = CubeRenderEngine(rotationEngine, projectionCalculator, traversalEngine)
    }

    @Test
    fun `updateCubeRotation should delegate to rotationEngine`() {
        renderEngine.updateCubeRotation(10f, 20f, 0.5f)
        verify { rotationEngine.updateRotation(10f, 20f, 0.5f) }
    }

    @Test
    fun `startInertia should delegate to rotationEngine`() {
        renderEngine.startInertia()
        verify { rotationEngine.startInertia() }
    }

    @Test
    fun `stopInertia should delegate to rotationEngine`() {
        renderEngine.stopInertia()
        verify { rotationEngine.stopInertia() }
    }

    @Test
    fun `onSurfaceChanged should delegate to projectionCalculator`() {
        renderEngine.onSurfaceChanged(1080, 1920)
        verify { projectionCalculator.onSurfaceChanged(1080, 1920) }
    }

    @Test
    fun `buildFrame should tick inertia and call traversalEngine`() {
        val gameEngine = mockk<ICubeGameEngine>()
        val settings = CubeSettings()
        val rotationState = CubeRotationState(angleX = 10f)
        val projMatrix = FloatArray(16)

        every { rotationEngine.getRotationState() } returns rotationState
        every { projectionCalculator.projectionMatrix } returns projMatrix
        every { traversalEngine.buildFrame(any(), any(), any(), any()) } returns emptyList()

        val result = renderEngine.buildFrame(gameEngine, settings)

        verify { rotationEngine.tickInertia() }
        verify { 
            traversalEngine.buildFrame(
                engine = gameEngine,
                settings = settings,
                rotationState = rotationState,
                projectionMatrix = projMatrix
            )
        }
        assertEquals(0, result.size)
    }

    @Test
    fun `properties should delegate to engines`() {
        every { rotationEngine.angleX } returns 15f
        every { rotationEngine.angleY } returns 25f
        every { rotationEngine.isInertiaActive } returns true
        val projMatrix = FloatArray(16) { 1f }
        every { projectionCalculator.projectionMatrix } returns projMatrix

        assertEquals(15f, renderEngine.angleRotateX)
        assertEquals(25f, renderEngine.angleRotateY)
        assertEquals(true, renderEngine.isInertiaActive)
        assertEquals(projMatrix, renderEngine.projectionMatrix)
    }
}

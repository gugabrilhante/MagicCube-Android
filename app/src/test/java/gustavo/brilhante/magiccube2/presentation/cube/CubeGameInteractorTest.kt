package gustavo.brilhante.magiccube2.presentation.cube

import gustavo.brilhante.magiccube2.domain.TimeProvider
import gustavo.brilhante.magiccube2.domain.cube.CubeInteractionProcessor
import gustavo.brilhante.magiccube2.domain.cube.CubeLogger
import gustavo.brilhante.magiccube2.domain.cube.MovementType
import gustavo.brilhante.magiccube2.grafic.ActiveSlice
import gustavo.brilhante.magiccube2.grafic.ICubeGameEngine
import gustavo.brilhante.magiccube2.grafic.PickingService
import gustavo.brilhante.magiccube2.grafic.RotationState
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CubeGameInteractorTest {

    private val engine = mockk<ICubeGameEngine>(relaxed = true)
    private val interaction = mockk<CubeInteractionProcessor>(relaxed = true)
    private val pickingService = mockk<PickingService>(relaxed = true)
    private val timeProvider = mockk<TimeProvider>()
    private val logger = mockk<CubeLogger>(relaxed = true)

    private lateinit var interactor: CubeGameInteractor

    @Before
    fun setUp() {
        interactor = CubeGameInteractor(
            engine,
            interaction,
            pickingService,
            timeProvider,
            logger
        )
        every { timeProvider.currentTimeMillis() } returns 0L
    }

    @Test
    fun `given onActionDown when drawCommands empty then returns SetDraggingSlice effect`() {
        // Given
        val x = 100f
        val y = 100f
        val width = 1080
        val height = 1920
        val commands = emptyList<CubeDrawCommand>()

        // When
        val effects = interactor.onActionDown(x, y, width, height, commands)

        // Then
        assertEquals(1, effects.size)
        assertTrue(effects[0] is CubeControllerEffect.SetDraggingSlice)
        assertEquals(false, (effects[0] as CubeControllerEffect.SetDraggingSlice).isDragging)
    }

    @Test
    fun `given no cubelet selected when onActionMove then returns RotateWholeCube effect`() {
        // Given
        every { timeProvider.currentTimeMillis() } returns 300L // Simulate DRAG
        every { interaction.classifyMovement(any(), any(), any()) } returns MovementType.DRAG
        
        interactor.onActionDown(100f, 100f, 1080, 1920, emptyList())

        // When
        val effects = interactor.onActionMove(110f, 110f, 100f, 100f, 0f, 0f, 1f)

        // Then
        assertTrue(effects.any { it is CubeControllerEffect.RotateWholeCube })
    }

    @Test
    fun `given movement is not drag when onActionMove then returns empty list`() {
        // Given
        every { timeProvider.currentTimeMillis() } returns 50L // Simulate fast movement
        every { interaction.classifyMovement(any(), any(), any()) } returns MovementType.NONE
        
        interactor.onActionDown(100f, 100f, 1080, 1920, emptyList())

        // When
        val effects = interactor.onActionMove(110f, 110f, 100f, 100f, 0f, 0f, 1f)

        // Then
        assertTrue(effects.isEmpty())
    }

    @Test
    fun `given active slice is not none when onActionUp then returns SnapSlice effect`() {
        // Given
        every { engine.rotation } returns RotationState(activeSlice = ActiveSlice.ROTATION_AXIS_X_0, isAnimating = false)
        every { engine.rotatedAngle } returns 45f
        
        // Setup initial state with a selected cubelet (mocking picking)
        val mockCubelet = mockk<gustavo.brilhante.magiccube2.grafic.Cube>(relaxed = true)
        every { pickingService.pickCubelet(any(), any(), any(), any(), any()) } returns PickingService.PickingResult(mockCubelet, Triple(1f, 0f, 0f))
        
        interactor.onActionDown(100f, 100f, 1080, 1920, listOf(mockk()))

        // When
        val effects = interactor.onActionUp(100f, 100f)

        // Then
        assertTrue(effects.any { it is CubeControllerEffect.SnapSlice })
        val snapEffect = effects.find { it is CubeControllerEffect.SnapSlice } as CubeControllerEffect.SnapSlice
        assertEquals(90f, snapEffect.snapAngle)
    }
}

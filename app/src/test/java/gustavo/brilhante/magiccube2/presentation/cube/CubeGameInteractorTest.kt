package gustavo.brilhante.magiccube2.presentation.cube

import gustavo.brilhante.magiccube2.domain.TimeProvider
import gustavo.brilhante.magiccube2.domain.cube.FaceInteractionCalculator
import gustavo.brilhante.magiccube2.domain.cube.GestureClassifier
import gustavo.brilhante.magiccube2.domain.cube.VisibleFacesResolver
import gustavo.brilhante.magiccube2.domain.cube.CubeLogger
import gustavo.brilhante.magiccube2.domain.cube.MovementType
import gustavo.brilhante.magiccube2.grafic.ICubeGameEngine
import gustavo.brilhante.magiccube2.grafic.PickingService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CubeGameInteractorTest {

    private val engine = mockk<ICubeGameEngine>(relaxed = true)
    private val gestureClassifier = mockk<GestureClassifier>(relaxed = true)
    private val faceInteractionCalculator = mockk<FaceInteractionCalculator>(relaxed = true)
    private val visibleFacesResolver = mockk<VisibleFacesResolver>(relaxed = true)
    private val pickingService = mockk<PickingService>(relaxed = true)
    private val timeProvider = mockk<TimeProvider>(relaxed = true)
    private val logger = mockk<CubeLogger>(relaxed = true)
    
    private lateinit var interactor: CubeGameInteractor

    @BeforeEach
    fun setUp() {
        interactor = CubeGameInteractor(
            engine,
            gestureClassifier,
            faceInteractionCalculator,
            visibleFacesResolver,
            pickingService,
            timeProvider,
            logger
        )
    }

    @Test
    fun `onActionDown should return SetDraggingSlice(false)`() {
        val effects = interactor.onActionDown(100f, 100f, 1080, 1920, emptyList())
        
        assertEquals(1, effects.size)
        assertEquals(CubeControllerEffect.SetDraggingSlice(false), effects[0])
    }

    @Test
    fun `onActionUp should return StartInertia and SetDraggingSlice(false) when no cubelet selected`() {
        every { pickingService.pickCubelet(any(), any(), any(), any(), any()) } returns null
        every { gestureClassifier.classifyMovement(any(), any(), any()) } returns MovementType.NONE

        interactor.onActionDown(100f, 100f, 1080, 1920, emptyList())
        val effects = interactor.onActionUp(110f, 110f)

        assertEquals(listOf(CubeControllerEffect.StartInertia, CubeControllerEffect.SetDraggingSlice(false)), effects)
    }
}

package gustavo.brilhante.magiccube2.presentation.cube

import gustavo.brilhante.magiccube2.domain.cube.CoordinateTransformer
import gustavo.brilhante.magiccube2.domain.cube.FaceInteractionCalculator
import gustavo.brilhante.magiccube2.domain.cube.GestureClassifier
import gustavo.brilhante.magiccube2.domain.cube.MovementType
import gustavo.brilhante.magiccube2.domain.cube.VisibleFacesResolver
import gustavo.brilhante.magiccube2.domain.usecase.ObserveSettingsUseCase
import gustavo.brilhante.magiccube2.grafic.PickingService
import gustavo.brilhante.magiccube2.grafic.RotationState
import gustavo.brilhante.magiccube2.testutil.FakeCubeGameEngine
import gustavo.brilhante.magiccube2.testutil.FakeSettingsRepository
import gustavo.brilhante.magiccube2.testutil.MainDispatcherRule
import gustavo.brilhante.magiccube2.testutil.NoOpCubeLogger
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CubeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeEngine: FakeCubeGameEngine
    private lateinit var fakeRepository: FakeSettingsRepository
    private var fakeTime: Long = 0L
    private lateinit var viewModel: CubeViewModel

    @Before
    fun setUp() {
        fakeEngine = FakeCubeGameEngine()
        fakeRepository = FakeSettingsRepository()
        fakeTime = 0L

        val gestureClassifier = GestureClassifier()
        val coordinateTransformer = CoordinateTransformer()
        val faceInteractionCalculator = FaceInteractionCalculator(coordinateTransformer)
        val visibleFacesResolver = VisibleFacesResolver()

        viewModel = CubeViewModel(
            observeSettings = ObserveSettingsUseCase(fakeRepository),
            engineFactory = { _ -> fakeEngine },
            controllerFactory = { engine ->
                CubeGameInteractor(
                    engine,
                    gestureClassifier,
                    faceInteractionCalculator,
                    visibleFacesResolver,
                    PickingService(),
                    { fakeTime },
                    NoOpCubeLogger()
                )
            },
        )
    }

    // --- Engine factory ---

    @Test
    fun `engine is the instance returned by the factory`() {
        assertTrue(viewModel.engine === fakeEngine)
    }

    // --- Movement type classification ---

    @Test
    fun `fast large horizontal right movement is SWIPE_RIGHT`() {
        val type = viewModel.getMovementType(dt = 100L, dx = 150f, dy = 20f)
        assertEquals(MovementType.SWIPE_RIGHT, type)
    }

    @Test
    fun `fast large horizontal left movement is SWIPE_LEFT`() {
        val type = viewModel.getMovementType(dt = 100L, dx = -150f, dy = 20f)
        assertEquals(MovementType.SWIPE_LEFT, type)
    }

    @Test
    fun `fast large vertical up movement is SWIPE_UP`() {
        val type = viewModel.getMovementType(dt = 100L, dx = 10f, dy = -150f)
        assertEquals(MovementType.SWIPE_UP, type)
    }

    @Test
    fun `fast large vertical down movement is SWIPE_DOWN`() {
        val type = viewModel.getMovementType(dt = 100L, dx = 10f, dy = 150f)
        assertEquals(MovementType.SWIPE_DOWN, type)
    }

    @Test
    fun `fast but small movement is NONE`() {
        val type = viewModel.getMovementType(dt = 100L, dx = 30f, dy = 30f)
        assertEquals(MovementType.NONE, type)
    }

    @Test
    fun `slow movement regardless of distance is DRAG`() {
        val type = viewModel.getMovementType(dt = 300L, dx = 300f, dy = 0f)
        assertEquals(MovementType.DRAG, type)
    }

    // --- Touch: swipe triggers rotation ---

    @Test
    fun `swipe up triggers rotation when engine is idle`() {
        fakeEngine.rotation = RotationState(isAnimating = false)

        fakeTime = 0L
        viewModel.onActionDown(x = 250f, y = 400f, screenWidth = 500, screenHeight = 800)
        fakeTime = 100L  // within 250 ms threshold → swipe
        viewModel.onActionUp(x = 250f, y = 250f)  // dy = -150 → SWIPE_UP

        assertEquals(1, fakeEngine.rotateClosestSideCallCount)
    }

    @Test
    fun `swipe down triggers rotation when engine is idle`() {
        fakeEngine.rotation = RotationState(isAnimating = false)

        fakeTime = 0L
        viewModel.onActionDown(x = 250f, y = 400f, screenWidth = 500, screenHeight = 800)
        fakeTime = 100L
        viewModel.onActionUp(x = 250f, y = 600f)  // dy = 200 → SWIPE_DOWN

        assertEquals(1, fakeEngine.rotateClosestSideCallCount)
    }

    @Test
    fun `swipe does NOT trigger rotation when engine is already animating`() {
        fakeEngine.rotation = RotationState(isAnimating = true)

        fakeTime = 0L
        viewModel.onActionDown(x = 250f, y = 400f, screenWidth = 500, screenHeight = 800)
        fakeTime = 100L
        viewModel.onActionUp(x = 250f, y = 250f)

        assertEquals(0, fakeEngine.rotateClosestSideCallCount)
    }

    @Test
    fun `slow drag does NOT trigger rotation`() {
        fakeEngine.rotation = RotationState(isAnimating = false)

        fakeTime = 0L
        viewModel.onActionDown(x = 0f, y = 0f, screenWidth = 500, screenHeight = 800)
        fakeTime = 400L  // > 250 ms → DRAG
        viewModel.onActionUp(x = 300f, y = 0f)

        assertEquals(0, fakeEngine.rotateClosestSideCallCount)
    }

    // --- Touch: drag updates rotation angles ---

    @Test
    fun `drag moves angleRotateX when horizontal`() {
        fakeTime = 0L
        viewModel.onActionDown(x = 100f, y = 100f, screenWidth = 500, screenHeight = 800)
        fakeTime = 400L  // DRAG threshold exceeded
        viewModel.onActionMove(x = 200f, y = 100f, previousX = 100f, previousY = 100f)

        assertTrue(viewModel.angleRotateX != 0f)
    }

    @Test
    fun `drag moves angleRotateY when vertical`() {
        fakeTime = 0L
        viewModel.onActionDown(x = 100f, y = 100f, screenWidth = 500, screenHeight = 800)
        fakeTime = 400L
        viewModel.onActionMove(x = 100f, y = 200f, previousX = 100f, previousY = 100f)

        assertTrue(viewModel.angleRotateY != 0f)
    }

    // --- advanceFrame delegates to engine ---

    @Test
    fun `advanceFrame calls engine postFrameAdvance`() {
        viewModel.advanceFrame()
        assertEquals(1, fakeEngine.postFrameAdvanceCallCount)
    }

    // --- Inertia ---

    @Test
    fun `onActionUp activates inertia`() {
        fakeTime = 0L
        viewModel.onActionDown(x = 100f, y = 100f, screenWidth = 500, screenHeight = 800)
        fakeTime = 100L
        viewModel.onActionUp(x = 100f, y = 100f)

        assertTrue(viewModel.isInertiaActive)
    }

    // --- onSurfaceChanged ---

    @Test
    fun `onSurfaceChanged does not throw`() {
        viewModel.onSurfaceChanged(1080, 1920)
    }
}

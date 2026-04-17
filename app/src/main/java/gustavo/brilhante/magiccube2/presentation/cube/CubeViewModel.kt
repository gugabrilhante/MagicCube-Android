package gustavo.brilhante.magiccube2.presentation.cube

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gustavo.brilhante.magiccube2.domain.CubeSettings
import gustavo.brilhante.magiccube2.domain.cube.MovementType
import gustavo.brilhante.magiccube2.domain.usecase.ObserveSettingsUseCase
import gustavo.brilhante.magiccube2.grafic.CubeGameEngineFactory
import gustavo.brilhante.magiccube2.grafic.ICubeGameEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * Thin orchestrator: owns [CubeUiState], wires the GL thread to [CubeRenderEngine], and
 * translates [CubeIntent]s into [CubeControllerEffect]s via [ICubeInteractor].
 *
 * No business logic lives here. Touch math → [CubeGameInteractor].
 * Rendering state → [CubeRenderEngine]. Interaction math → domain processors.
 */
class CubeViewModel(
    observeSettings: ObserveSettingsUseCase,
    engineFactory: CubeGameEngineFactory,
    controllerFactory: CubeControllerFactory,
) : ViewModel() {

    private val settingsState: StateFlow<CubeSettings> = observeSettings()
        .stateIn(viewModelScope, SharingStarted.Eagerly, CubeSettings())

    val settings: CubeSettings get() = settingsState.value

    val engine: ICubeGameEngine = engineFactory.create(settings.shuffle)

    private val renderEngine = CubeRenderEngine()
    private val interactor: ICubeInteractor = controllerFactory.create(engine)

    private val _uiState = MutableStateFlow(CubeUiState())
    val uiState: StateFlow<CubeUiState> = _uiState.asStateFlow()

    private val touchScaleFactor: Float
        get() = settings.speed * ((180f / 320) / 5)

    // --- GL-thread entry points (called by CubeRenderer) ---

    fun onSurfaceChanged(width: Int, height: Int) =
        renderEngine.onSurfaceChanged(width, height)

    fun buildFrame() {
        val commands = renderEngine.buildFrame(engine, settings)
        _uiState.value = _uiState.value.copy(drawCommands = commands)
    }

    fun advanceFrame() = engine.postFrameAdvance()

    // --- Touch entry points (called by CubeSurfaceView, UI thread) ---

    fun onActionDown(x: Float, y: Float, screenWidth: Int, screenHeight: Int) =
        dispatch(CubeIntent.ActionDown(x, y, screenWidth, screenHeight))

    fun onActionMove(x: Float, y: Float, previousX: Float, previousY: Float) =
        dispatch(CubeIntent.ActionMove(x, y, previousX, previousY))

    fun onActionUp(x: Float, y: Float) =
        dispatch(CubeIntent.ActionUp(x, y))

    // --- MVI dispatch ---

    fun dispatch(intent: CubeIntent) {
        val effects = when (intent) {
            is CubeIntent.ActionDown -> interactor.onActionDown(
                intent.x, intent.y, intent.screenWidth, intent.screenHeight,
                _uiState.value.drawCommands,
            )
            is CubeIntent.ActionMove -> interactor.onActionMove(
                intent.x, intent.y, intent.previousX, intent.previousY,
                renderEngine.angleRotateX, renderEngine.angleRotateY,
                touchScaleFactor,
            )
            is CubeIntent.ActionUp -> interactor.onActionUp(intent.x, intent.y)
        }
        effects.forEach(::applyEffect)
    }

    // Kept public for backward compatibility with existing tests.
    fun getMovementType(dt: Long, dx: Float, dy: Float): MovementType =
        interactor.classifyMovement(dt, dx, dy)

    val angleRotateX: Float get() = renderEngine.angleRotateX
    val angleRotateY: Float get() = renderEngine.angleRotateY
    val isInertiaActive: Boolean get() = renderEngine.isInertiaActive

    // --- Effect handler ---

    private fun applyEffect(effect: CubeControllerEffect) {
        when (effect) {
            is CubeControllerEffect.RotateWholeCube ->
                renderEngine.updateCubeRotation(effect.screenDx, effect.screenDy, effect.scale)

            is CubeControllerEffect.UpdateSliceAngle ->
                engine.rotatedAngle = effect.angle

            is CubeControllerEffect.SnapSlice ->
                engine.rotation = engine.rotation.copy(
                    isAnimating = true,
                    isDragSnap  = true,
                    snapTarget  = effect.snapAngle,
                )

            CubeControllerEffect.StartInertia ->
                renderEngine.startInertia()

            is CubeControllerEffect.TriggerSwipeRotation ->
                triggerRotation(effect.sense)

            is CubeControllerEffect.SetDraggingSlice ->
                _uiState.value = _uiState.value.copy(isDraggingSlice = effect.isDragging)
        }
    }

    private fun triggerRotation(rotationSense: Int) {
        if (!engine.rotation.isAnimating) {
            engine.rotation = engine.rotation.copy(isAnimating = true)
            engine.rotateClosestSideToScreen(rotationSense)
        }
    }
}

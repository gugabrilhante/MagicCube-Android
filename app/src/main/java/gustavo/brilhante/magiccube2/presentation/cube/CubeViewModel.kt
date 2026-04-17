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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Thin orchestrator: owns [CubeUiState], wires the GL thread to [CubeRenderEngine], and
 * translates [CubeIntent]s into [CubeControllerEffect]s via [ICubeInteractor].
 *
 * No business logic lives here. Touch math → [CubeGameInteractor].
 * Rendering state → [CubeRenderEngine]. Interaction math → domain processors.
 */
class CubeViewModel(
    observeSettings: ObserveSettingsUseCase,
    private val engineFactory: CubeGameEngineFactory,
    private val controllerFactory: CubeControllerFactory,
) : ViewModel() {

    private val _settingsState = MutableStateFlow<CubeSettings?>(null)
    val settingsState: StateFlow<CubeSettings?> = _settingsState.asStateFlow()

    private val _uiState = MutableStateFlow(CubeUiState())
    val uiState: StateFlow<CubeUiState> = _uiState.asStateFlow()

    private var _engine: ICubeGameEngine? = null
    val engine: ICubeGameEngine
        get() = _engine ?: throw IllegalStateException("Engine not initialized. Wait for settingsState to be non-null.")

    private val renderEngine = CubeRenderEngine()
    private var _interactor: ICubeInteractor? = null
    private val interactor: ICubeInteractor
        get() = _interactor ?: throw IllegalStateException("Interactor not initialized. Wait for settingsState to be non-null.")

    private val touchScaleFactor: Float
        get() = (settingsState.value?.speed ?: 5) * ((180f / 320) / 5)

    init {
        viewModelScope.launch {
            observeSettings().collectLatest { settings ->
                if (_engine == null) {
                    _engine = engineFactory.create(settings.shuffle)
                    _interactor = controllerFactory.create(engine)
                }
                _settingsState.value = settings
            }
        }
    }

    // --- GL-thread entry points (called by CubeRenderer) ---

    fun onSurfaceChanged(width: Int, height: Int) =
        renderEngine.onSurfaceChanged(width, height)

    fun buildFrame() {
        val currentSettings = settingsState.value ?: return
        val commands = renderEngine.buildFrame(engine, currentSettings)
        _uiState.value = _uiState.value.copy(drawCommands = commands)
    }

    fun advanceFrame() {
        if (_engine != null) {
            engine.postFrameAdvance()
        }
    }

    // --- Touch entry points (called by CubeSurfaceView, UI thread) ---

    fun onActionDown(x: Float, y: Float, screenWidth: Int, screenHeight: Int) {
        if (_settingsState.value != null) {
            dispatch(CubeIntent.ActionDown(x, y, screenWidth, screenHeight))
        }
    }

    fun onActionMove(x: Float, y: Float, previousX: Float, previousY: Float) {
        if (_settingsState.value != null) {
            dispatch(CubeIntent.ActionMove(x, y, previousX, previousY))
        }
    }

    fun onActionUp(x: Float, y: Float) {
        if (_settingsState.value != null) {
            dispatch(CubeIntent.ActionUp(x, y))
        }
    }

    fun onActionCancel() {
        if (_settingsState.value != null) {
            dispatch(CubeIntent.ActionCancel)
        }
    }

    // --- MVI dispatch ---

    fun dispatch(intent: CubeIntent) {
        val currentInteractor = _interactor ?: return
        val effects = when (intent) {
            is CubeIntent.ActionDown -> currentInteractor.onActionDown(
                intent.x, intent.y, intent.screenWidth, intent.screenHeight,
                _uiState.value.drawCommands,
            )
            is CubeIntent.ActionMove -> currentInteractor.onActionMove(
                intent.x, intent.y, intent.previousX, intent.previousY,
                renderEngine.angleRotateX, renderEngine.angleRotateY,
                touchScaleFactor,
            )
            is CubeIntent.ActionUp -> currentInteractor.onActionUp(intent.x, intent.y)
            CubeIntent.ActionCancel -> currentInteractor.onActionCancel()
        }
        effects.forEach(::applyEffect)
    }

    // Kept public for backward compatibility with existing tests.
    fun getMovementType(dt: Long, dx: Float, dy: Float): MovementType {
        val currentInteractor = _interactor ?: throw IllegalStateException("Interactor not initialized")
        return currentInteractor.classifyMovement(dt, dx, dy)
    }

    val angleRotateX: Float get() = renderEngine.angleRotateX
    val angleRotateY: Float get() = renderEngine.angleRotateY
    val isInertiaActive: Boolean get() = renderEngine.isInertiaActive

    // --- Effect handler ---

    private fun applyEffect(effect: CubeControllerEffect) {
        val currentEngine = _engine ?: return
        when (effect) {
            is CubeControllerEffect.RotateWholeCube ->
                renderEngine.updateCubeRotation(effect.screenDx, effect.screenDy, effect.scale)

            is CubeControllerEffect.UpdateSliceAngle ->
                currentEngine.rotatedAngle = effect.angle

            is CubeControllerEffect.SnapSlice ->
                currentEngine.rotation = currentEngine.rotation.copy(
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
        val currentEngine = _engine ?: return
        if (!currentEngine.rotation.isAnimating) {
            currentEngine.rotation = currentEngine.rotation.copy(isAnimating = true)
            currentEngine.rotateClosestSideToScreen(rotationSense)
        }
    }
}

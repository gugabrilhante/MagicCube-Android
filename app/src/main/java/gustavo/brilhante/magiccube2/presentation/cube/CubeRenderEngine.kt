package gustavo.brilhante.magiccube2.presentation.cube

import gustavo.brilhante.magiccube2.domain.CubeSettings
import gustavo.brilhante.magiccube2.grafic.ICubeGameEngine
import gustavo.brilhante.magiccube2.presentation.cube.engine.CubeProjectionCalculator
import gustavo.brilhante.magiccube2.presentation.cube.engine.CubeRotationEngine
import gustavo.brilhante.magiccube2.presentation.cube.engine.CubeTraversalEngine
import gustavo.brilhante.magiccube2.presentation.cube.engine.ICubeProjectionCalculator
import gustavo.brilhante.magiccube2.presentation.cube.engine.ICubeRotationEngine
import gustavo.brilhante.magiccube2.presentation.cube.engine.ICubeTraversalEngine

/**
 * Owns all GL-thread mutable state: projection matrix, model matrix, inertia, and the
 * global cube-rotation angles that bridge the UI and GL threads.
 *
 * All methods except [updateCubeRotation] and [startInertia] must be called exclusively
 * from the GL thread. [updateCubeRotation] and [startInertia] are called from the UI
 * thread; cross-thread visibility and atomicity are guaranteed by internal engines.
 */
class CubeRenderEngine(
    private val rotationEngine: ICubeRotationEngine = CubeRotationEngine(),
    private val projectionCalculator: ICubeProjectionCalculator = CubeProjectionCalculator(),
    private val traversalEngine: ICubeTraversalEngine = CubeTraversalEngine()
) {

    val projectionMatrix: FloatArray get() = projectionCalculator.projectionMatrix
    val angleRotateX: Float get() = rotationEngine.angleX
    val angleRotateY: Float get() = rotationEngine.angleY
    val isInertiaActive: Boolean get() = rotationEngine.isInertiaActive

    // --- UI-thread methods ---

    /**
     * Updates the whole-cube rotation angles and saves the previous angles as the
     * inertia reference. Call from the UI thread on every ACTION_MOVE event where no
     * cubelet is selected.
     */
    fun updateCubeRotation(screenDx: Float, screenDy: Float, scale: Float) {
        rotationEngine.updateRotation(screenDx, screenDy, scale)
    }

    /**
     * Activates inertia using the rotation delta captured by the last
     * [updateCubeRotation] call. Call from the UI thread on ACTION_UP.
     */
    fun startInertia() {
        rotationEngine.startInertia()
    }

    /**
     * Resets the rotation state. Call from the UI thread when needed (e.g. onActionDown).
     */
    fun stopInertia() {
        rotationEngine.stopInertia()
    }

    // --- GL-thread methods ---

    fun onSurfaceChanged(width: Int, height: Int) {
        projectionCalculator.onSurfaceChanged(width, height)
    }

    /**
     * Builds the full set of [CubeDrawCommand]s for the current frame.
     */
    fun buildFrame(engine: ICubeGameEngine, settings: CubeSettings): List<CubeDrawCommand> {
        rotationEngine.tickInertia()
        return traversalEngine.buildFrame(
            engine = engine,
            settings = settings,
            rotationState = rotationEngine.getRotationState(),
            projectionMatrix = projectionCalculator.projectionMatrix
        )
    }
}

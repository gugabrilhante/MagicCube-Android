package gustavo.brilhante.magiccube2.presentation.cube

import gustavo.brilhante.magiccube2.domain.CubeSettings
import gustavo.brilhante.magiccube2.grafic.ActiveSlice
import gustavo.brilhante.magiccube2.grafic.CubeAxis
import gustavo.brilhante.magiccube2.grafic.CubeStepDirection
import gustavo.brilhante.magiccube2.grafic.ICubeGameEngine
import gustavo.brilhante.magiccube2.grafic.MatrixMath
import gustavo.brilhante.magiccube2.grafic.MatrixTracker
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.math.tan

/**
 * Owns all GL-thread mutable state: projection matrix, model matrix, inertia, and the
 * global cube-rotation angles that bridge the UI and GL threads.
 *
 * All methods except [updateCubeRotation] and [startInertia] must be called exclusively
 * from the GL thread. [updateCubeRotation] and [startInertia] are called from the UI
 * thread; cross-thread visibility and atomicity are guaranteed by [lock].
 */
class CubeRenderEngine {

    private val matrixTracker = MatrixTracker()
    private val renderPosition = RenderPosition()
    val projectionMatrix = FloatArray(16)

    private val dist = 2.12f

    private val lock = ReentrantLock()

    // Protected by [lock] for cross-thread Read-Modify-Write safety.
    private var _angleRotateX: Float = 0f
    val angleRotateX: Float get() = lock.withLock { _angleRotateX }

    private var _angleRotateY: Float = 0f
    val angleRotateY: Float get() = lock.withLock { _angleRotateY }

    private var _isInertiaActive: Boolean = false
    val isInertiaActive: Boolean get() = lock.withLock { _isInertiaActive }

    private var angleRotateXAux: Float = 0f
    private var angleRotateYAux: Float = 0f
    private var inertiaInc: Float = 5f

    // --- UI-thread methods ---

    /**
     * Updates the whole-cube rotation angles and saves the previous angles as the
     * inertia reference. Call from the UI thread on every ACTION_MOVE event where no
     * cubelet is selected.
     */
    fun updateCubeRotation(screenDx: Float, screenDy: Float, scale: Float) {
        lock.withLock {
            _isInertiaActive = false // Disable inertia immediately on user interaction.
            angleRotateXAux = _angleRotateX
            angleRotateYAux = _angleRotateY
            _angleRotateX += screenDx * scale
            _angleRotateY += screenDy * scale
        }
    }

    /**
     * Activates inertia using the rotation delta captured by the last
     * [updateCubeRotation] call. Call from the UI thread on ACTION_UP.
     */
    fun startInertia() {
        lock.withLock {
            _isInertiaActive = true
            inertiaInc = 1f
        }
    }

    /**
     * Resets the rotation state. Call from the UI thread when needed (e.g. onActionDown).
     */
    fun stopInertia() {
        lock.withLock {
            _isInertiaActive = false
        }
    }

    // --- GL-thread methods ---

    fun onSurfaceChanged(width: Int, height: Int) {
        if (width == 0 || height == 0) return

        val zNear = 0.1f
        val zFar = 1000f
        val fov = 80.0f / 57.3f
        val size = zNear * tan(fov / 2.0).toFloat()
        val aspectRatio = width.toFloat() / height
        MatrixMath.frustumM(
            projectionMatrix, 0,
            -size, size,
            -size / aspectRatio, size / aspectRatio,
            zNear, zFar,
        )
    }

    /**
     * Builds the full set of [CubeDrawCommand]s for the current frame.
     *
     * Resets the matrix state, applies global rotation and inertia, then traverses the
     * 3×3×3 cube grid — injecting the active-slice rotation around the appropriate axis
     * before and undoing it after each affected group of cubelets.
     */
    fun buildFrame(engine: ICubeGameEngine, settings: CubeSettings): List<CubeDrawCommand> {
        matrixTracker.reset()
        renderPosition.reset()

        matrixTracker.translate(0f, 0f, (-20 + settings.size).toFloat())

        lock.withLock {
            tickInertia()
            matrixTracker.rotate(_angleRotateX, 0f, 1f, 0f)
            matrixTracker.rotate(_angleRotateY, 1f, 0f, 0f)
        }

        engine.prepareFrameRotation()

        val rotState = engine.rotation
        val commands = mutableListOf<CubeDrawCommand>()

        var sinal = 1
        var indexAxisZ = 0

        while (indexAxisZ < 3) {
            if (rotState.activeSlice == ActiveSlice.ROTATION_AXIS_Z_0 && indexAxisZ == 0) rotateAround(engine.rotatedAngle, CubeAxis.Y)
            if (rotState.activeSlice == ActiveSlice.ROTATION_AXIS_Z_1 && indexAxisZ == 1) rotateAround(engine.rotatedAngle, CubeAxis.Y)
            if (rotState.activeSlice == ActiveSlice.ROTATION_AXIS_Z_2 && indexAxisZ == 2) rotateAround(engine.rotatedAngle, CubeAxis.Y)

            val direction = if (sinal > 0) CubeStepDirection.UP else CubeStepDirection.DOWN
            moveOnAxis(1, CubeAxis.Y, direction)
            if (indexAxisZ > 0) {
                moveOnAxis(1, CubeAxis.X, CubeStepDirection.LEFT)
                moveOnAxis(1, CubeAxis.Z, CubeStepDirection.BACK)
            }
            moveOnAxis(2, CubeAxis.Z, CubeStepDirection.BACK)
            moveOnAxis(1, CubeAxis.X, CubeStepDirection.RIGHT)

            var indexAxisY = 0
            while (indexAxisY < 3) {
                if (rotState.activeSlice == ActiveSlice.ROTATION_AXIS_Y_0 && indexAxisY == 0) rotateAround(engine.rotatedAngle, CubeAxis.Z)
                if (rotState.activeSlice == ActiveSlice.ROTATION_AXIS_Y_1 && indexAxisY == 1) rotateAround(engine.rotatedAngle, CubeAxis.Z)
                if (rotState.activeSlice == ActiveSlice.ROTATION_AXIS_Y_2 && indexAxisY == 2) rotateAround(engine.rotatedAngle, CubeAxis.Z)

                moveOnAxis(1, CubeAxis.Z, CubeStepDirection.FORWARD)
                moveOnAxis(3, CubeAxis.X, CubeStepDirection.LEFT)

                var indexAxisX = 0
                while (indexAxisX < 3) {
                    if (rotState.activeSlice == ActiveSlice.ROTATION_AXIS_X_0 && indexAxisX == 0) rotateAround(engine.rotatedAngle, CubeAxis.X)
                    if (rotState.activeSlice == ActiveSlice.ROTATION_AXIS_X_1 && indexAxisX == 1) rotateAround(engine.rotatedAngle, CubeAxis.X)
                    if (rotState.activeSlice == ActiveSlice.ROTATION_AXIS_X_2 && indexAxisX == 2) rotateAround(engine.rotatedAngle, CubeAxis.X)

                    moveOnAxis(1, CubeAxis.X, CubeStepDirection.RIGHT)

                    val cubeIndex = engine.cubeGrid[indexAxisX][indexAxisZ][indexAxisY]
                    commands.add(CubeDrawCommand(engine.cubes[cubeIndex], computeMVP()))

                    if (isInertiaActive) {
                        engine.faceCenterCubes.forEachIndexed { idx, entry ->
                            if (cubeIndex == entry.first) {
                                engine.faceCenterPositions[idx].z = -matrixTracker.getZ()
                                engine.faceCenterPositions[idx].y = matrixTracker.getY()
                                engine.faceCenterPositions[idx].x = matrixTracker.getX()
                            }
                        }
                    }

                    if (rotState.activeSlice == ActiveSlice.ROTATION_AXIS_X_2 && indexAxisX == 2) rotateAround(-engine.rotatedAngle, CubeAxis.X)
                    if (rotState.activeSlice == ActiveSlice.ROTATION_AXIS_X_1 && indexAxisX == 1) rotateAround(-engine.rotatedAngle, CubeAxis.X)
                    if (rotState.activeSlice == ActiveSlice.ROTATION_AXIS_X_0 && indexAxisX == 0) rotateAround(-engine.rotatedAngle, CubeAxis.X)

                    indexAxisX++
                }

                if (rotState.activeSlice == ActiveSlice.ROTATION_AXIS_Y_2 && indexAxisY == 2) rotateAround(-engine.rotatedAngle, CubeAxis.Z)
                if (rotState.activeSlice == ActiveSlice.ROTATION_AXIS_Y_1 && indexAxisY == 1) rotateAround(-engine.rotatedAngle, CubeAxis.Z)
                if (rotState.activeSlice == ActiveSlice.ROTATION_AXIS_Y_0 && indexAxisY == 0) rotateAround(-engine.rotatedAngle, CubeAxis.Z)

                indexAxisY++
            }

            if (indexAxisZ == 0) sinal = -sinal

            if (rotState.activeSlice == ActiveSlice.ROTATION_AXIS_Z_2 && indexAxisZ == 2) rotateAround(-engine.rotatedAngle, CubeAxis.Y)
            if (rotState.activeSlice == ActiveSlice.ROTATION_AXIS_Z_1 && indexAxisZ == 1) rotateAround(-engine.rotatedAngle, CubeAxis.Y)
            if (rotState.activeSlice == ActiveSlice.ROTATION_AXIS_Z_0 && indexAxisZ == 0) rotateAround(-engine.rotatedAngle, CubeAxis.Y)

            indexAxisZ++
        }

        return commands
    }

    // Must be called inside a lock
    private fun tickInertia() {
        if (!_isInertiaActive) return
        if (_angleRotateX - angleRotateXAux < -2) _angleRotateX -= inertiaInc
        if (_angleRotateX - angleRotateXAux > 2) _angleRotateX += inertiaInc
        if (_angleRotateY - angleRotateYAux < -2) _angleRotateY -= inertiaInc
        if (_angleRotateY - angleRotateYAux > 2) _angleRotateY += inertiaInc
        inertiaInc -= 0.1f
        if (inertiaInc < 0.5f) {
            _isInertiaActive = false
            inertiaInc = 5f
        }
    }

    private fun computeMVP(): FloatArray {
        val mvp = FloatArray(16)
        MatrixMath.multiplyMM(mvp, 0, projectionMatrix, 0, matrixTracker.getMatrix(), 0)
        return mvp
    }

    private fun moveOnAxis(steps: Int, axis: CubeAxis, direction: CubeStepDirection) {
        val d = steps * dist * direction.orientation
        when (axis) {
            CubeAxis.X -> translate(d, 0f, 0f)
            CubeAxis.Y -> translate(0f, d, 0f)
            CubeAxis.Z -> translate(0f, 0f, d)
        }
    }

    private fun translate(x: Float, y: Float, z: Float) {
        matrixTracker.translate(x, y, z)
        renderPosition.x += x
        renderPosition.y += y
        renderPosition.z += z
    }

    private fun rotateAround(angle: Float, axis: CubeAxis) {
        matrixTracker.translate(-renderPosition.x, -renderPosition.y, -renderPosition.z)
        when (axis) {
            CubeAxis.X -> matrixTracker.rotate(angle, 1f, 0f, 0f)
            CubeAxis.Y -> matrixTracker.rotate(angle, 0f, 1f, 0f)
            CubeAxis.Z -> matrixTracker.rotate(angle, 0f, 0f, 1f)
        }
        matrixTracker.translate(renderPosition.x, renderPosition.y, renderPosition.z)
    }
}

private data class RenderPosition(var x: Float = 0f, var y: Float = 0f, var z: Float = 0f) {
    fun reset() { x = 0f; y = 0f; z = 0f }
}

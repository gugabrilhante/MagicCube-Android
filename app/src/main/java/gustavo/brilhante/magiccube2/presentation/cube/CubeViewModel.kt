package gustavo.brilhante.magiccube2.presentation.cube

import android.opengl.Matrix
import androidx.lifecycle.ViewModel
import gustavo.brilhante.magiccube2.data.SettingsRepository
import gustavo.brilhante.magiccube2.domain.CubeSettings
import gustavo.brilhante.magiccube2.grafic.ActiveSlice
import gustavo.brilhante.magiccube2.grafic.CubeAxis
import gustavo.brilhante.magiccube2.grafic.CubeGameEngine
import gustavo.brilhante.magiccube2.grafic.CubeStepDirection
import gustavo.brilhante.magiccube2.grafic.MatrixTracker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs

class CubeViewModel(private val repository: SettingsRepository) : ViewModel() {

    val settings: CubeSettings get() = repository.current
    val engine = CubeGameEngine(settings.shuffle)

    // --- Render state exposed to CubeRenderer ---
    private val _renderState = MutableStateFlow(CubeRenderState())
    val renderState: StateFlow<CubeRenderState> = _renderState.asStateFlow()

    // --- Matrix state (owned by ViewModel, used on GL thread) ---
    private val matrixTracker = MatrixTracker()
    private val renderPosition = RenderPosition()
    private val projectionMatrix = FloatArray(16)
    private val dist = 2.12f

    // --- Touch rotation (written by UI thread, read by GL thread) ---
    @Volatile var angleRotateX: Float = 0f
    @Volatile var angleRotateY: Float = 0f
    @Volatile var isInertiaActive: Boolean = false

    private var angleRotateXAux: Float = 0f
    private var angleRotateYAux: Float = 0f
    private var inertiaInc: Float = 5f

    private var startX: Float = 0f
    private var startY: Float = 0f
    private var startTime: Long = 0L
    private var horizontalOrientation: Int = 1
    private var verticalOrientation: Int = 1

    private val touchScaleFactor: Float
        get() = settings.speed * ((180f / 320) / 5)

    // --- Called by CubeRenderer from onSurfaceChanged (GL thread) ---

    fun onSurfaceChanged(width: Int, height: Int) {
        val zNear = 0.1f
        val zFar = 1000f
        val fov = 80.0f / 57.3f
        val size = zNear * Math.tan((fov / 2.0)).toFloat()
        val aspectRatio = width.toFloat() / height
        Matrix.frustumM(projectionMatrix, 0, -size, size, -size / aspectRatio, size / aspectRatio, zNear, zFar)
    }

    // --- Called by CubeRenderer from onDrawFrame (GL thread) ---

    fun buildFrame() {
        matrixTracker.reset()
        renderPosition.reset()

        val cameraZ = (-20 + settings.size).toFloat()
        matrixTracker.translate(0f, 0f, cameraZ)

        tickInertia()

        matrixTracker.rotate(angleRotateX, 0f, 1f, 0f)
        matrixTracker.rotate(angleRotateY, 1f, 0f, 0f)

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

        _renderState.value = CubeRenderState(commands)
    }

    fun advanceFrame() {
        engine.postFrameAdvance()
    }

    // --- Touch handlers (called from UI thread) ---

    fun onActionDown(x: Float, y: Float, screenWidth: Int, screenHeight: Int) {
        verticalOrientation = if (x > screenWidth / 2) 1 else -1
        horizontalOrientation = if (y < screenHeight / 2) -1 else 1
        startX = x
        startY = y
        startTime = System.currentTimeMillis()
    }

    fun onActionUp(x: Float, y: Float) {
        val dx = x - startX
        val dy = y - startY
        val dt = System.currentTimeMillis() - startTime

        isInertiaActive = true
        inertiaInc = 5f

        when (getMovementType(dt, dx, dy)) {
            MovementType.SWIPE_UP -> triggerRotation(-1 * verticalOrientation)
            MovementType.SWIPE_DOWN -> triggerRotation(1 * verticalOrientation)
            MovementType.SWIPE_LEFT -> triggerRotation(1 * horizontalOrientation)
            MovementType.SWIPE_RIGHT -> triggerRotation(-1 * horizontalOrientation)
            else -> Unit
        }
    }

    fun onActionMove(x: Float, y: Float, previousX: Float, previousY: Float) {
        val dt = System.currentTimeMillis() - startTime
        if (getMovementType(dt, x - startX, y - startY) == MovementType.DRAG) {
            angleRotateXAux = angleRotateX
            angleRotateYAux = angleRotateY
            angleRotateX += (x - previousX) * touchScaleFactor
            angleRotateY += (y - previousY) * touchScaleFactor
        }
    }

    // --- Private: matrix helpers (GL thread) ---

    private fun computeMVP(): FloatArray {
        val mvp = FloatArray(16)
        Matrix.multiplyMM(mvp, 0, projectionMatrix, 0, matrixTracker.getMatrix(), 0)
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

    // --- Private: inertia (GL thread) ---

    private fun tickInertia() {
        if (!isInertiaActive) return
        if (angleRotateX - angleRotateXAux < -2) angleRotateX -= inertiaInc
        if (angleRotateX - angleRotateXAux > 2) angleRotateX += inertiaInc
        if (angleRotateY - angleRotateYAux < -2) angleRotateY -= inertiaInc
        if (angleRotateY - angleRotateYAux > 2) angleRotateY += inertiaInc
        inertiaInc -= 0.1f
        if (inertiaInc < 0.5f) {
            isInertiaActive = false
            inertiaInc = 5f
        }
    }

    // --- Private: touch helpers ---

    private fun triggerRotation(rotationSense: Int) {
        if (!engine.rotation.isAnimating) {
            engine.rotation = engine.rotation.copy(isAnimating = true)
            engine.rotateClosestSideToScreen(rotationSense)
        }
    }

    private fun getMovementType(dt: Long, dx: Float, dy: Float): MovementType {
        val distThreshold = 100
        val timeThreshold = 250L
        return if (dt < timeThreshold) {
            when {
                abs(dx) > abs(dy) && abs(dx) > distThreshold -> if (dx > 0) MovementType.SWIPE_RIGHT else MovementType.SWIPE_LEFT
                abs(dy) > distThreshold -> if (dy > 0) MovementType.SWIPE_DOWN else MovementType.SWIPE_UP
                else -> MovementType.NONE
            }
        } else {
            MovementType.DRAG
        }
    }
}

private data class RenderPosition(var x: Float = 0f, var y: Float = 0f, var z: Float = 0f) {
    fun reset() { x = 0f; y = 0f; z = 0f }
}

private enum class MovementType {
    SWIPE_UP, SWIPE_DOWN, SWIPE_LEFT, SWIPE_RIGHT, DRAG, NONE
}

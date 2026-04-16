package gustavo.brilhante.magiccube2.presentation.cube

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gustavo.brilhante.magiccube2.domain.CubeSettings
import gustavo.brilhante.magiccube2.domain.TimeProvider
import gustavo.brilhante.magiccube2.domain.usecase.ObserveSettingsUseCase
import gustavo.brilhante.magiccube2.grafic.ActiveSlice
import gustavo.brilhante.magiccube2.grafic.Cube
import gustavo.brilhante.magiccube2.grafic.CubeAxis
import gustavo.brilhante.magiccube2.grafic.CubeGameEngineFactory
import gustavo.brilhante.magiccube2.grafic.CubeStepDirection
import gustavo.brilhante.magiccube2.grafic.ICubeGameEngine
import gustavo.brilhante.magiccube2.grafic.MatrixMath
import gustavo.brilhante.magiccube2.grafic.MatrixTracker
import gustavo.brilhante.magiccube2.grafic.PickingService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

class CubeViewModel(
    observeSettings: ObserveSettingsUseCase,
    engineFactory: CubeGameEngineFactory,
    private val timeProvider: TimeProvider,
) : ViewModel() {

    private val settingsState: StateFlow<CubeSettings> = observeSettings()
        .stateIn(viewModelScope, SharingStarted.Eagerly, CubeSettings())

    val settings: CubeSettings get() = settingsState.value

    // Engine is created via factory so it can be replaced with a test double in unit tests.
    // The shuffle count is taken from the cached settings value at construction time; the
    // persisted value takes effect on the next launch once the ViewModel is recreated.
    val engine: ICubeGameEngine = engineFactory.create(settings.shuffle)

    // --- Render state exposed to CubeRenderer ---
    private val _renderState = MutableStateFlow(CubeRenderState())
    val renderState: StateFlow<CubeRenderState> = _renderState.asStateFlow()

    // --- Matrix state (owned by ViewModel, used on GL thread) ---
    private val matrixTracker = MatrixTracker()
    private val renderPosition = RenderPosition()
    private val projectionMatrix = FloatArray(16)
    private val dist = 2.12f

    // --- Picking & Drag state ---
    private val pickingService = PickingService()

    private val _selectedCubelet = MutableStateFlow<Cube?>(null)
    val selectedCubelet: StateFlow<Cube?> = _selectedCubelet.asStateFlow()

    private val _selectedFaceNormal = MutableStateFlow<Triple<Float, Float, Float>?>(null)
    val selectedFaceNormal: StateFlow<Triple<Float, Float, Float>?> = _selectedFaceNormal.asStateFlow()
    
    // The vector representing the movement of the finger on the screen
    private val _dragVector = MutableStateFlow(Triple(0f, 0f, 0f))
    val dragVector: StateFlow<Triple<Float, Float, Float>> = _dragVector.asStateFlow()

    private val _rotationAxis = MutableStateFlow(Triple(0f, 0f, 0f))
    val rotationAxis: StateFlow<Triple<Float, Float, Float>> = _rotationAxis.asStateFlow()

    // Accumulated rotation angle for the active slice drag, in degrees.
    // Written on UI thread, read on GL thread via engine.rotatedAngle (@Volatile).
    private var accumulatedDragAngle = 0f

    companion object {
        // 0.3 deg/px → dragging ~300 px rotates a slice by 90°.
        private const val DRAG_TO_ANGLE_SCALE = 0.3f
        // Drag must exceed this angle (degrees) for the slice to complete; below → cancel.
        private const val SNAP_THRESHOLD = 30f
    }

    // --- Touch rotation (written by UI thread, read by GL thread) ---
    @Volatile var angleRotateX: Float = 0f
    @Volatile var angleRotateY: Float = 0f
    @Volatile var isInertiaActive: Boolean = false

    @Volatile private var angleRotateXAux: Float = 0f
    @Volatile private var angleRotateYAux: Float = 0f
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
        val size = zNear * tan(fov / 2.0).toFloat()
        val aspectRatio = width.toFloat() / height
        MatrixMath.frustumM(projectionMatrix, 0, -size, size, -size / aspectRatio, size / aspectRatio, zNear, zFar)
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
        startTime = timeProvider.currentTimeMillis()

        // Ray picking: store selected cubelet and face on ACTION_DOWN
        val drawCommands = _renderState.value.drawCommands
        if (drawCommands.isNotEmpty()) {
            val result = pickingService.pickCubelet(x, y, screenWidth, screenHeight, drawCommands)
            _selectedCubelet.value = result?.cubelet
            _selectedFaceNormal.value = result?.faceNormal
        }

        accumulatedDragAngle = 0f
    }

    fun onActionUp(x: Float, y: Float) {
        val dx = x - startX
        val dy = y - startY
        val dt = timeProvider.currentTimeMillis() - startTime

        if (_selectedCubelet.value != null
            && engine.rotation.activeSlice != ActiveSlice.NONE
            && !engine.rotation.isAnimating
        ) {
            // Slice drag ended: decide snap direction then start easing animation.
            // Past SNAP_THRESHOLD → complete to ±90°; below → cancel back to 0°.
            val snapAngle = when {
                engine.rotatedAngle >= SNAP_THRESHOLD  ->  90f
                engine.rotatedAngle <= -SNAP_THRESHOLD -> -90f
                else                                   ->   0f
            }
            engine.rotation = engine.rotation.copy(
                isAnimating = true,
                isDragSnap  = true,
                snapTarget  = snapAngle,
            )
        } else {
            // No slice drag: handle whole-cube swipe + inertia as before.
            isInertiaActive = true
            inertiaInc = 1f

            when (getMovementType(dt, dx, dy)) {
                MovementType.SWIPE_UP -> triggerRotation(-1 * verticalOrientation)
                MovementType.SWIPE_DOWN -> triggerRotation(1 * verticalOrientation)
                MovementType.SWIPE_LEFT -> triggerRotation(1 * horizontalOrientation)
                MovementType.SWIPE_RIGHT -> triggerRotation(-1 * horizontalOrientation)
                else -> Unit
            }
        }

        // Reset picking and drag state
        _selectedCubelet.value = null
        _selectedFaceNormal.value = null
        _dragVector.value = Triple(0f, 0f, 0f)
        _rotationAxis.value = Triple(0f, 0f, 0f)
        accumulatedDragAngle = 0f
    }

    fun onActionMove(x: Float, y: Float, previousX: Float, previousY: Float) {
        val dt = timeProvider.currentTimeMillis() - startTime
        val movementType = getMovementType(dt, x - startX, y - startY)

        if (movementType == MovementType.DRAG) {
            if (_selectedCubelet.value == null) {
                // No cubelet selected: rotate the entire cube
                angleRotateXAux = angleRotateX
                angleRotateYAux = angleRotateY
                angleRotateX += (x - previousX) * touchScaleFactor
                angleRotateY += (y - previousY) * touchScaleFactor
            } else {
                // Cubelet selected: project drag into face plane, then drive slice rotation.
                val dx = x - previousX
                val dy = y - previousY
                processDragOnFace(dx, dy)
                applySliceRotationFromDrag(dx, dy)
            }
        }
    }

    /**
     * Converts a screen-space drag delta (dx, dy) into a direction vector in the
     * cubelet's local space, constrained to the plane of the selected face.
     *
     * Steps:
     *  1. Derive the two tangent vectors that span the face plane (local space).
     *  2. Project each tangent through the cube's current global rotation to get
     *     its 2D screen-space direction.
     *  3. Measure how much of (dx, dy) aligns with each tangent's screen direction.
     *  4. Reconstruct the local-space drag as a weighted sum of the two tangents.
     */
    private fun processDragOnFace(dx: Float, dy: Float) {
        val normal = _selectedFaceNormal.value ?: return

        val (t1, t2) = faceLocalTangents(normal)

        // Project each local tangent into 2D screen space under the cube's current rotation.
        val st1 = localToScreenSpace(t1)
        val st2 = localToScreenSpace(t2)

        // How much of the screen drag aligns with each tangent's screen projection.
        val w1 = dx * st1.first + dy * st1.second
        val w2 = dx * st2.first + dy * st2.second

        // Reconstruct the drag direction in local space and normalize.
        val localDrag = Triple(
            t1.first * w1 + t2.first * w2,
            t1.second * w1 + t2.second * w2,
            t1.third * w1 + t2.third * w2,
        )
        _dragVector.value = MatrixMath.normalize(localDrag)

        _rotationAxis.value = computeRotationAxis(normal, _dragVector.value)
    }

    /**
     * Drives progressive slice rotation from a per-frame screen drag delta.
     *
     * The active slice is determined once (first non-trivial drag frame) and locked
     * for the remainder of the gesture. Each subsequent frame accumulates the signed
     * pixel delta and writes it directly to engine.rotatedAngle.
     *
     * postFrameAdvance honours isAnimating=false and does not advance the angle
     * on its own, so the slice follows the finger exactly.
     */
    private fun applySliceRotationFromDrag(screenDx: Float, screenDy: Float) {
        val cubelet = _selectedCubelet.value ?: return
        val normal = _selectedFaceNormal.value ?: return

        // Lock the active slice on the first drag frame; skip if already set.
        if (engine.rotation.activeSlice == ActiveSlice.NONE) {
            engine.updateRotationFromDrag(cubelet, normal, _dragVector.value)
        }
        if (engine.rotation.activeSlice == ActiveSlice.NONE) return

        // Project the screen delta onto each tangent's screen-space direction and
        // pick the dominant one to obtain a signed pixel magnitude.
        val (t1, t2) = faceLocalTangents(normal)
        val st1 = localToScreenSpace(t1)
        val st2 = localToScreenSpace(t2)
        val w1 = screenDx * st1.first + screenDy * st1.second
        val w2 = screenDx * st2.first + screenDy * st2.second
        val signedDelta = if (abs(w1) >= abs(w2)) w1 else w2

        // Accumulate and push to engine. 0.3 deg/px → 300 px ≈ 90°.
        accumulatedDragAngle += signedDelta * DRAG_TO_ANGLE_SCALE
        engine.rotatedAngle = accumulatedDragAngle
    }

    /**
     * Computes the rotation axis for a face drag using the cross product.
     *
     * The axis is perpendicular to both the face normal and the drag direction,
     * which is exactly the axis around which the slice would rotate.
     *
     * @param faceNormal Unit normal of the touched face (local space).
     * @param drag       Normalized drag direction on the face plane (local space).
     * @return           Normalized rotation axis, or zero vector if inputs are parallel.
     */
    private fun computeRotationAxis(
        faceNormal: Triple<Float, Float, Float>,
        drag: Triple<Float, Float, Float>,
    ): Triple<Float, Float, Float> = MatrixMath.normalize(MatrixMath.crossProduct(faceNormal, drag))

    /**
     * Returns the two local-space tangent vectors that span the plane of [normal].
     * Face normals from PickingService are always axis-aligned: (±1,0,0), (0,±1,0), (0,0,±1).
     */
    private fun faceLocalTangents(
        normal: Triple<Float, Float, Float>
    ): Pair<Triple<Float, Float, Float>, Triple<Float, Float, Float>> = when {
        abs(normal.first) > 0.5f  -> Pair(Triple(0f, 1f, 0f), Triple(0f, 0f, 1f)) // ±X face
        abs(normal.second) > 0.5f -> Pair(Triple(1f, 0f, 0f), Triple(0f, 0f, 1f)) // ±Y face
        else                       -> Pair(Triple(1f, 0f, 0f), Triple(0f, 1f, 0f)) // ±Z face
    }

    /**
     * Rotates a local-space vector by the cube's current global rotation and returns
     * its 2D screen-space projection (screenX, screenY).
     *
     * Rotation order (matching buildFrame):
     *  1. [angleRotateX] degrees around the Y axis
     *  2. [angleRotateY] degrees around the X axis
     *
     * Screen Y is the inverse of world Y because screen +Y points down.
     */
    private fun localToScreenSpace(v: Triple<Float, Float, Float>): Pair<Float, Float> {
        val radY = angleRotateX * (Math.PI / 180.0)
        val cosY = cos(radY).toFloat()
        val sinY = sin(radY).toFloat()

        val radX = angleRotateY * (Math.PI / 180.0)
        val cosX = cos(radX).toFloat()
        val sinX = sin(radX).toFloat()

        // Apply Y-axis rotation
        val x1 = v.first * cosY + v.third * sinY
        val y1 = v.second
        val z1 = -v.first * sinY + v.third * cosY

        // Apply X-axis rotation
        val x2 = x1
        val y2 = y1 * cosX - z1 * sinX

        return Pair(x2, -y2) // screen Y is flipped
    }

    // --- Private: matrix helpers (GL thread) ---

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

    fun getMovementType(dt: Long, dx: Float, dy: Float): MovementType {
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

enum class MovementType {
    SWIPE_UP, SWIPE_DOWN, SWIPE_LEFT, SWIPE_RIGHT, DRAG, NONE
}

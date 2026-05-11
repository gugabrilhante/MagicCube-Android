package gustavo.brilhante.magiccube2.presentation.cube

import gustavo.brilhante.magiccube2.domain.TimeProvider
import gustavo.brilhante.magiccube2.domain.cube.CubeInteractionProcessor
import gustavo.brilhante.magiccube2.domain.cube.CubeLogger
import gustavo.brilhante.magiccube2.domain.cube.MovementType
import gustavo.brilhante.magiccube2.domain.model.Vector2
import gustavo.brilhante.magiccube2.domain.model.Vector3
import gustavo.brilhante.magiccube2.grafic.ActiveSlice
import gustavo.brilhante.magiccube2.grafic.ColorLetter
import gustavo.brilhante.magiccube2.grafic.Cube
import gustavo.brilhante.magiccube2.grafic.CubeSide
import gustavo.brilhante.magiccube2.grafic.ICubeGameEngine
import gustavo.brilhante.magiccube2.grafic.PickingService
import kotlin.math.abs

/**
 * Application-service layer for cube touch interaction.
 */
class CubeGameInteractor(
    private val engine: ICubeGameEngine,
    private val interactionProcessor: CubeInteractionProcessor,
    private val pickingService: PickingService,
    private val timeProvider: TimeProvider,
    private val logger: CubeLogger,
) : ICubeInteractor {

    private var selectedCubelet: Cube? = null
    private var selectedFaceNormal: Vector3? = null
    private var localDragVector = Vector3.Zero
    private var accumulatedDragAngle = 0f

    private var startX = 0f
    private var startY = 0f
    private var startTime = 0L
    private var horizontalOrientation = 1
    private var verticalOrientation = 1

    override fun classifyMovement(dt: Long, dx: Float, dy: Float): MovementType =
        interactionProcessor.classifyMovement(dt, dx, dy)

    override fun onActionDown(
        x: Float,
        y: Float,
        screenWidth: Int,
        screenHeight: Int,
        drawCommands: List<CubeDrawCommand>,
    ): List<CubeControllerEffect> {
        verticalOrientation = if (x > screenWidth / 2) 1 else -1
        horizontalOrientation = if (y < screenHeight / 2) -1 else 1
        startX = x
        startY = y
        startTime = timeProvider.currentTimeMillis()
        accumulatedDragAngle = 0f

        if (drawCommands.isNotEmpty()) {
            val result = pickingService.pickCubelet(x, y, screenWidth, screenHeight, drawCommands)
            selectedCubelet = result?.cubelet
            selectedFaceNormal = result?.faceNormal
            logPickingResult(result)
        }

        return listOf(CubeControllerEffect.SetDraggingSlice(false))
    }

    override fun onActionMove(
        x: Float,
        y: Float,
        previousX: Float,
        previousY: Float,
        angleRotateX: Float,
        angleRotateY: Float,
        touchScaleFactor: Float,
    ): List<CubeControllerEffect> {
        val dt = timeProvider.currentTimeMillis() - startTime
        if (interactionProcessor.classifyMovement(dt, x - startX, y - startY) != MovementType.DRAG) return emptyList()

        val cubelet = selectedCubelet
        if (cubelet == null) {
            logger.d(TAG, "Cube rotation -> angleX=%.1f | angleY=%.1f".format(angleRotateX, angleRotateY))
            return listOf(CubeControllerEffect.RotateWholeCube(x - previousX, y - previousY, touchScaleFactor))
        }

        val normal = selectedFaceNormal ?: return emptyList()
        val screenDelta = Vector2(x - previousX, y - previousY)

        val wasSliceNone = engine.rotation.activeSlice == ActiveSlice.NONE
        if (wasSliceNone) logDragStart(screenDelta.x, screenDelta.y, angleRotateX, angleRotateY)

        localDragVector = interactionProcessor.computeDragOnFace(screenDelta, normal, angleRotateX, angleRotateY).localDragVector.vector

        // Lock the active slice on the first drag frame — read back immediately.
        if (engine.rotation.activeSlice == ActiveSlice.NONE) {
            engine.updateRotationFromDrag(cubelet, normal, localDragVector)
        }
        if (engine.rotation.activeSlice == ActiveSlice.NONE) return emptyList()

        accumulatedDragAngle += interactionProcessor.computeSliceDelta(
            screenDelta, normal, angleRotateX, angleRotateY,
        ) * DRAG_TO_ANGLE_SCALE

        val effects = mutableListOf<CubeControllerEffect>(
            CubeControllerEffect.UpdateSliceAngle(accumulatedDragAngle),
        )
        if (wasSliceNone && engine.rotation.activeSlice != ActiveSlice.NONE) {
            effects += CubeControllerEffect.SetDraggingSlice(true)
            logSliceLocked(angleRotateX, angleRotateY)
        }
        return effects
    }

    override fun onActionUp(x: Float, y: Float): List<CubeControllerEffect> {
        val dx = x - startX
        val dy = y - startY
        val dt = timeProvider.currentTimeMillis() - startTime

        val effects = mutableListOf<CubeControllerEffect>()

        if (selectedCubelet != null
            && engine.rotation.activeSlice != ActiveSlice.NONE
            && !engine.rotation.isAnimating
        ) {
            effects += CubeControllerEffect.SnapSlice(resolveSnapAngle(engine.rotatedAngle))
        } else {
            effects += CubeControllerEffect.StartInertia
            resolveSwipeSense(interactionProcessor.classifyMovement(dt, dx, dy))
                ?.let { effects += CubeControllerEffect.TriggerSwipeRotation(it) }
        }

        resetGestureState()
        effects += CubeControllerEffect.SetDraggingSlice(false)
        return effects
    }

    override fun onActionCancel(): List<CubeControllerEffect> {
        resetGestureState()
        return listOf(CubeControllerEffect.SetDraggingSlice(false))
    }

    // --- Private helpers ---

    private fun resetGestureState() {
        selectedCubelet = null
        selectedFaceNormal = null
        localDragVector = Vector3.Zero
        accumulatedDragAngle = 0f
    }

    private fun resolveSnapAngle(rotatedAngle: Float): Float = when {
        rotatedAngle >= SNAP_THRESHOLD  ->  90f
        rotatedAngle <= -SNAP_THRESHOLD -> -90f
        else -> 0f
    }

    private fun resolveSwipeSense(movement: MovementType): Int? = when (movement) {
        MovementType.SWIPE_UP    -> -1 * verticalOrientation
        MovementType.SWIPE_DOWN  ->  1 * verticalOrientation
        MovementType.SWIPE_LEFT  ->  1 * horizontalOrientation
        MovementType.SWIPE_RIGHT -> -1 * horizontalOrientation
        else -> null
    }

    private fun logPickingResult(result: PickingService.PickingResult?) {
        result ?: return
        val c = result.cubelet
        logger.d(TAG, "Selected cubelet: front=${c.getFrontSide().name} back=${c.getBackSide().name} " +
                "left=${c.getLeftSide().name} right=${c.getRightSide().name} " +
                "up=${c.getUpperSide().name} down=${c.getDownSide().name}")
        val n = result.faceNormal
        val faceColor = when {
            n.z >  0.5f -> c.getFrontSide()
            n.z < -0.5f -> c.getBackSide()
            n.x >  0.5f -> c.getRightSide()
            n.x < -0.5f -> c.getLeftSide()
            n.y >  0.5f -> c.getUpperSide()
            else       -> c.getDownSide()
        }
        logger.d(TAG, "Touched face: normal=(%.1f,%.1f,%.1f) color=${faceColor.name}".format(n.x, n.y, n.z))
    }

    private fun logDragStart(screenDx: Float, screenDy: Float, angleRotateX: Float, angleRotateY: Float) {
        val dir = when {
            abs(screenDx) >= abs(screenDy) -> if (screenDx > 0) "right" else "left"
            screenDy < 0 -> "up"
            else -> "down"
        }
        logger.d(TAG, "Drag direction: $dir (dx=%.1f dy=%.1f)".format(screenDx, screenDy))
        val visibleFaces = interactionProcessor.getVisibleFaces(angleRotateX, angleRotateY).map { it.colorName }
        logger.d(TAG, "Visible faces: $visibleFaces")
    }

    private fun logSliceLocked(angleRotateX: Float, angleRotateY: Float) {
        val activeSlice = engine.rotation.activeSlice
        val cubeSide = CubeSide.entries.find { it.rotation == activeSlice }
        if (cubeSide != null) {
            val sliceColor = engine.faceCenterCubes.find { it.second == cubeSide }?.first
                ?.let { engine.cubes[it] }
                ?.let { cube ->
                    listOf(cube.getFrontSide(), cube.getBackSide(), cube.getLeftSide(),
                           cube.getRightSide(), cube.getUpperSide(), cube.getDownSide())
                        .firstOrNull { it != ColorLetter.BLACK }?.name
                } ?: "unknown"
            logger.d(TAG, "Active slice: $sliceColor ($activeSlice)")
        } else {
            logger.d(TAG, "Active slice: middle ($activeSlice)")
        }
        val rotAxis = when {
            activeSlice.name.startsWith("ROTATION_AXIS_Z") -> "Y"
            activeSlice.name.startsWith("ROTATION_AXIS_Y") -> "Z"
            else -> "X"
        }
        val rotDir = if (engine.rotatedAngle >= 0f) "positive (+)" else "negative (-)"
        logger.d(TAG, "Rotation: axis=$rotAxis direction=$rotDir (angle=%.2f)".format(engine.rotatedAngle))
        logger.d(TAG, "Cube: angleRotateX=%.1f | angleRotateY=%.1f".format(angleRotateX, angleRotateY))
    }

    companion object {
        private const val TAG = "CubeGame"
        private const val DRAG_TO_ANGLE_SCALE = 0.3f
        private const val SNAP_THRESHOLD = 30f
    }
}

package gustavo.brilhante.magiccube2.presentation.cube

import gustavo.brilhante.magiccube2.domain.TimeProvider
import gustavo.brilhante.magiccube2.domain.cube.CubeInteractionProcessor
import gustavo.brilhante.magiccube2.domain.cube.CubeLogger
import gustavo.brilhante.magiccube2.domain.cube.MovementType
import gustavo.brilhante.magiccube2.grafic.ActiveSlice
import gustavo.brilhante.magiccube2.grafic.ColorLetter
import gustavo.brilhante.magiccube2.grafic.Cube
import gustavo.brilhante.magiccube2.grafic.CubeSide
import gustavo.brilhante.magiccube2.grafic.ICubeGameEngine
import gustavo.brilhante.magiccube2.grafic.PickingService
import kotlin.math.abs

/**
 * Application-service layer for cube touch interaction.
 *
 * Owns all drag, snap, and swipe orchestration that previously lived in the ViewModel.
 * Has no Android or OpenGL imports — every behaviour is unit-testable by asserting on the
 * [CubeControllerEffect] lists returned from each method.
 *
 * The only mutable state it holds is in-flight gesture data (start position, accumulated
 * angle, selected cubelet). All rendering and UI-state mutations are delegated back to the
 * ViewModel via [CubeControllerEffect]s.
 */
class CubeGameInteractor(
    private val engine: ICubeGameEngine,
    private val interaction: CubeInteractionProcessor,
    private val pickingService: PickingService,
    private val timeProvider: TimeProvider,
    private val logger: CubeLogger,
) : ICubeInteractor {

    private var selectedCubelet: Cube? = null
    private var selectedFaceNormal: Triple<Float, Float, Float>? = null
    private var localDragVector = Triple(0f, 0f, 0f)
    private var accumulatedDragAngle = 0f

    private var startX = 0f
    private var startY = 0f
    private var startTime = 0L
    private var horizontalOrientation = 1
    private var verticalOrientation = 1

    override fun classifyMovement(dt: Long, dx: Float, dy: Float): MovementType =
        interaction.classifyMovement(dt, dx, dy)

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
        if (interaction.classifyMovement(dt, x - startX, y - startY) != MovementType.DRAG) return emptyList()

        val cubelet = selectedCubelet
        if (cubelet == null) {
            logger.d(TAG, "Cube rotation -> angleX=%.1f | angleY=%.1f".format(angleRotateX, angleRotateY))
            return listOf(CubeControllerEffect.RotateWholeCube(x - previousX, y - previousY, touchScaleFactor))
        }

        val normal = selectedFaceNormal ?: return emptyList()
        val screenDx = x - previousX
        val screenDy = y - previousY
        val (localDx, localDy) = interaction.computeLocalDrag(screenDx, screenDy, angleRotateX, angleRotateY)

        val wasSliceNone = engine.rotation.activeSlice == ActiveSlice.NONE
        if (wasSliceNone) logDragStart(screenDx, screenDy, angleRotateX, angleRotateY)

        localDragVector = interaction.computeDragOnFace(localDx, localDy, normal, angleRotateX, angleRotateY).localDragVector

        // Lock the active slice on the first drag frame — read back immediately.
        if (engine.rotation.activeSlice == ActiveSlice.NONE) {
            engine.updateRotationFromDrag(cubelet, normal, localDragVector)
        }
        if (engine.rotation.activeSlice == ActiveSlice.NONE) return emptyList()

        accumulatedDragAngle += interaction.computeSliceDelta(
            localDx, localDy, normal, angleRotateX, angleRotateY,
        ) * CubeInteractionProcessor.DRAG_TO_ANGLE_SCALE

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
            resolveSwipeSense(interaction.classifyMovement(dt, dx, dy))
                ?.let { effects += CubeControllerEffect.TriggerSwipeRotation(it) }
        }

        resetGestureState()
        effects += CubeControllerEffect.SetDraggingSlice(false)
        return effects
    }

    // --- Private helpers ---

    private fun resetGestureState() {
        selectedCubelet = null
        selectedFaceNormal = null
        localDragVector = Triple(0f, 0f, 0f)
        accumulatedDragAngle = 0f
    }

    private fun resolveSnapAngle(rotatedAngle: Float): Float = when {
        rotatedAngle >= CubeInteractionProcessor.SNAP_THRESHOLD  ->  90f
        rotatedAngle <= -CubeInteractionProcessor.SNAP_THRESHOLD -> -90f
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
        val (nx, ny, nz) = result.faceNormal
        val faceColor = when {
            nz >  0.5f -> c.getFrontSide()
            nz < -0.5f -> c.getBackSide()
            nx >  0.5f -> c.getRightSide()
            nx < -0.5f -> c.getLeftSide()
            ny >  0.5f -> c.getUpperSide()
            else       -> c.getDownSide()
        }
        logger.d(TAG, "Touched face: normal=(%.1f,%.1f,%.1f) color=${faceColor.name}".format(nx, ny, nz))
    }

    private fun logDragStart(screenDx: Float, screenDy: Float, angleRotateX: Float, angleRotateY: Float) {
        val dir = when {
            abs(screenDx) >= abs(screenDy) -> if (screenDx > 0) "right" else "left"
            screenDy < 0 -> "up"
            else -> "down"
        }
        logger.d(TAG, "Drag direction: $dir (dx=%.1f dy=%.1f)".format(screenDx, screenDy))
        logger.d(TAG, "Visible faces: ${interaction.visibleFaceSlices(angleRotateX, angleRotateY)}")
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
    }
}

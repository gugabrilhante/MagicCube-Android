package gustavo.brilhante.magiccube2.domain.cube

import gustavo.brilhante.magiccube2.grafic.CubeSide
import gustavo.brilhante.magiccube2.grafic.MatrixMath
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * Pure, stateless processor for all cube interaction math.
 *
 * No Android imports — fully unit-testable in isolation.
 * All methods receive the data they need explicitly; no side-effects.
 */
class CubeInteractionProcessor {

    data class DragOnFaceResult(
        val localDragVector: Triple<Float, Float, Float>,
        val rotationAxis: Triple<Float, Float, Float>,
    )

    companion object {
        const val DRAG_TO_ANGLE_SCALE = 0.3f
        const val SNAP_THRESHOLD = 30f
    }

    /**
     * Classifies a touch gesture as a swipe, drag, or no-op based on elapsed time and
     * total displacement since the gesture started.
     */
    fun classifyMovement(dt: Long, dx: Float, dy: Float): MovementType {
        val distThreshold = 100
        val timeThreshold = 250L
        return if (dt < timeThreshold) {
            when {
                abs(dx) > abs(dy) && abs(dx) > distThreshold ->
                    if (dx > 0) MovementType.SWIPE_RIGHT else MovementType.SWIPE_LEFT
                abs(dy) > distThreshold ->
                    if (dy > 0) MovementType.SWIPE_DOWN else MovementType.SWIPE_UP
                else -> MovementType.NONE
            }
        } else {
            MovementType.DRAG
        }
    }

    /**
     * Converts a raw screen drag delta into the cube's local coordinate space by
     * reversing the cube's global Y and X rotation.
     *
     * Returns (localDx, localDy).
     */
    fun computeLocalDrag(
        screenDx: Float,
        screenDy: Float,
        angleRotateX: Float,
        angleRotateY: Float,
    ): Pair<Float, Float> {
        val (cosY, sinY) = cosSin(angleRotateX)
        val (cosX, sinX) = cosSin(angleRotateY)
        return Pair(
            screenDx * cosY - screenDy * sinX * sinY,
            screenDy * cosX,
        )
    }

    /**
     * Projects a local-space drag delta onto the plane of the selected face, then
     * reconstructs the drag direction in local space and derives the rotation axis.
     */
    fun computeDragOnFace(
        dx: Float,
        dy: Float,
        normal: Triple<Float, Float, Float>,
        angleRotateX: Float,
        angleRotateY: Float,
    ): DragOnFaceResult {
        val (t1, t2) = faceLocalTangents(normal)
        val st1 = localToScreenSpace(t1, angleRotateX, angleRotateY)
        val st2 = localToScreenSpace(t2, angleRotateX, angleRotateY)
        val w1 = dx * st1.first + dy * st1.second
        val w2 = dx * st2.first + dy * st2.second
        val rawDrag = Triple(
            t1.first * w1 + t2.first * w2,
            t1.second * w1 + t2.second * w2,
            t1.third * w1 + t2.third * w2,
        )
        val drag = MatrixMath.normalize(rawDrag)
        val axis = MatrixMath.normalize(MatrixMath.crossProduct(normal, drag))
        return DragOnFaceResult(drag, axis)
    }

    /**
     * Computes the signed rotation delta for a single drag frame.
     *
     * Selects the face tangent that best matches the gesture direction and projects
     * the drag onto it. Applies a sign correction for the (0,1,0) tangent on ±X and ±Z
     * faces: the screen-Y flip in [localToScreenSpace] inverts the dot product's sign,
     * so the correction `sign(normal.x − normal.z)` restores the expected direction.
     */
    fun computeSliceDelta(
        screenDx: Float,
        screenDy: Float,
        normal: Triple<Float, Float, Float>,
        angleRotateX: Float,
        angleRotateY: Float,
    ): Float {
        val (t1, t2) = faceLocalTangents(normal)
        val st1 = localToScreenSpace(t1, angleRotateX, angleRotateY)
        val st2 = localToScreenSpace(t2, angleRotateX, angleRotateY)
        val w1 = screenDx * st1.first + screenDy * st1.second
        val w2 = screenDx * st2.first + screenDy * st2.second
        val gestureIsHorizontal = abs(screenDx) >= abs(screenDy)
        val t1IsHorizontal = abs(st1.first) >= abs(st1.second)
        val useT1 = gestureIsHorizontal == t1IsHorizontal
        val chosenW = if (useT1) w1 else w2
        val chosenTangent = if (useT1) t1 else t2
        val signCorrection = if (chosenTangent.second > 0.5f) {
            if (normal.first - normal.third >= 0f) 1f else -1f
        } else 1f
        return chosenW * signCorrection
    }

    /**
     * Returns the names of the 6 face-slices currently visible to the camera, used for
     * debug logging.
     */
    fun visibleFaceSlices(angleRotateX: Float, angleRotateY: Float): List<String> {
        val (cosY, sinY) = cosSin(angleRotateX)
        val (cosX, sinX) = cosSin(angleRotateY)
        fun worldZ(nx: Float, ny: Float, nz: Float) =
            -nx * sinY + ny * cosY * sinX + nz * cosY * cosX
        return buildList {
            if (worldZ( 0f,  1f,  0f) > 0f) add(CubeSide.YELLOW.colorName)
            if (worldZ( 0f, -1f,  0f) > 0f) add(CubeSide.WHITE.colorName)
            if (worldZ( 0f,  0f,  1f) > 0f) add(CubeSide.BLUE.colorName)
            if (worldZ( 0f,  0f, -1f) > 0f) add(CubeSide.GREEN.colorName)
            if (worldZ( 1f,  0f,  0f) > 0f) add(CubeSide.RED.colorName)
            if (worldZ(-1f,  0f,  0f) > 0f) add(CubeSide.ORANGE.colorName)
        }
    }

    fun faceLocalTangents(
        normal: Triple<Float, Float, Float>,
    ): Pair<Triple<Float, Float, Float>, Triple<Float, Float, Float>> = when {
        abs(normal.first) > 0.5f  -> Pair(Triple(0f, 1f, 0f), Triple(0f, 0f, 1f))
        abs(normal.second) > 0.5f -> Pair(Triple(1f, 0f, 0f), Triple(0f, 0f, 1f))
        else                       -> Pair(Triple(1f, 0f, 0f), Triple(0f, 1f, 0f))
    }

    fun localToScreenSpace(
        v: Triple<Float, Float, Float>,
        angleRotateX: Float,
        angleRotateY: Float,
    ): Pair<Float, Float> {
        val (cosY, sinY) = cosSin(angleRotateX)
        val (cosX, sinX) = cosSin(angleRotateY)
        val x1 = v.first * cosY + v.third * sinY
        val y1 = v.second
        val z1 = -v.first * sinY + v.third * cosY
        val x2 = x1
        val y2 = y1 * cosX - z1 * sinX
        return Pair(x2, -y2)
    }

    private fun cosSin(angleDegrees: Float): Pair<Float, Float> {
        val rad = angleDegrees * (Math.PI / 180.0)
        return Pair(cos(rad).toFloat(), sin(rad).toFloat())
    }
}

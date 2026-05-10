package gustavo.brilhante.magiccube2.domain.cube

import gustavo.brilhante.magiccube2.domain.math.MatrixMath
import gustavo.brilhante.magiccube2.domain.model.DragVector
import gustavo.brilhante.magiccube2.domain.model.FaceTangents
import gustavo.brilhante.magiccube2.domain.model.RotationAxis
import gustavo.brilhante.magiccube2.domain.model.Vector2
import gustavo.brilhante.magiccube2.domain.model.Vector3
import kotlin.math.abs

class FaceInteractionCalculator(
    private val coordinateTransformer: CoordinateTransformer,
    private val matrixMath: MatrixMath
) {

    data class DragOnFaceResult(
        val localDragVector: DragVector,
        val rotationAxis: RotationAxis,
    )

    /**
     * Projects a local-space drag delta onto the plane of the selected face, then
     * reconstructs the drag direction in local space and derives the rotation axis.
     */
    fun computeDragOnFace(
        screenDelta: Vector2,
        normal: Vector3,
        angleRotateX: Float,
        angleRotateY: Float,
    ): DragOnFaceResult {
        val (t1, t2) = faceLocalTangents(normal)
        val st1 = coordinateTransformer.localToScreenSpace(t1, angleRotateX, angleRotateY)
        val st2 = coordinateTransformer.localToScreenSpace(t2, angleRotateX, angleRotateY)
        val w1 = screenDelta.x * st1.x + screenDelta.y * st1.y
        val w2 = screenDelta.x * st2.x + screenDelta.y * st2.y
        val rawDrag = Vector3(
            t1.x * w1 + t2.x * w2,
            t1.y * w1 + t2.y * w2,
            t1.z * w1 + t2.z * w2,
        )
        val drag = matrixMath.normalize(rawDrag)
        val axis = matrixMath.normalize(matrixMath.crossProduct(normal, drag))
        return DragOnFaceResult(DragVector(drag), RotationAxis(axis))
    }

    /**
     * Computes the signed rotation delta for a single drag frame.
     */
    fun computeSliceDelta(
        screenDelta: Vector2,
        normal: Vector3,
        angleRotateX: Float,
        angleRotateY: Float,
    ): Float {
        val (t1, t2) = faceLocalTangents(normal)
        val st1 = coordinateTransformer.localToScreenSpace(t1, angleRotateX, angleRotateY)
        val st2 = coordinateTransformer.localToScreenSpace(t2, angleRotateX, angleRotateY)
        val w1 = screenDelta.x * st1.x + screenDelta.y * st1.y
        val w2 = screenDelta.x * st2.x + screenDelta.y * st2.y
        val gestureIsHorizontal = abs(screenDelta.x) >= abs(screenDelta.y)
        val t1IsHorizontal = abs(st1.x) >= abs(st1.y)
        val useT1 = gestureIsHorizontal == t1IsHorizontal
        val chosenW = if (useT1) w1 else w2
        val chosenTangent = if (useT1) t1 else t2
        val signCorrection = if (chosenTangent.y > 0.5f) {
            if (normal.x - normal.z >= 0f) 1f else -1f
        } else 1f
        return chosenW * signCorrection
    }

    fun faceLocalTangents(
        normal: Vector3,
    ): FaceTangents = when {
        abs(normal.x) > 0.5f  -> FaceTangents(Vector3(0f, 1f, 0f), Vector3(0f, 0f, 1f))
        abs(normal.y) > 0.5f -> FaceTangents(Vector3(1f, 0f, 0f), Vector3(0f, 0f, 1f))
        else                       -> FaceTangents(Vector3(1f, 0f, 0f), Vector3(0f, 1f, 0f))
    }
}

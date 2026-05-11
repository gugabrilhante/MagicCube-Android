package gustavo.brilhante.magiccube2.domain.cube

import gustavo.brilhante.magiccube2.domain.math.MatrixMath
import gustavo.brilhante.magiccube2.domain.model.CubeFace
import gustavo.brilhante.magiccube2.domain.model.DragVector
import gustavo.brilhante.magiccube2.domain.model.RotationAxis
import gustavo.brilhante.magiccube2.domain.model.Vector2
import gustavo.brilhante.magiccube2.domain.model.Vector3

class CubeInteractionProcessor(
    private val gestureClassifier: GestureClassifier,
    private val rotationMath: RotationMath,
    private val geometryResolver: FaceGeometryResolver,
    private val sliceResolver: SliceInteractionResolver,
    private val visibilityCalculator: VisibleFacesCalculator,
    private val matrixMath: MatrixMath
) {

    data class DragOnFaceResult(
        val localDragVector: DragVector,
        val rotationAxis: RotationAxis,
    )

    fun classifyMovement(dt: Long, dx: Float, dy: Float): MovementType =
        gestureClassifier.classifyMovement(dt, dx, dy)

    fun computeLocalDrag(screenDelta: Vector2, angleRotateX: Float, angleRotateY: Float): Vector2 =
        rotationMath.computeLocalDrag(screenDelta, angleRotateX, angleRotateY)

    fun computeDragOnFace(
        screenDelta: Vector2,
        normal: Vector3,
        angleRotateX: Float,
        angleRotateY: Float,
    ): DragOnFaceResult {
        val (t1, t2) = geometryResolver.faceLocalTangents(normal)
        val st1 = rotationMath.localToScreenSpace(t1, angleRotateX, angleRotateY)
        val st2 = rotationMath.localToScreenSpace(t2, angleRotateX, angleRotateY)
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

    fun computeSliceDelta(
        screenDelta: Vector2,
        normal: Vector3,
        angleRotateX: Float,
        angleRotateY: Float,
    ): Float = sliceResolver.computeSliceDelta(screenDelta, normal, angleRotateX, angleRotateY)

    fun getVisibleFaces(angleRotateX: Float, angleRotateY: Float): List<CubeFace> =
        visibilityCalculator.getVisibleFaces(angleRotateX, angleRotateY)
}

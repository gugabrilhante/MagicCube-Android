package gustavo.brilhante.magiccube2.domain.cube

import gustavo.brilhante.magiccube2.domain.model.Vector2
import gustavo.brilhante.magiccube2.domain.model.Vector3
import kotlin.math.abs

interface SliceInteractionResolver {
    fun computeSliceDelta(
        screenDelta: Vector2,
        normal: Vector3,
        angleRotateX: Float,
        angleRotateY: Float,
    ): Float
}

class CubeSliceInteractionResolver(
    private val geometryResolver: FaceGeometryResolver,
    private val rotationMath: RotationMath,
) : SliceInteractionResolver {

    override fun computeSliceDelta(
        screenDelta: Vector2,
        normal: Vector3,
        angleRotateX: Float,
        angleRotateY: Float,
    ): Float {
        val (t1, t2) = geometryResolver.faceLocalTangents(normal)
        val st1 = rotationMath.localToScreenSpace(t1, angleRotateX, angleRotateY)
        val st2 = rotationMath.localToScreenSpace(t2, angleRotateX, angleRotateY)
        
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
}

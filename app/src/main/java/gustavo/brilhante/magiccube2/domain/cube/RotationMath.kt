package gustavo.brilhante.magiccube2.domain.cube

import gustavo.brilhante.magiccube2.domain.math.VectorMath
import gustavo.brilhante.magiccube2.domain.model.Vector2
import gustavo.brilhante.magiccube2.domain.model.Vector3

interface RotationMath {
    fun computeLocalDrag(screenDelta: Vector2, angleRotateX: Float, angleRotateY: Float): Vector2
    fun localToScreenSpace(v: Vector3, angleRotateX: Float, angleRotateY: Float): Vector2
}

class CubeRotationMath : RotationMath {
    override fun computeLocalDrag(
        screenDelta: Vector2,
        angleRotateX: Float,
        angleRotateY: Float,
    ): Vector2 {
        val (cosY, sinY) = VectorMath.cosSin(angleRotateX)
        val (cosX, sinX) = VectorMath.cosSin(angleRotateY)
        return Vector2(
            screenDelta.x * cosY - screenDelta.y * sinX * sinY,
            screenDelta.y * cosX,
        )
    }

    override fun localToScreenSpace(
        v: Vector3,
        angleRotateX: Float,
        angleRotateY: Float,
    ): Vector2 {
        val (cosY, sinY) = VectorMath.cosSin(angleRotateX)
        val (cosX, sinX) = VectorMath.cosSin(angleRotateY)
        val x1 = v.x * cosY + v.z * sinY
        val y1 = v.y
        val z1 = -v.x * sinY + v.z * cosY
        val x2 = x1
        val y2 = y1 * cosX - z1 * sinX
        return Vector2(x2, -y2)
    }
}

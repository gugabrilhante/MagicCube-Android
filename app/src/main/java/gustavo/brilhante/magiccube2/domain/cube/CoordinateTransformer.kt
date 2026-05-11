package gustavo.brilhante.magiccube2.domain.cube

import gustavo.brilhante.magiccube2.domain.model.Vector2
import gustavo.brilhante.magiccube2.domain.model.Vector3
import kotlin.math.cos
import kotlin.math.sin

class CoordinateTransformer {

    /**
     * Converts a raw screen drag delta into the cube's local coordinate space by
     * reversing the cube's global Y and X rotation.
     */
    fun computeLocalDrag(
        screenDelta: Vector2,
        angleRotateX: Float,
        angleRotateY: Float,
    ): Vector2 {
        val (cosY, sinY) = cosSin(angleRotateX)
        val (cosX, sinX) = cosSin(angleRotateY)
        return Vector2(
            screenDelta.x * cosY - screenDelta.y * sinX * sinY,
            screenDelta.y * cosX,
        )
    }

    fun localToScreenSpace(
        v: Vector3,
        angleRotateX: Float,
        angleRotateY: Float,
    ): Vector2 {
        val (cosY, sinY) = cosSin(angleRotateX)
        val (cosX, sinX) = cosSin(angleRotateY)
        val x1 = v.x * cosY + v.z * sinY
        val y1 = v.y
        val z1 = -v.x * sinY + v.z * cosY
        val x2 = x1
        val y2 = y1 * cosX - z1 * sinX
        return Vector2(x2, -y2)
    }

    private fun cosSin(angleDegrees: Float): Pair<Float, Float> {
        val rad = angleDegrees * (Math.PI / 180.0)
        return Pair(cos(rad).toFloat(), sin(rad).toFloat())
    }
}

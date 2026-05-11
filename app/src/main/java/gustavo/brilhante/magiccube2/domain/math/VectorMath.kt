package gustavo.brilhante.magiccube2.domain.math

import kotlin.math.cos
import kotlin.math.sin

object VectorMath {
    fun cosSin(angleDegrees: Float): Pair<Float, Float> {
        val rad = angleDegrees * (Math.PI / 180.0)
        return Pair(cos(rad).toFloat(), sin(rad).toFloat())
    }
}

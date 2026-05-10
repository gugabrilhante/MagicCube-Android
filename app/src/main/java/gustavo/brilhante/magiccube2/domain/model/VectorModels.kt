package gustavo.brilhante.magiccube2.domain.model

import kotlin.math.sqrt

data class Vector2(val x: Float, val y: Float)

data class Vector3(val x: Float, val y: Float, val z: Float) {
    fun normalize(): Vector3 {
        val length = sqrt(x * x + y * y + z * z)
        return if (length > 0) {
            Vector3(x / length, y / length, z / length)
        } else {
            this
        }
    }

    operator fun plus(other: Vector3) = Vector3(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Vector3) = Vector3(x - other.x, y - other.y, z - other.z)
    operator fun times(scale: Float) = Vector3(x * scale, y * scale, z * scale)

    companion object {
        val Zero = Vector3(0f, 0f, 0f)
    }
}

data class RotationAxis(val vector: Vector3)

data class DragVector(val vector: Vector3)

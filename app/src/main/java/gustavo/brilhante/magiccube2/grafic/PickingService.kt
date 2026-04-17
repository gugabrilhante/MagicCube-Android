package gustavo.brilhante.magiccube2.grafic

import android.opengl.Matrix
import gustavo.brilhante.magiccube2.presentation.cube.CubeDrawCommand
import kotlin.math.abs

/**
 * Service responsible for detecting which cubelet was touched using ray casting.
 */
class PickingService {

    data class PickingResult(
        val cubelet: Cube,
        val faceNormal: Triple<Float, Float, Float>
    )

    /**
     * Performs a ray cast from the touch coordinates into the 3D scene.
     * @return The closest cubelet and the normal of the hit face, or null if no cubelet was hit.
     */
    fun pickCubelet(
        x: Float, y: Float,
        screenWidth: Int, screenHeight: Int,
        drawCommands: List<CubeDrawCommand>
    ): PickingResult? {
        // 1. Transform screen coordinates to Normalized Device Coordinates (NDC)
        // x: [0, width] -> [-1, 1], y: [0, height] -> [1, -1]
        val xNdc = (2.0f * x) / screenWidth - 1.0f
        val yNdc = 1.0f - (2.0f * y) / screenHeight

        var closestResult: PickingResult? = null
        var minDistance = Float.MAX_VALUE

        for (command in drawCommands) {
            // We need to transform the ray from NDC to the local space of each cubelet.
            // Each cubelet has its own MVP matrix.
            val invertedMvp = FloatArray(16)
            if (!Matrix.invertM(invertedMvp, 0, command.mvpMatrix, 0)) continue

            // A ray in NDC goes from Z=-1 (near plane) to Z=1 (far plane)
            val nearPointNDC = floatArrayOf(xNdc, yNdc, -1f, 1f)
            val farPointNDC = floatArrayOf(xNdc, yNdc, 1f, 1f)

            val nearLocal = transformPoint(invertedMvp, nearPointNDC)
            val farLocal = transformPoint(invertedMvp, farPointNDC)

            val direction = floatArrayOf(
                farLocal[0] - nearLocal[0],
                farLocal[1] - nearLocal[1],
                farLocal[2] - nearLocal[2]
            )

            // Cubelets are unit cubes centered at origin in their local space (from -1 to 1)
            val intersection = intersectAABB(nearLocal, direction)
            if (intersection != null) {
                val (t, normal) = intersection
                if (t < minDistance && t > 0) {
                    minDistance = t
                    closestResult = PickingResult(command.cube, normal)
                }
            }
        }

        return closestResult
    }

    private fun transformPoint(matrix: FloatArray, point: FloatArray): FloatArray {
        val result = FloatArray(4)
        Matrix.multiplyMV(result, 0, matrix, 0, point, 0)
        // Perspective divide
        return floatArrayOf(result[0] / result[3], result[1] / result[3], result[2] / result[3])
    }

    /**
     * Ray-AABB (Axis-Aligned Bounding Box) intersection test for a unit cube (-1 to 1).
     * Returns the distance (t) and the normal of the hit face.
     */
    private fun intersectAABB(origin: FloatArray, dir: FloatArray): Pair<Float, Triple<Float, Float, Float>>? {
        var tMin = -Float.MAX_VALUE
        var tMax = Float.MAX_VALUE
        var hitNormal = Triple(0f, 0f, 0f)

        for (i in 0..2) {
            if (abs(dir[i]) < 1e-6) {
                // Ray is parallel to this axis
                if (origin[i] < -1f || origin[i] > 1f) return null
            } else {
                val invDir = 1.0f / dir[i]
                var t1 = (-1f - origin[i]) * invDir
                var t2 = (1f - origin[i]) * invDir
                var n1 = -1f
                var n2 = 1f

                if (t1 > t2) {
                    val tempT = t1; t1 = t2; t2 = tempT
                    val tempN = n1; n1 = n2; n2 = tempN
                }

                if (t1 > tMin) {
                    tMin = t1
                    hitNormal = when (i) {
                        0 -> Triple(n1, 0f, 0f)
                        1 -> Triple(0f, n1, 0f)
                        else -> Triple(0f, 0f, n1)
                    }
                }
                tMax = minOf(tMax, t2)

                if (tMin > tMax) return null
            }
        }

        return if (tMax > 0) Pair(tMin, hitNormal) else null
    }
}

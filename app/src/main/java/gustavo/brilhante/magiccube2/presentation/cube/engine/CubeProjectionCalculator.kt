package gustavo.brilhante.magiccube2.presentation.cube.engine

import gustavo.brilhante.magiccube2.domain.math.MatrixMath
import kotlin.math.tan

class CubeProjectionCalculator(private val matrixMath: MatrixMath) : ICubeProjectionCalculator {
    override val projectionMatrix = FloatArray(16)

    override fun onSurfaceChanged(width: Int, height: Int) {
        if (width == 0 || height == 0) return

        val zNear = 0.1f
        val zFar = 1000f
        val fov = 80.0f / 57.3f
        val size = zNear * tan(fov / 2.0).toFloat()
        val aspectRatio = width.toFloat() / height
        matrixMath.frustumM(
            projectionMatrix, 0,
            -size, size,
            -size / aspectRatio, size / aspectRatio,
            zNear, zFar,
        )
    }
}

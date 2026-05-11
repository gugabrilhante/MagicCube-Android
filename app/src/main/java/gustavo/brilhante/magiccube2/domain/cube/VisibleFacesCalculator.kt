package gustavo.brilhante.magiccube2.domain.cube

import gustavo.brilhante.magiccube2.domain.math.VectorMath
import gustavo.brilhante.magiccube2.domain.model.CubeFace

interface VisibleFacesCalculator {
    fun getVisibleFaces(angleRotateX: Float, angleRotateY: Float): List<CubeFace>
}

class CubeVisibleFacesCalculator : VisibleFacesCalculator {
    override fun getVisibleFaces(angleRotateX: Float, angleRotateY: Float): List<CubeFace> {
        val (cosY, sinY) = VectorMath.cosSin(angleRotateX)
        val (cosX, sinX) = VectorMath.cosSin(angleRotateY)

        fun worldZ(nx: Float, ny: Float, nz: Float) =
            -nx * sinY + ny * cosY * sinX + nz * cosY * cosX

        return buildList {
            if (worldZ(0f, 1f, 0f) > 0f) add(CubeFace.YELLOW)
            if (worldZ(0f, -1f, 0f) > 0f) add(CubeFace.WHITE)
            if (worldZ(0f, 0f, 1f) > 0f) add(CubeFace.BLUE)
            if (worldZ(0f, 0f, -1f) > 0f) add(CubeFace.GREEN)
            if (worldZ(1f, 0f, 0f) > 0f) add(CubeFace.RED)
            if (worldZ(-1f, 0f, 0f) > 0f) add(CubeFace.ORANGE)
        }
    }
}

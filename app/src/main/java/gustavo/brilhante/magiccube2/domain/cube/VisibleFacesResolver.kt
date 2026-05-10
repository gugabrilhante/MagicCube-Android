package gustavo.brilhante.magiccube2.domain.cube

import gustavo.brilhante.magiccube2.grafic.CubeSide
import kotlin.math.cos
import kotlin.math.sin

class VisibleFacesResolver {
    /**
     * Returns the names of the 6 face-slices currently visible to the camera.
     */
    fun visibleFaceSlices(angleRotateX: Float, angleRotateY: Float): List<String> {
        val radX = angleRotateX * (Math.PI / 180.0)
        val radY = angleRotateY * (Math.PI / 180.0)
        val cosY = cos(radX).toFloat()
        val sinY = sin(radX).toFloat()
        val cosX = cos(radY).toFloat()
        val sinX = sin(radY).toFloat()

        fun worldZ(nx: Float, ny: Float, nz: Float) =
            -nx * sinY + ny * cosY * sinX + nz * cosY * cosX

        return buildList {
            if (worldZ( 0f,  1f,  0f) > 0f) add(CubeSide.YELLOW.colorName)
            if (worldZ( 0f, -1f,  0f) > 0f) add(CubeSide.WHITE.colorName)
            if (worldZ( 0f,  0f,  1f) > 0f) add(CubeSide.BLUE.colorName)
            if (worldZ( 0f,  0f, -1f) > 0f) add(CubeSide.GREEN.colorName)
            if (worldZ( 1f,  0f,  0f) > 0f) add(CubeSide.RED.colorName)
            if (worldZ(-1f,  0f,  0f) > 0f) add(CubeSide.ORANGE.colorName)
        }
    }
}

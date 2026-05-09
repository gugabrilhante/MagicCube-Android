package gustavo.brilhante.magiccube2.presentation.cube.engine

interface ICubeProjectionCalculator {
    val projectionMatrix: FloatArray
    fun onSurfaceChanged(width: Int, height: Int)
}

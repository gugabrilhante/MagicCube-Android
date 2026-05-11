package gustavo.brilhante.magiccube2.grafic

interface IMatrixTracker {
    fun push()
    fun pop()
    fun translate(x: Float, y: Float, z: Float)
    fun rotate(angle: Float, x: Float, y: Float, z: Float)
    fun getZ(): Float
    fun getY(): Float
    fun getX(): Float
    fun getMatrix(): FloatArray
    fun reset()
}

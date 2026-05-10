package gustavo.brilhante.magiccube2.grafic

import gustavo.brilhante.magiccube2.domain.math.MatrixMath
import java.util.Stack

class MatrixTracker(private val matrixMath: MatrixMath) : IMatrixTracker {
    private val stack = Stack<FloatArray>()
    private var current = FloatArray(16)

    init {
        matrixMath.setIdentityM(current, 0)
    }

    override fun push() {
        val copy = FloatArray(16)
        System.arraycopy(current, 0, copy, 0, 16)
        stack.push(copy)
    }

    override fun pop() {
        if (stack.isNotEmpty()) {
            current = stack.pop()
        }
    }

    override fun translate(x: Float, y: Float, z: Float) {
        matrixMath.translateM(current, 0, x, y, z)
    }

    override fun rotate(angle: Float, x: Float, y: Float, z: Float) {
        matrixMath.rotateM(current, 0, angle, x, y, z)
    }

    override fun getZ(): Float = current[14]
    override fun getY(): Float = current[13]
    override fun getX(): Float = current[12]

    override fun getMatrix(): FloatArray {
        val copy = FloatArray(16)
        System.arraycopy(current, 0, copy, 0, 16)
        return copy
    }

    override fun reset() {
        matrixMath.setIdentityM(current, 0)
        stack.clear()
    }
}

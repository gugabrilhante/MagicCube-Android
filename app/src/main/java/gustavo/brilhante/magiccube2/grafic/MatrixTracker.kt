package gustavo.brilhante.magiccube2.grafic

import java.util.Stack

class MatrixTracker {
    private val stack = Stack<FloatArray>()
    private var current = FloatArray(16)

    init {
        MatrixMath.setIdentityM(current, 0)
    }

    fun push() {
        val copy = FloatArray(16)
        System.arraycopy(current, 0, copy, 0, 16)
        stack.push(copy)
    }

    fun pop() {
        if (stack.isNotEmpty()) {
            current = stack.pop()
        }
    }

    fun translate(x: Float, y: Float, z: Float) {
        MatrixMath.translateM(current, 0, x, y, z)
    }

    fun rotate(angle: Float, x: Float, y: Float, z: Float) {
        MatrixMath.rotateM(current, 0, angle, x, y, z)
    }

    fun getZ(): Float = current[14]
    fun getY(): Float = current[13]
    fun getX(): Float = current[12]

    fun getMatrix(): FloatArray {
        val copy = FloatArray(16)
        System.arraycopy(current, 0, copy, 0, 16)
        return copy
    }

    fun reset() {
        MatrixMath.setIdentityM(current, 0)
        stack.clear()
    }
}

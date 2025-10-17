package gustavo.brilhante.magiccubev2.grafic

import android.opengl.Matrix
import java.util.Stack

class MatrixTracker {
    private val stack = Stack<FloatArray>()
    private var current = FloatArray(16)

    init {
        Matrix.setIdentityM(current, 0)
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
        Matrix.translateM(current, 0, x, y, z)
    }

    fun rotate(angle: Float, x: Float, y: Float, z: Float) {
        Matrix.rotateM(current, 0, angle, x, y, z)
    }

    fun getZ(): Float = current[14]
    fun getY(): Float = current[13]
    fun getX(): Float = current[12]

    fun reset() {
        Matrix.setIdentityM(current, 0)
        stack.clear()
    }
}

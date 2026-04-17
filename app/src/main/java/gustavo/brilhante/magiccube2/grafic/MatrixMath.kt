package gustavo.brilhante.magiccube2.grafic

import kotlin.math.*

/**
 * Pure Kotlin implementation of common 4x4 matrix operations.
 * Replaces android.opengl.Matrix for unit test compatibility and consistent behavior.
 */
object MatrixMath {

    fun setIdentityM(sm: FloatArray, offset: Int) {
        for (i in 0 until 16) {
            sm[offset + i] = 0f
        }
        for (i in 0 until 4) {
            sm[offset + i * 5] = 1f
        }
    }

    fun translateM(m: FloatArray, mOffset: Int, x: Float, y: Float, z: Float) {
        for (i in 0 until 4) {
            val mi = mOffset + i
            m[12 + mi] += m[mi] * x + m[4 + mi] * y + m[8 + mi] * z
        }
    }

    fun rotateM(m: FloatArray, mOffset: Int, a: Float, x: Float, y: Float, z: Float) {
        val r = FloatArray(16)
        setRotateM(r, 0, a, x, y, z)
        val temp = FloatArray(16)
        multiplyMM(temp, 0, m, mOffset, r, 0)
        System.arraycopy(temp, 0, m, mOffset, 16)
    }

    private fun setRotateM(rm: FloatArray, rmOffset: Int, a: Float, x: Float, y: Float, z: Float) {
        val rad = a * (PI.toFloat() / 180.0f)
        val s = sin(rad)
        val c = cos(rad)

        val len = sqrt(x * x + y * y + z * z)
        val nx = x / len
        val ny = y / len
        val nz = z / len

        val nc = 1.0f - c
        val xy = nx * ny
        val yz = ny * nz
        val zx = nz * nx
        val xs = nx * s
        val ys = ny * s
        val zs = nz * s

        rm[rmOffset + 0] = nx * nx * nc + c
        rm[rmOffset + 4] = xy * nc - zs
        rm[rmOffset + 8] = zx * nc + ys
        rm[rmOffset + 12] = 0f

        rm[rmOffset + 1] = xy * nc + zs
        rm[rmOffset + 5] = ny * ny * nc + c
        rm[rmOffset + 9] = yz * nc - xs
        rm[rmOffset + 13] = 0f

        rm[rmOffset + 2] = zx * nc - ys
        rm[rmOffset + 6] = yz * nc + xs
        rm[rmOffset + 10] = nz * nz * nc + c
        rm[rmOffset + 14] = 0f

        rm[rmOffset + 3] = 0f
        rm[rmOffset + 7] = 0f
        rm[rmOffset + 11] = 0f
        rm[rmOffset + 15] = 1f
    }

    fun multiplyMM(result: FloatArray, resultOffset: Int, lhs: FloatArray, lhsOffset: Int, rhs: FloatArray, rhsOffset: Int) {
        for (i in 0 until 4) {
            val rhsI0 = rhs[rhsOffset + i * 4 + 0]
            var ri0 = lhs[lhsOffset + 0] * rhsI0
            var ri1 = lhs[lhsOffset + 1] * rhsI0
            var ri2 = lhs[lhsOffset + 2] * rhsI0
            var ri3 = lhs[lhsOffset + 3] * rhsI0

            val rhsI1 = rhs[rhsOffset + i * 4 + 1]
            ri0 += lhs[lhsOffset + 4] * rhsI1
            ri1 += lhs[lhsOffset + 5] * rhsI1
            ri2 += lhs[lhsOffset + 6] * rhsI1
            ri3 += lhs[lhsOffset + 7] * rhsI1

            val rhsI2 = rhs[rhsOffset + i * 4 + 2]
            ri0 += lhs[lhsOffset + 8] * rhsI2
            ri1 += lhs[lhsOffset + 9] * rhsI2
            ri2 += lhs[lhsOffset + 10] * rhsI2
            ri3 += lhs[lhsOffset + 11] * rhsI2

            val rhsI3 = rhs[rhsOffset + i * 4 + 3]
            ri0 += lhs[lhsOffset + 12] * rhsI3
            ri1 += lhs[lhsOffset + 13] * rhsI3
            ri2 += lhs[lhsOffset + 14] * rhsI3
            ri3 += lhs[lhsOffset + 15] * rhsI3

            result[resultOffset + i * 4 + 0] = ri0
            result[resultOffset + i * 4 + 1] = ri1
            result[resultOffset + i * 4 + 2] = ri2
            result[resultOffset + i * 4 + 3] = ri3
        }
    }

    /**
     * Multiplies a 4 element vector by a 4x4 matrix and stores the result in a 4-element column vector.
     */
    fun multiplyMV(result: FloatArray, resultOffset: Int, lhs: FloatArray, lhsOffset: Int, rhs: FloatArray, rhsOffset: Int) {
        val r0 = lhs[lhsOffset + 0] * rhs[rhsOffset + 0] +
                lhs[lhsOffset + 4] * rhs[rhsOffset + 1] +
                lhs[lhsOffset + 8] * rhs[rhsOffset + 2] +
                lhs[lhsOffset + 12] * rhs[rhsOffset + 3]
        val r1 = lhs[lhsOffset + 1] * rhs[rhsOffset + 0] +
                lhs[lhsOffset + 5] * rhs[rhsOffset + 1] +
                lhs[lhsOffset + 9] * rhs[rhsOffset + 2] +
                lhs[lhsOffset + 13] * rhs[rhsOffset + 3]
        val r2 = lhs[lhsOffset + 2] * rhs[rhsOffset + 0] +
                lhs[lhsOffset + 6] * rhs[rhsOffset + 1] +
                lhs[lhsOffset + 10] * rhs[rhsOffset + 2] +
                lhs[lhsOffset + 14] * rhs[rhsOffset + 3]
        val r3 = lhs[lhsOffset + 3] * rhs[rhsOffset + 0] +
                lhs[lhsOffset + 7] * rhs[rhsOffset + 1] +
                lhs[lhsOffset + 11] * rhs[rhsOffset + 2] +
                lhs[lhsOffset + 15] * rhs[rhsOffset + 3]

        result[resultOffset + 0] = r0
        result[resultOffset + 1] = r1
        result[resultOffset + 2] = r2
        result[resultOffset + 3] = r3
    }

    fun frustumM(m: FloatArray, offset: Int, left: Float, right: Float, bottom: Float, top: Float, near: Float, far: Float) {
        val rWidth = 1.0f / (right - left)
        val rHeight = 1.0f / (top - bottom)
        val rDepth = 1.0f / (near - far)
        val x = 2.0f * (near * rWidth)
        val y = 2.0f * (near * rHeight)
        val a = (right + left) * rWidth
        val b = (top + bottom) * rHeight
        val c = (far + near) * rDepth
        val d = 2.0f * (far * near * rDepth)
        
        m[offset + 0] = x
        m[offset + 5] = y
        m[offset + 8] = a
        m[offset + 9] = b
        m[offset + 10] = c
        m[offset + 14] = d
        m[offset + 11] = -1.0f
        
        m[offset + 1] = 0.0f
        m[offset + 2] = 0.0f
        m[offset + 3] = 0.0f
        m[offset + 4] = 0.0f
        m[offset + 6] = 0.0f
        m[offset + 7] = 0.0f
        m[offset + 12] = 0.0f
        m[offset + 13] = 0.0f
        m[offset + 15] = 0.0f
    }

    fun invertM(mInv: FloatArray, mInvOffset: Int, m: FloatArray, mOffset: Int): Boolean {
        // Simple 4x4 matrix inversion implementation
        val src = FloatArray(16)
        System.arraycopy(m, mOffset, src, 0, 16)
        val dst = mInv
        val off = mInvOffset

        val temp = FloatArray(12)
        val v0 = src[0] * src[5] - src[1] * src[4]
        val v1 = src[0] * src[6] - src[2] * src[4]
        val v2 = src[0] * src[7] - src[3] * src[4]
        val v3 = src[1] * src[6] - src[2] * src[5]
        val v4 = src[1] * src[7] - src[3] * src[5]
        val v5 = src[2] * src[7] - src[3] * src[6]
        val v6 = src[8] * src[13] - src[9] * src[12]
        val v7 = src[8] * src[14] - src[10] * src[12]
        val v8 = src[8] * src[15] - src[11] * src[12]
        val v9 = src[9] * src[14] - src[10] * src[13]
        val v10 = src[9] * src[15] - src[11] * src[13]
        val v11 = src[10] * src[15] - src[11] * src[14]

        val det = v0 * v11 - v1 * v10 + v2 * v9 + v3 * v8 - v4 * v7 + v5 * v6
        if (abs(det) < 1e-6) return false
        val invDet = 1f / det

        dst[off + 0] = (src[5] * v11 - src[6] * v10 + src[7] * v9) * invDet
        dst[off + 1] = (src[2] * v10 - src[1] * v11 - src[3] * v9) * invDet
        dst[off + 2] = (src[13] * v5 - src[14] * v4 + src[15] * v3) * invDet
        dst[off + 3] = (src[10] * v4 - src[9] * v5 - src[11] * v3) * invDet
        dst[off + 4] = (src[6] * v8 - src[4] * v11 - src[7] * v7) * invDet
        dst[off + 5] = (src[0] * v11 - src[2] * v8 + src[3] * v7) * invDet
        dst[off + 6] = (src[14] * v2 - src[12] * v5 - src[15] * v1) * invDet
        dst[off + 7] = (src[8] * v5 - src[10] * v2 + src[11] * v1) * invDet
        dst[off + 8] = (src[4] * v10 - src[5] * v8 + src[7] * v6) * invDet
        dst[off + 9] = (src[1] * v8 - src[0] * v10 - src[3] * v6) * invDet
        dst[off + 10] = (src[12] * v4 - src[13] * v2 + src[15] * v0) * invDet
        dst[off + 11] = (src[9] * v2 - src[8] * v4 - src[11] * v0) * invDet
        dst[off + 12] = (src[5] * v7 - src[4] * v9 - src[6] * v6) * invDet
        dst[off + 13] = (src[0] * v9 - src[1] * v7 + src[2] * v6) * invDet
        dst[off + 14] = (src[13] * v1 - src[12] * v3 - src[14] * v0) * invDet
        dst[off + 15] = (src[8] * v3 - src[9] * v1 + src[10] * v0) * invDet

        return true
    }

    /**
     * Calculates the cross product of two 3D vectors.
     */
    fun crossProduct(v1: Triple<Float, Float, Float>, v2: Triple<Float, Float, Float>): Triple<Float, Float, Float> {
        return Triple(
            v1.second * v2.third - v1.third * v2.second,
            v1.third * v2.first - v1.first * v2.third,
            v1.first * v2.second - v1.second * v2.first
        )
    }

    /**
     * Normalizes a 3D vector.
     */
    fun normalize(v: Triple<Float, Float, Float>): Triple<Float, Float, Float> {
        val length = sqrt(v.first * v.first + v.second * v.second + v.third * v.third)
        return if (length < 1e-6) {
            Triple(0f, 0f, 0f)
        } else {
            Triple(v.first / length, v.second / length, v.third / length)
        }
    }
}

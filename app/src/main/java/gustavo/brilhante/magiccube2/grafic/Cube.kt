package gustavo.brilhante.magiccube2.grafic

import android.opengl.GLES30
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Cube(
    frontColor: ColorLetter = ColorLetter.BLACK,
    upColor: ColorLetter = ColorLetter.BLACK,
    rightColor: ColorLetter = ColorLetter.BLACK,
    backColor: ColorLetter = ColorLetter.BLACK,
    leftColor: ColorLetter = ColorLetter.BLACK,
    downColor: ColorLetter = ColorLetter.BLACK
) {
    private val mFVertexBuffer: FloatBuffer
    private var mColorBuffer: ByteBuffer? = null
    private val mTfan1: ByteBuffer
    private val mTfan2: ByteBuffer
    private val colorSideList: ArrayList<CubeRgbColor> = ArrayList()

    init {
        val vertices = floatArrayOf(
            -1.0f,  1.0f,  1.0f,   1.0f,  1.0f,  1.0f,   1.0f, -1.0f,  1.0f,  -1.0f, -1.0f,  1.0f,
            -1.0f,  1.0f, -1.0f,   1.0f,  1.0f, -1.0f,   1.0f, -1.0f, -1.0f,  -1.0f, -1.0f, -1.0f,
            -1.0f,  1.0f,  1.0f,   1.0f,  1.0f,  1.0f,   1.0f, -1.0f,  1.0f,  -1.0f, -1.0f,  1.0f,
            -1.0f,  1.0f, -1.0f,   1.0f,  1.0f, -1.0f,   1.0f, -1.0f, -1.0f,  -1.0f, -1.0f, -1.0f,
            -1.0f,  1.0f,  1.0f,   1.0f,  1.0f,  1.0f,   1.0f, -1.0f,  1.0f,  -1.0f, -1.0f,  1.0f,
            -1.0f,  1.0f, -1.0f,   1.0f,  1.0f, -1.0f,   1.0f, -1.0f, -1.0f,  -1.0f, -1.0f, -1.0f,
            -1.0f,  1.0f,  1.0f,   1.0f,  1.0f,  1.0f,   1.0f, -1.0f,  1.0f,  -1.0f, -1.0f,  1.0f,
            -1.0f,  1.0f, -1.0f,   1.0f,  1.0f, -1.0f,   1.0f, -1.0f, -1.0f,  -1.0f, -1.0f, -1.0f,
             1.0f,  1.0f,  1.0f,  -1.0f, -1.0f, -1.0f,   1.0f,  1.0f,  1.0f,  -1.0f, -1.0f, -1.0f
        )

        for (i in 0..26) colorSideList.add(CubeRgbColor())
        setSideColors(frontColor, upColor, rightColor, backColor, leftColor, downColor)

        val vbb = ByteBuffer.allocateDirect(vertices.size * 4)
        vbb.order(ByteOrder.nativeOrder())
        mFVertexBuffer = vbb.asFloatBuffer()
        mFVertexBuffer.put(vertices)
        mFVertexBuffer.position(0)

        val tfan1 = byteArrayOf(1, 0, 3, 9, 11, 2, 17, 10, 6, 25, 14, 5, 32, 13, 4, 34, 12, 8)
        val tfan2 = byteArrayOf(7, 20, 21, 15, 29, 22, 23, 30, 18, 31, 26, 19, 33, 27, 16, 35, 24, 28)

        mTfan1 = ByteBuffer.allocateDirect(tfan1.size).apply { put(tfan1); position(0) }
        mTfan2 = ByteBuffer.allocateDirect(tfan2.size).apply { put(tfan2); position(0) }
    }

    fun draw(mvpMatrix: FloatArray) {
        GLES30.glFrontFace(GLES30.GL_CCW)

        GLES30.glUniformMatrix4fv(CubeShader.mvpMatrixHandle, 1, false, mvpMatrix, 0)

        GLES30.glEnableVertexAttribArray(CubeShader.positionHandle)
        mFVertexBuffer.position(0)
        GLES30.glVertexAttribPointer(CubeShader.positionHandle, 3, GLES30.GL_FLOAT, false, 0, mFVertexBuffer)

        GLES30.glEnableVertexAttribArray(CubeShader.colorHandle)
        mColorBuffer?.position(0)
        GLES30.glVertexAttribPointer(CubeShader.colorHandle, 4, GLES30.GL_UNSIGNED_BYTE, true, 0, mColorBuffer)

        mTfan1.position(0)
        GLES30.glDrawElements(GLES30.GL_TRIANGLE_FAN, 6 * 3, GLES30.GL_UNSIGNED_BYTE, mTfan1)
        mTfan2.position(0)
        GLES30.glDrawElements(GLES30.GL_TRIANGLE_FAN, 6 * 3, GLES30.GL_UNSIGNED_BYTE, mTfan2)

        GLES30.glDisableVertexAttribArray(CubeShader.positionHandle)
        GLES30.glDisableVertexAttribArray(CubeShader.colorHandle)
    }

    // --- Color accessors ---

    fun getFrontSide(): ColorLetter = colorSideList[0].colorLetter
    fun getUpperSide(): ColorLetter = colorSideList[1].colorLetter
    fun getRightSide(): ColorLetter = colorSideList[2].colorLetter
    fun getBackSide(): ColorLetter = colorSideList[3].colorLetter
    fun getLeftSide(): ColorLetter = colorSideList[4].colorLetter
    fun getDownSide(): ColorLetter = colorSideList[5].colorLetter

    fun setfront(cor: ColorLetter) { setColorAt(0, cor) }
    fun setup(cor: ColorLetter) { setColorAt(1, cor) }
    fun setright(cor: ColorLetter) { setColorAt(2, cor) }
    fun setback(cor: ColorLetter) { setColorAt(3, cor) }
    fun setleft(cor: ColorLetter) { setColorAt(4, cor) }
    fun setdown(cor: ColorLetter) { setColorAt(5, cor) }

    private fun setColorAt(index: Int, cor: ColorLetter) {
        colorSideList[index].colorLetter = cor
        applyColor(colorSideList[index])
        rebuildColorBuffer()
    }

    // --- Private helpers ---

    private fun setSideColors(
        cor1: ColorLetter, cor2: ColorLetter, cor3: ColorLetter,
        cor4: ColorLetter, cor5: ColorLetter, cor6: ColorLetter
    ) {
        colorSideList[0].colorLetter = cor1
        colorSideList[1].colorLetter = cor2
        colorSideList[2].colorLetter = cor3
        colorSideList[3].colorLetter = cor4
        colorSideList[4].colorLetter = cor5
        colorSideList[5].colorLetter = cor6
        for (i in colorSideList.indices) applyColor(colorSideList[i])
        rebuildColorBuffer()
    }

    private fun applyColor(cor: CubeRgbColor) {
        val argb = cor.colorLetter.argb
        cor.v1 = ((argb shr 16) and 0xFF).toByte()
        cor.v2 = ((argb shr 8) and 0xFF).toByte()
        cor.v3 = (argb and 0xFF).toByte()
        cor.v4 = ((argb shr 24) and 0xFF).toByte()
    }

    private fun rebuildColorBuffer() {
        val c = colorSideList
        val bytes = byteArrayOf(
            c[0].v1, c[0].v2, c[0].v3, c[0].v4, c[0].v1, c[0].v2, c[0].v3, c[0].v4,
            c[0].v1, c[0].v2, c[0].v3, c[0].v4, c[0].v1, c[0].v2, c[0].v3, c[0].v4,
            c[1].v1, c[1].v2, c[1].v3, c[1].v4, c[2].v1, c[2].v2, c[2].v3, c[2].v4,
            c[2].v1, c[2].v2, c[2].v3, c[2].v4, c[3].v1, c[3].v2, c[3].v3, c[3].v4,
            c[1].v1, c[1].v2, c[1].v3, c[1].v4, c[0].v1, c[0].v2, c[0].v3, c[0].v4,
            c[2].v1, c[2].v2, c[2].v3, c[2].v4, c[0].v1, c[0].v2, c[0].v3, c[0].v4,
            c[1].v1, c[1].v2, c[1].v3, c[1].v4, c[1].v1, c[1].v2, c[1].v3, c[1].v4,
            c[2].v1, c[2].v2, c[2].v3, c[2].v4, c[3].v1, c[3].v2, c[3].v3, c[3].v4,
            c[4].v1, c[4].v2, c[4].v3, c[4].v4, c[2].v1, c[2].v2, c[2].v3, c[2].v4,
            c[5].v1, c[5].v2, c[5].v3, c[5].v4, c[5].v1, c[5].v2, c[5].v3, c[5].v4,
            c[3].v1, c[3].v2, c[3].v3, c[3].v4, c[3].v1, c[3].v2, c[3].v3, c[3].v4,
            c[3].v1, c[3].v2, c[3].v3, c[3].v4, c[5].v1, c[5].v2, c[5].v3, c[5].v4,
            c[4].v1, c[4].v2, c[4].v3, c[4].v4, c[2].v1, c[2].v2, c[2].v3, c[2].v4,
            c[5].v1, c[5].v2, c[5].v3, c[5].v4, c[4].v1, c[4].v2, c[4].v3, c[4].v4,
            c[4].v1, c[4].v2, c[4].v3, c[4].v4, c[3].v1, c[3].v2, c[3].v3, c[3].v4,
            c[5].v1, c[5].v2, c[5].v3, c[5].v4, c[5].v1, c[5].v2, c[5].v3, c[5].v4,
            c[1].v1, c[1].v2, c[1].v3, c[1].v4, c[4].v1, c[4].v2, c[4].v3, c[4].v4,
            c[1].v1, c[1].v2, c[1].v3, c[1].v4, c[4].v1, c[4].v2, c[4].v3, c[4].v4
        )
        mColorBuffer = ByteBuffer.allocateDirect(bytes.size).apply { put(bytes); position(0) }
    }
}

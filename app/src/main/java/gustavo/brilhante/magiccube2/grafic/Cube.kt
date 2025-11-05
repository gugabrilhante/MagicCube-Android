package gustavo.brilhante.magiccube2.grafic

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10

class Cube(
    frontColor: ColorLetter = ColorLetter.BLACK,
    upColor: ColorLetter = ColorLetter.BLACK,
    rightColor: ColorLetter = ColorLetter.BLACK,
    backColor: ColorLetter = ColorLetter.BLACK,
    leftColor: ColorLetter = ColorLetter.BLACK,
    downColor: ColorLetter = ColorLetter.BLACK
) {
    // cor1 = front
    //cor2 = up
    //cor3 = right
    //cor4 = back
    //cor5 = left
    //cor6 = down
    var mFVertexBuffer: FloatBuffer
    var mColorBuffer: ByteBuffer? = null
    var mTfan1: ByteBuffer
    var mTfan2: ByteBuffer
    var colorSideList: ArrayList<CubeRgbColor> = ArrayList()

    //float[] normalData = new float[108];
    //FloatBuffer m_NormalData;
    fun setSideColors(cor1: ColorLetter, cor2: ColorLetter, cor3: ColorLetter, cor4: ColorLetter, cor5: ColorLetter, cor6: ColorLetter) {
        val maxColor = 255.toByte()

        colorSideList[0].colorLetter = cor1
        colorSideList[1].colorLetter = cor2
        colorSideList[2].colorLetter = cor3
        colorSideList[3].colorLetter = cor4
        colorSideList[4].colorLetter = cor5
        colorSideList[5].colorLetter = cor6

        //RED (R)   : (byte) 130,0,0,maxColor
        //YELLOW (Y) : maxColor,maxColor,0,maxColor
        //BLUE  (B) : 0,0,maxColor,maxColor 
        //GREEN (G) : 0,maxColor,0,maxColor
        //WHITE (W) : maxColor,maxColor,maxColor,maxColor
        //ORANGE (O) : maxColor,69,0,maxColor
        //BLACK (K) : 0,0,0,maxColor
        for (i in colorSideList.indices) {
            applyColor(colorSideList[i])
        }

        val biggerCubeByteArrayColorList = byteArrayOf(
            colorSideList[0].v1, colorSideList[0].v2, colorSideList[0].v3, colorSideList[0].v4,  //0
            colorSideList[0].v1, colorSideList[0].v2, colorSideList[0].v3, colorSideList[0].v4,  //1
            colorSideList[0].v1, colorSideList[0].v2, colorSideList[0].v3, colorSideList[0].v4,  //2
            colorSideList[0].v1, colorSideList[0].v2, colorSideList[0].v3, colorSideList[0].v4,  //3

            colorSideList[1].v1, colorSideList[1].v2, colorSideList[1].v3, colorSideList[1].v4,  //4
            colorSideList[2].v1, colorSideList[2].v2, colorSideList[2].v3, colorSideList[2].v4,  //5
            colorSideList[2].v1, colorSideList[2].v2, colorSideList[2].v3, colorSideList[2].v4,  //6
            colorSideList[3].v1, colorSideList[3].v2, colorSideList[3].v3, colorSideList[3].v4,  //7

            colorSideList[1].v1, colorSideList[1].v2, colorSideList[1].v3, colorSideList[1].v4,  //8
            colorSideList[0].v1, colorSideList[0].v2, colorSideList[0].v3, colorSideList[0].v4,  //9
            colorSideList[2].v1, colorSideList[2].v2, colorSideList[2].v3, colorSideList[2].v4,  //10
            colorSideList[0].v1, colorSideList[0].v2, colorSideList[0].v3, colorSideList[0].v4,  //11

            colorSideList[1].v1, colorSideList[1].v2, colorSideList[1].v3, colorSideList[1].v4,  //12
            colorSideList[1].v1, colorSideList[1].v2, colorSideList[1].v3, colorSideList[1].v4,  //13
            colorSideList[2].v1, colorSideList[2].v2, colorSideList[2].v3, colorSideList[2].v4,  //14
            colorSideList[3].v1, colorSideList[3].v2, colorSideList[3].v3, colorSideList[3].v4,  //15

            colorSideList[4].v1, colorSideList[4].v2, colorSideList[4].v3, colorSideList[4].v4,  //16
            colorSideList[2].v1, colorSideList[2].v2, colorSideList[2].v3, colorSideList[2].v4,  //17
            colorSideList[5].v1, colorSideList[5].v2, colorSideList[5].v3, colorSideList[5].v4,  //18
            colorSideList[5].v1, colorSideList[5].v2, colorSideList[5].v3, colorSideList[5].v4,  //19

            colorSideList[3].v1, colorSideList[3].v2, colorSideList[3].v3, colorSideList[3].v4,  //20
            colorSideList[3].v1, colorSideList[3].v2, colorSideList[3].v3, colorSideList[3].v4,  //21
            colorSideList[3].v1, colorSideList[3].v2, colorSideList[3].v3, colorSideList[3].v4,  //22
            colorSideList[5].v1, colorSideList[5].v2, colorSideList[5].v3, colorSideList[5].v4,  //23

            colorSideList[4].v1, colorSideList[4].v2, colorSideList[4].v3, colorSideList[4].v4,  //24
            colorSideList[2].v1, colorSideList[2].v2, colorSideList[2].v3, colorSideList[2].v4,  //25
            colorSideList[5].v1, colorSideList[5].v2, colorSideList[5].v3, colorSideList[5].v4,  //26
            colorSideList[4].v1, colorSideList[4].v2, colorSideList[4].v3, colorSideList[4].v4,  //27

            colorSideList[4].v1, colorSideList[4].v2, colorSideList[4].v3, colorSideList[4].v4,  //28
            colorSideList[3].v1, colorSideList[3].v2, colorSideList[3].v3, colorSideList[3].v4,  //29
            colorSideList[5].v1, colorSideList[5].v2, colorSideList[5].v3, colorSideList[5].v4,  //30
            colorSideList[5].v1, colorSideList[5].v2, colorSideList[5].v3, colorSideList[5].v4,  //31

            colorSideList[1].v1, colorSideList[1].v2, colorSideList[1].v3, colorSideList[1].v4,  //32
            colorSideList[4].v1, colorSideList[4].v2, colorSideList[4].v3, colorSideList[4].v4,  //33
            colorSideList[1].v1, colorSideList[1].v2, colorSideList[1].v3, colorSideList[1].v4,  //34
            colorSideList[4].v1, colorSideList[4].v2, colorSideList[4].v3, colorSideList[4].v4,  //35

        )
        mColorBuffer = ByteBuffer.allocateDirect(biggerCubeByteArrayColorList.size)
        mColorBuffer?.put(biggerCubeByteArrayColorList)
        mColorBuffer?.position(0)
    }

    fun applyColor(cor: CubeRgbColor) {
        val argb = cor.colorLetter.argb // padrão = preto

        // Extrai cada componente (0–255)
        val a = (argb shr 24) and 0xFF
        val r = (argb shr 16) and 0xFF
        val g = (argb shr 8) and 0xFF
        val b = argb and 0xFF

        // Atribui aos campos
        cor.v1 = r.toByte()
        cor.v2 = g.toByte()
        cor.v3 = b.toByte()
        cor.v4 = a.toByte()
    }

    init {
        val vertices = floatArrayOf(
            -1.0f, 1.0f, 1.0f,  //0
            1.0f, 1.0f, 1.0f,  //1  P1
            1.0f, -1.0f, 1.0f,  //2
            -1.0f, -1.0f, 1.0f,  //3

            -1.0f, 1.0f, -1.0f,  //4
            1.0f, 1.0f, -1.0f,  //5
            1.0f, -1.0f, -1.0f,  //6
            -1.0f, -1.0f, -1.0f,  //7  P2

            -1.0f, 1.0f, 1.0f,  //8
            1.0f, 1.0f, 1.0f,  //9  P1
            1.0f, -1.0f, 1.0f,  //10
            -1.0f, -1.0f, 1.0f,  //11

            -1.0f, 1.0f, -1.0f,  //12
            1.0f, 1.0f, -1.0f,  //13
            1.0f, -1.0f, -1.0f,  //14
            -1.0f, -1.0f, -1.0f,  //15 P2

            -1.0f, 1.0f, 1.0f,  //16
            1.0f, 1.0f, 1.0f,  //17 P1
            1.0f, -1.0f, 1.0f,  //18
            -1.0f, -1.0f, 1.0f,  //19

            -1.0f, 1.0f, -1.0f,  //20
            1.0f, 1.0f, -1.0f,  //21
            1.0f, -1.0f, -1.0f,  //22
            -1.0f, -1.0f, -1.0f,  //23 P2

            -1.0f, 1.0f, 1.0f,  //24
            1.0f, 1.0f, 1.0f,  //25 P1
            1.0f, -1.0f, 1.0f,  //26
            -1.0f, -1.0f, 1.0f,  //27

            -1.0f, 1.0f, -1.0f,  //28
            1.0f, 1.0f, -1.0f,  //29
            1.0f, -1.0f, -1.0f,  //30
            -1.0f, -1.0f, -1.0f,  //31 P2

            1.0f, 1.0f, 1.0f,  //32 P1
            -1.0f, -1.0f, -1.0f,  //33 P2
            1.0f, 1.0f, 1.0f,  //34 P1
            -1.0f, -1.0f, -1.0f,  //35 P2

        )
        for (i in 0..26) {
            colorSideList.add(CubeRgbColor())
        }

        setSideColors(frontColor, upColor, rightColor, backColor, leftColor, downColor)
        val tfan1 = byteArrayOf(
            1, 0, 3,
            9, 11, 2,
            17, 10, 6,
            25, 14, 5,
            32, 13, 4,  //
            34, 12, 8
        )

        val tfan2 = byteArrayOf(
            7, 20, 21,
            15, 29, 22,
            23, 30, 18,
            31, 26, 19,
            33, 27, 16,
            35, 24, 28
        )


//		for(int i=0; i<36; i++){
//			normalData[3*i] = vertices[3*i];
//			normalData[3*i+1] = vertices[3*i+1];
//			normalData[3*i+2] = vertices[3*i+2];
//		}
//		
//		m_NormalData = makeFloatBuffer(normalData);
        val vbb = ByteBuffer.allocateDirect(vertices.size * 4)
        vbb.order(ByteOrder.nativeOrder())
        mFVertexBuffer = vbb.asFloatBuffer()
        mFVertexBuffer.put(vertices)
        mFVertexBuffer.position(0)

        mTfan1 = ByteBuffer.allocateDirect(tfan1.size)
        mTfan1.put(tfan1)
        mTfan1.position(0)

        mTfan2 = ByteBuffer.allocateDirect(tfan2.size)
        mTfan2.put(tfan2)
        mTfan2.position(0)
    }

    fun getFrontSide(): ColorLetter {
        return colorSideList[0].colorLetter
    }

    fun getUpperSide(): ColorLetter {
        return colorSideList[1].colorLetter
    }

    fun getRightSide(): ColorLetter {
        return colorSideList[2].colorLetter
    }

    fun getBackSide(): ColorLetter {
        return colorSideList[3].colorLetter
    }

    fun getLeftSide(): ColorLetter {
        return colorSideList[4].colorLetter
    }

    fun getDownSide(): ColorLetter {
        return colorSideList[5].colorLetter
    }

    fun setfront(cor: ColorLetter) {
        setSideColors(
            cor,
            this.colorSideList[1].colorLetter,
            this.colorSideList[2].colorLetter,
            this.colorSideList[3].colorLetter,
            this.colorSideList[4].colorLetter,
            this.colorSideList[5].colorLetter
        )
        this.colorSideList[0].colorLetter = cor
    }

    fun setup(cor: ColorLetter) {
        setSideColors(
            this.colorSideList[0].colorLetter,
            cor,
            this.colorSideList[2].colorLetter,
            this.colorSideList[3].colorLetter,
            this.colorSideList[4].colorLetter,
            this.colorSideList[5].colorLetter
        )
        this.colorSideList[1].colorLetter = cor
    }

    fun setright(cor: ColorLetter) {
        setSideColors(
            this.colorSideList[0].colorLetter,
            this.colorSideList[1].colorLetter,
            cor,
            this.colorSideList[3].colorLetter,
            this.colorSideList[4].colorLetter,
            this.colorSideList[5].colorLetter
        )
        this.colorSideList[2].colorLetter = cor
    }

    fun setback(cor: ColorLetter) {
        setSideColors(
            this.colorSideList[0].colorLetter,
            this.colorSideList[1].colorLetter,
            this.colorSideList[2].colorLetter,
            cor,
            this.colorSideList[4].colorLetter,
            this.colorSideList[5].colorLetter
        )
        this.colorSideList[3].colorLetter = cor
    }

    fun setleft(cor: ColorLetter) {
        setSideColors(
            this.colorSideList[0].colorLetter,
            this.colorSideList[1].colorLetter,
            this.colorSideList[2].colorLetter,
            this.colorSideList[3].colorLetter,
            cor,
            this.colorSideList[5].colorLetter
        )
        this.colorSideList[4].colorLetter = cor
    }

    fun setdown(cor: ColorLetter) {
        setSideColors(
            this.colorSideList[0].colorLetter,
            this.colorSideList[1].colorLetter,
            this.colorSideList[2].colorLetter,
            this.colorSideList[3].colorLetter,
            this.colorSideList[4].colorLetter,
            cor
        )
        this.colorSideList[5].colorLetter = cor
    }

    fun draw(gl: GL10) {
        gl.glFrontFace(GL10.GL_CCW)
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mFVertexBuffer)
        gl.glColorPointer(4, GL10.GL_UNSIGNED_BYTE, 0, mColorBuffer)


//			gl.glNormalPointer(GL10.GL_FLOAT, 0, m_NormalData);
//			gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
        gl.glDrawElements(GL10.GL_TRIANGLE_FAN, 6 * 3, GL10.GL_UNSIGNED_BYTE, mTfan1)
        gl.glDrawElements(GL10.GL_TRIANGLE_FAN, 6 * 3, GL10.GL_UNSIGNED_BYTE, mTfan2)
        gl.glFrontFace(GL10.GL_CCW)
    }

    companion object {
        fun makeFloatBuffer(arr: FloatArray): FloatBuffer {
            val bb = ByteBuffer.allocateDirect(arr.size * 4)
            bb.order(ByteOrder.nativeOrder())
            val fb = bb.asFloatBuffer()
            fb.put(arr)
            fb.position(0)
            return fb
        }
    }
}
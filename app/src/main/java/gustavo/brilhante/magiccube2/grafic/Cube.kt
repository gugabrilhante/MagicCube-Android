package gustavo.brilhante.magiccube2.grafic

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10

class Cube(cor1: Char, cor2: Char, cor3: Char, cor4: Char, cor5: Char, cor6: Char) {
    var mFVertexBuffer: FloatBuffer
    var mColorBuffer: ByteBuffer? = null
    var mTfan1: ByteBuffer
    var mTfan2: ByteBuffer
    var cor: ArrayList<CubeRgbColor> = ArrayList()

    private val colorMap = mapOf(
        'R' to 0xFFFF0000.toInt(), // Red
        'Y' to 0xFFFFFF00.toInt(), // Yellow
        'B' to 0xFF0000FF.toInt(), // Blue
        'G' to 0xFF00FF00.toInt(), // Green
        'W' to 0xFFFFFFFF.toInt(), // White
        'O' to 0xFFFFA500.toInt(), // Orange
        'K' to 0xFF000000.toInt()  // Black
    )

    //float[] normalData = new float[108];
    //FloatBuffer m_NormalData;
    fun colors(cor1: Char, cor2: Char, cor3: Char, cor4: Char, cor5: Char, cor6: Char) {
        val maxColor = 255.toByte()

        cor[0].Letra = cor1
        cor[1].Letra = cor2
        cor[2].Letra = cor3
        cor[3].Letra = cor4
        cor[4].Letra = cor5
        cor[5].Letra = cor6

        //RED (R)   : (byte) 130,0,0,maxColor
        //YELLOW (Y) : maxColor,maxColor,0,maxColor
        //BLUE  (B) : 0,0,maxColor,maxColor 
        //GREEN (G) : 0,maxColor,0,maxColor
        //WHITE (W) : maxColor,maxColor,maxColor,maxColor
        //ORANGE (O) : maxColor,69,0,maxColor
        //BLACK (K) : 0,0,0,maxColor
        for (i in cor.indices) {
            applyColor(cor[i])
        }

        val colors = byteArrayOf(
            cor[0].v1, cor[0].v2, cor[0].v3, cor[0].v4,  //0
            cor[0].v1, cor[0].v2, cor[0].v3, cor[0].v4,  //1
            cor[0].v1, cor[0].v2, cor[0].v3, cor[0].v4,  //2
            cor[0].v1, cor[0].v2, cor[0].v3, cor[0].v4,  //3

            cor[1].v1, cor[1].v2, cor[1].v3, cor[1].v4,  //4
            cor[2].v1, cor[2].v2, cor[2].v3, cor[2].v4,  //5
            cor[2].v1, cor[2].v2, cor[2].v3, cor[2].v4,  //6
            cor[3].v1, cor[3].v2, cor[3].v3, cor[3].v4,  //7

            cor[1].v1, cor[1].v2, cor[1].v3, cor[1].v4,  //8
            cor[0].v1, cor[0].v2, cor[0].v3, cor[0].v4,  //9
            cor[2].v1, cor[2].v2, cor[2].v3, cor[2].v4,  //10
            cor[0].v1, cor[0].v2, cor[0].v3, cor[0].v4,  //11

            cor[1].v1, cor[1].v2, cor[1].v3, cor[1].v4,  //12	
            cor[1].v1, cor[1].v2, cor[1].v3, cor[1].v4,  //13
            cor[2].v1, cor[2].v2, cor[2].v3, cor[2].v4,  //14
            cor[3].v1, cor[3].v2, cor[3].v3, cor[3].v4,  //15

            cor[4].v1, cor[4].v2, cor[4].v3, cor[4].v4,  //16
            cor[2].v1, cor[2].v2, cor[2].v3, cor[2].v4,  //17
            cor[5].v1, cor[5].v2, cor[5].v3, cor[5].v4,  //18
            cor[5].v1, cor[5].v2, cor[5].v3, cor[5].v4,  //19

            cor[3].v1, cor[3].v2, cor[3].v3, cor[3].v4,  //20
            cor[3].v1, cor[3].v2, cor[3].v3, cor[3].v4,  //21
            cor[3].v1, cor[3].v2, cor[3].v3, cor[3].v4,  //22
            cor[5].v1, cor[5].v2, cor[5].v3, cor[5].v4,  //23

            cor[4].v1, cor[4].v2, cor[4].v3, cor[4].v4,  //24
            cor[2].v1, cor[2].v2, cor[2].v3, cor[2].v4,  //25
            cor[5].v1, cor[5].v2, cor[5].v3, cor[5].v4,  //26
            cor[4].v1, cor[4].v2, cor[4].v3, cor[4].v4,  //27

            cor[4].v1, cor[4].v2, cor[4].v3, cor[4].v4,  //28
            cor[3].v1, cor[3].v2, cor[3].v3, cor[3].v4,  //29
            cor[5].v1, cor[5].v2, cor[5].v3, cor[5].v4,  //30
            cor[5].v1, cor[5].v2, cor[5].v3, cor[5].v4,  //31

            cor[1].v1, cor[1].v2, cor[1].v3, cor[1].v4,  //32
            cor[4].v1, cor[4].v2, cor[4].v3, cor[4].v4,  //33
            cor[1].v1, cor[1].v2, cor[1].v3, cor[1].v4,  //34
            cor[4].v1, cor[4].v2, cor[4].v3, cor[4].v4,  //35

        )
        mColorBuffer = ByteBuffer.allocateDirect(colors.size)
        mColorBuffer?.put(colors)
        mColorBuffer?.position(0)
    }

    fun applyColor(cor: CubeRgbColor) {
        val argb = colorMap[cor.Letra] ?: 0xFF000000.toInt() // padrão = preto

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
            cor.add(CubeRgbColor())
        }

        colors(cor1, cor2, cor3, cor4, cor5, cor6)
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

    fun getfront(): Char {
        return cor[0].Letra
    }

    fun getup(): Char {
        return cor[1].Letra
    }

    fun getright(): Char {
        return cor[2].Letra
    }

    fun getback(): Char {
        return cor[3].Letra
    }

    fun getleft(): Char {
        return cor[4].Letra
    }

    fun getdown(): Char {
        return cor[5].Letra
    }

    fun setfront(cor: Char) {
        colors(cor, this.cor[1].Letra, this.cor[2].Letra, this.cor[3].Letra, this.cor[4].Letra, this.cor[5].Letra)
        this.cor[0].Letra = cor
    }

    fun setup(cor: Char) {
        colors(this.cor[0].Letra, cor, this.cor[2].Letra, this.cor[3].Letra, this.cor[4].Letra, this.cor[5].Letra)
        this.cor[1].Letra = cor
    }

    fun setright(cor: Char) {
        colors(this.cor[0].Letra, this.cor[1].Letra, cor, this.cor[3].Letra, this.cor[4].Letra, this.cor[5].Letra)
        this.cor[2].Letra = cor
    }

    fun setback(cor: Char) {
        colors(this.cor[0].Letra, this.cor[1].Letra, this.cor[2].Letra, cor, this.cor[4].Letra, this.cor[5].Letra)
        this.cor[3].Letra = cor
    }

    fun setleft(cor: Char) {
        colors(this.cor[0].Letra, this.cor[1].Letra, this.cor[2].Letra, this.cor[3].Letra, cor, this.cor[5].Letra)
        this.cor[4].Letra = cor
    }

    fun setdown(cor: Char) {
        colors(this.cor[0].Letra, this.cor[1].Letra, this.cor[2].Letra, this.cor[3].Letra, this.cor[4].Letra, cor)
        this.cor[5].Letra = cor
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
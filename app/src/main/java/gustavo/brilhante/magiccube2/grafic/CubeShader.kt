package gustavo.brilhante.magiccube2.grafic

import android.opengl.GLES30

object CubeShader {

    private const val VERTEX_SHADER = """#version 300 es
        uniform mat4 uMVPMatrix;
        in vec4 aPosition;
        in vec4 aColor;
        flat out vec4 vColor;
        void main() {
            gl_Position = uMVPMatrix * aPosition;
            vColor = aColor;
        }
    """

    private const val FRAGMENT_SHADER = """#version 300 es
        precision mediump float;
        flat in vec4 vColor;
        out vec4 fragColor;
        void main() {
            fragColor = vColor;
        }
    """

    var programHandle: Int = -1
    var positionHandle: Int = -1
    var colorHandle: Int = -1
    var mvpMatrixHandle: Int = -1

    fun init() {
        val vertexShader = compileShader(GLES30.GL_VERTEX_SHADER, VERTEX_SHADER)
        val fragmentShader = compileShader(GLES30.GL_FRAGMENT_SHADER, FRAGMENT_SHADER)

        programHandle = GLES30.glCreateProgram()
        GLES30.glAttachShader(programHandle, vertexShader)
        GLES30.glAttachShader(programHandle, fragmentShader)
        GLES30.glLinkProgram(programHandle)

        positionHandle = GLES30.glGetAttribLocation(programHandle, "aPosition")
        colorHandle = GLES30.glGetAttribLocation(programHandle, "aColor")
        mvpMatrixHandle = GLES30.glGetUniformLocation(programHandle, "uMVPMatrix")
    }

    private fun compileShader(type: Int, source: String): Int {
        val shader = GLES30.glCreateShader(type)
        GLES30.glShaderSource(shader, source)
        GLES30.glCompileShader(shader)
        return shader
    }
}

package gustavo.brilhante.magiccube2.grafic

import android.opengl.GLES30
import android.opengl.GLSurfaceView
import gustavo.brilhante.magiccube2.presentation.cube.CubeViewModel
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CubeRenderer(
    private val viewModel: CubeViewModel
) : GLSurfaceView.Renderer {

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        GLES30.glClearColor(0f, 0.5f, 0.5f, 1f)
        GLES30.glEnable(GLES30.GL_CULL_FACE)
        GLES30.glCullFace(GLES30.GL_BACK)
        GLES30.glFrontFace(GLES30.GL_CCW)
        GLES30.glEnable(GLES30.GL_DEPTH_TEST)
        GLES30.glDepthFunc(GLES30.GL_LEQUAL)
        CubeShader.init()
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        viewModel.onSurfaceChanged(width, height)
    }

    override fun onDrawFrame(unused: GL10) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)
        GLES30.glUseProgram(CubeShader.programHandle)

        viewModel.buildFrame()

        for (command in viewModel.uiState.value.drawCommands) {
            command.cube.draw(command.mvpMatrix)
        }

        viewModel.advanceFrame()
    }
}

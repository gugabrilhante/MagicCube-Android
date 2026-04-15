package gustavo.brilhante.magiccube2.grafic

import android.opengl.GLES30
import android.opengl.GLSurfaceView
import gustavo.brilhante.magiccube2.presentation.cube.CubeViewModel
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL1

class CubeRenderer(
    private val viewModel: CubeViewModel
) : GLSurfaceView.Renderer {

    private val pickingService = PickingService()

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

        for (command in viewModel.renderState.value.drawCommands) {
            command.cube.draw(command.mvpMatrix)
        }

        viewModel.advanceFrame()
    }

    /**
     * Performs ray picking to identify which cubelet and face were touched.
     * Called from CubeSurfaceView on ACTION_DOWN.
     */
    fun handleTouchPicking(x: Float, y: Float, width: Int, height: Int) {
        val drawCommands = viewModel.renderState.value.drawCommands
        if (drawCommands.isEmpty()) return

        val result = pickingService.pickCubelet(x, y, width, height, drawCommands)
        if (result != null) {
            android.util.Log.d("CubePicking","")
            android.util.Log.d("CubePicking", "Cubelet touched: ${result.cubelet}, Normal: ${result.faceNormal}")
            android.util.Log.d("CubePicking", "FrontSide: ${result.cubelet.getFrontSide()}, BackSide: ${result.cubelet.getBackSide()}")
            android.util.Log.d("CubePicking", "UpperSide: ${result.cubelet.getUpperSide()}, DownSide: ${result.cubelet.getDownSide()}")
            android.util.Log.d("CubePicking", "LeftSide: ${result.cubelet.getLeftSide()}, RightSide: ${result.cubelet.getRightSide()}")
        }
    }
}

package gustavo.brilhante.magiccube2.grafic

import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import gustavo.brilhante.magiccube2.presentation.cube.CubeViewModel
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CubeRenderer(
    private val viewModel: CubeViewModel
) : GLSurfaceView.Renderer {

    private val engine: CubeGameEngine get() = viewModel.engine

    private val projectionMatrix = FloatArray(16)
    private val matrixTracker = MatrixTracker()
    private val glRenderPosition = RenderPosition()
    private val dist = 2.12f

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
        val zNear = 0.1f
        val zFar = 1000f
        val fov = 80.0f / 57.3f
        val size = zNear * Math.tan((fov / 2.0)).toFloat()
        val aspectRatio = width.toFloat() / height
        Matrix.frustumM(projectionMatrix, 0, -size, size, -size / aspectRatio, size / aspectRatio, zNear, zFar)
    }

    override fun onDrawFrame(unused: GL10) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)
        GLES30.glUseProgram(CubeShader.programHandle)

        matrixTracker.reset()
        glRenderPosition.reset()

        val cameraZ = (-20 + viewModel.settings.size).toFloat()
        matrixTracker.translate(0f, 0f, cameraZ)

        viewModel.tickInertia()

        matrixTracker.rotate(viewModel.angleRotateX, 0f, 1f, 0f)
        matrixTracker.rotate(viewModel.angleRotateY, 1f, 0f, 0f)

        engine.prepareFrameRotation()

        var sinal = 1
        var indexAxisZ = 0

        while (indexAxisZ < 3) {
            if (engine.rot == 0 && indexAxisZ == 0) rotateAround(engine.rotatedAngle, CubeAxis.Y)
            if (engine.rot == 6 && indexAxisZ == 1) rotateAround(engine.rotatedAngle, CubeAxis.Y)
            if (engine.rot == 1 && indexAxisZ == 2) rotateAround(engine.rotatedAngle, CubeAxis.Y)

            val direction = if (sinal > 0) CubeStepDirection.UP else CubeStepDirection.DOWN
            moveOnAxis(1, CubeAxis.Y, direction)
            if (indexAxisZ > 0) {
                moveOnAxis(1, CubeAxis.X, CubeStepDirection.LEFT)
                moveOnAxis(1, CubeAxis.Z, CubeStepDirection.BACK)
            }
            moveOnAxis(2, CubeAxis.Z, CubeStepDirection.BACK)
            moveOnAxis(1, CubeAxis.X, CubeStepDirection.RIGHT)

            var indexAxisY = 0
            while (indexAxisY < 3) {
                if (engine.rot == 2 && indexAxisY == 0) rotateAround(engine.rotatedAngle, CubeAxis.Z)
                if (engine.rot == 7 && indexAxisY == 1) rotateAround(engine.rotatedAngle, CubeAxis.Z)
                if (engine.rot == 3 && indexAxisY == 2) rotateAround(engine.rotatedAngle, CubeAxis.Z)

                moveOnAxis(1, CubeAxis.Z, CubeStepDirection.FORWARD)
                moveOnAxis(3, CubeAxis.X, CubeStepDirection.LEFT)

                var indexAxisX = 0
                while (indexAxisX < 3) {
                    if (engine.rot == 4 && indexAxisX == 0) rotateAround(engine.rotatedAngle, CubeAxis.X)
                    if (engine.rot == 8 && indexAxisX == 1) rotateAround(engine.rotatedAngle, CubeAxis.X)
                    if (engine.rot == 5 && indexAxisX == 2) rotateAround(engine.rotatedAngle, CubeAxis.X)

                    moveOnAxis(1, CubeAxis.X, CubeStepDirection.RIGHT)

                    val cubeIndex = engine.pos[indexAxisX][indexAxisZ][indexAxisY]
                    engine.cubeList[cubeIndex].draw(computeMVP())

                    if (viewModel.isInertiaActive) {
                        engine.cubeSideIndex.forEachIndexed { idx, entry ->
                            if (cubeIndex == entry.first) {
                                engine.cubeSide[idx].z = -matrixTracker.getZ()
                                engine.cubeSide[idx].y = matrixTracker.getY()
                                engine.cubeSide[idx].x = matrixTracker.getX()
                            }
                        }
                    }

                    if (engine.rot == 5 && indexAxisX == 2) rotateAround(-engine.rotatedAngle, CubeAxis.X)
                    if (engine.rot == 8 && indexAxisX == 1) rotateAround(-engine.rotatedAngle, CubeAxis.X)
                    if (engine.rot == 4 && indexAxisX == 0) rotateAround(-engine.rotatedAngle, CubeAxis.X)

                    indexAxisX++
                }

                if (engine.rot == 3 && indexAxisY == 2) rotateAround(-engine.rotatedAngle, CubeAxis.Z)
                if (engine.rot == 7 && indexAxisY == 1) rotateAround(-engine.rotatedAngle, CubeAxis.Z)
                if (engine.rot == 2 && indexAxisY == 0) rotateAround(-engine.rotatedAngle, CubeAxis.Z)

                indexAxisY++
            }

            if (indexAxisZ == 0) sinal = -sinal

            if (engine.rot == 1 && indexAxisZ == 2) rotateAround(-engine.rotatedAngle, CubeAxis.Y)
            if (engine.rot == 6 && indexAxisZ == 1) rotateAround(-engine.rotatedAngle, CubeAxis.Y)
            if (engine.rot == 0 && indexAxisZ == 0) rotateAround(-engine.rotatedAngle, CubeAxis.Y)

            indexAxisZ++
        }

        engine.postFrameAdvance()
    }

    // --- Matrix helpers ---

    private fun computeMVP(): FloatArray {
        val mvp = FloatArray(16)
        Matrix.multiplyMM(mvp, 0, projectionMatrix, 0, matrixTracker.getMatrix(), 0)
        return mvp
    }

    private fun moveOnAxis(steps: Int, axis: CubeAxis, direction: CubeStepDirection) {
        val d = steps * dist * direction.orientation
        when (axis) {
            CubeAxis.X -> translate(d, 0f, 0f)
            CubeAxis.Y -> translate(0f, d, 0f)
            CubeAxis.Z -> translate(0f, 0f, d)
        }
    }

    private fun translate(x: Float, y: Float, z: Float) {
        matrixTracker.translate(x, y, z)
        glRenderPosition.x += x
        glRenderPosition.y += y
        glRenderPosition.z += z
    }

    private fun rotateAround(angle: Float, axis: CubeAxis) {
        matrixTracker.translate(-glRenderPosition.x, -glRenderPosition.y, -glRenderPosition.z)
        when (axis) {
            CubeAxis.X -> matrixTracker.rotate(angle, 1f, 0f, 0f)
            CubeAxis.Y -> matrixTracker.rotate(angle, 0f, 1f, 0f)
            CubeAxis.Z -> matrixTracker.rotate(angle, 0f, 0f, 1f)
        }
        matrixTracker.translate(glRenderPosition.x, glRenderPosition.y, glRenderPosition.z)
    }
}

private data class RenderPosition(var x: Float = 0f, var y: Float = 0f, var z: Float = 0f) {
    fun reset() { x = 0f; y = 0f; z = 0f }
}

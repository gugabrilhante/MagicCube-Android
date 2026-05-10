package gustavo.brilhante.magiccube2.presentation.cube.engine

import gustavo.brilhante.magiccube2.domain.CubeSettings
import gustavo.brilhante.magiccube2.grafic.ActiveSlice
import gustavo.brilhante.magiccube2.grafic.CubeAxis
import gustavo.brilhante.magiccube2.grafic.CubeStepDirection
import gustavo.brilhante.magiccube2.grafic.ICubeGameEngine
import gustavo.brilhante.magiccube2.grafic.IMatrixTracker
import gustavo.brilhante.magiccube2.domain.math.MatrixMath
import gustavo.brilhante.magiccube2.presentation.cube.CubeDrawCommand

class CubeTraversalEngine(
    private val matrixTracker: IMatrixTracker,
    private val matrixMath: MatrixMath
) : ICubeTraversalEngine {

    private val renderPosition = RenderPosition()
    private val dist = 2.12f

    override fun buildFrame(
        engine: ICubeGameEngine,
        settings: CubeSettings,
        rotationState: CubeRotationState,
        projectionMatrix: FloatArray
    ): List<CubeDrawCommand> {
        matrixTracker.reset()
        renderPosition.reset()

        matrixTracker.translate(0f, 0f, (-20 + settings.size).toFloat())

        matrixTracker.rotate(rotationState.angleX, 0f, 1f, 0f)
        matrixTracker.rotate(rotationState.angleY, 1f, 0f, 0f)

        engine.prepareFrameRotation()

        val rotState = engine.rotation
        val commands = mutableListOf<CubeDrawCommand>()

        var sinal = 1
        var indexAxisZ = 0

        while (indexAxisZ < 3) {
            if (rotState.activeSlice == ActiveSlice.ROTATION_AXIS_Z_0 && indexAxisZ == 0) rotateAround(engine.rotatedAngle, CubeAxis.Y)
            if (rotState.activeSlice == ActiveSlice.ROTATION_AXIS_Z_1 && indexAxisZ == 1) rotateAround(engine.rotatedAngle, CubeAxis.Y)
            if (rotState.activeSlice == ActiveSlice.ROTATION_AXIS_Z_2 && indexAxisZ == 2) rotateAround(engine.rotatedAngle, CubeAxis.Y)

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
                if (rotState.activeSlice == ActiveSlice.ROTATION_AXIS_Y_0 && indexAxisY == 0) rotateAround(engine.rotatedAngle, CubeAxis.Z)
                if (rotState.activeSlice == ActiveSlice.ROTATION_AXIS_Y_1 && indexAxisY == 1) rotateAround(engine.rotatedAngle, CubeAxis.Z)
                if (rotState.activeSlice == ActiveSlice.ROTATION_AXIS_Y_2 && indexAxisY == 2) rotateAround(engine.rotatedAngle, CubeAxis.Z)

                moveOnAxis(1, CubeAxis.Z, CubeStepDirection.FORWARD)
                moveOnAxis(3, CubeAxis.X, CubeStepDirection.LEFT)

                var indexAxisX = 0
                while (indexAxisX < 3) {
                    if (rotState.activeSlice == ActiveSlice.ROTATION_AXIS_X_0 && indexAxisX == 0) rotateAround(engine.rotatedAngle, CubeAxis.X)
                    if (rotState.activeSlice == ActiveSlice.ROTATION_AXIS_X_1 && indexAxisX == 1) rotateAround(engine.rotatedAngle, CubeAxis.X)
                    if (rotState.activeSlice == ActiveSlice.ROTATION_AXIS_X_2 && indexAxisX == 2) rotateAround(engine.rotatedAngle, CubeAxis.X)

                    moveOnAxis(1, CubeAxis.X, CubeStepDirection.RIGHT)

                    val cubeIndex = engine.cubeGrid[indexAxisX][indexAxisZ][indexAxisY]
                    commands.add(CubeDrawCommand(engine.cubes[cubeIndex], computeMVP(projectionMatrix)))

                    if (rotationState.isInertiaActive) {
                        engine.faceCenterCubes.forEachIndexed { idx, entry ->
                            if (cubeIndex == entry.first) {
                                engine.faceCenterPositions[idx].z = -matrixTracker.getZ()
                                engine.faceCenterPositions[idx].y = matrixTracker.getY()
                                engine.faceCenterPositions[idx].x = matrixTracker.getX()
                            }
                        }
                    }

                    if (rotState.activeSlice == ActiveSlice.ROTATION_AXIS_X_2 && indexAxisX == 2) rotateAround(-engine.rotatedAngle, CubeAxis.X)
                    if (rotState.activeSlice == ActiveSlice.ROTATION_AXIS_X_1 && indexAxisX == 1) rotateAround(-engine.rotatedAngle, CubeAxis.X)
                    if (rotState.activeSlice == ActiveSlice.ROTATION_AXIS_X_0 && indexAxisX == 0) rotateAround(-engine.rotatedAngle, CubeAxis.X)

                    indexAxisX++
                }

                if (rotState.activeSlice == ActiveSlice.ROTATION_AXIS_Y_2 && indexAxisY == 2) rotateAround(-engine.rotatedAngle, CubeAxis.Z)
                if (rotState.activeSlice == ActiveSlice.ROTATION_AXIS_Y_1 && indexAxisY == 1) rotateAround(-engine.rotatedAngle, CubeAxis.Z)
                if (rotState.activeSlice == ActiveSlice.ROTATION_AXIS_Y_0 && indexAxisY == 0) rotateAround(-engine.rotatedAngle, CubeAxis.Z)

                indexAxisY++
            }

            if (indexAxisZ == 0) sinal = -sinal

            if (rotState.activeSlice == ActiveSlice.ROTATION_AXIS_Z_2 && indexAxisZ == 2) rotateAround(-engine.rotatedAngle, CubeAxis.Y)
            if (rotState.activeSlice == ActiveSlice.ROTATION_AXIS_Z_1 && indexAxisZ == 1) rotateAround(-engine.rotatedAngle, CubeAxis.Y)
            if (rotState.activeSlice == ActiveSlice.ROTATION_AXIS_Z_0 && indexAxisZ == 0) rotateAround(-engine.rotatedAngle, CubeAxis.Y)

            indexAxisZ++
        }

        return commands
    }

    private fun computeMVP(projectionMatrix: FloatArray): FloatArray {
        val mvp = FloatArray(16)
        matrixMath.multiplyMM(mvp, 0, projectionMatrix, 0, matrixTracker.getMatrix(), 0)
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
        renderPosition.x += x
        renderPosition.y += y
        renderPosition.z += z
    }

    private fun rotateAround(angle: Float, axis: CubeAxis) {
        matrixTracker.translate(-renderPosition.x, -renderPosition.y, -renderPosition.z)
        when (axis) {
            CubeAxis.X -> matrixTracker.rotate(angle, 1f, 0f, 0f)
            CubeAxis.Y -> matrixTracker.rotate(angle, 0f, 1f, 0f)
            CubeAxis.Z -> matrixTracker.rotate(angle, 0f, 0f, 1f)
        }
        matrixTracker.translate(renderPosition.x, renderPosition.y, renderPosition.z)
    }

    private data class RenderPosition(var x: Float = 0f, var y: Float = 0f, var z: Float = 0f) {
        fun reset() { x = 0f; y = 0f; z = 0f }
    }
}

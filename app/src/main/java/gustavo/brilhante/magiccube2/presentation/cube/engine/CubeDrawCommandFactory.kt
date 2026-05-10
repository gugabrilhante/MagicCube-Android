package gustavo.brilhante.magiccube2.presentation.cube.engine

import gustavo.brilhante.magiccube2.domain.math.MatrixMath
import gustavo.brilhante.magiccube2.grafic.Cube
import gustavo.brilhante.magiccube2.grafic.IMatrixTracker
import gustavo.brilhante.magiccube2.presentation.cube.CubeDrawCommand

class CubeDrawCommandFactory(
    private val matrixMath: MatrixMath
) {
    fun createCommand(cube: Cube, projectionMatrix: FloatArray, matrixTracker: IMatrixTracker): CubeDrawCommand {
        val mvp = FloatArray(16)
        matrixMath.multiplyMM(mvp, 0, projectionMatrix, 0, matrixTracker.getMatrix(), 0)
        return CubeDrawCommand(cube, mvp)
    }
}

package gustavo.brilhante.magiccube2.testutil

import gustavo.brilhante.magiccube2.domain.cube.CubeLogger

class NoOpCubeLogger : CubeLogger {
    override fun d(tag: String, message: String) = Unit
}

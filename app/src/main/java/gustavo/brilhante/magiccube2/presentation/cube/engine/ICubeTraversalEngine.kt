package gustavo.brilhante.magiccube2.presentation.cube.engine

import gustavo.brilhante.magiccube2.domain.CubeSettings
import gustavo.brilhante.magiccube2.grafic.ICubeGameEngine
import gustavo.brilhante.magiccube2.presentation.cube.CubeDrawCommand

interface ICubeTraversalEngine {
    fun buildFrame(
        engine: ICubeGameEngine,
        settings: CubeSettings,
        rotationState: CubeRotationState,
        projectionMatrix: FloatArray
    ): List<CubeDrawCommand>
}

package gustavo.brilhante.magiccube2.presentation.cube

import gustavo.brilhante.magiccube2.grafic.Cube

data class CubeRenderState(
    val drawCommands: List<CubeDrawCommand> = emptyList()
)

data class CubeDrawCommand(
    val cube: Cube,
    val mvpMatrix: FloatArray
)

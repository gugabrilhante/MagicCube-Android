package gustavo.brilhante.magiccube2.presentation.cube

sealed class CubeIntent {

    data class ActionDown(
        val x: Float,
        val y: Float,
        val screenWidth: Int,
        val screenHeight: Int,
    ) : CubeIntent()

    data class ActionMove(
        val x: Float,
        val y: Float,
        val previousX: Float,
        val previousY: Float,
    ) : CubeIntent()

    data class ActionUp(
        val x: Float,
        val y: Float,
    ) : CubeIntent()

    data object ActionCancel : CubeIntent()
}

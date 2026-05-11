package gustavo.brilhante.magiccube2.presentation.cube.engine

data class CubeRotationState(
    val angleX: Float = 0f,
    val angleY: Float = 0f,
    val angleXAux: Float = 0f,
    val angleYAux: Float = 0f,
    val inertiaInc: Float = 5f,
    val isInertiaActive: Boolean = false
)

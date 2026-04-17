package gustavo.brilhante.magiccube2.presentation.cube

import gustavo.brilhante.magiccube2.grafic.ICubeGameEngine

fun interface CubeControllerFactory {
    fun create(engine: ICubeGameEngine): ICubeInteractor
}

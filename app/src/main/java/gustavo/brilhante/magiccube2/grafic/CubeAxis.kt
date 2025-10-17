package gustavo.brilhante.magiccube2.grafic

enum class CubeAxis {
    X, Y, Z
}

enum class CubeStepDirection(val orientation: Int) {
    UP(1), DOWN(-1), LEFT(-1), RIGHT(1), BACK(-1), FORWARD(1)
}
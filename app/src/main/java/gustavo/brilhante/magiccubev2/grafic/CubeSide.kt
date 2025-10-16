package gustavo.brilhante.magiccubev2.grafic

enum class CubeSide(val rotation: Int, val colorName: String, val orientation: Int) {
    YELLOW(0, "Yellow", -1),
    RED(5, "Red", -1),
    BLUE(3, "Blue", -1),
    GREEN(2, "Green", 1),
    ORANGE(4, "Orange", 1),
    WHITE(1, "White", 1);
}
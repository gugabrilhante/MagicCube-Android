package gustavo.brilhante.magiccube2.grafic

class CubeRgbColor {
//    var Letra: Char = 0.toChar()
    var colorLetter: ColorLetter = ColorLetter.BLACK
    var v1: Byte = 0
    var v2: Byte = 0
    var v3: Byte = 0
    var v4: Byte = 0
}

enum class ColorLetter(val letter: Char, val argb: Int) {
    RED('R', 0xFFFF0000.toInt()),
    YELLOW('Y', 0xFFFFFF00.toInt()),
    BLUE('B', 0xFF0000FF.toInt()),
    GREEN('G', 0xFF00FF00.toInt()),
    WHITE('W', 0xFFFFFFFF.toInt()),
    ORANGE('O', 0xFFFFA500.toInt()),
    BLACK('K', 0xFF000000.toInt());

    companion object {
        private val map = values().associateBy(ColorLetter::letter)
        fun fromLetter(letter: Char) = map[letter]
    }
}
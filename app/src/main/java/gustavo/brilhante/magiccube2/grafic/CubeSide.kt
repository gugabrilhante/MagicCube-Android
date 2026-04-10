package gustavo.brilhante.magiccube2.grafic

enum class CubeSide(val rotation: ActiveSlice, val colorName: String, val orientation: Int) {
    YELLOW(ActiveSlice.ROTATION_AXIS_Z_0, "Yellow", -1),
    RED(ActiveSlice.ROTATION_AXIS_X_2,   "Red",    -1),
    BLUE(ActiveSlice.ROTATION_AXIS_Y_2,  "Blue",   -1),
    GREEN(ActiveSlice.ROTATION_AXIS_Y_0, "Green",   1),
    ORANGE(ActiveSlice.ROTATION_AXIS_X_0,"Orange",  1),
    WHITE(ActiveSlice.ROTATION_AXIS_Z_2, "White",   1);
}

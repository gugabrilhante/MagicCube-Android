package gustavo.brilhante.magiccube2.presentation.options

data class OptionsUiState(
    val shuffle: Int = 3,
    val speed: Int = 5,
    val size: Int = 9
) {
    val shuffleDisplay: Int get() = shuffle * 10
}

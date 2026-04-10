package gustavo.brilhante.magiccube2.presentation.options

import androidx.lifecycle.ViewModel
import gustavo.brilhante.magiccube2.data.SettingsRepository
import gustavo.brilhante.magiccube2.domain.CubeSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class OptionsViewModel(private val repository: SettingsRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(
        repository.current.toUiState()
    )
    val uiState: StateFlow<OptionsUiState> = _uiState.asStateFlow()

    fun increaseShuffle() = updateState { it.copy(shuffle = (it.shuffle + 1).coerceAtMost(10)) }
    fun decreaseShuffle() = updateState { it.copy(shuffle = (it.shuffle - 1).coerceAtLeast(1)) }

    fun increaseSpeed() = updateState { it.copy(speed = (it.speed + 1).coerceAtMost(10)) }
    fun decreaseSpeed() = updateState { it.copy(speed = (it.speed - 1).coerceAtLeast(1)) }

    fun increaseSize() = updateState { it.copy(size = (it.size + 1).coerceAtMost(10)) }
    fun decreaseSize() = updateState { it.copy(size = (it.size - 1).coerceAtLeast(1)) }

    private fun updateState(transform: (OptionsUiState) -> OptionsUiState) {
        _uiState.update(transform)
        repository.update(_uiState.value.toSettings())
    }

    private fun CubeSettings.toUiState() = OptionsUiState(shuffle, speed, size)
    private fun OptionsUiState.toSettings() = CubeSettings(shuffle, speed, size)
}

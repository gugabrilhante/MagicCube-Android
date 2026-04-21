package gustavo.brilhante.magiccube2.presentation.options

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gustavo.brilhante.magiccube2.domain.CubeSettings
import gustavo.brilhante.magiccube2.domain.usecase.ObserveSettingsUseCase
import gustavo.brilhante.magiccube2.domain.usecase.SaveSettingsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OptionsViewModel(
    private val saveSettings: SaveSettingsUseCase,
    observeSettings: ObserveSettingsUseCase,
) : ViewModel() {

    val uiState: StateFlow<OptionsUiState> = observeSettings()
        .map { it.toUiState() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, OptionsUiState())

    fun increaseShuffle() = updateState { it.copy(shuffle = (it.shuffle + 1).coerceAtMost(10)) }
    fun decreaseShuffle() = updateState { it.copy(shuffle = (it.shuffle - 1).coerceAtLeast(0)) }
    fun setShuffle(value: Int) = updateState { it.copy(shuffle = value.coerceIn(0, 10)) }

    fun increaseSpeed() = updateState { it.copy(speed = (it.speed + 1).coerceAtMost(10)) }
    fun decreaseSpeed() = updateState { it.copy(speed = (it.speed - 1).coerceAtLeast(1)) }
    fun setSpeed(value: Int) = updateState { it.copy(speed = value.coerceIn(1, 10)) }

    fun increaseSize() = updateState { it.copy(size = (it.size + 1).coerceAtMost(10)) }
    fun decreaseSize() = updateState { it.copy(size = (it.size - 1).coerceAtLeast(1)) }
    fun setSize(value: Int) = updateState { it.copy(size = value.coerceIn(1, 10)) }

    fun resetSettings() = updateState { OptionsUiState() }

    private fun updateState(transform: (OptionsUiState) -> OptionsUiState) {
        viewModelScope.launch {
            saveSettings(transform(uiState.value).toSettings())
        }
    }

    private fun CubeSettings.toUiState() = OptionsUiState(shuffle, speed, size)
    private fun OptionsUiState.toSettings() = CubeSettings(shuffle, speed, size)
}

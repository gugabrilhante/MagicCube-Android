package gustavo.brilhante.magiccube2.presentation.options

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gustavo.brilhante.magiccube2.domain.CubeSettings
import gustavo.brilhante.magiccube2.domain.usecase.ObserveSettingsUseCase
import gustavo.brilhante.magiccube2.domain.usecase.SaveSettingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OptionsViewModel(
    private val saveSettings: SaveSettingsUseCase,
    observeSettings: ObserveSettingsUseCase,
) : ViewModel() {

    private val localUpdates = MutableStateFlow<OptionsUiState?>(null)

    val uiState: StateFlow<OptionsUiState> = combine(
        observeSettings().map { it.toUiState() },
        localUpdates
    ) { remote, local ->
        local ?: remote
    }.stateIn(viewModelScope, SharingStarted.Eagerly, OptionsUiState())

    init {
        viewModelScope.launch {
            uiState.collectLatest { state ->
                saveSettings(state.toSettings())
            }
        }
    }

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
        localUpdates.update { current ->
            transform(current ?: uiState.value)
        }
    }

    private fun CubeSettings.toUiState() = OptionsUiState(shuffle, speed, size)
    private fun OptionsUiState.toSettings() = CubeSettings(shuffle, speed, size)
}

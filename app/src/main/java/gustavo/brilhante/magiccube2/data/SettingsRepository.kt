package gustavo.brilhante.magiccube2.data

import gustavo.brilhante.magiccube2.domain.CubeSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository {

    private val _settings = MutableStateFlow(CubeSettings())
    val settings: StateFlow<CubeSettings> = _settings.asStateFlow()

    val current: CubeSettings get() = _settings.value

    fun update(settings: CubeSettings) {
        _settings.value = settings
    }
}

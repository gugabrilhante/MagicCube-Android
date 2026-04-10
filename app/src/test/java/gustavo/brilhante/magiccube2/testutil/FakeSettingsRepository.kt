package gustavo.brilhante.magiccube2.testutil

import gustavo.brilhante.magiccube2.domain.CubeSettings
import gustavo.brilhante.magiccube2.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeSettingsRepository(
    initial: CubeSettings = CubeSettings()
) : SettingsRepository {

    private val _settings = MutableStateFlow(initial)

    override val settingsFlow: StateFlow<CubeSettings> = _settings.asStateFlow()
    override val current: CubeSettings get() = _settings.value

    var lastSaved: CubeSettings? = null

    override suspend fun save(settings: CubeSettings) {
        lastSaved = settings
        _settings.value = settings
    }

    fun emit(settings: CubeSettings) {
        _settings.value = settings
    }
}

package gustavo.brilhante.magiccube2.data

import gustavo.brilhante.magiccube2.domain.CubeSettings
import gustavo.brilhante.magiccube2.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first

class SettingsRepositoryImpl(private val dataSource: SettingsLocalDataSource) : SettingsRepository {

    private val _settings = MutableStateFlow(CubeSettings())

    override val settingsFlow: Flow<CubeSettings> = _settings.asStateFlow()
    override val current: CubeSettings get() = _settings.value

    override suspend fun load() {
        _settings.value = dataSource.settingsFlow.first()
    }

    override suspend fun save(settings: CubeSettings) {
        _settings.value = settings
        dataSource.save(settings)
    }
}

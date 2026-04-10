package gustavo.brilhante.magiccube2.data

import gustavo.brilhante.magiccube2.domain.CubeSettings
import gustavo.brilhante.magiccube2.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class SettingsRepositoryImpl(
    private val dataSource: SettingsLocalDataSource,
    scope: CoroutineScope
) : SettingsRepository {

    override val settingsFlow: StateFlow<CubeSettings> = dataSource.settingsFlow
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CubeSettings()
        )

    override val current: CubeSettings get() = settingsFlow.value

    override suspend fun save(settings: CubeSettings) {
        dataSource.save(settings)
    }
}

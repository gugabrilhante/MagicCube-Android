package gustavo.brilhante.magiccube2.data

import gustavo.brilhante.magiccube2.domain.CubeSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsRepositoryTest {

    private val testScope = CoroutineScope(SupervisorJob())

    @Test
    fun `getCurrent returns latest value from data source`() = runTest {
        val stored = CubeSettings(shuffle = 7, speed = 3, size = 5)
        val dataSource = FakeSettingsLocalDataSource(stored)
        val repository = SettingsRepositoryImpl(dataSource, testScope)

        assertEquals(stored, repository.getCurrent())
    }

    @Test
    fun `save persists to data source`() = runTest {
        val dataSource = FakeSettingsLocalDataSource()
        val repository = SettingsRepositoryImpl(dataSource, testScope)
        val newSettings = CubeSettings(shuffle = 5, speed = 8, size = 2)

        repository.save(newSettings)

        assertEquals(newSettings, dataSource.lastSaved)
    }

    @Test
    fun `getCurrent reflects changes after save`() = runTest {
        val dataSource = FakeSettingsLocalDataSource()
        val repository = SettingsRepositoryImpl(dataSource, testScope)
        val newSettings = CubeSettings(shuffle = 2, speed = 4, size = 6)

        repository.save(newSettings)

        assertEquals(newSettings, repository.getCurrent())
    }
}

private class FakeSettingsLocalDataSource(
    initial: CubeSettings = CubeSettings()
) : SettingsLocalDataSource {

    private val _flow = MutableStateFlow(initial)
    var lastSaved: CubeSettings? = null

    override val settingsFlow: Flow<CubeSettings> = _flow.asStateFlow()

    override suspend fun save(settings: CubeSettings) {
        lastSaved = settings
        _flow.value = settings
    }
}

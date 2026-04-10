package gustavo.brilhante.magiccube2.data

import gustavo.brilhante.magiccube2.domain.CubeSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsRepositoryTest {

    @Test
    fun `current returns defaults before load`() {
        val dataSource = FakeSettingsLocalDataSource(CubeSettings(shuffle = 9, speed = 1, size = 2))
        val repository = SettingsRepositoryImpl(dataSource)

        assertEquals(CubeSettings(), repository.current)
    }

    @Test
    fun `load populates current from data source`() = runTest {
        val stored = CubeSettings(shuffle = 7, speed = 3, size = 5)
        val dataSource = FakeSettingsLocalDataSource(stored)
        val repository = SettingsRepositoryImpl(dataSource)

        repository.load()

        assertEquals(stored, repository.current)
    }

    @Test
    fun `load updates the settings flow`() = runTest {
        val stored = CubeSettings(shuffle = 4, speed = 8, size = 1)
        val dataSource = FakeSettingsLocalDataSource(stored)
        val repository = SettingsRepositoryImpl(dataSource)

        repository.load()

        assertEquals(stored, repository.settingsFlow.first())
    }

    @Test
    fun `save updates current immediately`() = runTest {
        val dataSource = FakeSettingsLocalDataSource()
        val repository = SettingsRepositoryImpl(dataSource)
        val newSettings = CubeSettings(shuffle = 5, speed = 8, size = 2)

        repository.save(newSettings)

        assertEquals(newSettings, repository.current)
    }

    @Test
    fun `save persists to data source`() = runTest {
        val dataSource = FakeSettingsLocalDataSource()
        val repository = SettingsRepositoryImpl(dataSource)
        val newSettings = CubeSettings(shuffle = 5, speed = 8, size = 2)

        repository.save(newSettings)

        assertEquals(newSettings, dataSource.lastSaved)
    }

    @Test
    fun `save emits updated value on settings flow`() = runTest {
        val dataSource = FakeSettingsLocalDataSource()
        val repository = SettingsRepositoryImpl(dataSource)
        val newSettings = CubeSettings(shuffle = 2, speed = 4, size = 6)

        val emitted = mutableListOf<CubeSettings>()
        val job = launch { repository.settingsFlow.collect { emitted.add(it) } }

        repository.save(newSettings)
        advanceUntilIdle()
        job.cancel()

        assertEquals(newSettings, emitted.last())
    }

    @Test
    fun `data source is not called before save`() = runTest {
        val dataSource = FakeSettingsLocalDataSource()
        SettingsRepositoryImpl(dataSource)

        assertNull(dataSource.lastSaved)
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

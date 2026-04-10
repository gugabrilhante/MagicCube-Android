package gustavo.brilhante.magiccube2.domain.usecase

import gustavo.brilhante.magiccube2.domain.CubeSettings
import gustavo.brilhante.magiccube2.testutil.FakeSettingsRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SaveSettingsUseCaseTest {

    @Test
    fun `invoke delegates to repository save`() = runTest {
        val repository = FakeSettingsRepository()
        val useCase = SaveSettingsUseCase(repository)
        val settings = CubeSettings(shuffle = 7, speed = 2, size = 4)

        useCase(settings)

        assertEquals(settings, repository.lastSaved)
    }

    @Test
    fun `invoke updates current in repository`() = runTest {
        val repository = FakeSettingsRepository()
        val useCase = SaveSettingsUseCase(repository)
        val settings = CubeSettings(shuffle = 3, speed = 9, size = 1)

        useCase(settings)

        assertEquals(settings, repository.current)
    }
}

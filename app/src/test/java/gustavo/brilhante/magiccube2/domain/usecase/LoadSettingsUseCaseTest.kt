package gustavo.brilhante.magiccube2.domain.usecase

import gustavo.brilhante.magiccube2.domain.CubeSettings
import gustavo.brilhante.magiccube2.testutil.FakeSettingsRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LoadSettingsUseCaseTest {

    @Test
    fun `invoke delegates to repository load`() = runTest {
        val repository = FakeSettingsRepository()
        val useCase = LoadSettingsUseCase(repository)

        useCase()

        assertTrue(repository.loadCalled)
    }

    @Test
    fun `invoke does not affect current value directly`() = runTest {
        val repository = FakeSettingsRepository(CubeSettings(shuffle = 5))
        val useCase = LoadSettingsUseCase(repository)

        useCase()

        assertEquals(CubeSettings(shuffle = 5), repository.current)
    }
}

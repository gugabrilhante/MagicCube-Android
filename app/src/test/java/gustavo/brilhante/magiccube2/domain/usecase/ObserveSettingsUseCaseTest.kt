package gustavo.brilhante.magiccube2.domain.usecase

import gustavo.brilhante.magiccube2.domain.CubeSettings
import gustavo.brilhante.magiccube2.testutil.FakeSettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ObserveSettingsUseCaseTest {

    @Test
    fun `invoke returns settings flow from repository`() = runTest {
        val initial = CubeSettings(shuffle = 6, speed = 3, size = 8)
        val repository = FakeSettingsRepository(initial)
        val useCase = ObserveSettingsUseCase(repository)

        val emitted = useCase().first()

        assertEquals(initial, emitted)
    }

    @Test
    fun `flow emits updated value when repository changes`() = runTest {
        val repository = FakeSettingsRepository()
        val useCase = ObserveSettingsUseCase(repository)
        val updated = CubeSettings(shuffle = 2, speed = 7, size = 5)

        val emitted = mutableListOf<CubeSettings>()
        val job = launch { useCase().collect { emitted.add(it) } }

        repository.emit(updated)
        job.cancel()

        assertEquals(updated, emitted.last())
    }
}

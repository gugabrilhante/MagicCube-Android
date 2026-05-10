package gustavo.brilhante.magiccube2.di

import android.os.SystemClock
import gustavo.brilhante.magiccube2.data.DataStoreSettingsDataSource
import gustavo.brilhante.magiccube2.data.SettingsLocalDataSource
import gustavo.brilhante.magiccube2.data.SettingsRepositoryImpl
import gustavo.brilhante.magiccube2.domain.TimeProvider
import gustavo.brilhante.magiccube2.domain.cube.CoordinateTransformer
import gustavo.brilhante.magiccube2.domain.cube.FaceInteractionCalculator
import gustavo.brilhante.magiccube2.domain.cube.GestureClassifier
import gustavo.brilhante.magiccube2.domain.cube.VisibleFacesResolver
import gustavo.brilhante.magiccube2.domain.repository.SettingsRepository
import gustavo.brilhante.magiccube2.domain.usecase.ObserveSettingsUseCase
import gustavo.brilhante.magiccube2.domain.usecase.SaveSettingsUseCase
import gustavo.brilhante.magiccube2.grafic.CubeGameEngine
import gustavo.brilhante.magiccube2.grafic.CubeGameEngineFactory
import gustavo.brilhante.magiccube2.grafic.PickingService
import gustavo.brilhante.magiccube2.presentation.MainMenuViewModel
import gustavo.brilhante.magiccube2.presentation.cube.AndroidCubeLogger
import gustavo.brilhante.magiccube2.presentation.cube.CubeControllerFactory
import gustavo.brilhante.magiccube2.presentation.cube.CubeGameInteractor
import gustavo.brilhante.magiccube2.presentation.cube.CubeViewModel
import gustavo.brilhante.magiccube2.presentation.options.OptionsViewModel
import gustavo.brilhante.magiccube2.domain.cube.CubeLogger
import gustavo.brilhante.magiccube2.grafic.MatrixTracker
import gustavo.brilhante.magiccube2.presentation.cube.engine.CubeProjectionCalculator
import gustavo.brilhante.magiccube2.presentation.cube.engine.CubeRotationEngine
import gustavo.brilhante.magiccube2.presentation.cube.engine.CubeTraversalEngine
import gustavo.brilhante.magiccube2.presentation.cube.engine.ICubeProjectionCalculator
import gustavo.brilhante.magiccube2.presentation.cube.engine.ICubeRotationEngine
import gustavo.brilhante.magiccube2.presentation.cube.engine.ICubeTraversalEngine
import gustavo.brilhante.magiccube2.presentation.cube.CubeRenderEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {

    // Global Scope for Repository
    single { CoroutineScope(Dispatchers.Default + SupervisorJob()) }

    // Data layer
    single<SettingsLocalDataSource> { DataStoreSettingsDataSource(androidContext()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get(), get()) }

    // Domain — use cases
    singleOf(::SaveSettingsUseCase)
    singleOf(::ObserveSettingsUseCase)

    // Engine factory — creates ICubeGameEngine instances
    single<CubeGameEngineFactory> { CubeGameEngineFactory { shuffleCount -> CubeGameEngine(shuffleCount) } }

    // System utilities
    single<TimeProvider> { TimeProvider { SystemClock.elapsedRealtime() } }

    // Interaction services — stateless, safe as singletons
    singleOf(::GestureClassifier)
    singleOf(::CoordinateTransformer)
    singleOf(::FaceInteractionCalculator)
    singleOf(::VisibleFacesResolver)
    singleOf(::PickingService)
    single<ICubeRotationEngine> { CubeRotationEngine() }
    single<ICubeProjectionCalculator> { CubeProjectionCalculator() }
    single<ICubeTraversalEngine> { CubeTraversalEngine(MatrixTracker()) }
    single { CubeRenderEngine(get(), get(), get()) }
    single<CubeLogger> { AndroidCubeLogger() }

    // Controller factory — wires domain services into a fresh controller per engine instance
    single<CubeControllerFactory> {
        CubeControllerFactory { engine ->
            CubeGameInteractor(engine, get(), get(), get(), get(), get(), get())
        }
    }

    // Presentation
    viewModelOf(::MainMenuViewModel)
    viewModelOf(::CubeViewModel)
    viewModelOf(::OptionsViewModel)
}

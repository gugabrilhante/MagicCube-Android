package gustavo.brilhante.magiccube2.di

import android.os.SystemClock
import gustavo.brilhante.magiccube2.data.DataStoreSettingsDataSource
import gustavo.brilhante.magiccube2.data.SettingsLocalDataSource
import gustavo.brilhante.magiccube2.data.SettingsRepositoryImpl
import gustavo.brilhante.magiccube2.domain.TimeProvider
import gustavo.brilhante.magiccube2.domain.cube.*
import gustavo.brilhante.magiccube2.domain.math.MatrixMath
import gustavo.brilhante.magiccube2.domain.repository.SettingsRepository
import gustavo.brilhante.magiccube2.domain.usecase.ObserveSettingsUseCase
import gustavo.brilhante.magiccube2.domain.usecase.SaveSettingsUseCase
import gustavo.brilhante.magiccube2.grafic.CubeGameEngine
import gustavo.brilhante.magiccube2.grafic.CubeGameEngineFactory
import gustavo.brilhante.magiccube2.grafic.MatrixTracker
import gustavo.brilhante.magiccube2.grafic.PickingService
import gustavo.brilhante.magiccube2.presentation.MainMenuViewModel
import gustavo.brilhante.magiccube2.presentation.cube.AndroidCubeLogger
import gustavo.brilhante.magiccube2.presentation.cube.CubeControllerFactory
import gustavo.brilhante.magiccube2.presentation.cube.CubeGameInteractor
import gustavo.brilhante.magiccube2.presentation.cube.CubeRenderEngine
import gustavo.brilhante.magiccube2.presentation.cube.CubeViewModel
import gustavo.brilhante.magiccube2.presentation.cube.engine.CubeProjectionCalculator
import gustavo.brilhante.magiccube2.presentation.cube.engine.CubeRotationEngine
import gustavo.brilhante.magiccube2.presentation.cube.engine.CubeTraversalEngine
import gustavo.brilhante.magiccube2.presentation.cube.engine.ICubeProjectionCalculator
import gustavo.brilhante.magiccube2.presentation.cube.engine.ICubeRotationEngine
import gustavo.brilhante.magiccube2.presentation.cube.engine.ICubeTraversalEngine
import gustavo.brilhante.magiccube2.presentation.options.OptionsViewModel
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
    single { MatrixMath() }

    // Engine factory — creates ICubeGameEngine instances
    single<CubeGameEngineFactory> {
        val matrixMath = get<MatrixMath>()
        CubeGameEngineFactory { shuffleCount -> CubeGameEngine(shuffleCount, matrixMath) }
    }

    // System utilities
    single<TimeProvider> { TimeProvider { SystemClock.elapsedRealtime() } }

    // Interaction services — stateless, safe as singletons
    single { GestureClassifier() }
    single<RotationMath> { CubeRotationMath() }
    single<FaceGeometryResolver> { CubeFaceGeometryResolver() }
    single<SliceInteractionResolver> { CubeSliceInteractionResolver(get(), get()) }
    single<VisibleFacesCalculator> { CubeVisibleFacesCalculator() }
    single { 
        CubeInteractionProcessor(
            gestureClassifier = get(),
            rotationMath = get(),
            geometryResolver = get(),
            sliceResolver = get(),
            visibilityCalculator = get(),
            matrixMath = get()
        )
    }
    single { PickingService(get()) }

    single<ICubeRotationEngine> { CubeRotationEngine() }
    single<ICubeProjectionCalculator> { CubeProjectionCalculator(get()) }
    single<ICubeTraversalEngine> { CubeTraversalEngine(MatrixTracker(get()), get()) }
    single { CubeRenderEngine(get(), get(), get()) }
    single<CubeLogger> { AndroidCubeLogger() }

    // Controller factory — wires domain services into a fresh controller per engine instance
    single<CubeControllerFactory> {
        CubeControllerFactory { engine ->
            CubeGameInteractor(
                engine = engine,
                interactionProcessor = get(),
                pickingService = get(),
                timeProvider = get(),
                logger = get()
            )
        }
    }

    // Presentation
    viewModelOf(::MainMenuViewModel)
    viewModelOf(::CubeViewModel)
    viewModelOf(::OptionsViewModel)
}

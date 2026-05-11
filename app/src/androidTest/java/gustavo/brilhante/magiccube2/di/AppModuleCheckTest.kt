package gustavo.brilhante.magiccube2.di

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import gustavo.brilhante.magiccube2.domain.cube.CubeInteractionProcessor
import gustavo.brilhante.magiccube2.grafic.ICubeGameEngine
import gustavo.brilhante.magiccube2.presentation.cube.CubeControllerFactory
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.context.GlobalContext
import org.koin.dsl.koinApplication
import org.koin.test.KoinTest
import org.koin.test.verify.verify

class AppModuleCheckTest : KoinTest {

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun verifyKoinModules() {
        appModule.verify(
            extraTypes = listOf(
                ICubeGameEngine::class,
                Context::class
            )
        )
    }

    @Test
    fun checkKoinInitializationAndResolution() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        
        // Start a fresh Koin app for this test if not already started
        val koinApp = koinApplication {
            androidContext(context)
            modules(appModule)
        }
        
        val koin = koinApp.koin
        
        // Assert that critical components can be resolved without crash
        koin.get<CubeInteractionProcessor>()
        koin.get<CubeControllerFactory>()
    }
}

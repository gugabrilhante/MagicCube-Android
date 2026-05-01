package gustavo.brilhante.magiccube2.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import gustavo.brilhante.magiccube2.domain.CubeSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class DataStoreSettingsDataSourceTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    
    private val testDataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create(
        scope = testScope,
        produceFile = { context.preferencesDataStoreFile("test_settings") }
    )

    private lateinit var dataSource: DataStoreSettingsDataSource

    @Before
    fun setUp() {
        dataSource = DataStoreSettingsDataSource(context, testDataStore)
    }

    @After
    fun tearDown() = runTest {
        File(context.filesDir, "datastore/test_settings.preferences_pb").delete()
    }

    @Test
    fun givenNewSettingsWhenSaveThenSettingsArePersisted() = runTest {
        // Given
        val newSettings = CubeSettings(shuffle = 5, speed = 8, size = 12)

        // When
        dataSource.save(newSettings)

        // Then
        val retrieved = dataSource.settingsFlow.first()
        assertEquals(newSettings, retrieved)
    }

    @Test
    fun givenNoSavedSettingsWhenObserveThenReturnsDefaults() = runTest {
        // When
        val retrieved = dataSource.settingsFlow.first()

        // Then
        assertEquals(CubeSettings(), retrieved)
    }
}

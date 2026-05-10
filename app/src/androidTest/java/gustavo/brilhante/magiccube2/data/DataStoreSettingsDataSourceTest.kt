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
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class DataStoreSettingsDataSourceTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private lateinit var testDispatcher: TestDispatcher
    private lateinit var testScope: TestScope
    private lateinit var testDataStore: DataStore<Preferences>
    private lateinit var dataSource: DataStoreSettingsDataSource
    private lateinit var testFile: File

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        testScope = TestScope(testDispatcher)

        // Use a unique file name for each test to avoid "Multiple DataStores active for the same file" error
        val fileName = "test_settings_${UUID.randomUUID()}.preferences_pb"
        testFile = File(context.filesDir, "datastore/$fileName")

        testDataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { testFile }
        )
        dataSource = DataStoreSettingsDataSource(context, testDataStore)
    }

    @After
    fun tearDown() {
        testScope.cancel()
        if (testFile.exists()) {
            testFile.delete()
        }
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

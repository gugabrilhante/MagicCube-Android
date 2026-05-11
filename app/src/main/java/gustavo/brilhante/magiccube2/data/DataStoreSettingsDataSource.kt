package gustavo.brilhante.magiccube2.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import gustavo.brilhante.magiccube2.data.mapper.SettingsMapper
import gustavo.brilhante.magiccube2.domain.CubeSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "cube_settings")

class DataStoreSettingsDataSource(
    private val context: Context,
    private val dataStore: DataStore<Preferences> = context.dataStore
) : SettingsLocalDataSource {

    override val settingsFlow: Flow<CubeSettings> = dataStore.data
        .catch { e ->
            if (e is IOException) {
                emit(emptyPreferences())
            } else {
                throw e
            }
        }
        .map { prefs -> SettingsMapper.mapToDomain(prefs) }

    override suspend fun save(settings: CubeSettings) {
        dataStore.edit { prefs ->
            SettingsMapper.mapToPreferences(prefs, settings)
        }
    }
}

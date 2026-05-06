package gustavo.brilhante.magiccube2.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
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

    private object Keys {
        val SHUFFLE = intPreferencesKey("shuffle")
        val SPEED = intPreferencesKey("speed")
        val SIZE = intPreferencesKey("size")
    }

    private val defaults = CubeSettings()

    override val settingsFlow: Flow<CubeSettings> = dataStore.data
        .catch { e ->
            if (e is IOException) {
                emit(emptyPreferences())
            } else {
                throw e
            }
        }
        .map { prefs ->
            CubeSettings(
                shuffle = prefs[Keys.SHUFFLE] ?: defaults.shuffle,
                speed = prefs[Keys.SPEED] ?: defaults.speed,
                size = prefs[Keys.SIZE] ?: defaults.size
            )
        }

    override suspend fun save(settings: CubeSettings) {
        dataStore.edit { prefs ->
            prefs[Keys.SHUFFLE] = settings.shuffle
            prefs[Keys.SPEED] = settings.speed
            prefs[Keys.SIZE] = settings.size
        }
    }
}

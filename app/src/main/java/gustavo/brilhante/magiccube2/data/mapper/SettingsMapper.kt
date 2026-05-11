package gustavo.brilhante.magiccube2.data.mapper

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import gustavo.brilhante.magiccube2.domain.CubeSettings

object SettingsMapper {
    private val SHUFFLE_KEY = intPreferencesKey("shuffle")
    private val SPEED_KEY = intPreferencesKey("speed")
    private val SIZE_KEY = intPreferencesKey("size")

    fun mapToDomain(preferences: Preferences): CubeSettings {
        val defaults = CubeSettings()
        return CubeSettings(
            shuffle = preferences[SHUFFLE_KEY] ?: defaults.shuffle,
            speed = preferences[SPEED_KEY] ?: defaults.speed,
            size = preferences[SIZE_KEY] ?: defaults.size
        )
    }

    fun mapToPreferences(preferences: androidx.datastore.preferences.core.MutablePreferences, settings: CubeSettings) {
        preferences[SHUFFLE_KEY] = settings.shuffle
        preferences[SPEED_KEY] = settings.speed
        preferences[SIZE_KEY] = settings.size
    }
}

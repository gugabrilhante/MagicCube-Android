package gustavo.brilhante.magiccube2.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Sealed hierarchy of all navigation destinations.
 * Each subtype implements [NavKey] as required by Navigation3.
 */
@Serializable
sealed class AppRoute : NavKey {
    @Serializable
    data object MainMenu : AppRoute()
    @Serializable
    data object Cube : AppRoute()
    @Serializable
    data object Options : AppRoute()
}

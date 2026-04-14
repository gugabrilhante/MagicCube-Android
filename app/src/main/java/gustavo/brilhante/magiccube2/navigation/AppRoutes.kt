package gustavo.brilhante.magiccube2.navigation

import androidx.navigation3.runtime.NavKey

/**
 * Sealed hierarchy of all navigation destinations.
 * Each subtype implements [NavKey] as required by Navigation3.
 */
sealed class AppRoute : NavKey {
    data object MainMenu : AppRoute()
    data object Cube : AppRoute()
    data object Options : AppRoute()
}

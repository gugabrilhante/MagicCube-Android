package gustavo.brilhante.magiccube2.presentation.cube.engine

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CubeRotationEngineTest {

    private lateinit var rotationEngine: CubeRotationEngine

    @BeforeEach
    fun setUp() {
        rotationEngine = CubeRotationEngine()
    }

    @Test
    fun `initial state should be zero rotation and no inertia`() {
        assertEquals(0f, rotationEngine.angleX)
        assertEquals(0f, rotationEngine.angleY)
        assertFalse(rotationEngine.isInertiaActive)
    }

    @Test
    fun `updateRotation should update angles and disable inertia`() {
        rotationEngine.startInertia()
        assertTrue(rotationEngine.isInertiaActive)

        rotationEngine.updateRotation(10f, 20f, 0.5f)

        assertEquals(5f, rotationEngine.angleX)
        assertEquals(10f, rotationEngine.angleY)
        assertFalse(rotationEngine.isInertiaActive)
    }

    @Test
    fun `startInertia should enable inertia and reset inertiaInc`() {
        rotationEngine.startInertia()
        assertTrue(rotationEngine.isInertiaActive)
        assertEquals(1f, rotationEngine.getRotationState().inertiaInc)
    }

    @Test
    fun `stopInertia should disable inertia`() {
        rotationEngine.startInertia()
        rotationEngine.stopInertia()
        assertFalse(rotationEngine.isInertiaActive)
    }

    @Test
    fun `tickInertia should increase rotation when delta is positive`() {
        // Setup: moved from 0 to 10
        rotationEngine.updateRotation(10f, 0f, 1f) 
        // angleX = 10, angleXAux = 0. delta = 10 > 2.
        
        rotationEngine.startInertia()
        rotationEngine.tickInertia()

        // inertiaInc starts at 1f (from startInertia)
        assertEquals(11f, rotationEngine.angleX)
        assertEquals(0.9f, rotationEngine.getRotationState().inertiaInc)
    }

    @Test
    fun `tickInertia should decrease rotation when delta is negative`() {
        // Setup: moved from 0 to -10
        rotationEngine.updateRotation(-10f, 0f, 1f) 
        // angleX = -10, angleXAux = 0. delta = -10 < -2.
        
        rotationEngine.startInertia()
        rotationEngine.tickInertia()

        assertEquals(-11f, rotationEngine.angleX)
    }

    @Test
    fun `tickInertia should stop inertia when inertiaInc is low`() {
        rotationEngine.updateRotation(10f, 10f, 1f)
        rotationEngine.startInertia()
        
        // Manual state manipulation via reflection or just ticking many times
        // Let's tick 6 times. 1.0 -> 0.9 -> 0.8 -> 0.7 -> 0.6 -> 0.5 -> 0.4 (stops)
        repeat(6) { rotationEngine.tickInertia() }
        
        assertFalse(rotationEngine.isInertiaActive)
        assertEquals(5f, rotationEngine.getRotationState().inertiaInc)
    }
}

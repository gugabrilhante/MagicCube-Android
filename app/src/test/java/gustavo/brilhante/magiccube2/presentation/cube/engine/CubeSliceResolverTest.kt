package gustavo.brilhante.magiccube2.presentation.cube.engine

import gustavo.brilhante.magiccube2.grafic.ActiveSlice
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CubeSliceResolverTest {

    private val resolver = CubeSliceResolver()

    @Test
    fun `shouldRotateX tests`() {
        assertTrue(resolver.shouldRotateX(ActiveSlice.ROTATION_AXIS_X_0, 0))
        assertTrue(resolver.shouldRotateX(ActiveSlice.ROTATION_AXIS_X_1, 1))
        assertTrue(resolver.shouldRotateX(ActiveSlice.ROTATION_AXIS_X_2, 2))
        assertFalse(resolver.shouldRotateX(ActiveSlice.ROTATION_AXIS_X_0, 1))
        assertFalse(resolver.shouldRotateX(ActiveSlice.NONE, 0))
    }

    @Test
    fun `shouldRotateY tests`() {
        assertTrue(resolver.shouldRotateY(ActiveSlice.ROTATION_AXIS_Z_0, 0))
        assertFalse(resolver.shouldRotateY(ActiveSlice.ROTATION_AXIS_Y_0, 0))
    }

    @Test
    fun `shouldRotateZ tests`() {
        assertTrue(resolver.shouldRotateZ(ActiveSlice.ROTATION_AXIS_Y_0, 0))
        assertFalse(resolver.shouldRotateZ(ActiveSlice.ROTATION_AXIS_Z_0, 0))
    }
}

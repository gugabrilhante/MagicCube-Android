package gustavo.brilhante.magiccube2.domain.cube

import gustavo.brilhante.magiccube2.domain.model.Vector2
import org.junit.Assert.assertEquals
import org.junit.Test

class CoordinateTransformerTest {

    private val transformer = CoordinateTransformer()

    @Test
    fun `given zero rotation when computeLocalDrag then returns same delta`() {
        val screenDelta = Vector2(10f, 20f)
        val result = transformer.computeLocalDrag(screenDelta, 0f, 0f)

        assertEquals(10f, result.x, 0.01f)
        assertEquals(20f, result.y, 0.01f)
    }
}

package gustavo.brilhante.magiccube2.domain.cube

import org.junit.Assert.assertEquals
import org.junit.Test

class CubeInteractionProcessorTest {

    private val processor = CubeInteractionProcessor()

    @Test
    fun `given short time and large horizontal delta when classifyMovement then returns SWIPE_RIGHT`() {
        // Given
        val dt = 100L
        val dx = 150f
        val dy = 10f

        // When
        val result = processor.classifyMovement(dt, dx, dy)

        // Then
        assertEquals(MovementType.SWIPE_RIGHT, result)
    }

    @Test
    fun `given short time and large negative horizontal delta when classifyMovement then returns SWIPE_LEFT`() {
        // Given
        val dt = 100L
        val dx = -150f
        val dy = 10f

        // When
        val result = processor.classifyMovement(dt, dx, dy)

        // Then
        assertEquals(MovementType.SWIPE_LEFT, result)
    }

    @Test
    fun `given short time and large vertical delta when classifyMovement then returns SWIPE_DOWN`() {
        // Given
        val dt = 100L
        val dx = 10f
        val dy = 150f

        // When
        val result = processor.classifyMovement(dt, dx, dy)

        // Then
        assertEquals(MovementType.SWIPE_DOWN, result)
    }

    @Test
    fun `given short time and large negative vertical delta when classifyMovement then returns SWIPE_UP`() {
        // Given
        val dt = 100L
        val dx = 10f
        val dy = -150f

        // When
        val result = processor.classifyMovement(dt, dx, dy)

        // Then
        assertEquals(MovementType.SWIPE_UP, result)
    }

    @Test
    fun `given long time when classifyMovement then returns DRAG`() {
        // Given
        val dt = 300L
        val dx = 10f
        val dy = 10f

        // When
        val result = processor.classifyMovement(dt, dx, dy)

        // Then
        assertEquals(MovementType.DRAG, result)
    }

    @Test
    fun `given zero rotation when computeLocalDrag then returns same delta with flipped screen Y if applicable`() {
        // Given
        val screenDx = 10f
        val screenDy = 20f
        val angleX = 0f
        val angleY = 0f

        // When
        val result = processor.computeLocalDrag(screenDx, screenDy, angleX, angleY)

        // Then
        assertEquals(10f, result.first, 0.01f)
        assertEquals(20f, result.second, 0.01f)
    }
}

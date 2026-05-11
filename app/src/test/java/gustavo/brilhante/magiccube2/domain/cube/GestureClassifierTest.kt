package gustavo.brilhante.magiccube2.domain.cube

import org.junit.Assert.assertEquals
import org.junit.Test

class GestureClassifierTest {

    private val classifier = GestureClassifier()

    @Test
    fun `given short time and large horizontal delta when classifyMovement then returns SWIPE_RIGHT`() {
        val result = classifier.classifyMovement(100L, 150f, 10f)
        assertEquals(MovementType.SWIPE_RIGHT, result)
    }

    @Test
    fun `given short time and large negative horizontal delta when classifyMovement then returns SWIPE_LEFT`() {
        val result = classifier.classifyMovement(100L, -150f, 10f)
        assertEquals(MovementType.SWIPE_LEFT, result)
    }

    @Test
    fun `given short time and large vertical delta when classifyMovement then returns SWIPE_DOWN`() {
        val result = classifier.classifyMovement(100L, 10f, 150f)
        assertEquals(MovementType.SWIPE_DOWN, result)
    }

    @Test
    fun `given short time and large negative vertical delta when classifyMovement then returns SWIPE_UP`() {
        val result = classifier.classifyMovement(100L, 10f, -150f)
        assertEquals(MovementType.SWIPE_UP, result)
    }

    @Test
    fun `given long time when classifyMovement then returns DRAG`() {
        val result = classifier.classifyMovement(300L, 10f, 10f)
        assertEquals(MovementType.DRAG, result)
    }
}

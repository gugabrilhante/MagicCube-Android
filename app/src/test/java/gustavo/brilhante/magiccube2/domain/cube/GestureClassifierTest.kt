package gustavo.brilhante.magiccube2.domain.cube

import org.junit.Assert.assertEquals
import org.junit.Test

class GestureClassifierTest {

    private val classifier = GestureClassifier()

    @Test
    fun `given short time and small displacement when classify then returns NONE`() {
        assertEquals(MovementType.NONE, classifier.classifyMovement(100L, 50f, 50f))
    }

    @Test
    fun `given short time and large horizontal displacement when classify then returns SWIPE`() {
        assertEquals(MovementType.SWIPE_RIGHT, classifier.classifyMovement(100L, 150f, 20f))
        assertEquals(MovementType.SWIPE_LEFT, classifier.classifyMovement(100L, -150f, 20f))
    }

    @Test
    fun `given short time and large vertical displacement when classify then returns SWIPE`() {
        assertEquals(MovementType.SWIPE_DOWN, classifier.classifyMovement(100L, 20f, 150f))
        assertEquals(MovementType.SWIPE_UP, classifier.classifyMovement(100L, 20f, -150f))
    }

    @Test
    fun `given long time when classify then returns DRAG`() {
        assertEquals(MovementType.DRAG, classifier.classifyMovement(300L, 50f, 50f))
    }
}

package gustavo.brilhante.magiccube2.domain.cube

import gustavo.brilhante.magiccube2.domain.model.CubeFace
import org.junit.Assert.assertTrue
import org.junit.Test

class CubeVisibleFacesCalculatorTest {

    private val calculator = CubeVisibleFacesCalculator()

    @Test
    fun `given zero rotation when getVisibleFaces then returns front faces`() {
        val faces = calculator.getVisibleFaces(0f, 0f)
        // worldZ(nx,ny,nz) = -nx * sinY + ny * cosY * sinX + nz * cosY * cosX
        // For angleX=0, angleY=0: worldZ = nz
        // nz=1 is BLUE, so BLUE should be visible.
        assertTrue(faces.contains(CubeFace.BLUE))
    }

    @Test
    fun `given 180 deg X rotation when getVisibleFaces then returns back faces`() {
        val faces = calculator.getVisibleFaces(180f, 0f)
        // sin(180)=0, cos(180)=-1. 
        // worldZ = -nx * 0 + ny * (-1) * 0 + nz * (-1) * 1 = -nz
        // nz=-1 is GREEN, so GREEN should be visible.
        assertTrue(faces.contains(CubeFace.GREEN))
    }

    @Test
    fun `given 90 deg Y rotation when getVisibleFaces then returns top face`() {
        val faces = calculator.getVisibleFaces(0f, 90f)
        // angleX=0, angleY=90: sinY=0, cosY=1, sinX=1, cosX=0
        // worldZ = -nx * 0 + ny * 1 * 1 + nz * 1 * 0 = ny
        // ny=1 is YELLOW, so YELLOW should be visible.
        assertTrue(faces.contains(CubeFace.YELLOW))
    }
}

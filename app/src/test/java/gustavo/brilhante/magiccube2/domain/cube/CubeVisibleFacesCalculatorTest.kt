package gustavo.brilhante.magiccube2.domain.cube

import gustavo.brilhante.magiccube2.domain.model.CubeFace
import org.junit.Assert.assertTrue
import org.junit.Test

class CubeVisibleFacesCalculatorTest {

    private val calculator = CubeVisibleFacesCalculator()

    @Test
    fun `given zero rotation when getVisibleFaces then returns front faces`() {
        // At 0,0 only BLUE (Z=1), YELLOW (Y=1), RED (X=1) are potentially visible depending on camera
        // In the formula: worldZ(nx, ny, nz) = -nx * sinY + ny * cosY * sinX + nz * cosY * cosX
        // If angleRotateX=0, angleRotateY=0 -> sinY=0, cosY=1, sinX=0, cosX=1
        // worldZ = nz. So BLUE (0,0,1) should be visible.
        val result = calculator.getVisibleFaces(0f, 0f)
        assertTrue(result.contains(CubeFace.BLUE))
    }

    @Test
    fun `given 180 degree Y rotation when getVisibleFaces then returns back face`() {
        // angleRotateX=180 -> sinY=0, cosY=-1. sinX=0, cosX=1
        // worldZ = -nx * 0 + ny * (-1) * 0 + nz * (-1) * 1 = -nz
        // GREEN (0,0,-1) -> worldZ = -(-1) = 1 (Visible)
        val result = calculator.getVisibleFaces(180f, 0f)
        assertTrue(result.contains(CubeFace.GREEN))
    }
}

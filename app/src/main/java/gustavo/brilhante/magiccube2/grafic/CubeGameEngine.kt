package gustavo.brilhante.magiccube2.grafic

import kotlin.random.Random

data class CubePosition(var x: Float = 0f, var y: Float = 0f, var z: Float = 0f)

/**
 * Encapsulates the state of an in-progress or idle slice rotation.
 * @param activeSlice  Which slice is rotating. [ActiveSlice.NONE] = no active rotation.
 * @param direction    Rotation direction: 1 or -1.
 * @param isAnimating  Whether a rotation is currently being animated.
 */
data class RotationState(
    val activeSlice: ActiveSlice = ActiveSlice.NONE,
    val direction: Int = -1,
    val isAnimating: Boolean = true
)

class CubeGameEngine(shuffleCount: Int) : ICubeGameEngine {

    // --- Cube objects ---
    override val cubes: MutableList<Cube> = mutableListOf()

    // --- Position grid: cubeGrid[x][z][y] = cubeIndex ---
    override val cubeGrid: Array<Array<IntArray>> = Array(3) { Array(3) { IntArray(3) } }

    // --- Rotation state ---
    @Volatile override var rotation: RotationState = RotationState()
    override var rotatedAngle: Float = 0f

    // --- Shuffle state ---
    var isShuffling: Boolean = true
    val totalShuffleMoves: Int = 10 * shuffleCount
    var shuffleMovesCompleted: Int = 0

    // --- Face tracking for closest-side detection ---
    override val faceCenterPositions: Array<CubePosition> = Array(6) { CubePosition() }
    override val faceCenterCubes: List<Pair<Int, CubeSide>> = listOf(
        4 to CubeSide.YELLOW,
        10 to CubeSide.GREEN,
        12 to CubeSide.ORANGE,
        14 to CubeSide.RED,
        16 to CubeSide.BLUE,
        22 to CubeSide.WHITE
    )

    // Face index cycles used when rotating colors after a slice move
    private val yAxisFaceCycle = intArrayOf(0, 1, 2, 3)
    private val zAxisFaceCycle = intArrayOf(1, 5, 3, 4)
    private val xAxisFaceCycle = intArrayOf(0, 4, 2, 5)

    private val shuffleableSlices = listOf(
        ActiveSlice.ROTATION_AXIS_Z_0,
        ActiveSlice.ROTATION_AXIS_Z_2,
        ActiveSlice.ROTATION_AXIS_Y_0,
        ActiveSlice.ROTATION_AXIS_Y_2,
        ActiveSlice.ROTATION_AXIS_X_0,
        ActiveSlice.ROTATION_AXIS_X_2
    )

    init {
        initCubes()
        initPositions()
    }

    private fun initCubes() {
        cubes.add(Cube(upColor = ColorLetter.YELLOW, backColor = ColorLetter.GREEN, leftColor = ColorLetter.ORANGE)) //0
        cubes.add(Cube(upColor = ColorLetter.YELLOW, backColor = ColorLetter.GREEN)) //1
        cubes.add(Cube(upColor = ColorLetter.YELLOW, rightColor = ColorLetter.RED, backColor = ColorLetter.GREEN)) //2
        cubes.add(Cube(upColor = ColorLetter.YELLOW, leftColor = ColorLetter.ORANGE)) //3
        cubes.add(Cube(upColor = ColorLetter.YELLOW)) //4 middle yellow
        cubes.add(Cube(upColor = ColorLetter.YELLOW, rightColor = ColorLetter.RED)) //5
        cubes.add(Cube(frontColor = ColorLetter.BLUE, upColor = ColorLetter.YELLOW, leftColor = ColorLetter.ORANGE)) //6
        cubes.add(Cube(frontColor = ColorLetter.BLUE, upColor = ColorLetter.YELLOW)) //7
        cubes.add(Cube(frontColor = ColorLetter.BLUE, upColor = ColorLetter.YELLOW, rightColor = ColorLetter.RED)) //8
        cubes.add(Cube(backColor = ColorLetter.GREEN, leftColor = ColorLetter.ORANGE)) //9
        cubes.add(Cube(backColor = ColorLetter.GREEN)) //10 middle green
        cubes.add(Cube(rightColor = ColorLetter.RED, backColor = ColorLetter.GREEN)) //11
        cubes.add(Cube(leftColor = ColorLetter.ORANGE)) //12 middle orange
        cubes.add(Cube()) //13
        cubes.add(Cube(rightColor = ColorLetter.RED)) //14 middle red
        cubes.add(Cube(frontColor = ColorLetter.BLUE, leftColor = ColorLetter.ORANGE)) //15
        cubes.add(Cube(frontColor = ColorLetter.BLUE)) //16 middle blue
        cubes.add(Cube(frontColor = ColorLetter.BLUE, rightColor = ColorLetter.RED)) //17
        cubes.add(Cube(backColor = ColorLetter.GREEN, leftColor = ColorLetter.ORANGE, downColor = ColorLetter.WHITE)) //18
        cubes.add(Cube(backColor = ColorLetter.GREEN, downColor = ColorLetter.WHITE)) //19
        cubes.add(Cube(rightColor = ColorLetter.RED, backColor = ColorLetter.GREEN, downColor = ColorLetter.WHITE)) //20
        cubes.add(Cube(leftColor = ColorLetter.ORANGE, downColor = ColorLetter.WHITE)) //21
        cubes.add(Cube(downColor = ColorLetter.WHITE)) //22 middle white
        cubes.add(Cube(rightColor = ColorLetter.RED, downColor = ColorLetter.WHITE)) //23
        cubes.add(Cube(frontColor = ColorLetter.BLUE, leftColor = ColorLetter.ORANGE, downColor = ColorLetter.WHITE)) //24
        cubes.add(Cube(frontColor = ColorLetter.BLUE, downColor = ColorLetter.WHITE)) //25
        cubes.add(Cube(frontColor = ColorLetter.BLUE, rightColor = ColorLetter.RED, downColor = ColorLetter.WHITE)) //26
    }

    private fun initPositions() {
        var indexAxisZ = 0
        var indexAxisY = 0
        for (j in 0..26) {
            cubeGrid[j % 3][indexAxisZ % 3][indexAxisY % 3] = j
            if (indexAxisY % 3 == 2 && j % 3 == 2) indexAxisZ++
            if (j % 3 == 2) indexAxisY++
        }
    }

    // --- Public API ---

    override fun rotateClosestSideToScreen(rotationSense: Int) {
        val closestFaceIndex = faceCenterPositions.withIndex().minByOrNull { it.value.z }?.index ?: -1
        if (closestFaceIndex >= 0) {
            rotation = rotation.copy(
                activeSlice = faceCenterCubes[closestFaceIndex].second.rotation,
                direction = rotationSense * faceCenterCubes[closestFaceIndex].second.orientation
            )
        }
    }

    /** Corrects angle sign at the start of each frame before rendering. */
    override fun prepareFrameRotation() {
        if (rotatedAngle >= 0 && rotation.direction == -1) rotatedAngle *= -1f
    }

    /** Advances game state after rendering. */
    override fun postFrameAdvance() {
        if (rotatedAngle == 90f || rotatedAngle == -90f) {
            rotatedAngle = 0f
            commitSliceRotation()
            handlePostRotation()
            return
        }
        if (rotation.isAnimating) {
            if (rotatedAngle >= 0) rotatedAngle += 9f
            else rotatedAngle -= 9f
        }
    }

    private fun handlePostRotation() {
        if (!isShuffling) {
            rotation = rotation.copy(isAnimating = false, activeSlice = ActiveSlice.NONE)
        } else {
            val newDirection = if (Random.nextDouble() < 0.5) 1 else -1
            rotation = rotation.copy(activeSlice = shuffleableSlices.random(), direction = newDirection)
            shuffleMovesCompleted++
            if (shuffleMovesCompleted == totalShuffleMoves) {
                isShuffling = false
                rotation = rotation.copy(isAnimating = false, activeSlice = ActiveSlice.NONE)
            }
        }
    }

    // --- Private helpers ---

    private fun computeSliceIndex(): Int = when (rotation.activeSlice) {
        ActiveSlice.ROTATION_AXIS_Z_1, ActiveSlice.ROTATION_AXIS_Y_1, ActiveSlice.ROTATION_AXIS_X_1 -> 1
        ActiveSlice.ROTATION_AXIS_Z_2, ActiveSlice.ROTATION_AXIS_Y_2, ActiveSlice.ROTATION_AXIS_X_2 -> 2
        else -> 0
    }

    private fun getFaceColor(cubeIdx: Int, face: Int): ColorLetter = when (face) {
        0 -> cubes[cubeIdx].getFrontSide()
        1 -> cubes[cubeIdx].getRightSide()
        2 -> cubes[cubeIdx].getBackSide()
        3 -> cubes[cubeIdx].getLeftSide()
        4 -> cubes[cubeIdx].getDownSide()
        5 -> cubes[cubeIdx].getUpperSide()
        else -> ColorLetter.BLACK
    }

    private fun setFaceColor(cubeIdx: Int, face: Int, color: ColorLetter) {
        when (face) {
            0 -> cubes[cubeIdx].setfront(color)
            1 -> cubes[cubeIdx].setright(color)
            2 -> cubes[cubeIdx].setback(color)
            3 -> cubes[cubeIdx].setleft(color)
            4 -> cubes[cubeIdx].setdown(color)
            5 -> cubes[cubeIdx].setup(color)
        }
    }

    private fun applyFaceColorRotation(cubeIdx: Int) {
        val faceCycle = when (rotation.activeSlice) {
            ActiveSlice.ROTATION_AXIS_Z_0, ActiveSlice.ROTATION_AXIS_Z_1, ActiveSlice.ROTATION_AXIS_Z_2 -> yAxisFaceCycle
            ActiveSlice.ROTATION_AXIS_Y_0, ActiveSlice.ROTATION_AXIS_Y_1, ActiveSlice.ROTATION_AXIS_Y_2 -> zAxisFaceCycle
            else -> xAxisFaceCycle
        }
        var nextColor = ColorLetter.BLACK
        for (i in 0..3) {
            val dirStart: Int; val dirStep: Int; val faceIndex: Int
            if (rotation.direction == 1) { dirStart = 0; dirStep = 1; faceIndex = i }
            else { dirStart = 3; dirStep = 0; faceIndex = (3 - i) }

            val fromColor = if (i == 0) getFaceColor(cubeIdx, faceCycle[faceIndex]) else nextColor
            nextColor = getFaceColor(cubeIdx, faceCycle[((faceIndex + dirStep) % 4 + dirStart) % 4])
            setFaceColor(cubeIdx, faceCycle[((faceIndex + dirStep) % 4 + dirStart) % 4], fromColor)
        }
    }

    private fun rotateSlice(
        getCell: (col: Int, row: Int) -> Int,
        setCell: (col: Int, row: Int, value: Int) -> Unit
    ) {
        repeat(2) { iteration ->
            var row = 0; var col = 0
            var current = if (rotation.direction == 1) getCell(col, row) else getCell(row, col)
            repeat(8) {
                val displaced = current
                if (row == 0 && col != 0) col--
                else if (col == 2) row--
                else if (row == 2) col++
                else if (col == 0) row++
                if (rotation.direction == 1) {
                    current = getCell(col, row); setCell(col, row, displaced)
                } else {
                    current = getCell(row, col); setCell(row, col, displaced)
                }
                if (iteration == 1) applyFaceColorRotation(displaced)
            }
        }
    }

    private fun commitSliceRotation() {
        val sliceIdx = computeSliceIndex()
        when (rotation.activeSlice) {
            ActiveSlice.ROTATION_AXIS_Z_0, ActiveSlice.ROTATION_AXIS_Z_1, ActiveSlice.ROTATION_AXIS_Z_2 ->
                rotateSlice(
                    getCell = { col, row -> cubeGrid[col][sliceIdx][row] },
                    setCell = { col, row, v -> cubeGrid[col][sliceIdx][row] = v }
                )
            ActiveSlice.ROTATION_AXIS_Y_0, ActiveSlice.ROTATION_AXIS_Y_1, ActiveSlice.ROTATION_AXIS_Y_2 ->
                rotateSlice(
                    getCell = { col, row -> cubeGrid[col][row][sliceIdx] },
                    setCell = { col, row, v -> cubeGrid[col][row][sliceIdx] = v }
                )
            ActiveSlice.ROTATION_AXIS_X_0, ActiveSlice.ROTATION_AXIS_X_1, ActiveSlice.ROTATION_AXIS_X_2 ->
                rotateSlice(
                    getCell = { col, row -> cubeGrid[sliceIdx][col][row] },
                    setCell = { col, row, v -> cubeGrid[sliceIdx][col][row] = v }
                )
            ActiveSlice.NONE -> Unit
        }
    }
}

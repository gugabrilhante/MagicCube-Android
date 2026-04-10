package gustavo.brilhante.magiccube2.grafic

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

class CubeGameEngine(shuffleCount: Int) {

    // --- Cube objects ---
    val cubeList: ArrayList<Cube> = ArrayList()

    // --- Position grid: pos[x][z][y] = cubeIndex ---
    val pos: Array<Array<IntArray>> = Array(3) { Array(3) { IntArray(3) } }

    // --- Rotation state ---
    @Volatile var rotation: RotationState = RotationState()
    var rotatedAngle: Float = 0f

    // --- Shuffle state ---
    var embaralhando: Boolean = true
    val numEmbaralhar: Int = 10 * shuffleCount
    var cont: Int = 0

    // --- Face tracking for closest-side detection ---
    val cubeSide: Array<CubePosition> = Array(6) { CubePosition() }
    val cubeSideIndex: List<Pair<Int, CubeSide>> = listOf(
        Pair(4, CubeSide.YELLOW),
        Pair(10, CubeSide.GREEN),
        Pair(12, CubeSide.ORANGE),
        Pair(14, CubeSide.RED),
        Pair(16, CubeSide.BLUE),
        Pair(22, CubeSide.WHITE)
    )

    private val eixoy = intArrayOf(0, 1, 2, 3)
    private val eixoz = intArrayOf(1, 5, 3, 4)
    private val eixox = intArrayOf(0, 4, 2, 5)

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
        cubeList.add(Cube(upColor = ColorLetter.YELLOW, backColor = ColorLetter.GREEN, leftColor = ColorLetter.ORANGE)) //0
        cubeList.add(Cube(upColor = ColorLetter.YELLOW, backColor = ColorLetter.GREEN)) //1
        cubeList.add(Cube(upColor = ColorLetter.YELLOW, rightColor = ColorLetter.RED, backColor = ColorLetter.GREEN)) //2
        cubeList.add(Cube(upColor = ColorLetter.YELLOW, leftColor = ColorLetter.ORANGE)) //3
        cubeList.add(Cube(upColor = ColorLetter.YELLOW)) //4 middle yellow
        cubeList.add(Cube(upColor = ColorLetter.YELLOW, rightColor = ColorLetter.RED)) //5
        cubeList.add(Cube(frontColor = ColorLetter.BLUE, upColor = ColorLetter.YELLOW, leftColor = ColorLetter.ORANGE)) //6
        cubeList.add(Cube(frontColor = ColorLetter.BLUE, upColor = ColorLetter.YELLOW)) //7
        cubeList.add(Cube(frontColor = ColorLetter.BLUE, upColor = ColorLetter.YELLOW, rightColor = ColorLetter.RED)) //8
        cubeList.add(Cube(backColor = ColorLetter.GREEN, leftColor = ColorLetter.ORANGE)) //9
        cubeList.add(Cube(backColor = ColorLetter.GREEN)) //10 middle green
        cubeList.add(Cube(rightColor = ColorLetter.RED, backColor = ColorLetter.GREEN)) //11
        cubeList.add(Cube(leftColor = ColorLetter.ORANGE)) //12 middle orange
        cubeList.add(Cube()) //13
        cubeList.add(Cube(rightColor = ColorLetter.RED)) //14 middle red
        cubeList.add(Cube(frontColor = ColorLetter.BLUE, leftColor = ColorLetter.ORANGE)) //15
        cubeList.add(Cube(frontColor = ColorLetter.BLUE)) //16 middle blue
        cubeList.add(Cube(frontColor = ColorLetter.BLUE, rightColor = ColorLetter.RED)) //17
        cubeList.add(Cube(backColor = ColorLetter.GREEN, leftColor = ColorLetter.ORANGE, downColor = ColorLetter.WHITE)) //18
        cubeList.add(Cube(backColor = ColorLetter.GREEN, downColor = ColorLetter.WHITE)) //19
        cubeList.add(Cube(rightColor = ColorLetter.RED, backColor = ColorLetter.GREEN, downColor = ColorLetter.WHITE)) //20
        cubeList.add(Cube(leftColor = ColorLetter.ORANGE, downColor = ColorLetter.WHITE)) //21
        cubeList.add(Cube(downColor = ColorLetter.WHITE)) //22 middle white
        cubeList.add(Cube(rightColor = ColorLetter.RED, downColor = ColorLetter.WHITE)) //23
        cubeList.add(Cube(frontColor = ColorLetter.BLUE, leftColor = ColorLetter.ORANGE, downColor = ColorLetter.WHITE)) //24
        cubeList.add(Cube(frontColor = ColorLetter.BLUE, downColor = ColorLetter.WHITE)) //25
        cubeList.add(Cube(frontColor = ColorLetter.BLUE, rightColor = ColorLetter.RED, downColor = ColorLetter.WHITE)) //26
    }

    private fun initPositions() {
        var indexAxisZ = 0
        var indexAxisY = 0
        for (j in 0..26) {
            pos[j % 3][indexAxisZ % 3][indexAxisY % 3] = j
            if (indexAxisY % 3 == 2 && j % 3 == 2) indexAxisZ++
            if (j % 3 == 2) indexAxisY++
        }
    }

    // --- Public API ---

    fun rotateClosestSideToScreen(rotationSense: Int = 1) {
        val indexMin = cubeSide.withIndex().minByOrNull { it.value.z }?.index ?: -1
        if (indexMin >= 0) {
            rotation = rotation.copy(
                activeSlice = cubeSideIndex[indexMin].second.rotation,
                direction = rotationSense * cubeSideIndex[indexMin].second.orientation
            )
        }
    }

    /** Corrects angle sign at the start of each frame before rendering. */
    fun prepareFrameRotation() {
        if (rotatedAngle >= 0 && rotation.direction == -1) rotatedAngle *= -1f
    }

    /** Advances game state after rendering. */
    fun postFrameAdvance() {
        if (rotatedAngle == 90f || rotatedAngle == -90f) {
            rotatedAngle = 0f
            save()
            handlePostRotation()
            return
        }
        if (rotation.isAnimating) {
            if (rotatedAngle >= 0) rotatedAngle += 9f
            else rotatedAngle -= 9f
        }
    }

    private fun handlePostRotation() {
        if (!embaralhando) {
            rotation = rotation.copy(isAnimating = false, activeSlice = ActiveSlice.NONE)
        } else {
            val newDirection = if (Math.random() < 0.5) 1 else -1
            rotation = rotation.copy(activeSlice = shuffleableSlices.random(), direction = newDirection)
            cont++
            if (cont == numEmbaralhar) {
                embaralhando = false
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

    private fun getColor(cubo: Int, face: Int): ColorLetter = when (face) {
        0 -> cubeList[cubo].getFrontSide()
        1 -> cubeList[cubo].getRightSide()
        2 -> cubeList[cubo].getBackSide()
        3 -> cubeList[cubo].getLeftSide()
        4 -> cubeList[cubo].getDownSide()
        5 -> cubeList[cubo].getUpperSide()
        else -> ColorLetter.BLACK
    }

    private fun changeColor(cubo: Int, face: Int, letter: ColorLetter) {
        when (face) {
            0 -> cubeList[cubo].setfront(letter)
            1 -> cubeList[cubo].setright(letter)
            2 -> cubeList[cubo].setback(letter)
            3 -> cubeList[cubo].setleft(letter)
            4 -> cubeList[cubo].setdown(letter)
            5 -> cubeList[cubo].setup(letter)
        }
    }

    private fun saveRot(cubo: Int) {
        val eixo = when (rotation.activeSlice) {
            ActiveSlice.ROTATION_AXIS_Z_0, ActiveSlice.ROTATION_AXIS_Z_1, ActiveSlice.ROTATION_AXIS_Z_2 -> eixoy
            ActiveSlice.ROTATION_AXIS_Y_0, ActiveSlice.ROTATION_AXIS_Y_1, ActiveSlice.ROTATION_AXIS_Y_2 -> eixoz
            else -> eixox
        }
        var cor2 = ColorLetter.BLACK
        for (q in 0..3) {
            val t1: Int; val t2: Int; val indice: Int
            if (rotation.direction == 1) { t1 = 0; t2 = 1; indice = q }
            else { t1 = 3; t2 = 0; indice = (3 - q) }

            val cor1 = if (q == 0) getColor(cubo, eixo[indice]) else cor2
            cor2 = getColor(cubo, eixo[((indice + t2) % 4 + t1) % 4])
            changeColor(cubo, eixo[((indice + t2) % 4 + t1) % 4], cor1)
        }
    }

    private fun save() {
        val n2 = computeSliceIndex()

        if (rotation.activeSlice == ActiveSlice.ROTATION_AXIS_Z_0 || rotation.activeSlice == ActiveSlice.ROTATION_AXIS_Z_2 || rotation.activeSlice == ActiveSlice.ROTATION_AXIS_Z_1) {
            repeat(2) { pass ->
                var i2 = 0; var j2 = 0; var aux1 = 0; var aux2 = 0
                for (s2 in 0 until 8) {
                    aux1 = if (rotation.direction == 1) {
                        if (s2 == 0) pos[j2][n2][i2] else aux2
                    } else {
                        if (s2 == 0) pos[i2][n2][j2] else aux2
                    }
                    if (i2 == 0 && j2 != 0) j2--
                    else if (j2 == 2 && i2 != 0) i2--
                    else if (i2 == 2 && j2 != 2) j2++
                    else if (j2 == 0 && i2 != 2) i2++
                    if (rotation.direction == 1) {
                        aux2 = pos[j2][n2][i2]; pos[j2][n2][i2] = aux1
                    } else {
                        aux2 = pos[i2][n2][j2]; pos[i2][n2][j2] = aux1
                    }
                    if (pass == 1) saveRot(aux1)
                }
            }
        }
        if (rotation.activeSlice == ActiveSlice.ROTATION_AXIS_Y_0 || rotation.activeSlice == ActiveSlice.ROTATION_AXIS_Y_2 || rotation.activeSlice == ActiveSlice.ROTATION_AXIS_Y_1) {
            repeat(2) { pass ->
                var i2 = 0; var j2 = 0; var aux1 = 0; var aux2 = 0
                for (s2 in 0 until 8) {
                    aux1 = if (rotation.direction == 1) {
                        if (s2 == 0) pos[j2][i2][n2] else aux2
                    } else {
                        if (s2 == 0) pos[i2][j2][n2] else aux2
                    }
                    if (i2 == 0 && j2 != 0) j2--
                    else if (j2 == 2 && i2 != 0) i2--
                    else if (i2 == 2 && j2 != 2) j2++
                    else if (j2 == 0 && i2 != 2) i2++
                    if (rotation.direction == 1) {
                        aux2 = pos[j2][i2][n2]; pos[j2][i2][n2] = aux1
                    } else {
                        aux2 = pos[i2][j2][n2]; pos[i2][j2][n2] = aux1
                    }
                    if (pass == 1) saveRot(aux1)
                }
            }
        }
        if (rotation.activeSlice == ActiveSlice.ROTATION_AXIS_X_0 || rotation.activeSlice == ActiveSlice.ROTATION_AXIS_X_2 || rotation.activeSlice == ActiveSlice.ROTATION_AXIS_X_1) {
            repeat(2) { pass ->
                var i2 = 0; var j2 = 0; var aux1 = 0; var aux2 = 0
                for (s2 in 0 until 8) {
                    aux1 = if (rotation.direction == 1) {
                        if (s2 == 0) pos[n2][j2][i2] else aux2
                    } else {
                        if (s2 == 0) pos[n2][i2][j2] else aux2
                    }
                    if (i2 == 0 && j2 != 0) j2--
                    else if (j2 == 2 && i2 != 0) i2--
                    else if (i2 == 2 && j2 != 2) j2++
                    else if (j2 == 0 && i2 != 2) i2++
                    if (rotation.direction == 1) {
                        aux2 = pos[n2][j2][i2]; pos[n2][j2][i2] = aux1
                    } else {
                        aux2 = pos[n2][i2][j2]; pos[n2][i2][j2] = aux1
                    }
                    if (pass == 1) saveRot(aux1)
                }
            }
        }
    }
}

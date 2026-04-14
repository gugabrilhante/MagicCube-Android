package gustavo.brilhante.magiccube2.domain

fun interface TimeProvider {
    fun currentTimeMillis(): Long
}
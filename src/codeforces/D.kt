package codeforces

import kotlin.math.abs

fun readArgs(): List<Int> = readLine()!!.split(" ").map(String::toInt)
fun readVector() = readArgs().map(Int::toDouble).toTypedArray()

class Gradient(
    n: Int,
    private val m: Int,
    private val vectors: Array<Array<Double>>,
    private val a: Array<Double>,
) {
    private val ys = Array(n) { i -> vectors[i][m].also { vectors[i][m] = 1.0 } }
    private fun scalar(i: Int): Double = (0 until m).fold(0.0) { acc, j -> acc + a[j] * vectors[i][j] }

    fun gradientValue(i: Int): Array<Double> {
        val scalar = scalar(i) + a[m]
        return Array(m + 1) {
            val numerator = abs(ys[i] - scalar)
            val module1 = if (numerator == 0.0) 0.0 else (-numerator / (ys[i] - scalar)) * vectors[i][it]
            val denominator = abs(ys[i]) + abs(scalar)
            val module2 = if (scalar == 0.0) 0.0 else (abs(scalar) / scalar) * vectors[i][it]

            return@Array (module1 * denominator - numerator * module2) / (denominator * denominator)
        }
    }
}

fun main() {
    val (n, m) = readArgs()
    val gradient = Gradient(n, m, Array(n) { readVector() }, readVector())

    (0 until n).forEach { i ->
        gradient.gradientValue(i).forEach {
            print("$it ")
        }
        println()
    }
}
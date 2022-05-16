package codeforces

import kotlin.math.sqrt

private fun input() = readLine()!!.split(" ").map { it.toLong() }

fun main() {
    val (n) = input()
    val xs: MutableList<Long> = mutableListOf()
    val ys: MutableList<Long> = mutableListOf()

    for (i in 1..n) {
        val (x, y) = input()
        xs.add(x); ys.add(y)
    }
    val ex: Double = xs.average()
    val ey: Double = ys.average()

    println (((xs zip ys).sumOf { (it.first - ex) * (it.second - ey) } /
            sqrt(
                xs.map { it - ex }.sumOf { it * it } *
                        ys.map { it - ey }.sumOf { it * it }
            )).let { if (it.isNaN()) 0.0 else it }
    )
}
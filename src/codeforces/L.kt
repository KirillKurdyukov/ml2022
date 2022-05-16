package codeforces

import kotlin.math.pow

fun `in`() = readLine()!!.split(" ").map { it.toLong() }

fun main() {
    val (n) = `in`()
    val xs = mutableListOf<Long>()
    val ys = mutableListOf<Long>()

    for (i in 1..n) {
        val (x, y) = `in`()

        xs.add(x); ys.add(y)
    }

    val rx = xs.toMutableList().sorted().mapIndexed { index, l -> Pair(l, index) }.toMap()
    val ry = ys.toMutableList().sorted().mapIndexed { index, l -> Pair(l, index) }.toMap()

    println(1 - 6 * (xs zip ys).sumOf { (rx[it.first]!! - ry[it.second]!!).toDouble().pow(2) } / (n * (n * n - 1)))
}
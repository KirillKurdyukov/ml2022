package codeforces

import kotlin.math.ln

private fun input() = readLine()!!.split(" ").map(String::toInt)

fun main() {
    val (k1, k2) = input()
    val (n) = input()

    val pairs = mutableListOf<Pair<Int, Int>>()
    (0 until n).forEach {
        val (x, y) = input()
        pairs.add(Pair(x, y))
    }

    val px = pairs.groupBy {
        it.first
    }.mapValues { it.value.size.toDouble() }

    println(pairs.groupBy {
        it.first
    }.mapValues {
        it.value.groupBy {
            t -> t.second
        }.mapValues { i -> i.value.size }
    }.map {
        it.value.map { el ->
            -1.0 * el.value / n * ln(el.value / px[it.key]!!)
        }.reduce(Double::plus)
    }.reduce(Double::plus))
}
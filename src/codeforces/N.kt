package codeforces

import kotlin.math.pow

private fun input() = readLine()!!.split(" ").map(String::toLong)

fun evalE(ys: List<Long>, size: Int, py: Double): Double {
    return ys
        .groupBy { it }
        .mapValues { it.value.size }
        .map { it.key * (it.value.toDouble() / size) / py }
        .reduce(Double::plus)
}

fun main() {
    val (k) = input()
    val (n) = input()

    val pair = mutableListOf<Pair<Long, Long>>()

    for (i in 1..n) {
        val (x, y) = input()
        pair.add(Pair(x, y))
    }

    val p = pair.groupBy {
        it.first
    }.mapValues { it.value.size.toDouble() / pair.size }

    val category = pair.groupBy {
        it.first
    }.mapValues { it.value.map { i -> i.second } }


    println(category.map {
        p[it.key]!! * (evalE(it.value.map { i -> i * i }, pair.size, it.value.size.toDouble() / pair.size) -
                evalE(it.value, pair.size, it.value.size.toDouble() / pair.size).pow(2))
    }.reduce(Double::plus))
}
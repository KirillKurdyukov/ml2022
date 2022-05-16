package codeforces

private fun input() = readLine()!!.split(" ").map(String::toInt)

fun main() {
    val (k1, k2) = input()
    val (n) = input()

    val pair = mutableListOf<Pair<Int, Int>>()
    val ni = LongArray(k1 + 1)
    val nj = LongArray(k2 + 1)

    for (i in 0 until n) {
        val (x, y) = input()
        ni[x]++; nj[y]++
        pair.add(Pair(x, y))
    }

    println(n * (pair.groupBy { it }
        .mapValues { it.value.size }
        .map { it.value * it.value.toDouble() / (ni[it.key.first] * nj[it.key.second]) }
        .reduce(Double::plus) - 1))
}
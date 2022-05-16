package codeforces

private fun input() = readLine()!!.split(" ").map(String::toLong)

private fun summary(list: List<Long>): Long {
    var l: Long = 0;
    var r = MutableList(list.size - 1) { list[it + 1] - list[it] }
        .runningReduce(Long::plus).sum()
    var res = r
    for (i in 1 until list.size) {
        l += i * (list[i] - list[i - 1])
        r -= (list.size - i) * (list[i] - list[i - 1])
        res += r + l
    }

    return res
}

fun main() {
    val (k) = input()
    val (n) = input()

    val pair = mutableListOf<Pair<Long, Long>>()

    for (i in 1..n) {
        val (x, y) = input()
        pair.add(Pair(x, y))
    }

    pair.sortBy { it.first }

    val out = summary(pair.map { it.first })
    val classes: Map<Long, Long> = pair.groupBy {
        it.second
    }.mapValues { summary(it.value.map { i -> i.first }) }
    val `in` = classes.values.sum()

    println(`in`)
    println(out - `in`)
}
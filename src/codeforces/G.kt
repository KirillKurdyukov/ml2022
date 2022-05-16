package codeforces

private fun input() = readLine()!!.split(" ").map { it.toInt() }.first()

fun main() {
    val m = input()

    val f = Array(1 shl m) { input() }
    val countF = f.groupingBy { it }.eachCount()
    for ((key, value) in countF) {
        if (value == f.size) {
            println(
                """
                1
                1
            """.trimIndent()
            )
            (1..m).forEach { _ -> print("0 ") }
            print(if (key == 0) "-0.5" else "0.5")
            return
        }
    }


    if (countF[0]!! >= countF[1]!!) {
        println(
            """
                2
                ${countF[1]} 1
            """.trimIndent()
        )
        f.forEachIndexed { index, i ->
            if (i == 1) {
                var bit = index
                var count = 0.5
                (0 until m).forEach { _ ->
                    if (bit % 2 == 1) {
                        print("1.0 ")
                        count--
                    } else {
                        print("-1.0 ")
                    }
                    bit /= 2
                }
                println("$count")
            }
        }
        for (i in 1..countF[1]!!) {
            print("1 ")
        }
        print("-0.5")
    } else {
        println(
            """
                2
                ${countF[0]} 1
            """.trimIndent())
        f.forEachIndexed { index, i ->
            if (i == 0) {
                var bit = index
                var count = -0.5
                (0 until m).forEach { _ ->
                    if (bit % 2 == 1) {
                        print("-1.0 ")
                        count++
                    } else {
                        print("1.0 ")
                    }
                    bit /= 2
                }
                println("$count")
            }
        }
        for (i in 1..countF[0]!!) {
            print("1 ")
        }
        print("${0.5 - countF[0]!!}")
    }
}
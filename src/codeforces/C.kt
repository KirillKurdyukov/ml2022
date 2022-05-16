package codeforces

import java.lang.RuntimeException
import kotlin.math.*

fun read() = readLine()!!.split(" ").map(String::toInt)

data class Point(
    val coordinates: List<Int>,
    val y: Int,
    val dist: Double
)

const val e = 10e-10
fun main() {
    val (n, m) = read()

    val data: List<Pair<List<Int>, Int>> = Array(n) { read() }.map { Pair(it.dropLast(1), it.last()) }
    val `object` = read()

    val measure: (List<Int>, List<Int>) -> Double = when (readLine()) {
        "manhattan" -> { a, b -> (a zip b).map { abs(it.first - it.second) }.reduce(Int::plus).toDouble() }
        "euclidean" -> { a, b ->
            sqrt((a zip b).map { (it.first - it.second) * (it.first - it.second) }.reduce(Int::plus).toDouble())
        }
        "chebyshev" -> { a, b -> (a zip b).map { abs(it.first - it.second) }.reduce(::max).toDouble() }
        else -> throw RuntimeException("No data dist")
    }

    val core: (u: Double) -> Double = when (readLine()) {
        "uniform" -> { u -> if (u < 1.0) 0.5 else 0.0 }
        "triangular" -> { u -> if (u < 1.0) 1.0 - u else 0.0 }
        "epanechnikov" -> { u -> if (u < 1.0) 0.75 * (1 - (u * u)) else 0.0 }
        "quartic" -> { u -> if (u < 1.0) (15.0 / 16.0) * (1.0 - u * u).pow(2) else 0.0 }
        "triweight" -> { u -> if (u < 1.0) (35.0 / 32.0) * (1.0 - u * u).pow(3) else 0.0 }
        "tricube" -> { u -> if (u < 1.0) (70.0 / 81.0) * (1 - u.pow(3)).pow(3) else 0.0 }
        "gaussian" -> { u -> exp(-0.5 * u * u) / sqrt(2 * Math.PI) }
        "cosine" -> { u -> if (u < 1.0) (Math.PI / 4) * cos(Math.PI * u / 2) else 0.0 }
        "logistic" -> { u -> 1.0 / (exp(u) + 2 + exp(-u)) }
        "sigmoid" -> { u -> (2.0 / Math.PI) / (exp(u) + exp(-u)) }
        else -> throw RuntimeException("No data dist")
    }

    val type = readLine()
    val (hOrk) = read()

    val sortedData = data.map { p -> Point(p.first, p.second, measure(p.first, `object`)) }.sortedBy { it.dist }

    val window = when (type) {
        "fixed" -> hOrk.toDouble()
        "variable" -> sortedData[hOrk].dist
        else -> throw RuntimeException("No data dist")
    }

    if (window < e) {
        val tags = if (sortedData[0].coordinates == `object`)
             sortedData.filter { it.coordinates == (`object`) } else sortedData
        print(tags.map { it.y }.reduce(Int::plus).toDouble() / tags.size)
    } else {
        val denominator = sortedData.map { core(it.dist / window) }.reduce(Double::plus)

        if (denominator < e) {
            print(sortedData.map { it.y }.reduce(Int::plus).toDouble() / sortedData.size)
        } else {
            print(sortedData.map { it.y * core(it.dist / window) }.reduce(Double::plus) / denominator)
        }
    }
}
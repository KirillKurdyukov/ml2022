package codeforces

import java.util.*
import kotlin.math.abs

const val eps = 10e-6
fun isZero(z: Double) = abs(z) < eps
fun precision(tp: Double, fp: Double) = if (isZero(tp + fp)) 0.0 else tp / (tp + fp)
fun recall(tp: Double, fn: Double) = if (isZero(tp + fn)) 0.0 else tp / (tp + fn)
fun `f-score`(precision: Double, recall: Double): Double =
    if (isZero(precision + recall)) 0.0 else 2.0 * precision * recall / (precision + recall)

fun fpCount(cm: Array<DoubleArray>, i: Int): Double {
    var res = 0.0
    for (j in cm.indices) if (j != i) res += cm[j][i]
    return res
}

fun fnCount(cm: DoubleArray, i: Int): Double {
    var res = 0.0
    for (j in cm.indices) if (j != i) res += cm[j]
    return res
}

data class Triple(val tp: Double, val fp: Double, val fn: Double)

fun main() {
    val sc = Scanner(System.`in`)
    val k = sc.nextInt()

    val countClass = DoubleArray(k)
    val confusionMatrix = Array(k) { DoubleArray(k) }
    for (i in 0 until k) {
        for (j in 0 until k) {
            confusionMatrix[i][j] = sc.nextInt().toDouble()
            countClass[i] += confusionMatrix[i][j]
        }
    }

    val all = countClass.reduce(Double::plus)

    val triples = Array(k) {
        Triple(
            confusionMatrix[it][it],
            fpCount(confusionMatrix, it),
            fnCount(confusionMatrix[it], it)
        )
    }

    val pairsPAndR = triples.mapIndexed { _, it ->
        Pair(
            precision(it.tp, it.fp),
            recall(it.tp, it.fn)
        )
    }

    val fScores = pairsPAndR.mapIndexed { i, it -> `f-score`(it.first, it.second) * countClass[i] }

    println(
        triples
            .mapIndexed { i, it -> Triple(it.tp * countClass[i], it.fp * countClass[i], it.fn * countClass[i]) }
            .reduce { acc, it -> Triple(acc.tp + it.tp, acc.fp + it.fp, acc.fn + it.fn) }
            .run {
                Pair(precision(tp, fp), recall(tp, fn))
            }.run {
                `f-score`(first, second)
            }
    )

    println(pairsPAndR.mapIndexed { i, it -> Pair(it.first * countClass[i], it.second * countClass[i]) }
        .reduce { acc, pair -> Pair(acc.first + pair.first, acc.second + pair.second) }
        .run { `f-score`(first / all, second / all) })

    println(fScores.reduce(Double::plus) / all)
}

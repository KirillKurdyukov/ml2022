private fun input() = readLine()!!.split(" ").map(String::toInt)
private fun readDoc(): Pair<Int, Set<String>> {
    val str = readLine()!!.split(" ")

    return Pair(str[0].toInt() - 1, str.subList(2, str.size).toSet())
}

class NaiveBayesClassifier(
    private val n: Int,
    private val k: Int,
    private val lambdas: List<Int>,
    private val alpha: Int,
) {
    companion object {
        private const val q = 2
    }

    private val words = HashSet<String>()
    private val classSize = IntArray(k)
    private val classWordCount = List(k) { HashMap<String, Int>() }
    private val classWordProbability = List(k) { HashMap<String, Double>() }

    fun fit(df: Array<Pair<Int, Set<String>>>) {
        df.forEach {
            words.addAll(it.second)
            classSize[it.first]++
            it.second.forEach { word ->
                classWordCount[it.first].merge(word, 1, Int::plus)
            }
        }

        classWordCount.forEachIndexed { i, it ->
            it.forEach { (key, value) ->
                classWordProbability[i][key] = (alpha + value).toDouble() / (classSize[i] + alpha * q)
            }
        }
    }

    fun predict(message: Set<String>): List<Double> {
        var sumProbability = 0.0

        return List(k) {
            var prob: Double = (classSize[it].toDouble() * lambdas[it]) / n

            words.forEach { word ->
                val cur = classWordProbability[it][word] ?: (alpha.toDouble() / (classSize[it] + q * alpha))
                prob *= if (message.contains(word)) cur else 1 - cur
            }
            sumProbability += prob
            prob
        }.map { it / sumProbability }
    }
}

fun main() {
    val (k) = input()
    val lambdas = input()
    val (alpha) = input()
    val (n) = input()
    val df = Array(n) { readDoc() }
    val classifier = NaiveBayesClassifier(n, k, lambdas, alpha)
    classifier.fit(df)

    val (m) = input()
    for (i in 1 .. m) {
        val str = readLine()!!.split(" ")
        classifier.predict(str.subList(1, str.size).toSet()).forEach {
            print("$it ")
        }
        println()
    }
}

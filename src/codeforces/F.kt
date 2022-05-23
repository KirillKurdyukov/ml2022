package codeforces

import java.io.*
import java.util.*
import kotlin.math.log2

private data class Node(
    val feature: Int? = null,
    val threshold: Double? = null,
    val left: Node? = null,
    val right: Node? = null,
    val value: Int? = null,
    val id: Int
) {
    fun isLeaf() = value != null

    override fun toString(): String = if (isLeaf())
        "C ${value?.plus(1)}" else "Q ${feature!! + 1} $threshold ${left!!.id} ${right!!.id}"
}

class DecisionTreeClassifier(
    private val countFeatures: Int,
    private val maxDepth: Int = 10,
    private val n: Int,
) {
    private lateinit var root: Node
    var vertexId = 0
        private set

    fun fit(objects: Array<Pair<IntArray, Int>>, countClasses: IntArray) {
        root = buildTree(objects, countClasses)
    }

    fun printTree() = recursivePrintNode(root)

    private fun entropy(list: IntArray, size: Int) = if (n < 1000) {
        var acc = 0.0
        for (i in list) {
            val p = i.toDouble() / size
            acc += if (p > 0.0) p * log2(p) else 0.0
        }
        -acc
    } else {
        var acc = 0.0
        for (i in list) {
            val p = i.toDouble() / size
            acc += p * (1 - p)
        }
        acc
    }

    private fun isFinished(
        depth: Int,
        classLabels: IntArray,
    ): Boolean {
        var cn = 0
        for (i in classLabels) {
            cn += if (i > 0) 1 else 0
        }

        return depth >= maxDepth || cn <= 1
    }

    private fun recursivePrintNode(node: Node) {
        if (node.isLeaf()) {
            println(node)
        } else {
            println(node)
            recursivePrintNode(node.left!!)
            recursivePrintNode(node.right!!)
        }
    }

    private fun getLabelClass(countClasses: IntArray): Int {
        var mx = 0
        var ans = 0
        for (i in countClasses.indices) {
            if (mx < countClasses[i]) {
                ans = i
                mx = countClasses[i]
            }
        }

        return ans
    }

    private fun informationGain(
        entropyY: Double,
        parentSize: Int,
        countClassesLeft: IntArray,
        sizeLeft: Int,
        countClassesRight: IntArray,
        sizeRight: Int,
    ): Double = if (sizeLeft == 0 || sizeRight == 0) 0.0 else entropyY - (sizeLeft.toDouble() / parentSize) *
            entropy(countClassesLeft, sizeLeft) - (sizeRight.toDouble() / parentSize) *
            entropy(countClassesRight, sizeRight)


    private fun bestSplit(
        objects: Array<Pair<IntArray, Int>>,
        countClasses: IntArray,
    ): Pair<Int, Double> {
        val entropyY: Double = entropy(countClasses, objects.size)
        var bestScore = Double.MIN_VALUE
        var bestThreshold = 0.0
        var bestFeature = 0

        for (feature in 0 until countFeatures) {
            objects.sortBy { it.first[feature] }
            val countClassesLeft = IntArray(countClasses.size)
            val countClassesRight = countClasses.copyOf()
            var sizeLeft = 0
            var sizeRight = objects.size
            if (objects.first().first[feature] == objects.last().first[feature]) {
                if (bestScore < 0.0) {
                    bestScore = 0.0
                    bestFeature = feature
                    bestThreshold = objects.first().first[feature].toDouble()
                }
                continue
            }

            var prevThreshold = 0
            objects.forEach {
                val threshold = it.first[feature]

                if (threshold == prevThreshold) {
                    countClassesLeft[it.second]++
                    countClassesRight[it.second]--
                    sizeLeft++
                    sizeRight--
                    return@forEach
                }

                val score =
                    informationGain(entropyY, objects.size, countClassesLeft, sizeLeft, countClassesRight, sizeRight)

                if (score > bestScore) {
                    bestScore = score
                    bestFeature = feature
                    bestThreshold = (threshold + prevThreshold) / 2.0
                }
                countClassesLeft[it.second]++
                countClassesRight[it.second]--
                sizeLeft++
                sizeRight--
                prevThreshold = threshold
            }
        }

        return Pair(bestFeature, bestThreshold)
    }

    private fun buildTree(
        objects: Array<Pair<IntArray, Int>>,
        countClasses: IntArray,
        depth: Int = 0,
    ): Node {
        if (isFinished(depth, countClasses)) {
            vertexId++
            return Node(value = getLabelClass(countClasses), id = vertexId)
        }

        val (feature, threshold) = bestSplit(objects, countClasses)
        val left = mutableListOf<Pair<IntArray, Int>>()
        val countLeftClasses = IntArray(countClasses.size)
        val right = mutableListOf<Pair<IntArray, Int>>()
        val countRightClasses = IntArray(countClasses.size)

        objects.forEach {
            if (it.first[feature] < threshold) {
                left.add(it)
                countLeftClasses[it.second]++
            } else {
                right.add(it)
                countRightClasses[it.second]++
            }
        }

        return if (left.size == 0 || right.size == 0) {
            vertexId++
            Node(value = getLabelClass(countClasses), id = vertexId)
        } else {
            val currentVertexId = ++vertexId
            val leftNode = buildTree(left.toTypedArray(), countLeftClasses, depth + 1)
            val rightNode = buildTree(right.toTypedArray(), countRightClasses, depth + 1)
            Node(feature, threshold, leftNode, rightNode, id = currentVertexId)
        }
    }
}

class FastReader(input: InputStream) {
    var br: BufferedReader
    var st: StringTokenizer? = null

    init {
        br = BufferedReader(InputStreamReader(input))
    }

    operator fun next(): String? {
        while (st == null || !st!!.hasMoreElements()) {
            st = try {
                StringTokenizer(br.readLine())
            } catch (e: Exception) {
                return null
            }
        }
        return st!!.nextToken()
    }

    fun nextInt(): Int {
        return next()!!.toInt()
    }
}

fun main() {
    val fastReader = FastReader(System.`in`)
    val m = fastReader.nextInt()
    val k = fastReader.nextInt()
    val h = fastReader.nextInt()
    val n = fastReader.nextInt()
    val countClasses = IntArray(k)
    val objects = Array(n) {
        val list = IntArray(m) { fastReader.nextInt() }
        val label = fastReader.nextInt()
        countClasses[label - 1]++
        return@Array Pair(list, label - 1)
    }

    val tree = DecisionTreeClassifier(m, h, n)
    tree.fit(objects, countClasses)
    println(tree.vertexId)
    tree.printTree()
}

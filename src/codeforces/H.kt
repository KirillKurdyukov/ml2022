package codeforces

import kotlin.math.max
import kotlin.math.tanh

private fun input(): List<Int> = readLine()!!.split(" ").map { it.toInt() }

sealed class Vertex {
    private val zero by lazy { getMatrixValue(0.0) }

    lateinit var matrix: Matrix
        protected set

    var derivative: Matrix? = null
        get() = field ?: zero
        private set

    abstract fun eval(): Matrix
    abstract fun backpropagation()

    fun calc() = if (this::matrix.isInitialized) matrix else eval().also { matrix = it }

    fun addDerivative(derivative: Matrix) {
        this.derivative = this.derivative!! + derivative
    }

    fun getMatrixValue(value: Double) = Matrix(List(matrix.r) { List(matrix.c) { value } }, matrix.r, matrix.c)

    class Matrix(
        private val matrix: List<List<Double>>,
        val r: Int,
        val c: Int
    ) {
        fun mapMatrix(f: (Int, Int, Double) -> Double) = Matrix(matrix.mapIndexed { i, doubles ->
            doubles.mapIndexed { j, d -> f(i, j, d) }
        }, r, c)

        operator fun plus(matrix: Matrix): Matrix = mapMatrix { i, j, d -> matrix[i, j] + d }

        operator fun times(matrix: Matrix): Matrix = Matrix(List(r) { i ->
            List(matrix.c) { j -> (0 until c).map { this.matrix[i][it] * matrix[it, j] }.reduce(Double::plus) }
        }, r, matrix.c)

        fun adamar(matrix: Matrix): Matrix = mapMatrix { i, j, d -> matrix[i, j] * d }

        fun transpose(): Matrix = Matrix(List(c) { i -> List(r) { j -> matrix[j][i] } }, c, r)

        operator fun get(i: Int, j: Int) = matrix[i][j]

        override fun toString(): String = matrix.joinToString("\n", transform = {
            it.joinToString(" ")
        })
    }
}

class Var(private val r: Int, private val c: Int) : Vertex() {
    fun readMatrix(): Matrix = Matrix(List(r) { input().map { it.toDouble() } }, r, c).also { matrix = it }

    override fun eval() = matrix

    override fun backpropagation() = Unit
}

class Tnh(val x: Vertex) : Vertex() {
    override fun eval(): Matrix = x.calc().mapMatrix { _, _, d -> tanh(d) }

    override fun backpropagation() {
        x.addDerivative(matrix.mapMatrix { i, j, tnh -> (1 - tnh * tnh) * derivative!![i, j] })
    }
}

class Rlu(val alpha: Double, val x: Vertex) : Vertex() {
    override fun eval(): Matrix = x.calc().mapMatrix { _, _, d -> max(d, alpha * d) }

    override fun backpropagation() {
        x.addDerivative(matrix.mapMatrix { i, j, d -> derivative!![i, j] * if (d < 0) alpha else 1.0 })
    }
}

class Mul(val a: Vertex, val b: Vertex) : Vertex() {
    override fun eval(): Matrix = (a.calc() * b.calc())

    override fun backpropagation() {
        a.addDerivative(derivative!! * b.matrix.transpose())
        b.addDerivative(a.matrix.transpose() * derivative!!)
    }
}

class Sum(val us: List<Vertex>) : Vertex() {
    override fun eval(): Matrix = us.map { it.calc() }.reduce(Matrix::plus)

    override fun backpropagation() {
        us.forEach { it.addDerivative(derivative!!) }
    }
}

class Had(val us: List<Vertex>) : Vertex() {
    override fun eval(): Matrix = us.map { it.calc() }.reduce(Matrix::adamar)

    override fun backpropagation() {
        us.forEachIndexed { indArg, v ->
            v.addDerivative(us.foldIndexed(getMatrixValue(1.0)) { index, acc, vertex ->
                if (index == indArg) acc.adamar(derivative!!) else acc.adamar(vertex.matrix)
            })
        }
    }
}

fun main() {
    val (n, m, k) = input()

    val graph = mutableListOf<Vertex>()
    (1..n).forEach { _ ->
        val args: List<String> = readLine()!!.split(" ")
        val arg = { i: Int -> args[i].toInt() }
        graph.add(
            when (args[0]) {
                "var" -> Var(arg(1), arg(2))
                "tnh" -> Tnh(graph[arg(1) - 1])
                "rlu" -> Rlu(1.0 / arg(1), graph[arg(2) - 1])
                "mul" -> Mul(graph[arg(1) - 1], graph[arg(2) - 1])
                "sum" -> Sum(args.drop(2).map { graph[it.toInt() - 1] })
                "had" -> Had(args.drop(2).map { graph[it.toInt() - 1] })
                else -> throw IllegalArgumentException("${args[0]} isn't correct")
            }
        )
    }
    (0 until m).forEach { (graph[it] as Var).readMatrix() }
    graph.takeLast(k).forEach {
        val res = it.calc()
        println(res)
        it.addDerivative(Var(res.r, res.c).readMatrix())
    }
    graph.reversed().forEach { it.backpropagation() }
    graph.take(m).forEach { println(it.derivative!!) }
}
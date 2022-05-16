import java.util.*

fun <T, U> pairComparator(
        firstComparator: Comparator<T>,
        secondComparator: Comparator<U>
): Comparator<Pair<T, U>> =
        compareBy(firstComparator) { p: Pair<T, U> -> p.first }
                .thenBy(secondComparator) { p: Pair<T, U> -> p.second }

fun main() {
    val sc = Scanner(System.`in`)

    val n: Int = sc.nextInt()
    val m: Int = sc.nextInt()
    val k: Int = sc.nextInt()

    val cs: MutableList<Pair<Int, Int>> = mutableListOf()

    for (i in 1 .. n) {
        cs.add(sc.nextInt() to i)
    }

    cs.sortWith(pairComparator(naturalOrder(), naturalOrder()))

    val batches = MutableList<MutableList<Int>>(k) { mutableListOf() }


    for (i in 1 .. n) {
        batches[i % batches.size].add(cs[i - 1].second)
    }

    batches.forEach { b ->
        print("${b.size} ")
        b.forEach {
            print("$it ")
        }
        println()
    }
}


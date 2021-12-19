package year2021.days

import Utils
import java.util.*

fun day9() {
    val inp = Utils.getListInput(9)
//    val inp = """2199943210
//3987894921
//9856789892
//8767896789
//9899965678""".split("\n")
    var s = 0;
    val locs = mutableListOf<Pair<Int, Int>>();
    val I = inp.size - 1
    val J = inp[0].length - 1
    for (i in 0 .. I) {
        for (j in 0..J) {
            val c = inp[i][j]
            if (
                (i == 0 || c < inp[i-1][j]) &&
                (i == I || c < inp[i+1][j]) &&
                (j == 0 || c < inp[i][j-1]) &&
                (j == J || c < inp[i][j+1])
            ) {
                s += c.digitToInt() + 1
                locs.add(Pair(i, j))
            }
        }
    }
    println(s)

    // part 2
    fun getNeighbors(loc: Pair<Int, Int>): Sequence<Pair<Int, Int>> {
        val (i, j) = loc
        return sequence {
            if (i > 0) yield(Pair(i - 1, j))
            if (i < I) yield(Pair(i + 1, j))
            if (j > 0) yield(Pair(i, j - 1))
            if (j < J) yield(Pair(i, j + 1))
        }
    }

    val basins = locs.map { loc ->
        val toExplore = Stack<Pair<Int, Int>>()
        val explored = mutableSetOf<Pair<Int, Int>>()
        toExplore.add(loc)
        while (toExplore.size > 0) {
            val loc = toExplore.pop()
            explored.add(loc)
            for (n in getNeighbors(loc)) {
                val (i, j) = n
                if (!explored.contains(n) && inp[i][j] != '9') {
                    toExplore.add(n)
                }
            }
        }
        explored.size
    }
    print(basins.sortedDescending().take(3).fold(1, Int::times))
    return

}
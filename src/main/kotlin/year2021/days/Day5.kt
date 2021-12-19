package year2021.days

import Utils
import year2021.Line
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun day5(): List<Int> {
    val inp = Utils.getListInput(5)
    val lines: List<Line>  = inp.map{it.split(" -> ").flatMap{it.split(",").map {it.toInt()}}}
    val xmax = lines.maxOf {l -> l.maxOf { it }} + 1
    val matrix = Array(xmax) { IntArray(xmax) {0} }
    for ((x1, y1, x2, y2) in lines) {
        if (x1 == x2 || y1 == y2) {
            for (i in min(x1, x2)..max(x1, x2)) for (j in min(y1, y2)..max(y1, y2))
                matrix[j][i]++
        }
        // Comment out the following `else if` statement for part 1 only
        else if (abs(x1 - x2) == abs(y1 - y2)) {
            val dx = if (x2 > x1) 1 else -1
            val dy = if (y2 > y1) 1 else -1
            for (c in 0..abs(x1 - x2)) {
                matrix[y1 + c * dy][x1 + c * dx] += 1
            }
        }
    }
    var c = 0
    for (i in 0 until xmax) for (j in 0 until xmax) c += if(matrix[i][j] >= 2) 1 else 0
    return listOf(c);
}
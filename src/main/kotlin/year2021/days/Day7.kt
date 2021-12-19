package year2021.days

import Utils
import kotlin.math.abs
import kotlin.math.roundToInt

fun day7() {
    val inp = Utils.getListInput(7)[0]
    val inf = "16,1,2,0,4,2,7,1,2,14"
    var crabs = inp.split(",").map{ it.toInt() }.toMutableList()
    crabs.sort()
    val pos = crabs[crabs.size / 2]
    val cost = crabs.map { abs(it - pos) }.sum()
    println(cost)

    val avg = (crabs.sum().toDouble()/ crabs.size).roundToInt()
    fun cfun(av: Int, crab: Int): Int {
        val n = abs(crab - av)
        return n * (n + 1) / 2
    }
    val costfun = {av: Int -> crabs.map{c -> cfun(av, c)}.sum()}
    val cost2 = (450..475).minOf(costfun)
    println(cost2)
    return

}
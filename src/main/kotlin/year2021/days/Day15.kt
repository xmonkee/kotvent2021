package year2021.days

import Utils

fun day15() {
    val inp = Utils.getListInput(15).map{it.toList().map{it.digitToInt()}}
//    val inp = """1163751742
//1381373672
//2136511328
//3694931569
//7463417111
//1319128137
//1359912421
//3125421639
//1293138521
//2311944581""".split("\n").map{it.toList().map{it.digitToInt()}}

    fun solve(multiplier: Int) {

        fun getInp(i: Int, j: Int): Int {
            val I = inp.size
            val J = inp[0].size
            val _i = i % I
            val _j = j % J
            val orig = inp[_i][_j]
            val d = (i / I) + (j / J)
            var it = (orig + d)
            while (it > 9) it -= 9
            return it
        }
        val I = inp.size * multiplier
        val J = inp[0].size * multiplier
        val Inp = Array(I) {i -> Array<Int>(J) {j -> getInp(i, j)} }
        val scores = Array(I) { Array<Int>(J) {Int.MAX_VALUE} }
        scores[0][0] = 0
        repeat(10) {
            val curr = scores[I-1][J-1]
            for(i in 0 until I) {
                for (j in 0 until J) {
                    if (i == 0 && j == 0) continue
                    val s1 = if (i > 0) scores[i-1][j] else Int.MAX_VALUE
                    val s2 = if (j > 0) scores[i][j-1] else Int.MAX_VALUE
                    val s3 = if (i < I - 1) scores[i+1][j] else Int.MAX_VALUE
                    val s4 = if (j < J - 1) scores[i][j+1] else Int.MAX_VALUE
                    val min = listOf(s1, s2, s3, s4).minOrNull()!!
                    scores[i][j] = min + Inp[i][j]
//                val minIdx = listOf(s1, s2, s3, s4).indexOf(min)
                }
            }
            println(scores.last().last())
        }
//    println(Inp.map{it.joinToString(""){it.toString().padStart(1)} }.joinToString ("\n") + "\n")
//    println(scores.map{it.joinToString(" "){it.toString().padStart(2)} }.joinToString ("\n") + "\n")
        println(scores.last().last())
    }
//    solve(1) // part 1
    solve(5) // part 2
}
package year2021.days

import Utils
import kotlin.math.max

fun day13() {
    val inp = Utils.getRawInput(13)
//    val inp = """6,10
//0,14
//9,10
//0,3
//10,4
//4,11
//6,0
//6,12
//4,1
//0,13
//10,12
//3,4
//3,0
//8,4
//1,10
//2,14
//8,10
//9,0
//
//fold along y=7
//fold along x=5"""
    val (_points, _instrs) = inp.split("\n\n")
    val points = _points.split("\n").map{p -> p.split(",").map(String::toInt)}
    val instrs = _instrs.split("\n").filter { it.isNotEmpty() }

    var X = points.map{it[0]}.maxOrNull()!!
    var Y = points.map{it[1]}.maxOrNull()!!

    val grid = Array(Y) {IntArray(X) {0} }

    fun fold(instr: String) {
        val (dir, n) = instr.split(" ").last().split("=")
        if (dir == "x") { // fold left
            val _x = n.toInt()
            for (j in 0 until Y) {
                var i = 1; while (_x + i < X) {
                    grid[j][_x - i] = max(grid[j][_x - i], grid[j][_x + i])
                    i++
                }
            }
            X = _x
        }
        if (dir == "y") { // fold up
            val _y = n.toInt()
            for (i in 0 until X) {
                var j = 1; while (_y + j < Y) {
                    grid[_y - j][i] = max(grid[_y - j][i], grid[_y + j][i])
                    j++
                }
            }
            Y = _y
        }
    }

    fun printGrid() {
        for (j in 0 until Y) {
            for (i in 0 until X) {
                print(if(grid[j][i] == 1) '#' else ' ')
            }
            println()
        }
        println()
    }

    fun countDots(): Int {
        return grid.take(Y).map{it.take(X).sum()}.sum()
    }

    points.forEach{(x, y) -> grid[y][x] = 1}
    fold(instrs[0])
    println(countDots())

    // part 2

    instrs.drop(1).forEach{ fold(it) }
    printGrid()

}
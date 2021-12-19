package year2021.days

import Utils
import java.util.*

fun day11() {
    val inp = Utils.getListInput(11)
//    val inp = """5483143223
//2745854711
//5264556173
//6141336146
//6357385478
//4167524645
//2176841721
//6882881134
//4846848554
//5283751526""".split("\n")
    val inpp = inp.map{row -> row.map{c -> c.digitToInt()}.toMutableList()}

    val H = inpp.size
    val W = inpp[0].size

    var flashes = 0

    fun flash(i: Int, j: Int): List<Pair<Int, Int>> {
//        println(inpp.map{it.joinToString(" "){it.toString().padStart(2)} }.joinToString ("\n") + "\n")
        flashes++;
        val toFlash = mutableListOf<Pair<Int, Int>>()
        for(k in i-1..i+1)
            for (l in j-1..j+1)
                if (k in 0 until H && l in 0 until W)
                    if (k != i || l != j)
                        if(++inpp[k][l] == 10)
                            toFlash.add(Pair(k, l))
        return toFlash
    }

    fun step() {
        val toFlash = Stack<Pair<Int, Int>>()

        for (i in 0 until H) {
            for (j in 0 until W) {
                inpp[i][j]++
                if(inpp[i][j] == 10) {
                    toFlash.add(Pair(i, j))
                }
            }
        }

        while(toFlash.isNotEmpty()) {
            val (i, j) = toFlash.pop()
            toFlash.addAll(flash(i, j))
        }

        inpp.forEach{it.replaceAll { if (it > 9) 0 else it }}
//        println(inpp.map{it.joinToString(" "){it.toString().padStart(2)} }.joinToString ("\n") + "\n")
    }
//    println(inpp.map{it.joinToString(" "){it.toString().padStart(2)} }.joinToString ("\n") + "\n")
    (1..100).forEach{
        step()
    }
    println(inpp.map{it.joinToString(" "){it.toString().padStart(2)} }.joinToString ("\n") + "\n")
    println(flashes)

    // part 2

    var steps = 100
    while (!inpp.all {row -> row.all{it == 0}}) {
        step()
        steps++
    }
    println(steps)
}
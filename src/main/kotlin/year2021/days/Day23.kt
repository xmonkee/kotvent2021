package year2021.days

import java.lang.Math.abs

typealias Config = List<List<Char>>
data class Point2(val i: Int, val j: Int)

fun day23() {
    val start = """
#############
#...........#
###A#D#A#B###
  #C#C#D#B#  
  #########  
""".trimIndent().split("\n").map(String::toList)

    val finished = """
#############
#...........#
###A#B#C#D###
  #A#B#C#D#  
  #########  
""".trimIndent().split("\n").map(String::toList)

    val I = start.size
    val J = start[0].size

    val scoreMap = mapOf('A' to 1, 'B' to 10, 'C' to 100, 'D' to 1000)
    val homeCols = "ABCD".associateWith { finished[2].indexOf(it) }
    println(homeCols)

    fun move(c1: Config, i1: Int, j1: Int, i2: Int, j2: Int): Config {
        return List(I) { i ->
            List(J) { j ->
                when (i to j) {
                    i1 to j1 -> c1[i2][j2]
                    i2 to j2 -> c1[i1][j1]
                    else -> c1[i][j]
                }
            }
        }
    }

    fun isFinished(c: Config): Boolean {
        for ((char, x) in listOf('A' to 3, 'B' to 5, 'C' to 7, 'D' to 9)) {
            if (c[2][x] != char || c[3][x] != char) return false
        }
        return true
    }
    fun corrIsClear(conf: Config, src: Int, trg: Int): Boolean {
        val (left, right) = if(src < trg) src to trg else trg to src
        if (conf[1].slice(left+1 until right).any {it != '.'}) return false
        return conf[1][trg] == '.'
    }
    fun getValidMoves(conf: Config, i: Int, j: Int) = sequence {
        val c = conf[i][j]
        val homeCol = homeCols[c]!!
        if (i == 1) { // corridor
            if (corrIsClear(conf, j, homeCol)) { // path is clear
                if (conf[2][homeCol] == '.') {
                    if (conf[3][homeCol] == '.') yield(Point2(3, homeCol))
                    if (conf[3][homeCol] == c) yield(Point2(2, homeCol))
                }
            }
        } else { // 2 or 3
            if (j != homeCol) {
                if ((i == 2 || conf[2][j] == '.') && conf[1][j] == '.') {
                    for (jt in 1..11) if (corrIsClear(conf, j, jt) && jt !in listOf(3, 5, 7, 9)) yield(Point2(1, jt))
                    if (conf[2][homeCol] == '.' && corrIsClear(conf, j, homeCol)) {
                        if (conf[3][homeCol] == '.') yield(Point2(3, homeCol))
                        else if (conf[3][homeCol] == c) yield(Point2(2, homeCol))
                    }
                }
            } else if (i == 2 && conf[3][j] != c && conf[1][j] == '.'){
                for (jt in 1..11) if (corrIsClear(conf, j, jt) && jt !in listOf(3, 5, 7, 9)) yield(Point2(1, jt))
            }
        }
    }

    val costMap = mutableMapOf<Config, Int>()
    fun findMinScore(start: Config): Triple<Config, MutableMap<Config, Config>, MutableMap<Config, Int>> {
        val done = mutableSetOf<Config>()
        val dist = mutableMapOf<Config, Int>()
        val dist2 = mutableMapOf<Config, Int>()
        val prev = mutableMapOf<Config, Config>()
        dist[start] = 0
        dist2[start] = 0
        while (true) {
            val (conf, score) = dist.toList().minByOrNull { (k, v) -> v }!!
            done.add(conf)
            dist.remove(conf)

//            println(conf.map{it.joinToString("")}.joinToString("\n"))
//            println(score.toString() + "\n")
            if (isFinished(conf)) return Triple(conf, prev, dist2)

            for (i in 1 until I-1) for (j in 1 until J -1 ) {
                if (conf[i][j] in "ABCD") {
                    nbors@ for (t in getValidMoves(conf, i, j)) {
                        val k = t.i
                        val l = t.j
                        if (conf[k][l] == '.') {
                            val next = move(conf, i, j, k, l)
                            if (next in done) continue@nbors
                            val nscore = score + scoreMap[conf[i][j]]!! * (abs(j - l) + abs(i - 1) + abs(k - 1))
                            val pscore = dist[next] ?: Int.MAX_VALUE
                            if (nscore < pscore) {
                                dist[next] = nscore
                                dist2[next] = nscore
                                prev[next] = conf
                            }
                        }
                    }
                }
            }
        }
    }

    var (curr, prev, dist) = findMinScore(start)
    while(curr in prev) {
        println(curr.map{it.joinToString("")}.joinToString("\n"))
        println(dist[curr])
        println()
        curr = prev[curr]!!
    }
    println(curr.map{it.joinToString("")}.joinToString("\n"))

//    val testIn = """#############
//#.B.....A.D.#
//###.#C#B#.###
//  #A#D#C#.#
//  #########  """.split("\n").map(String::toList)
//
//    println(getValidMoves(testIn, 1, 8).toList())
//    for (m in getValidMoves(testIn, 1, 8)) {
//        val out = move(testIn, 1, 8, m.i, m.j)
//        println(out.map{it.joinToString("")}.joinToString("\n"))
//    }

}

fun day23_2() {
//    val start = """
//#############
//#...........#
//###B#C#B#D###
//  #D#C#B#A#
//  #D#B#A#C#
//  #A#D#C#A#
//  #########
//""".trimIndent().split("\n").map(String::toList)
    val start = """
#############
#...........#
###A#D#A#B###
  #D#C#B#A#  
  #D#B#A#C#  
  #C#C#D#B#  
  #########  
""".trimIndent().split("\n").map(String::toList)

    val finished = """
#############
#...........#
###A#B#C#D###
  #A#B#C#D#  
  #A#B#C#D#  
  #A#B#C#D#  
  #########  
""".trimIndent().split("\n").map(String::toList)

    val I = start.size
    val J = start[0].size

    val scoreMap = mapOf('A' to 1, 'B' to 10, 'C' to 100, 'D' to 1000)
    val homeCols = "ABCD".associateWith { finished[2].indexOf(it) }
    println(homeCols)

    fun move(c1: Config, i1: Int, j1: Int, i2: Int, j2: Int): Config {
        return List(I) { i ->
            List(J) { j ->
                when (i to j) {
                    i1 to j1 -> c1[i2][j2]
                    i2 to j2 -> c1[i1][j1]
                    else -> c1[i][j]
                }
            }
        }
    }

    fun isFinished(c: Config): Boolean {
        for ((char, x) in listOf('A' to 3, 'B' to 5, 'C' to 7, 'D' to 9)) {
            if ((2..5).any {c[it][x] != char})  return false
        }
        return true
    }

    fun corrIsClear(conf: Config, src: Int, trg: Int): Boolean {
        val (left, right) = if(src < trg) src to trg else trg to src
        if (conf[1].slice(left+1 until right).any {it != '.'}) return false
        return conf[1][trg] == '.'
    }

    fun getValidMoves(conf: Config, i: Int, j: Int): List<Point2> {
        val c = conf[i][j]
        val homeCol = homeCols[c]!!
        if (i == 1) { // corridor to room
            if (!corrIsClear(conf, j, homeCol)) return emptyList()
            if ((2..5).any{conf[it][homeCol] !in ".$c"}) return emptyList()
            val ti = (2..5).findLast { conf[it][homeCol] == '.' }!!
            return listOf(Point2(ti, homeCol))
        } else { // room to corridor
            if ((2 until i).any {conf[it][j] != '.'}) return emptyList()
            if (j == homeCol && (i+1..5).all{conf[it][j] == c}) return emptyList()
            return (1..11).filter{it !in listOf(3, 5, 7, 9) && corrIsClear(conf, j, it)}.map{Point2(1, it)}
        }
    }

    fun findMinScore(start: Config): Triple<Config, MutableMap<Config, Config>, MutableMap<Config, Int>> {
        val done = mutableSetOf<Config>()
        val dist = mutableMapOf<Config, Int>()
        val dist2 = mutableMapOf<Config, Int>()
        val prev = mutableMapOf<Config, Config>()
        dist[start] = 0
        dist2[start] = 0
        while (true) {
            val (conf, score) = dist.toList().minByOrNull { (k, v) -> v }!!
            done.add(conf)
            dist.remove(conf)

//            println(conf.map{it.joinToString("")}.joinToString("\n"))
            println(score.toString() + "\n")
            if (isFinished(conf)) return Triple(conf, prev, dist2)

            for (i in 1 until I-1) for (j in 1 until J -1 ) {
                if (conf[i][j] in "ABCD") {
                    nbors@ for (t in getValidMoves(conf, i, j)) {
                        val k = t.i
                        val l = t.j
                        if (conf[k][l] == '.') {
                            val next = move(conf, i, j, k, l)
                            if (next in done) continue@nbors
                            val nscore = score + scoreMap[conf[i][j]]!! * (abs(j - l) + abs(i - 1) + abs(k - 1))
                            val pscore = dist[next] ?: Int.MAX_VALUE
                            if (nscore < pscore) {
                                dist[next] = nscore
                                dist2[next] = nscore
                                prev[next] = conf
                            }
                        }
                    }
                }
            }
        }
    }

    var (curr, prev, dist) = findMinScore(start)
    while(curr in prev) {
        println(curr.map{it.joinToString("")}.joinToString("\n"))
        println(dist[curr])
        println()
        curr = prev[curr]!!
    }
    println(curr.map{it.joinToString("")}.joinToString("\n"))

//    val testIn = """
//#############
//#...........#
//###B#C#.#D###
//  #D#C#.#A#
//  #D#B#C#C#
//  #A#D#A#C#
//  #########  """.trimIndent().split("\n").map(String::toList)
//
//    val (i, j) = 4 to 7
//    println(getValidMoves(testIn, i, j).toList())
//    for (m in getValidMoves(testIn, i, j)) {
//        val out = move(testIn, i, j, m.i, m.j)
//        println(out.map{it.joinToString("")}.joinToString("\n"))
//    }

}

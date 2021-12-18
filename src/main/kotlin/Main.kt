import java.math.BigInteger
import java.security.InvalidAlgorithmParameterException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.*

fun main(args: Array<String>) {
    //println(day1.toList())
    //println(day2.toList())
    //println(day3.toList())
    //println(day4())
//    println(day5())
//    day6()
//    day7()
//    day8()
//    day9()
//    day10()
//    day11()
//    day12()
//    day13()
//    day14()
//    day15()
//    day16()
//    day17()
    day18()
}

typealias Line = List<Int>
typealias Board = List<List<Int>>

inline fun <K, reified V: Number> counter(c: Iterable<K>): Map<K, V> {
    if (1 is V) {
        return c.map {k -> k to c.count {it == k}}.toMap() as Map<K, V>
    } else if (1.toLong() is V) {
        return c.map {k -> k to c.count {it == k}.toLong()}.toMap() as Map<K, V>
    }
    else throw UnsupportedOperationException()
}

val day1 = sequence<Int> {
    val inp = Utils.getNumInput(1)
    yield(inp.windowed(2).sumOf { (x, y) -> if (y > x) 1 as Int else 0 })
    yield(inp.windowed(3).map { x -> x.sum() }.windowed(2).sumOf { (x, y) -> if (y > x) 1 as Int else 0 })
}

val day2 = sequence<Int> {
    val inp = Utils.getListInput(2);

    // One
    var pos = 0
    var dep = 0
    for (instr in inp) {
        val (cmd, value) = instr.split(" ")
        val v = value.toInt()
        when (cmd) {
            "forward" -> pos += v
            "up" -> dep -= v
            "down" -> dep += v
        }
    }
    yield(pos * dep)

    //two
    pos = 0
    dep = 0
    var aim = 0
    for (instr in inp) {
        val (cmd, value) = instr.split(" ")
        val v = value.toInt()
        when (cmd) {
            "forward" -> {
                pos += v; dep += aim * v
            }
            "up" -> aim -= v
            "down" -> aim += v
        }
    }
    yield(pos * dep)
}

val day3 = sequence<Int> {
    val inp = Utils.getListInput(3);
    val sum = inp.fold(IntArray(inp[0].length)) { acc, s ->
        acc.zip(s.toList()).map { (i, c) -> i + c.toString().toInt() }.toIntArray()
    }
    val gamma = sum.map { if (it > inp.size / 2) 1 else 0 }.joinToString("").toInt(2);
    val epsil = sum.map { if (it > inp.size / 2) 0 else 1 }.joinToString("").toInt(2);
    yield(gamma * epsil)

    val oxy = doFilterThing(inp, 0) { c1, c0 -> if (c1 > c0) '1' else if (c1 < c0) '0' else '1' }.toInt(2)
    val co2 = doFilterThing(inp, 0) { c1, c0 -> if (c1 > c0) '0' else if (c1 < c0) '1' else '0' }.toInt(2)
    yield(oxy * co2)
}

fun doFilterThing(inp: List<String>, idx: Int, chooser: (Int, Int) -> Char): String {
    if (inp.size == 1) return inp[0];
    val count1 = inp.count { it[idx] == '1' }
    val count0 = inp.size - count1;
    val filterFor = chooser(count1, count0)
    return doFilterThing(inp.filter { it[idx] == filterFor }, idx + 1, chooser)
}

fun day4(): List<Int> {
    val inp = Utils.getRawInput(4);
    val movesAndBoards = inp.split("\n\n");
    val moves = movesAndBoards[0].split(",").map { it.toInt() }
    val boards: List<Board> = movesAndBoards.drop(1).map {board -> board.split("\n").filter{row -> row.isNotEmpty()}.map {row -> row.trim().split("\\s+".toRegex()).map {num -> num.toInt() } } }
    val cols = { b: Board -> sequence { for (c in 0 until b[0].size) yield(b.map { it[c] }) } }
    val newBoards: List<Board> = boards.map { it + cols(it) }
    val isWinner = { b: Board, n: List<Int> -> b.any { row -> row.all { n.contains(it) } } }
    val winningMoves =
        moves.runningFold(listOf<Int>()) { l, i -> l + i }.first { m -> newBoards.any { isWinner(it, m) } }
    val winner = boards[newBoards.indexOfFirst { isWinner(it, winningMoves) }]
    fun score(b: Board, m: List<Int>): Int {
        var s = 0
        for (row in b) for (el in row) if (!m.contains(el)) s += el
        return s * m.last()
    }
    val part1 = score(winner, winningMoves)
    val losingMoves = moves.runningFold(emptyList(), List<Int>::plus).reversed().first {m -> !newBoards.all {isWinner(it, m)}}
    val losers = boards.filterIndexed { i, _ -> !isWinner(newBoards[i], losingMoves)}
    val losingMovesPlusOne = moves.take(losingMoves.size + 1)
    val part2 = losers.map {score(it, losingMovesPlusOne)}
    return listOf(part1, part2[0])
}

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

fun day6() {
    val inp = Utils.getListInput(6)[0]
    var fish = inp.split(",").map{ it.toInt() }.toMutableList()
    (1..80).forEach { _ ->
        val s = fish.size
        for(i in 0 until s) {
            if (fish[i] == 0) {
                fish[i] = 6
                fish.add(8)
            } else {
                fish[i]--
            }
        }
    }
    println(fish.size)

    // part 2
    //val inf = "3,4,3,1,2"
    fish = inp.split(",").map{ it.toInt() }.toMutableList()
    var counts = Array<BigInteger>(9) {BigInteger.ZERO }
    for (f in fish) {
        counts[f] += BigInteger.ONE
    }

    (1..256).forEach { _ ->
        val ncounts = Array<BigInteger>(9) { BigInteger.ZERO }
        for (i in 1..8) {
            ncounts[i - 1] = counts[i]
        }
        ncounts[8] = counts[0]
        ncounts[6] += counts[0]
        counts = ncounts
//        println(counts.joinToString(","))
    }

    println(counts.reduce {a, b -> a + b})
}

fun day7() {
    val inp = Utils.getListInput(7)[0]
    val inf = "16,1,2,0,4,2,7,1,2,14"
    var crabs = inp.split(",").map{ it.toInt() }.toMutableList()
    crabs.sort()
    val pos = crabs[crabs.size / 2]
    val cost = crabs.map {abs(it - pos)}.sum()
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

fun day8() {
    val inp = Utils.getListInput(8)
    val inpp = inp.map{it.split(" | ")}
    val uniques = inpp.map{ it[1].split(" ").filter {listOf(2, 4, 3, 7).contains(it.length)}}
    val count = uniques.map{ it.size }.sum()
    print(count)

    // part2

    val segmentToNum = hashMapOf<String, Int>(
        "abcefg" to 0,
        "cf" to 1,
        "acdeg" to 2,
        "acdfg" to 3,
        "bcdf" to 4,
        "abdfg" to 5,
        "abdefg" to 6,
        "acf" to 7,
        "abcdefg" to 8,
        "abcdfg" to 9,
    )

    fun <R> permutations(l: List<R>): List<List<R>> {
        if (l.size == 0) return emptyList()
        if (l.size == 1) return listOf(l)
        val first = l[0]
        val rest = l.drop(1)
        val res = mutableListOf<List<R>>()
        for (p in permutations((rest))) {
            for (i in 0..p.size) {
                res.add(p.take(i) + first + p.drop(i))
            }
        }
        return res.toList()
    }

    fun decode(signals: List<String>): String {
        val orig = "abcdefg".toList()
        val permutes = permutations(orig)
        // a permutation represents a mapping from wire to segment. i.e. when permutation = "cabdefg", a wire of "c" means the segment "a"
        perm@ for (p in permutes) {
            // for a valid permutation, each signal will correspond to one of the 10 possible segment configs
            for (s in signals) {
                val segment = s.map{c -> orig[p.indexOf(c)]}.sorted().joinToString("")
                if (!segmentToNum.contains(segment)) continue@perm
            }
            return p.joinToString("")
        }
        return "NONEFOUND"
    }

    fun encode(signal: String, permutation: String): Int {
        val orig = "abcdefg".toList()
        val segment = signal.map{c -> orig[permutation.indexOf(c)]}.sorted().joinToString("")
        return segmentToNum[segment]!!
    }

    var s = 0
    for ((signals, code) in inpp) {
        val permutation = decode(signals.split(" "))
        val code = code.split(" ").map { encode(it, permutation) }.joinToString("").toInt()
        s += code
    }
    println(s)

    return
}

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

fun day10() {
    val inp = Utils.getListInput(10)
//    val inp = """[({(<(())[]>[[{[]{<()<>>
//[(()[<>])]({[<{<<[]>>(
//{([(<{}[<>[]}>{[]{[(<()>
//(((({<>}<{<{<>}{[]{[]{}
//[[<[([]))<([[{}[[()]]]
//[{[{({}]{}}([{[{{{}}([]
//{<[[]]>}<{[{[{[]{()[[[]
//[<(<(<(<{}))><([]([]()
//<{([([[(<>()){}]>(<<{{
//<{([{{}}[<[[[<>{}]]]>[]]""".split("\n")

    val matches = hashMapOf<Char, Char>(
        '(' to ')',
        '[' to ']',
        '{' to '}',
        '<' to '>'
    )

    fun getFirstBadChar(s: String): Char? {
        val stack = Stack<Char>();
        for (c in s) {
            if ("{[<(".toList().contains(c)) {
                stack.push(c)
            } else {
                val t = stack.pop()
                if (matches[t] != c) {
                    return c
                }
            }
        }
        return null
    }
    val badChars = inp.map{getFirstBadChar(it)}.filterNotNull();
    val score = badChars.map{c ->
        when (c) {
            ')' -> 3
            ']' -> 57
            '}' -> 1197
            '>' -> 25137
            else -> 0
        }
    }.sum()
    print(score)


    // part 2

    fun complete(s: String): BigInteger {
        val stack = Stack<Char>();
        for (c in s) {
            if ("{[<(".toList().contains(c)) {
                stack.push(c)
            } else {
                val t = stack.pop()
                if (matches[t] != c) {
                    return BigInteger.ZERO
                }
            }
        }
        var score: BigInteger = BigInteger.ZERO
        while (stack.size > 0) {
            val t = stack.pop()
            score *= BigInteger.valueOf(5)
            score += when(t) {
                '(' -> BigInteger.ONE
                '[' -> BigInteger.TWO
                '{' -> BigInteger.valueOf(3)
                '<' -> BigInteger.valueOf(4)
                else -> BigInteger.ZERO
            }
        }
        println(s)
        println(score)
        return score
    }
    val scores = inp.map{complete(it)}.filter { it > BigInteger.ZERO }.sorted()
    println(scores[scores.size/2])

}

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

fun day12 () {
    val inp = Utils.getListInput(12)
//    val inp = """start-A
//start-b
//A-c
//A-b
//b-d
//A-end
//b-end""".split("\n")
    val graph = mutableMapOf<String, MutableSet<String>>()
    inp.forEach{line->
        val (left, right) = line.split("-")
        if (!graph.contains(left)) graph[left] = mutableSetOf()
        if (!graph.contains(right)) graph[right] = mutableSetOf()
        graph[left]!!.add(right)
        graph[right]!!.add(left)
    }
    val paths = mutableListOf<List<String>>()

    fun isBig(s: String): Boolean {
        return s.uppercase() == s
    }

    fun makePaths(pathSoFar: List<String>) {
        val curr = pathSoFar.last()
        if (curr == "end") {
            paths.add(pathSoFar)
            return
        }
        for (n in graph[curr]!!) {
            if (isBig(n) || !pathSoFar.contains(n)) {
                makePaths(pathSoFar+n)
            }
        }
    }

    makePaths(listOf("start"))
//    println (paths.map{it.joinToString(",")}.joinToString("\n"))
    println(paths.size)

    // Part 2
    val paths2 = mutableListOf<List<String>>()
    fun makePaths2(pathSoFar: List<String>) {
        val curr = pathSoFar.last()
        if (curr == "end") {
            paths2.add(pathSoFar)
            return
        }
        for (n in graph[curr]!!) {
            if (isBig(n) || !pathSoFar.contains(n)) {
                makePaths2(pathSoFar+n)
            }
            else if (n != "start" && !pathSoFar.contains("__dbl__")) {
                makePaths2((pathSoFar + "__dbl__") + n)
            }
        }
    }
    makePaths2(listOf("start"))
//    println (paths2.map{it.joinToString(",")}.joinToString("\n"))
    println(paths2.toSet().size)

}

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

fun day14() {
    val inp = Utils.getRawInput(14)
//    val inp = """NNCB
//
//CH -> B
//HH -> N
//CB -> H
//NH -> C
//HB -> C
//HC -> B
//HN -> C
//NN -> C
//BH -> H
//NC -> B
//NB -> B
//BN -> B
//BB -> N
//BC -> B
//CC -> N
//CN -> C"""

    val (temp, _instrs) = inp.split("\n\n")
    val rules = _instrs.split("\n").filter { it.isNotEmpty() }.map{
        val (left, right) = it.split(" -> ")
        left to right.first()
    }.toMap()

    val pairs = temp.windowed(2)
//    var pairCounts = pairs.toSet().associateWith { p -> pairs.count { it == p }.toLong() }.toMutableMap().withDefault { 0 }

    var pairCounts: Map<String, Long> = counter(pairs)
    val charCounts = counter<Char, Long>(temp.toList()).toMutableMap()

    for (x in 1..40) {
        var newPairCount = mutableMapOf<String, Long>().withDefault { 0 }
        for ((pair, pairCount) in pairCounts.filter { (_, v) -> v > 0 }) {
            val newChar = rules[pair]!!
            val firstNewPair = pair.first().toString() + rules[pair]!!
            val secondNewPair = rules[pair]!!.toString() + pair.last()
            newPairCount[firstNewPair] = newPairCount.getValue(firstNewPair) + pairCount
            newPairCount[secondNewPair] = newPairCount.getValue(secondNewPair) + pairCount
            charCounts[newChar] = (charCounts[newChar] ?: 0) + pairCount
        }
        pairCounts = newPairCount
        if (x == 10) println(charCounts.values.maxOrNull()!! - charCounts.values.minOrNull()!!) //part 1
    }
    // part 2
    println(charCounts.values.maxOrNull()!! - charCounts.values.minOrNull()!!)
}

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

fun day16() {
    val inp = Utils.getRawInput(16).trim()
    fun hex2bin(h: String): String {
        return h.map{it.toString().toLong(16).toString(2).padStart(4, '0')}.joinToString("")
    }
    assert(hex2bin("D2FE28") == "110100101111111000101000")

    data class Packet(
        val start: Int,
        val end: Int,
        val ver: Long,
        val tid: Long,
        val value: Long,
        val children: List<Packet>?,
        val totVer: Long
    )

    fun calcValue(tid: Long, children: List<Packet>): Long = when (tid.toInt()) {
        0 -> children.sumOf {it.value}
        1 -> children.fold(1L) {a, b -> a * b.value}
        2 -> children.minOf { it.value }
        3 -> children.maxOf { it.value }
        5 -> if (children[0].value > children[1].value) 1 else 0
        6 -> if (children[0].value < children[1].value) 1 else 0
        7 -> if (children[0].value == children[1].value) 1 else 0
        else -> throw IllegalArgumentException(tid.toString())
    }
    fun parse(pkt: String, start: Int): Packet {
        var curr = start
        val ver = pkt.slice(curr until curr+3).toLong(2)
        curr += 3
        var totVer = ver
        val tid = pkt.slice(curr until curr+3).toLong(2)
        curr += 3
        if (tid == 4.toLong()) {
            var isLast = false
            var value = ""
            do {
                val byte = pkt.slice(curr until curr + 5)
                curr += 5
                value += byte.drop(1)
                isLast = byte.take(1) == "0"
            } while (!isLast)
            return Packet(start, curr, ver, tid, value.toLong(2), null, totVer)
        } else {
            val ltid = pkt[curr]
            curr++
            if (ltid == '0') {
                var len = pkt.slice(curr until curr+15).toLong(2)
                curr += 15
                val children: ArrayList<Packet> = ArrayList()
                while (len > 0) {
                    val child = parse(pkt, curr)
                    len -= (child.end - child.start)
                    curr = child.end
                    children.add(child)
                    totVer += child.totVer
                }
                return Packet(start, curr, ver, tid, calcValue(tid, children), children, totVer)
            } else {
                var num = pkt.slice(curr until curr+11).toLong(2)
                curr += 11
                val children: ArrayList<Packet> = ArrayList()
                while (num > 0) {
                    val child = parse(pkt, curr)
                    num -= 1
                    curr = child.end
                    children.add(child)
                    totVer += child.totVer
                }
                return Packet(start, curr, ver, tid, calcValue(tid, children), children, totVer)
            }
        }
    }
//    println(parse(hex2bin("D2FE28"), 0))
//    println(parse(hex2bin("38006F45291200"), 0))
//    println(parse(hex2bin("EE00D40C823060"), 0))
//    println(parse(hex2bin("A0016C880162017C3686B18A3D4780"), 0))
      println(parse(hex2bin(inp), 0))
}

fun day17() {
//    val (X1, X2) =  20 to 30
//    val (Y1, Y2) = -5 to -10

    val (X1, X2) =  79 to 137
    val (Y1, Y2) = -117 to -176

    fun dvx(vx: Int) = when {
        vx > 0 -> -1
        vx < 0 -> 1
        else -> 0
    }

    data class Result(val works: Boolean, val x: Int, val y: Int, val ymax: Int)

    val (W, H) = 20 to 20
    var mat = Array(H+1) {Array(W+1) {'.'} }

    fun matt(x: Int, y: Int, c: Char) {
        // Only to plot
        val (_X1, _X2) = 0 to 200
        val (_Y1, _Y2) = -200 to 16000

        if (x < _X1 || x > _X2 || y < _Y1 || y > _Y2 ) return
        val i = (x - _X1) * W / (_X2 - _X1)
        val j = H - (y - _Y1) * H / (_Y2 - _Y1)
        mat[j][i] = c
    }

    // for plotting
    for (x in X1..X2)
        for(y in Y2..Y1)
            matt(x, y, '@')

    fun traj(x: Int, y: Int, vx: Int, vy: Int, gravity: Boolean = true, debug:Boolean=false): Result {
        for (x in X1..X2)
            for(y in Y2..Y1)
                matt(x, y, '@')
        var vx = vx
        var vy = vy
        var x = x
        var y = y
        var ymax = Int.MIN_VALUE
        while (x <= X2 && y >= Y2) {
            if(debug) {
                println("x:${x} y:${y} vx:${vx} vy:${vy} ymax:${ymax}")
                matt(x, y, '#')
            }
            x += vx
            y += vy
            vx += dvx(vx)
            if (gravity) vy -= 1
            if (y > ymax) ymax = y
            if (x <= X2 && y >= Y2 && x >= X1 && y <= Y1) return Result(true, x, y, ymax)
        }
        return Result(false, x, y, ymax)
    }

    var ymax = 0
    var vxmax: Int = 0
    var vymax: Int = 0
    var count = 0
    for(vx in 1..150) {
        for (vy in -200..200) {
            val r = traj(0, 0, vx, vy, true)
            if (r.works) {
                count++
//                println("vx: ${vx} vy: ${vy}: ${r}")
                if (r.ymax >= ymax) {
                    ymax = r.ymax
                    vxmax = vx
                    vymax = vy
//                    println("vx: ${vx} vy: ${vy}: ${r}")
                }
            }
        }
    }

//    println(traj(0, 0, vxmax, vymax, debug = true))
//    println(mat.map{it.joinToString(" ")}.joinToString("\n"))
    println(ymax)
    println(count)

}

fun day18() {

    fun parse(s: String, p: Int): Pair<SN, Int> {
       if(s[p].isDigit()) return SNI(s[p].digitToInt()) to p+1
        else if (s[p] == '[') {
           val (left, pl) = parse(s, p+1)
           assert(s[pl] == ',')
           val (right, pr) = parse(s, pl+1)
           assert(s[pr] == ']')
           return SNP(left, right) to pr + 1
       } else {
           throw InvalidAlgorithmParameterException()
       }
    }
    fun parse(s: String): SNP =  parse(s, 0).first as SNP

//    println(parse("[[[[[9,8],1],2],3],4]").explode().first)
//    println(parse("[7,[6,[5,[4,[3,2]]]]]").explode().first)
//    println(parse("[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]").explode().first)
//    println(parse("[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]").explode().first)

    fun addUp(s: String) {
        val t = s.trimIndent().split("\n").map(::parse)
        var acc = t[0]
//        println("  $acc ${acc.sum()}")
        for (snp in t.drop(1)) {
//            println("+ $snp ${snp.sum()}")
            acc += snp
        }
        println(acc.magnitude())
    }
    addUp(Utils.getRawInput(18).trim())

    // part 2
    fun getMaxMagnitude(s: String) {
        val inps = s.split("\n").map(::parse)
        val len = inps.size
        var maxMag = 0
        for(i in 0 until (len-1)) {
            for(j in (i+1) until len) {
                val s1 = (inps[i] + inps[j]).magnitude()
                val s2 = (inps[j] + inps[i]).magnitude()
                if (s1 > maxMag) { maxMag = s1 }
                if (s2 > maxMag) { maxMag = s2 }
            }
        }
        println(maxMag)
    }
    getMaxMagnitude(Utils.getRawInput(18).trim())

}

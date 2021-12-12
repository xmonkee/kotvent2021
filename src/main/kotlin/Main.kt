import java.math.BigInteger
import java.util.*
import kotlin.collections.HashMap
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
    day12()
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


typealias Line = List<Int>
typealias Board = List<List<Int>>
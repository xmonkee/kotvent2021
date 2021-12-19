package year2021.days

import Utils

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
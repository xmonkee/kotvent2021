package year2021.days

import Utils

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

    var pairCounts: Map<String, Long> = Utils.counter(pairs)
    val charCounts = Utils.counter<Char, Long>(temp.toList()).toMutableMap()

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
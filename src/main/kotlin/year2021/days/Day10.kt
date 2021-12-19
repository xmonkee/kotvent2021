package year2021.days

import Utils
import java.math.BigInteger
import java.util.*

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
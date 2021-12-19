package year2021.days

import Utils
import year2021.Board

fun day4(): List<Int> {
    val inp = Utils.getRawInput(4);
    val movesAndBoards = inp.split("\n\n");
    val moves = movesAndBoards[0].split(",").map { it.toInt() }
    val boards: List<Board> = movesAndBoards.drop(1).map { board -> board.split("\n").filter{ row -> row.isNotEmpty()}.map { row -> row.trim().split("\\s+".toRegex()).map { num -> num.toInt() } } }
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
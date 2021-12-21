package year2021.days

import Utils.counter
import java.util.*

fun day21() {

    fun part1() {
        fun newPos(oldPos: Int, move: Int) = (oldPos - 1 + move) % 10 + 1
        data class Player(val name: Int, var pos: Int, var score: Int)
        val p1 = Player(1, 8, 0)
        val p2 = Player(2, 4, 0)
        var die = (1..1000).toList()
        fun otherP(p: Player) = setOf(p1, p2).minus(p).first()
        var currP = p1
        while(true) {
            val move = die.take(3).sum()
            die = die.drop(3)
            currP.pos = newPos(currP.pos, move)
            currP.score += currP.pos
            if(currP.score >= 1000) break
            currP = otherP(currP)
        }
        val final = otherP(currP).score * (die.first() - 1)
        println(final)
    }

    fun part2() {
        data class Player(val name: Int, val pos: Int, val score: Int) {
            fun newPos(move: Int) = (pos - 1 + move) % 10 + 1
            fun update(move: Int): Player  = Player(name, newPos(move), score+newPos(move))
        }
        data class Universe(val p1: Player, val p2: Player) {
            fun get(i: Int) = if(i == 1) p1 else p2
            fun set(i: Int, p: Player) = if(i == 1) Universe(p, p2) else Universe(p1, p)
            fun update(player: Int, move: Int) = this.set(player, this.get(player).update(move))
        }
        val outcomes = sequence { for(i in 1..3) for (j in 1..3) for (k in 1..3) yield(i+j+k)}.toList()
        val outCounts = outcomes.toSet().associateWith { outcome -> outcomes.count{it == outcome} }

        fun step(uniCount: Map<Universe, Long>, player: Int): Pair<Map<Universe, Long>, Long> {
            var newUniCount = mutableMapOf<Universe, Long>()
            var won = 0L
            fun addUniverse(player: Int, move: Int, num: Int) {
                for ((uni, cnt) in uniCount) {
                    val newUni = uni.update(player, move)
                    if (newUni.get(player).score >= 21) {
                        won += cnt * num
                    } else {
                        val newCnt = (newUniCount[newUni] ?: 0) + cnt * num
                        newUniCount[newUni] = newCnt
                    }
                }
            }
            for ((move, num) in outCounts) {
                addUniverse(player, move, num)
            }
            return newUniCount.toMap() to won
        }

        fun printit(uniCount: Map<Universe, Long>) {
            for((u, l) in uniCount) println("$l $u")
        }

        var uniCount = mapOf(Universe(Player(1, 8, 0), Player(2, 4, 0)) to 1L)
        var player = 0
        val won= mutableListOf<Long>(0L, 0L)
        while ( uniCount.size > 0 ) {
            val result = step(uniCount, player + 1)
            uniCount = result.first
            won[player] = won[player] +result.second
//            printit(uniCount)
            println(uniCount.size)
//            println()
            player = (player + 1) % 2
//            System.`in`.read()
        }
        println(won.maxOrNull())

    }

    part2()
}
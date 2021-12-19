package year2021.days

import Utils
import java.math.BigInteger

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
    var counts = Array<BigInteger>(9) { BigInteger.ZERO }
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
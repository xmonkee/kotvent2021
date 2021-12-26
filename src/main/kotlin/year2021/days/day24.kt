package year2021.days

import java.lang.Math.max

fun day24() {


    val inp = Utils.getRawInput(24)
//    showInp(inp)
    val sections = inp.split("inp w\n").map{it.trim().split("\n")}.drop(1)

    data class vars(val a: Long, val b: Long, val c: Long)
    val allSecVars = sections.map {s ->
        fun getVar(idx: Int) = s[idx].split(" ").last().toInt().toLong()
        vars(getVar(3), getVar(4), getVar(14))
    }

    var zs = setOf(0L)
    var zmap = mutableMapOf<Long, String>(0L to "") // z to max input that caused it
    for (secVars in allSecVars) {
        var zmap2 = mutableMapOf<Long, String>()
        val nzs = mutableSetOf<Long>()
        val isZeroable = secVars.b < 9
        for (zp in zs) for(w in 1L..9L) {
            val x = if((zp % 26) + secVars.b == w) 0 else 1
            if (!isZeroable || x == 0) {
                val z = (zp/secVars.a) * (25L * x + 1) + (w + secVars.c)*x
                nzs.add(z)
                // since w goes from 1 to 9, the larger value will always win
                // for part 2, change loop to (w in 9L downTo 1L)
                zmap2[z] = zmap[zp]!! + w.toString()
            }
        }
        zmap = zmap2
        zs = nzs.toSet()
//        println(zs)
    }
    println(zmap)

}

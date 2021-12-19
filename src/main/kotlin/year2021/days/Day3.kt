package year2021.days

import Utils

fun day3() {
    val inp = Utils.getListInput(3);
    val sum = inp.fold(IntArray(inp[0].length)) { acc, s ->
        acc.zip(s.toList()).map { (i, c) -> i + c.toString().toInt() }.toIntArray()
    }
    val gamma = sum.map { if (it > inp.size / 2) 1 else 0 }.joinToString("").toInt(2);
    val epsil = sum.map { if (it > inp.size / 2) 0 else 1 }.joinToString("").toInt(2);
    print(gamma * epsil)

    fun doFilterThing(inp: List<String>, idx: Int, chooser: (Int, Int) -> Char): String {
        if (inp.size == 1) return inp[0];
        val count1 = inp.count { it[idx] == '1' }
        val count0 = inp.size - count1;
        val filterFor = chooser(count1, count0)
        return doFilterThing(inp.filter { it[idx] == filterFor }, idx + 1, chooser)
    }

    val oxy = doFilterThing(inp, 0) { c1, c0 -> if (c1 > c0) '1' else if (c1 < c0) '0' else '1' }.toInt(2)
    val co2 = doFilterThing(inp, 0) { c1, c0 -> if (c1 > c0) '0' else if (c1 < c0) '1' else '0' }.toInt(2)
    print(oxy * co2)
}
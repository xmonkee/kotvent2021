package year2021.days

import Utils

fun day2()  {
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
    print(pos * dep)

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
    print(pos * dep)
}
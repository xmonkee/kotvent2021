fun main(args: Array<String>) {
    //print(day1.toList())
    print(day2.toList())
}

val day1 = sequence<Int> {
    val inp = Utils.getNumInput(1)
    yield(inp.windowed(2).sumOf{ (x, y) -> if (y > x) 1 as Int else 0 })
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
    yield(pos*dep)

    //two
    pos = 0
    dep = 0
    var aim = 0
    for (instr in inp) {
        val (cmd, value) = instr.split(" ")
        val v = value.toInt()
        when (cmd) {
            "forward" -> {pos += v ; dep += aim * v}
            "up" -> aim -= v
            "down" -> aim += v
        }
    }
    yield(pos*dep)
}
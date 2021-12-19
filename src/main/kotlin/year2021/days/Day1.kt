package year2021.days

fun day1() {
    val inp = Utils.getNumInput(1)
    println(inp.windowed(2).sumOf { (x, y) -> if (y > x) 1 as Int else 0 })
    println(inp.windowed(3).map { x -> x.sum() }.windowed(2).sumOf { (x, y) -> if (y > x) 1 as Int else 0 })
}
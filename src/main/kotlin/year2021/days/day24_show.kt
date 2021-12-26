package year2021.days

fun showInp(inp: String) {

    val inp = inp.trim().split("\n")
    val sections = Array(14) {Array(18) {""} }
    var i = -1
    var j = 0
    for (line in inp) {
        if (line.startsWith("inp")) {
            i++
            j = 0
        }
        sections[i][j] = line
        j++
    }
    println(sections.map{it.joinToString("")}.joinToString { "\n" })
    val outS = Array(18) { Array(250) {' '} }
    for (i in 0..17) {
        for (j in 0..8) {
            for (k in 0..6) {
                if (j < sections[k][i].length)
                    outS[i][(k)*15 + j] = sections[k][i][j]
            }
        }
    }
    for (i in 0..17) {
        for (j in 0..8) {
            for (k in 7..13) {
                if (j < sections[k][i].length)
                    outS[i][(k-7)*15 + j] = sections[k][i][j]
            }
        }
    }
    println(outS.map{it.joinToString("")}.joinToString("\n"))
}

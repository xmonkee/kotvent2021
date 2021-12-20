package year2021.days

import java.lang.Math.max
import java.lang.Math.min

typealias Image = Array<Array<Char>>

val inp = """#.#.#..#####.#.#.#.###.##.....###.##.#..###.####..#####..#....#..#..##..###..######.###...####..#..#####..##..#.#####...##.#.#..#.##..#.#......#.###.######.###.####...#.##.##..#..#..#####.....#.#....###..#.##......#.....#..#..#..##..#...##.######.####.####.#.#...#.......#..#.#.#...####.##.#......#..#...##.#.##..#...##.#.##..###.#......#.#.......#.#.#.####.###.##...#.....####.#..#..#.##.#....##..#.####....##...##..#...#......#.#.......#.......##..####..#...#.#.#...##..#.#..###..#####........#..####......#..#

#..#.
#....
##..#
..#..
..###"""

fun day20() {
    fun parseInp(inp: String): Pair<String, Image> {
        val (algo, image) = inp.split("\n\n")
        val img = image.split("\n").map{it.toCharArray().toTypedArray()}.toTypedArray()
        return algo to img
    }
    fun img2str(img: Image) = img.map{it.joinToString("")}.joinToString("\n")

    val (algo, img) = parseInp(Utils.getRawInput(20).trim())
//    val (algo, img) = parseInp(inp)

    fun grow(img: Image, growth: Int): Image {
        val w = img[0].size
        val h = img.size
        val W = w + growth*2
        val H = h + growth*2
        val bigImg = Array(H) { Array(W) {img[0][0]} }
        for (i in 0 until h)
            for (j in 0 until w)
                bigImg[i+growth][j+growth] = img[i][j]
        return bigImg
    }

//    println(img2str(bigImg))
    fun getSquareValue(img: Image, i: Int, j:Int): Int {
        val chars = sequence {  for (y in i-1..i+1) for (x in j-1..j+1) {
            val y = min(img.size-1, max(y, 0))
            val x = min(img[0].size-1, max(x, 0))
            yield(img[y][x])
        } }
        val binary = chars.map{ mapOf('.' to '0', '#' to '1')[it] }.joinToString("")
        return binary.toInt(2)
    }

    fun step(img: Image): Array<Array<Char>> {
        val bigImg = grow(img, 1)
        val W = bigImg[0].size
        val H = bigImg.size
        val newImg = Array(H) {Array(W) {'.'} }
        for (i in 0 until H) {
            for (j in 0 until W) {
                newImg[i][j] = algo[getSquareValue(bigImg, i, j)]
            }
        }
        return newImg
    }

    fun count(img: Image) = img.sumOf {row -> row.count { it == '#' } }
    var bigImg = img
    repeat(100) {
        bigImg = step(bigImg)
    }
    println(img2str(bigImg))
    println()
    println(count(bigImg))
}

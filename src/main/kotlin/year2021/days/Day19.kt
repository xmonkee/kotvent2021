package year2021.days

import Utils.allDirectionalPairs
import java.lang.Math.abs

data class Point(val x: Int, val y: Int, val z: Int) {
    companion object {
        fun fromList(l: List<Int>) = Point(l[0], l[1], l[2])
        fun fromString(s: String) = fromList(s.split(",").map(String::toInt))
        fun allTransforms(): List<Point> = sequence<Point> {
            val crossProducts = mapOf(1 to 2 to 3, 2 to 3 to 1, 3 to 1 to 2, 2 to 1 to -3, 3 to 2 to -1, 1 to 3 to -2)
            val l = listOf(1, 2, 3).toSet()
            val s = listOf(1, -1)
            for (x in l) for (xs in s) for (y in l.minus(x)) for (ys in s) {
                val z = crossProducts[x to y]!! * xs * ys
                yield(Point(x*xs, y*ys, z))
            }
        }.toList()
    }
    fun _get(idx: Int) = when( idx ) { 1 -> this.x 2 -> this.y 3 -> this.z else -> throw IllegalArgumentException() }
    fun get(idx: Int) = if (idx < 0) -_get(-idx) else _get(idx)
    fun get(x: Int, y: Int, z: Int) = Point(this.get(x), this.get(y), this.get(z))
    fun transform(p: Point) = get(p.x, p.y, p.z)
    fun magnitude(): Long = x.toLong()*x + y.toLong()*y + z.toLong()*z
    operator fun plus(other: Point) = Point(x+other.x, y+other.y, z+other.z)
    fun negative() = Point(-x, -y, -z)
    operator fun minus(o: Point) = this + o.negative()
    override fun toString() = "[$x $y $z]"
    fun manhattan() = abs(x) + abs(y) + abs(z)
}

fun day19() {
    fun parseInput(s: String): MutableMap<Int, List<Point>> {
        val s2p = mutableMapOf<Int, List<Point>>()
        val scanners = s.split("\n\n")
        for (scanner in scanners) {
            val lines = scanner.split("\n")
            val scannerNumber = Regex("--- scanner (\\d+) ---").find(lines[0])!!.destructured.component1()
            val coords = lines.drop(1).map(Point::fromString)
            s2p[scannerNumber.toInt()] = coords
        }
        return s2p
    }
    val s2p = parseInput(Utils.getRawInput(19).trim())

    /* Basic Idea:
    Given 2 scanner S and S*
    Given 2 beacons B1 and B2 that are reported by both S and S*
    Where BS is the coords of B as reported by S
    T(BS*) is the coords of B with a transform that makes S* have the same orientation as S
    Then B1S - B2S == T(B1S*) - T(B2S*), since the origin will become irrelevant when taking the difference of two vectors
    Then, for all pairs of S and S*, we try all transforms and all pairs of beacons till we find 12 such beacons
     */

    val scannerToBeaconPairs = s2p.map { (k, v) -> k to allDirectionalPairs(v) }.toMap()
    fun findMatchingBeaconPairsUnderTransform(s1: Int, s2: Int, transform: Point): MutableSet<Pair<Point, Point>> {
        val matchingBeaconPairs = mutableSetOf<Pair<Point, Point>>()
        val s1BeaconDiffToPair = mutableMapOf<Point, Pair<Point, Point>>()
        for ((p11, p12) in scannerToBeaconPairs[s1]!!) {
            s1BeaconDiffToPair.put(p11 - p12, p11 to p12)
        }
        for ((p21, p22) in scannerToBeaconPairs[s2]!!) {
            val s2BeaconDiffTransformed = (p21 - p22).transform(transform)
            if(s2BeaconDiffTransformed in s1BeaconDiffToPair) {
                val (p11, p12) = s1BeaconDiffToPair[s2BeaconDiffTransformed]!!
                matchingBeaconPairs.add(p11 to p21)
                matchingBeaconPairs.add(p12 to p22)
            }
        }
        return matchingBeaconPairs
    }

    fun findTransformThatGives12CommonBeacons(s1: Int, s2: Int) = Point.allTransforms().find { findMatchingBeaconPairsUnderTransform(s1, s2, it).size >= 12 }

    val scannerPairs = allDirectionalPairs(s2p.keys.toList())
    val relativeTransforms = scannerPairs.associateWith { findTransformThatGives12CommonBeacons(it.first, it.second) }.filter { (k, v) -> v != null }
    println("pair transforms")
    println(relativeTransforms)

    val absoluteTransforms = mutableMapOf<Int, Point>(0 to Point(1, 2, 3)) // s2 to s0 == (s2 to s1).(s1 to s0)
    while(absoluteTransforms.size < s2p.size) {
        for ((pair, t2to1) in relativeTransforms) {
            val (s1, s2) = pair
            if (s2 in absoluteTransforms) continue
            if (s1 in absoluteTransforms) {
                val t1to0 = absoluteTransforms[s1]!!
                absoluteTransforms[s2] = t2to1!!.transform(t1to0)
            }
        }
    }
    println("found absolute transforms")
    println(absoluteTransforms)

    fun getRelativeOrigin(pair: Pair<Int, Int>): Point {
        // S*S = BS - BS*
        val (s1, s2) = pair
        val transform = relativeTransforms[pair]!!
        val matchingPairsOfBeacons = findMatchingBeaconPairsUnderTransform(s1, s2, transform)
        val s1b = matchingPairsOfBeacons.first().first
        val s2b = matchingPairsOfBeacons.first().second.transform(transform)
        return s1b - s2b
    }

    val relativeOrigins = relativeTransforms.keys.associateWith { getRelativeOrigin(it) }
    println("relative origins")
    println(relativeOrigins)

    val absoluteOrigins = mutableMapOf<Int, Point>(0 to Point(0, 0, 0))
    while(absoluteOrigins.size < s2p.size) {
        for ((pair, o2to1) in relativeOrigins) {
            val (s1, s2) = pair
            if (s2 in absoluteOrigins) continue
            if (s1 in absoluteOrigins) {
                val o1to0 = absoluteOrigins[s1]!!
                absoluteOrigins[s2] = o1to0 + o2to1!!.transform(absoluteTransforms[s1]!!)
            }
        }
    }
    println("absolute origins")
    println(absoluteOrigins)

    fun beaconsToAbsolute(s: Int): List<Point> {
        return s2p[s]!!.map{b ->
           absoluteOrigins[s]!! + b.transform(absoluteTransforms[s]!!)
        }
    }

    // part 1
    val allBeaconsToAbsolute = s2p[0]!! + s2p.keys.drop(1).flatMap { beaconsToAbsolute(it) }
    println(allBeaconsToAbsolute.toSet().size)

    // part 2
    val maxManhattan = scannerPairs.maxOf { (s1, s2) -> (absoluteOrigins[s1]!! - absoluteOrigins[s2]!!).manhattan() }
    println(maxManhattan)
}

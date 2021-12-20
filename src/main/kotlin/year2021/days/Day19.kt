package year2021.days

import java.lang.Math.abs
import java.security.InvalidAlgorithmParameterException

val input = """--- scanner 0 ---
404,-588,-901
528,-643,409
-838,591,734
390,-675,-793
-537,-823,-458
-485,-357,347
-345,-311,381
-661,-816,-575
-876,649,763
-618,-824,-621
553,345,-567
474,580,667
-447,-329,318
-584,868,-557
544,-627,-890
564,392,-477
455,729,728
-892,524,684
-689,845,-530
423,-701,434
7,-33,-71
630,319,-379
443,580,662
-789,900,-551
459,-707,401

--- scanner 1 ---
686,422,578
605,423,415
515,917,-361
-336,658,858
95,138,22
-476,619,847
-340,-569,-846
567,-361,727
-460,603,-452
669,-402,600
729,430,532
-500,-761,534
-322,571,750
-466,-666,-811
-429,-592,574
-355,545,-477
703,-491,-529
-328,-685,520
413,935,-424
-391,539,-444
586,-435,557
-364,-763,-893
807,-499,-711
755,-354,-619
553,889,-390

--- scanner 2 ---
649,640,665
682,-795,504
-784,533,-524
-644,584,-595
-588,-843,648
-30,6,44
-674,560,763
500,723,-460
609,671,-379
-555,-800,653
-675,-892,-343
697,-426,-610
578,704,681
493,664,-388
-671,-858,530
-667,343,800
571,-461,-707
-138,-166,112
-889,563,-600
646,-828,498
640,759,510
-630,509,768
-681,-892,-333
673,-379,-804
-742,-814,-386
577,-820,562

--- scanner 3 ---
-589,542,597
605,-692,669
-500,565,-823
-660,373,557
-458,-679,-417
-488,449,543
-626,468,-788
338,-750,-386
528,-832,-391
562,-778,733
-938,-730,414
543,643,-506
-524,371,-870
407,773,750
-104,29,83
378,-903,-323
-778,-728,485
426,699,580
-438,-605,-362
-469,-447,-387
509,732,623
647,635,-688
-868,-804,481
614,-800,639
595,780,-596

--- scanner 4 ---
727,592,562
-293,-554,779
441,611,-461
-714,465,-776
-743,427,-804
-660,-479,-426
832,-632,460
927,-485,-438
408,393,-506
466,436,-512
110,16,151
-258,-428,682
-393,719,612
-211,-452,876
808,-476,-593
-575,615,604
-485,667,467
-680,325,-822
-627,-443,-432
872,-547,-609
833,512,582
807,604,487
839,-516,451
891,-625,532
-652,-548,-490
30,-46,-14"""

data class Point(val x: Int, val y: Int, val z: Int) {
    companion object {
        fun fromList(l: List<Int>) = Point(l[0], l[1], l[2])
        fun fromString(s: String) = fromList(s.split(",").map(String::toInt))
        fun allTransforms(): List<Point> {
            val self = this
            return sequence<Point> {
                val crossProducts = mapOf(1 to 2 to 3, 2 to 3 to 1, 3 to 1 to 2, 2 to 1 to -3, 3 to 2 to -1, 1 to 3 to -2)
                val l = listOf(1, 2, 3).toSet()
                val s = listOf(1, -1)
                for (x in l) for (xs in s) for (y in l.minus(x)) for (ys in s) {
                    val z = crossProducts[x to y]!! * xs * ys
                    yield(Point(x*xs, y*ys, z))
                }
            }.toList()
        }
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

//    val s2p = parseInput(input)
    val s2p = parseInput(Utils.getRawInput(19).trim())

    fun <T> allDirectionalPairs(l: List<T>) = sequence {
        for (i in 0 until l.size - 1)
            for (j in i + 1 until l.size) {
                yield(l[i] to l[j]); yield(l[j] to l[i])
            }
    }.toList()

    // 12 common beacons = 66 [due to n(n-1)/2 ] pairs where (p1 - p2) under some transform will have a match in another scanner
    val scannersToPairs = s2p.map { (k, v) -> k to allDirectionalPairs(v) }.toMap()
    val scannersToPairDiffs = scannersToPairs.map { (k, v) -> k to v.map { (p1, p2) -> p1 - p2 } }.toMap()

    fun findTransformThatGives12CommonBeacons(s1: Int, s2: Int): Point? {
        // What transform applied to beacons diffs in scanner 2 will find enough equivalent beacon diffs in scanner 1
        return Point.allTransforms().find { t ->
            val transformedDiffs = scannersToPairDiffs[s2]!!.map { it.transform(t) }.toSet()
            scannersToPairDiffs[s1]!!.intersect(transformedDiffs).size >= 132 // 132 = 66 * 2 (forward and backwards diff)
        }
    }

    val scannerPairs = allDirectionalPairs(s2p.keys.toList())
    val scannerPairsToTransforms = // (s1, s2) to t where s1 orientation is s2 transformed under t
        scannerPairs.map { (s1, s2) -> s1 to s2 to findTransformThatGives12CommonBeacons(s1, s2) }.toMap()
            .filter { (k, v) -> v != null }
    println(scannerPairsToTransforms)

    val scannerToAbsoluteTransform = mutableMapOf<Int, Point>(0 to Point(1, 2, 3)) // s2 to s0 == (s2 to s1).(s1 to s0)
    while(scannerToAbsoluteTransform.size < s2p.size) {
        for ((pair, t2to1) in scannerPairsToTransforms) {
            val (s1, s2) = pair
            if (s2 in scannerToAbsoluteTransform) continue
            if (s1 in scannerToAbsoluteTransform) {
                val t1to0 = scannerToAbsoluteTransform[s1]!!
                scannerToAbsoluteTransform[s2] = t2to1!!.transform(t1to0)
            }
        }
    }

    println(scannerToAbsoluteTransform)

    val scannerPairToPointMap = scannerPairsToTransforms.map { (sp, t) ->
        val pointMap = mutableMapOf<Point, Point>()
        val (s1, s2) = sp
        val s1PairsToDiffs = scannersToPairs[s1]!!.map { (p1, p2) -> p1 to p2 to (p1 - p2) }
        val s2PairsToTransformedDiffs = scannersToPairs[s2]!!.map { (p1, p2) -> p1 to p2 to (p1 - p2).transform(t!!) }
        for ((pointPair1, pairDiff1) in s1PairsToDiffs) {
            for ((pointPair2, pairDiff2) in s2PairsToTransformedDiffs) {
                if (pairDiff1 == pairDiff2) {
                    pointMap[pointPair1.first] = pointPair2.first
                    pointMap[pointPair1.second] = pointPair2.second
                }
            }
        }
        sp to pointMap
    }.toMap()

    println(scannerPairToPointMap)

    fun getS2OriginWRTS1(spair: Pair<Int, Int>): Point {
        //(s1, s2) = spair
        val s1b = scannerPairToPointMap[spair]!!.keys.first()
        val s2b = scannerPairToPointMap[spair]!![s1b]!!
        val transform = scannerPairsToTransforms[spair]!!
        val s2bt = s2b.transform(transform)
        val s2s1 = s1b - s2bt
        return s2s1
    }

    val scannerPairToRelativeOrigin = scannerPairsToTransforms.keys.map{spair -> spair to getS2OriginWRTS1(spair)}.toMap()
    println(scannerPairToRelativeOrigin)

    val scannerToAbsoluteOrigin = mutableMapOf<Int, Point>(0 to Point(0, 0, 0))
    while(scannerToAbsoluteOrigin.size < s2p.size) {
        for ((pair, o2to1) in scannerPairToRelativeOrigin) {
            val (s1, s2) = pair
            if (s2 in scannerToAbsoluteOrigin) continue
            if (s1 in scannerToAbsoluteOrigin) {
                val o1to0 = scannerToAbsoluteOrigin[s1]!!
                scannerToAbsoluteOrigin[s2] = o1to0 + o2to1!!.transform(scannerToAbsoluteTransform[s1]!!)
            }
        }
    }
    println(scannerToAbsoluteOrigin)

    fun mapAllBeaconsRelativeTo0(s: Int): List<Point> {
        return s2p[s]!!.map{b ->
           scannerToAbsoluteOrigin[s]!! + b.transform(scannerToAbsoluteTransform[s]!!)
        }
    }

    // part 1
    val allBeaconsWRT0 = s2p[0]!! + s2p.keys.drop(1).flatMap { mapAllBeaconsRelativeTo0(it) }
    println(allBeaconsWRT0.toSet().size)

    // part 2
    val maxManhattan = scannerPairs.maxOf { (s1, s2) -> (scannerToAbsoluteOrigin[s1]!! - scannerToAbsoluteOrigin[s2]!!).manhattan() }
    println(maxManhattan)
}

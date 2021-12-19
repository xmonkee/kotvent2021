package year2021.days

import Utils

fun day16() {
    val inp = Utils.getRawInput(16).trim()
    fun hex2bin(h: String): String {
        return h.map{it.toString().toLong(16).toString(2).padStart(4, '0')}.joinToString("")
    }
    assert(hex2bin("D2FE28") == "110100101111111000101000")

    data class Packet(
        val start: Int,
        val end: Int,
        val ver: Long,
        val tid: Long,
        val value: Long,
        val children: List<Packet>?,
        val totVer: Long
    )

    fun calcValue(tid: Long, children: List<Packet>): Long = when (tid.toInt()) {
        0 -> children.sumOf {it.value}
        1 -> children.fold(1L) {a, b -> a * b.value}
        2 -> children.minOf { it.value }
        3 -> children.maxOf { it.value }
        5 -> if (children[0].value > children[1].value) 1 else 0
        6 -> if (children[0].value < children[1].value) 1 else 0
        7 -> if (children[0].value == children[1].value) 1 else 0
        else -> throw IllegalArgumentException(tid.toString())
    }
    fun parse(pkt: String, start: Int): Packet {
        var curr = start
        val ver = pkt.slice(curr until curr+3).toLong(2)
        curr += 3
        var totVer = ver
        val tid = pkt.slice(curr until curr+3).toLong(2)
        curr += 3
        if (tid == 4.toLong()) {
            var isLast = false
            var value = ""
            do {
                val byte = pkt.slice(curr until curr + 5)
                curr += 5
                value += byte.drop(1)
                isLast = byte.take(1) == "0"
            } while (!isLast)
            return Packet(start, curr, ver, tid, value.toLong(2), null, totVer)
        } else {
            val ltid = pkt[curr]
            curr++
            if (ltid == '0') {
                var len = pkt.slice(curr until curr+15).toLong(2)
                curr += 15
                val children: ArrayList<Packet> = ArrayList()
                while (len > 0) {
                    val child = parse(pkt, curr)
                    len -= (child.end - child.start)
                    curr = child.end
                    children.add(child)
                    totVer += child.totVer
                }
                return Packet(start, curr, ver, tid, calcValue(tid, children), children, totVer)
            } else {
                var num = pkt.slice(curr until curr+11).toLong(2)
                curr += 11
                val children: ArrayList<Packet> = ArrayList()
                while (num > 0) {
                    val child = parse(pkt, curr)
                    num -= 1
                    curr = child.end
                    children.add(child)
                    totVer += child.totVer
                }
                return Packet(start, curr, ver, tid, calcValue(tid, children), children, totVer)
            }
        }
    }
//    println(parse(hex2bin("D2FE28"), 0))
//    println(parse(hex2bin("38006F45291200"), 0))
//    println(parse(hex2bin("EE00D40C823060"), 0))
//    println(parse(hex2bin("A0016C880162017C3686B18A3D4780"), 0))
    println(parse(hex2bin(inp), 0))
}
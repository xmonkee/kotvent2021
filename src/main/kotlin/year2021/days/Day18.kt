package year2021.days

import Utils.cross
import java.security.InvalidAlgorithmParameterException
import kotlin.math.ceil
import kotlin.math.floor

abstract class SN { // SnailFishNumber
    abstract fun addToLeftMost(value: Int): SN
    abstract fun addToRightMost(value: Int): SN
    abstract fun split(): Pair<SN, Boolean>
    abstract fun explode(depth: Int): Pair<SN, Parts?>
    abstract fun reduce(): SN
    abstract operator fun plus(other: SN): SN
    abstract fun magnitude(): Int
}

data class SNI(val value: Int) : SN() { // SN that holds an Int only
    override fun toString(): String = this.value.toString()
    override fun addToLeftMost(value: Int): SN {
        return SNI(this.value + value)
    }

    override fun addToRightMost(value: Int): SN {
        return SNI(this.value + value)
    }

    override fun split(): Pair<SN, Boolean> {
        if (this.value < 10) return this to false
        val left = floor(this.value.toDouble() / 2).toInt()
        val right = ceil(this.value.toDouble() / 2).toInt()
        return SNP(SNI(left), SNI(right)) to true
    }

    override fun explode(depth: Int): Pair<SN, Parts?> {
        return this to null
    }

    override fun reduce(): SN {
        return this
    }

    override fun plus(other: SN): SN {
        TODO("Not yet implemented")
    }

    override fun magnitude(): Int {
        return this.value
    }

}

data class Parts(val left: Int, val right: Int)

data class SNP(val left: SN, val right: SN) : SN() { // SN that holds a pair
    override fun toString(): String = "[${this.left.toString()}, ${this.right.toString()}]"

    override operator fun plus(other: SN): SNP = SNP(this, other).reduce()

    override fun addToLeftMost(value: Int): SN {
        return SNP(this.left.addToLeftMost(value), this.right)
    }

    override fun addToRightMost(value: Int): SN {
        return SNP(this.left, this.right.addToRightMost(value))
    }

    override fun explode(depth: Int): Pair<SN, Parts?> {
        /* Basic idea:
        Every explode call returns 1) The new SN to replace itself with and 2) The two parts that need to get absorbed
        In every call:
        If this.left explodes, this.right absorbs (addToLeftMost) the right exploded part. The left exploded part is passed back to the parent in `Parts`
        If the this.right explodes, this.left absorbs (addToRightMost) the left exploded part. The right exploded part is passed back to the parent in `Parts`
        Then, the parent would place the received part based on whether it came from it's left or right child and so on
         */

        // Check if left child explodes
        val (newLeft, parts) = this.left.explode(depth + 1)
        if (parts != null) {
            val (leftPart, rightPart) = parts
            return SNP(newLeft, this.right.addToLeftMost(rightPart)) to Parts(leftPart, 0)
        }

        // Check if left child explodes
        val (newRight, parts2) = this.right.explode(depth + 1)
        if (parts2 != null) {
            val (leftPart, rightPart) = parts2
            return SNP(this.left.addToRightMost(leftPart), newRight) to Parts(0, rightPart)
        }

        // Check if I explode
        if (depth >= 5) {
            // If depth >= 5 and my children didn't explode, they must be SNIs
            return SNI(0) to Parts((this.left as SNI).value, (this.right as SNI).value)
        }
        // Nothing explodes
        return this to null
    }

    fun explode(): Pair<SNP, Boolean> {
        val (newSNP, parts) = this.explode(1)
        return newSNP as SNP to (parts != null)
    }

    override fun split(): Pair<SNP, Boolean> {
        val (newLeft, split) = this.left.split()
        if (split) return SNP(newLeft, this.right) to true
        val (newRight, splitR) = this.right.split()
        if (splitR) return SNP(this.left, newRight) to true
        return this to false
    }

    override fun reduce(): SNP {
        val (exploded, didExplode) = this.explode()
        if (didExplode) return exploded.reduce()
        val (split, didSplit) = exploded.split()
        if (didSplit) return split.reduce()
        return this
    }

    override fun magnitude(): Int {
        return left.magnitude() * 3 + right.magnitude() * 2
    }
}

fun day18() {

    fun parse(s: String, p: Int): Pair<SN, Int> {
        if (s[p].isDigit()) return SNI(s[p].digitToInt()) to p + 1
        else if (s[p] == '[') {
            val (left, pl) = parse(s, p + 1)
            assert(s[pl] == ',')
            val (right, pr) = parse(s, pl + 1)
            assert(s[pr] == ']')
            return SNP(left, right) to pr + 1
        } else {
            throw InvalidAlgorithmParameterException()
        }
    }

    fun parse(s: String): SNP = parse(s, 0).first as SNP

    val inp = Utils.getRawInput(18).trim().split("\n").map(::parse)
    // part 1
    println(inp.reduce { a, b -> a + b }.magnitude())

    // part 2
    println(inp.cross(inp).filter { (x, y) -> x != y }.maxOf { (x, y) -> (x + y).magnitude() })
}


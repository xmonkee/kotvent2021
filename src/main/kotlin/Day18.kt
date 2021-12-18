import kotlin.math.ceil
import kotlin.math.floor

abstract class SN { // SnailFishNumber
    abstract fun addToLeftMost(value: Int): SN
    abstract fun addToRightMost(value: Int): SN
    abstract fun split(): Pair<SN, Boolean>
    abstract fun explode(): Pair<SN, Boolean>
    abstract fun _explode(depth: Int): Pair<SN, Parts?>
    abstract fun reduce(): SN
    abstract operator fun plus(other: SN): SN
    abstract fun magnitude(): Int
    abstract fun toStringWithDepth(n: Int): String
}

data class SNI(val value: Int): SN() { // SN that holds an Int only
    override fun toString(): String = this.value.toString()
    override fun addToLeftMost(value: Int): SN {
        return SNI(this.value + value)
    }
    override fun addToRightMost(value: Int): SN {
        return SNI(this.value + value)
    }
    override fun split(): Pair<SN, Boolean> {
        if (this.value < 10) return this to false
        val left = floor(this.value.toDouble()/2).toInt()
        val right = ceil(this.value.toDouble()/2).toInt()
        return SNP(SNI(left), SNI(right)) to true
    }

    override fun explode(): Pair<SN, Boolean> {
        return this to false
    }

    override fun _explode(depth: Int): Pair<SN, Parts?> {
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

    override fun toStringWithDepth(n: Int): String = this.value.toString()

}

data class Parts(val left: Int, val right: Int)

fun toSubscript(n: Int) = if(n >= 5) Character.toChars('₀'.code + n)[0] else ""

data class SNP(val left: SN, val right: SN): SN() { // SN that holds a pair
    override fun toString(): String = "[${this.left.toString()}, ${this.right.toString()}]"
//    override fun toString(): String = this.toStringWithDepth(1)
    override fun toStringWithDepth(n: Int): String = "${toSubscript(n)}[${this.left.toStringWithDepth(n+1)}, ${this.right.toStringWithDepth(n+1)}]"

    override operator fun plus(other: SN): SNP = SNP(this, other).reduce() as SNP

    override fun addToLeftMost(value: Int): SN {
        return SNP(this.left.addToLeftMost(value), this.right)
    }
    override fun addToRightMost(value: Int): SN {
        return SNP(this.left, this.right.addToRightMost(value))
    }

    override fun _explode(depth: Int): Pair<SN, Parts?> {
        // Check if left child explodes
        val (newLeft, parts) = this.left._explode(depth + 1)
        if (parts != null) {
            val (leftPart, rightPart) = parts
            return SNP(newLeft, this.right.addToLeftMost(rightPart)) to Parts(leftPart, 0)
        }
        // Check if left child explodes
        val (newRight, parts2) = this.right._explode(depth + 1)
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

    override fun explode(): Pair<SN, Boolean> {
        val (newSNP, parts) = this._explode(1)
        return newSNP to (parts != null)
    }

    override fun split(): Pair<SN, Boolean> {
        val (newLeft, split) = this.left.split()
        if (split) return SNP(newLeft, this.right) to true
        val (newRight, splitR) = this.right.split()
        if (splitR) return SNP(this.left, newRight) to true
        return this to false
    }

    override fun reduce(): SN {
        val (exploded, didExplode) = this.explode()
//        println("e $exploded ${exploded.sum()}")
        if (didExplode) return exploded.reduce()
        val (split, didSplit) = exploded.split()
//        println("s $split ${split.sum()}")
        if (didSplit) return split.reduce()
        return this
    }

    override fun magnitude(): Int {
        return left.magnitude()*3 + right.magnitude()*2
    }
}


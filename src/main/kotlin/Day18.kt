import kotlin.math.ceil
import kotlin.math.floor

abstract class SN { // SnailFishNumber
    abstract fun addToLeftMost(value: Int): SN
    abstract fun addToRightMost(value: Int): SN
    abstract fun split(): Pair<SN, Boolean>
    abstract fun explode(): Pair<SN, Boolean>
    abstract fun reduce(): SN
    abstract fun magnitude(): Int
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

    override fun reduce(): SN {
        return this
    }

    override fun magnitude(): Int {
        return this.value
    }

}

data class Parts(val left: Int, val right: Int)

data class SNP(val left: SN, val right: SN): SN() { // SN that holds a pair
    override fun toString(): String = "[${this.left.toString()}, ${this.right.toString()}]"
    operator fun plus(other: SN) = SNP(this, other)

    override fun addToLeftMost(value: Int): SN {
        return SNP(this.left.addToLeftMost(value), this.right)
    }
    override fun addToRightMost(value: Int): SN {
        return SNP(this.left, this.right.addToRightMost(value))
    }

    fun _explode(depth: Int): Pair<SN, Parts?> {
        if (depth == 5) {
            return SNI(0) to Parts((this.left as SNI).value, (this.right as SNI).value)
        }
        if (this.left is SNP) {
            val (newLeft, parts) = this.left._explode(depth + 1)
            if (parts != null) {
                val (leftPart, rightPart) = parts
                return SNP(newLeft, this.right.addToLeftMost(rightPart)) to Parts(leftPart, 0)
            }
        }
        if (this.right is SNP) {
            val (newRight, parts) = this.right._explode(depth + 1)
            if (parts != null) {
                val (leftPart, rightPart) = parts
                return SNP(this.left.addToRightMost(leftPart), newRight) to Parts(0, rightPart)
            }
        }
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
        val (split, didSplit) = exploded.split()
        if (didExplode || didSplit) return split.reduce()
        else return this
    }

    override fun magnitude(): Int {
        return left.magnitude()*3 + right.magnitude()*2
    }
}


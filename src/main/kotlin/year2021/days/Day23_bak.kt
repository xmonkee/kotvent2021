package year2021.days

val inp23 = """
    #############
    #...........#
    ###B#C#B#D###
      #A#D#C#A#
      #########
    """.trimIndent()

fun typeToScore(type: String) = when (type) {
    "A" -> 1
    "B" -> 10
    "C" -> 100
    "D" -> 1000
    else -> 0
}

data class Person(val type: String, val score: Int, var hole: Hole?) {
    override fun toString(): String = type
    constructor(type: String) : this(type, typeToScore(type), null)
}

class Hole(val id: String, val adj: MutableSet<Hole> = mutableSetOf()) {
    var occupant: Person? = null
        set(person) {
            field = person
            person!!.hole = this
        }

    val occupied: Boolean get() = this.occupant!= null

    fun connect(other: Hole) {
        this.adj.add(other)
        other.adj.add(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return (id == (other as Hole).id)
    }

    override fun hashCode() = id.hashCode()
    override fun toString(): String {
        return "Hole(id=$id, adj=${adj.map{it.id}.joinToString(",")}, occupant=$occupant)"
    }
}
fun day23_bak() {
    val hall = (0..10).map{Hole("H$it")}
    for (i in 1..10) hall[i].connect(hall[i-1])
    val rooms = "ABCD".map {
        val room = listOf(Hole("${it}0"), Hole("${it}1"))
        room[0].connect(room[1])
        room
    }
    rooms[0][0].connect(hall[2])
    rooms[1][0].connect(hall[4])
    rooms[2][0].connect(hall[6])
    rooms[3][0].connect(hall[8])


    fun show() {
        fun o(h: Hole) = if(h.occupant == null) "." else h.occupant!!.toString()
        println("#############")
        println(hall.joinToString("", "#", "#", transform = ::o))
        println(rooms.joinToString("#", "###", "###") {o(it[0])})
        println(rooms.joinToString("#", "  #", "#  ") {o(it[1])})
        println("  #########  ")
    }

    val allRooms = hall + rooms.flatten()
    fun parse(inp: String) {
        inp.split("\n")[2].filter { it.isLetter() }.forEachIndexed {i, c -> rooms[i][0].occupant = Person(c.toString()) }
        inp.split("\n")[3].filter { it.isLetter() }.forEachIndexed {i, c -> rooms[i][1].occupant = Person(c.toString()) }
    }

    parse(inp23)
    val allPersons = allRooms.filter{it.occupant != null}.map{it.occupant!!}
    show()
    println(allPersons.map{it.type to it.score})


}
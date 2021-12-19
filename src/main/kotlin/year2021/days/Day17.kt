package year2021.days

fun day17() {
//    val (X1, X2) =  20 to 30
//    val (Y1, Y2) = -5 to -10

    val (X1, X2) =  79 to 137
    val (Y1, Y2) = -117 to -176

    fun dvx(vx: Int) = when {
        vx > 0 -> -1
        vx < 0 -> 1
        else -> 0
    }

    data class Result(val works: Boolean, val x: Int, val y: Int, val ymax: Int)

    val (W, H) = 20 to 20
    var mat = Array(H+1) {Array(W+1) {'.'} }

    fun matt(x: Int, y: Int, c: Char) {
        // Only to plot
        val (_X1, _X2) = 0 to 200
        val (_Y1, _Y2) = -200 to 16000

        if (x < _X1 || x > _X2 || y < _Y1 || y > _Y2 ) return
        val i = (x - _X1) * W / (_X2 - _X1)
        val j = H - (y - _Y1) * H / (_Y2 - _Y1)
        mat[j][i] = c
    }

    // for plotting
    for (x in X1..X2)
        for(y in Y2..Y1)
            matt(x, y, '@')

    fun traj(x: Int, y: Int, vx: Int, vy: Int, gravity: Boolean = true, debug:Boolean=false): Result {
        for (x in X1..X2)
            for(y in Y2..Y1)
                matt(x, y, '@')
        var vx = vx
        var vy = vy
        var x = x
        var y = y
        var ymax = Int.MIN_VALUE
        while (x <= X2 && y >= Y2) {
            if(debug) {
                println("x:${x} y:${y} vx:${vx} vy:${vy} ymax:${ymax}")
                matt(x, y, '#')
            }
            x += vx
            y += vy
            vx += dvx(vx)
            if (gravity) vy -= 1
            if (y > ymax) ymax = y
            if (x <= X2 && y >= Y2 && x >= X1 && y <= Y1) return Result(true, x, y, ymax)
        }
        return Result(false, x, y, ymax)
    }

    var ymax = 0
    var vxmax: Int = 0
    var vymax: Int = 0
    var count = 0
    for(vx in 1..150) {
        for (vy in -200..200) {
            val r = traj(0, 0, vx, vy, true)
            if (r.works) {
                count++
//                println("vx: ${vx} vy: ${vy}: ${r}")
                if (r.ymax >= ymax) {
                    ymax = r.ymax
                    vxmax = vx
                    vymax = vy
//                    println("vx: ${vx} vy: ${vy}: ${r}")
                }
            }
        }
    }

//    println(traj(0, 0, vxmax, vymax, debug = true))
//    println(mat.map{it.joinToString(" ")}.joinToString("\n"))
    println(ymax)
    println(count)

}
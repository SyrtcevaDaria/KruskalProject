import kotlin.math.sqrt

class Graph {
    private val matrix = mutableListOf<Int>()
    val size get() = sqrt(matrix.size.toDouble()).toInt()
    fun addVertex() {
        val initialEdge = 0
        matrix.listIterator().let { iterator ->
            var index = 0
            val size = size
            if (!iterator.hasNext()) {
                iterator.add(initialEdge)
                return
            }
            while (iterator.hasNext()) {
                iterator.next()
                index++
                if (index % size == 0) {
                    iterator.add(initialEdge)
                }
            }
            repeat(size + 1) {
                iterator.add(initialEdge)
            }
        }
    }

    override fun toString(): String {
        val size = size
        val builder = StringBuilder()
        if (size == 0) return "empty graph"
        val maxDigits = matrix.maxOf { it.toString().length }
        val formatter = "%0${maxDigits}d"
        var i = 0
        while (i < size) {
            builder.append("| ")
            var j = 0
            while (j < size) {
                val edge = matrix[i * size + j]
                val cell = formatter.format(edge)
                builder.append(cell)
                if (j < size - 1) {
                    builder.append(" ")
                }
                j++
            }
            builder.append(" |\n")
            i++
        }
        return builder.toString()
    }

    operator fun set(src: Int, dest: Int, weight: Int) {
        val size = size
        matrix[src * size + dest] = weight
        matrix[src + dest * size] = weight
    }

    operator fun get(src: Int, dest: Int) = matrix[src * size + dest]

    fun forEach(action: (i: Int, j: Int) -> Unit) {
        var i = 0
        val size = size
        while (i < size) {
            var j = 0
            while (j < i) {
                action(i, j)
                j++
            }
            i++
        }
    }
}
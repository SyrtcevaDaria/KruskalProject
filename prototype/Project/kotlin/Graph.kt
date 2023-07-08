import kotlin.math.sqrt

class Graph {
    private val matrix = mutableListOf<Int>()
    val size get() = sqrt(matrix.size.toDouble()).toInt()

    fun addVertex() {
        val initialEdge = 0
        val iterator = matrix.listIterator()
        var index = 0
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

    override fun toString(): String {
        val builder = StringBuilder()
        if (size == 0) return "empty graph"
        val maxDigits = matrix.maxOf { it.toString().length }
        val formatter = "%0${maxDigits}d"
        for (i in 0 until size) {
            builder.append("| ")
            for (j in 0 until size) {
                val edge = matrix[i * size + j]
                val cell = formatter.format(edge)
                builder.append(cell)
                if (j < size - 1) {
                    builder.append(" ")
                }
            }
            builder.append(" |\n")
        }
        return builder.toString()
    }

    operator fun set(src: Int, dest: Int, weight: Int) {
        if (src == dest) return // no self reference
        matrix[src * size + dest] = weight
        matrix[src + dest * size] = weight
    }

    operator fun get(src: Int, dest: Int) = matrix[src * size + dest]

    fun edges(): Sequence<Edge> = sequence {
        for (i in 0 until size) {
            for (j in 0 until i) {
                val weight = this@Graph[i, j]
                if (weight > 0) {
                    yield(Edge(i, j, weight))
                }
            }
        }
    }

    fun replace(graph: Graph) {
        matrix.clear()
        matrix.addAll(graph.matrix)
    }

    fun isConnected() =
        try {
            kruskal().lastOrNull()
            true
        } catch (e: GraphNotConnectedException) {
            false
        }

    fun removeVertex(vertex: Int) {
        val iterator = matrix.listIterator()
        var index = 0
        if (!iterator.hasNext()) {
            return
        }
        while (iterator.hasNext()) {
            iterator.next()
            if (index % size == vertex || index in size * vertex until size * (vertex + 1)) {
                iterator.remove()
            }
            index++
        }

    }

}

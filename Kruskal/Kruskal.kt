data class Edge(val src: Int, val dest: Int, val weight: Int)

fun find(subsets: IntArray, i: Int): Int {
    if (subsets[i] != i) {
        subsets[i] = find(subsets, subsets[i])
    }
    return subsets[i]
}

fun union(subsets: IntArray, x: Int, y: Int) {
    val xRoot = find(subsets, x)
    val yRoot = find(subsets, y)
    subsets[xRoot] = yRoot
}

fun Graph.kruskal()  {
    val result = mutableListOf<Edge>()
    val edges = mutableListOf<Edge>()
    forEach { i, j ->
        val weight = this@kruskal[i, j]
        if (weight != 0) {
            edges.add(Edge(i, j, weight))
        }
    }
    val sortedEdges = edges.sortedBy(Edge::weight)
    val vertices = size
    val subsets = IntArray(vertices) { it }
    var i = 0
    var e = 0
    while (e < vertices - 1) {
        if (i >= sortedEdges.size) {
            throw GraphNotConnectedException()
        }
        val nextEdge = sortedEdges[i++]

        val x = find(subsets, nextEdge.src)
        val y = find(subsets, nextEdge.dest)

        if (x != y) {
            result.add(nextEdge)
            union(subsets, x, y)
            e++
        }
    }

    println("Minimum Spanning Tree:")
    for (edge in result) {
        println("${edge.src} -- ${edge.dest}  weight: ${edge.weight}")
    }
}

class GraphNotConnectedException : IllegalStateException()
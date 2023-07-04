import java.util.stream.Stream
import kotlin.streams.toList

fun Graph.fillGraph(lines: Stream<String>) {
    val connections = lines.map { line ->
        line.split(" ").take(3).map(kotlin.String::toInt)
    }.toList()
    val vertices = connections.flatMap { it.take(2) }.max() + 1
    repeat(vertices) { addVertex() }
    connections.forEach { (from, to, edge) -> this[from, to] = edge }
    println(this)
    kruskal()
}
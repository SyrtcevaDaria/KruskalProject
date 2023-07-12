import java.io.File
import kotlin.streams.asStream

fun main() {
    val graph = Graph()
    graph.apply {// apply позволяет применять блок кода к объекту класса.
        File("C:\\Users\\alina\\IdeaProjects\\firstprj-ui\\demo1\\src\\jvmMain\\resources\\file.txt").useLines { lines ->
            fillGraph(lines.asStream())//строку преобразуем в поток
        }
    }
}
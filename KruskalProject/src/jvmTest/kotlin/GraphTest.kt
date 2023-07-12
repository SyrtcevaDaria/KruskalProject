import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GraphTest {
    private lateinit var graph: Graph

    @Before
    fun setUp() {
        graph = Graph()
    }

    @Test
    fun addVertex_addsVertexToMatrix() {
        val expectedMatrixSize = 1

        graph.addVertex()
        val actualSize = graph.size
        assertEquals(expectedMatrixSize, actualSize)
    }

    @Test
    fun addVertex_addsCorrectNumberOfEdgesToMatrix() {
        val expectedMatrixSize = 2

        graph.addVertex()
        graph.addVertex()
        val actualMatrixSize = graph.size

        assertEquals(expectedMatrixSize, actualMatrixSize)
    }


    @Test
    fun testSetAndGet() {
        graph.addVertex()
        graph.addVertex()
        graph[0, 1] = 5
        assertEquals(5, graph[0, 1])
    }

    @Test
    fun testRemoveVertex() {
        graph.addVertex()
        graph.addVertex()
        graph.removeVertex(1)
        assertEquals(1, graph.size)
    }


    @Test
    fun testIsConnected() {
        graph.isConnected()
        graph.addVertex()
        graph.addVertex()
        graph[0, 1] = 5
        assertTrue(graph.isConnected())
    }
}
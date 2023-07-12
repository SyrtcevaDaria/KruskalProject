import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class KruskalTests {
    private lateinit var graph: Graph

    @Before
    fun setUp() {
        graph = Graph()
    }

    @Test
    fun testKruskalEmptyGraph() {
        try {
            graph.kruskal().toList()
            fail("Expected GraphNotConnectedException")
        } catch (e: GraphNotConnectedException) {
            // Exception
        }
    }

    @Test
    fun testKruskalConnectedGraph() {
        graph.addVertex()
        graph.addVertex()
        graph.addVertex()
        graph[0, 1] = 5
        graph[0, 2] = 3
        graph[1, 2] = 2

        val minimumSpanningTree = graph.kruskal().toList()
        assertEquals(2, minimumSpanningTree.size)
        assertEquals(2, minimumSpanningTree[0].weight)
        assertEquals(3, minimumSpanningTree[1].weight)
        assertEquals(1, minimumSpanningTree[0].src)
        assertEquals(2, minimumSpanningTree[0].dest)
        assertEquals(0, minimumSpanningTree[1].src)
        assertEquals(2, minimumSpanningTree[1].dest)
    }

    @Test
    fun testKruskalDisconnectedGraph() {
        graph.addVertex()
        graph.addVertex()
        graph.addVertex()
        graph.addVertex()
        graph[0, 1] = 5
        graph[3, 2] = 3

        try {
            graph.kruskal().toList()
            fail("Expected GraphNotConnectedException")
        } catch (e: GraphNotConnectedException) {
            // Exception
        }
    }

    @Test
    fun testKruskal() {
        graph.addVertex()
        graph.addVertex()
        graph[0, 1] = 5
        graph.addVertex()
        graph[0, 2] = 7
        graph[1, 2] = 3

        val minimumSpanningTree = graph.kruskal().toList()
        assertEquals(2, minimumSpanningTree.size)
        assertEquals(3, minimumSpanningTree[0].weight)
        assertEquals(5, minimumSpanningTree[1].weight)
    }


    @Test(expected = GraphNotConnectedException::class)
    fun testKruskalException() {
        graph.addVertex()
        graph.addVertex()
        graph.addVertex()

        graph.kruskal().toList() // генерирует GraphNotConnectedException
    }

}
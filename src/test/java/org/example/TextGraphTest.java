package org.example;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TextGraphTest {

    static TextGraph textGraph;

    @BeforeAll
    static void setup() {
        textGraph = new TextGraph();
        boolean result = textGraph.processFile("src/test/resources/test.txt");
        assertTrue(result, "File processing should succeed");
    }

    @Test
    void testNonExistentNodes() {
        List<List<String>> paths = textGraph.calcShortestPath("now", "lod");
        assertNotNull(paths, "Paths should not be null");
        assertTrue(paths.isEmpty(), "There should be no path");
    }

    @Test
    void testSameNode() {
        List<List<String>> paths = textGraph.calcShortestPath("to", "to");
        assertNotNull(paths, "Paths should not be null");
        assertEquals(1, paths.size(), "There should be exactly one path");
        assertEquals(1, paths.get(0).size(), "Path should contain exactly one node");
        assertEquals("to", paths.get(0).get(0), "Path should start and end with to");
    }

    @Test
    void testNodesWithNoPath() {
        List<List<String>> paths = textGraph.calcShortestPath("civilizations", "to");
        assertNotNull(paths, "Paths should not be null");
        assertTrue(paths.isEmpty(), "There should be no path");
    }

    @Test
    void testNodesWithPath() {
        List<List<String>> paths = textGraph.calcShortestPath("to", "out");
        assertNotNull(paths, "Paths should not be null");
        assertFalse(paths.isEmpty(), "There should be at least one path");

        // Verify that all paths start with word1 and end with word3
        for (List<String> path : paths) {
            assertEquals("to", path.get(0), "Path should start with to");
            assertEquals("out", path.get(path.size() - 1), "Path should end with out");
        }
    }

    @Test
    void testShortestPath() {
        List<List<String>> paths = textGraph.calcShortestPath("to", "new");
        assertNotNull(paths, "Paths should not be null");
        assertFalse(paths.isEmpty(), "There should be at least one path");

        // Find the shortest path length
        int shortestLength = Integer.MAX_VALUE;
        for (List<String> path : paths) {
            int pathLength = path.size();
            if (pathLength < shortestLength) {
                shortestLength = pathLength;
            }
        }

        // Verify that all shortest paths have the same length
        for (List<String> path : paths) {
            assertEquals(shortestLength, path.size(), "Path should be shortest");
        }
    }

    @Test
    void testAllShortestPaths() {
        List<List<String>> paths = textGraph.calcShortestPath("to", "worlds");
        assertNotNull(paths, "Paths should not be null");
        assertFalse(paths.isEmpty(), "There should be at least one path");

        // Retrieve all shortest paths of minimum length
        int shortestLength = Integer.MAX_VALUE;
        List<List<String>> shortestPaths = new ArrayList<>();
        for (List<String> path : paths) {
            int pathLength = path.size();
            if (pathLength < shortestLength) {
                shortestLength = pathLength;
                shortestPaths.clear();
            }
            if (pathLength == shortestLength) {
                shortestPaths.add(path);
            }
        }

        // Verify that all shortest paths are in the output
        for (List<String> shortestPath : shortestPaths) {
            assertTrue(paths.contains(shortestPath), "Shortest path should be included");
        }
    }
}

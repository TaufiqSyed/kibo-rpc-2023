package jp.jaxa.iss.kibo.rpc.uae;

import java.util.*;

public class Graph {
    private List<Point3D> vertices;
    private Map<Point3D, List<Edge>> adjacencyList;
    private Point3D start;
    private Target end;

    public Graph(Point3D start, Target end, List<Obstacle> obstacles) {
        this.start = start;
        this.end = end;
        this.vertices = new ArrayList<>();
        this.adjacencyList = new HashMap<>();

        // Add start and end points to vertices.
        vertices.add(start);
        vertices.add(end);

        // Add obstacle corners to vertices.
        for (Obstacle obstacle : obstacles) {
            vertices.addAll(obstacle.getVertices());
        }

        // Construct edges (visibility graph).
        for (Point3D vertex : vertices) {
            for (Point3D otherVertex : vertices) {
                if (!vertex.equals(otherVertex) && isVisible(vertex, otherVertex, obstacles)) {
                    addEdge(vertex, otherVertex);
                }
            }
        }
    }

    private boolean isVisible(Point3D point1, Point3D point2, List<Obstacle> obstacles) {
        // Create a line segment between point1 and point2
        LineSegment3D segment = new LineSegment3D(point1, point2);
        for (Obstacle obstacle : obstacles) {
            // If the line segment intersects any obstacle, return false
            if (segment.intersects(obstacle)) {
                return false;
            }
        }

        Point3D KIZ1TopLeft = new Point3D(10.3f, -10.2f, 4.32f);
        Point3D KIZ1BottomRight = new Point3D(11.55f, -6.0f, 5.57f);

        Point3D KIZ2TopLeft = new Point3D(9.5f, -10.5f, 4.02f);
        Point3D KIZ2BottomRight = new Point3D(10.5f, -9.6f, 4.8f);

        if (point1.isInsideCuboid(KIZ1TopLeft, KIZ1BottomRight) || point1.isInsideCuboid(KIZ2TopLeft, KIZ2BottomRight))
            if (point2.isInsideCuboid(KIZ1TopLeft, KIZ1BottomRight) || point2.isInsideCuboid(KIZ2TopLeft, KIZ2BottomRight))
                return true;

        // If no intersections found, return true
        return false;
    }

    public List<Point3D> computeShortestPath() {
        Map<Point3D, Point3D> predecessors = new HashMap<>();
        Map<Point3D, Double> distances = new HashMap<>();

        // Initialize the distances with infinity, except for the start point
        for (Point3D vertex : vertices) {
            distances.put(vertex, Double.POSITIVE_INFINITY);
        }
        distances.replace(start, 0.0);

        // Create a priority queue to hold vertices to explore, sorted by their distance from the start point
        PriorityQueue<Point3D> queue = new PriorityQueue<>(Comparator.comparing(distances::get));
        queue.add(start);

        while (!queue.isEmpty()) {
            Point3D current = queue.poll();

            // For each neighbor of the current vertex
            for (Edge edge : adjacencyList.get(current)) {
                Point3D neighbor = edge.getEndPoint();
                double tentativeDistance = distances.get(current) + edge.getLength();

                // If a shorter path to this neighbor is found
                if (tentativeDistance < distances.get(neighbor)) {
                    distances.replace(neighbor, tentativeDistance);
                    predecessors.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        // Construct the shortest path from start to end by walking backwards from end
        List<Point3D> shortestPath = new ArrayList<>();
        for (Point3D vertex = end; vertex != null; vertex = predecessors.get(vertex)) {
            shortestPath.add(0, vertex);
        }

        return shortestPath;
    }

    private void addEdge(Point3D point1, Point3D point2) {
        Edge edge = new Edge(point1, point2);
        adjacencyList.putIfAbsent(point1, new ArrayList<>());
        adjacencyList.get(point1).add(edge);
    }

}


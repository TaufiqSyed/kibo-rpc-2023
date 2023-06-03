package jp.jaxa.iss.kibo.rpc.uae;

import java.util.*;
import android.util.Log;

public class Graph {
    private List<Point3D> vertices;
    private HashMap<Point3D, List<Edge>> adjacencyList;
    private Point3D start;
    private Target end;

    public Graph(Point3D start, Target end, List<Obstacle> obstacles) {
        this.start = start;
        this.end = end;
        this.vertices = new ArrayList<Point3D>();
        this.adjacencyList = new HashMap<Point3D, List<Edge>>();

        // Add start and end points to vertices.
        vertices.add(start);
        vertices.add(end);

        // Add obstacle corners to vertices.
        for (Obstacle obstacle : obstacles) {
            vertices.addAll(obstacle.getVertices());
        }

        // Construct edges (visibility graph).
        for (Point3D vertex : vertices) {
            adjacencyList.putIfAbsent(vertex, new ArrayList<Edge>());
            for (Point3D otherVertex : vertices) {
                if (!vertex.equals(otherVertex) && isVisible(vertex, otherVertex, obstacles)) {
                    addEdge(vertex, otherVertex);
                }
            }
        }
    }

    private boolean isVisible(Point3D src, Point3D dest, List<Obstacle> obstacles) {
        // Create a line segment between point1 and point2
        LineSegment3D segment = new LineSegment3D(src, dest);
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

        if (src.isInsideCuboid(KIZ1TopLeft, KIZ1BottomRight) || src.isInsideCuboid(KIZ2TopLeft, KIZ2BottomRight))
            if (dest.isInsideCuboid(KIZ1TopLeft, KIZ1BottomRight) || dest.isInsideCuboid(KIZ2TopLeft, KIZ2BottomRight))
                return true;

        // If no intersections found, return true
        return false;
    }

//    public List<Point3D> computeShortestPath() {
//        Map<Point3D, Point3D> predecessors = new HashMap<>();
//        final Map<Point3D, Double> distances = new HashMap<>();
//
//        // Initialize the distances with infinity, except for the start point
//        for (Point3D vertex : vertices) {
//            distances.put(vertex, Double.POSITIVE_INFINITY);
//        }
//        distances.replace(start, 0.0);
//
//        // Create a priority queue to hold vertices to explore, sorted by their distance from the start point
//        PriorityQueue<Point3D> queue = new PriorityQueue<>(new Comparator<Point3D>() {
//            @Override
//            public int compare(Point3D p1, Point3D p2) {
//                return Double.compare(distances.get(p1), distances.get(p2));
//            }
//        });
//        queue.add(start);
//
//        while (!queue.isEmpty()) {
//            Point3D current = queue.poll();
//
//            // For each neighbor of the current vertex
//            for (Edge edge : adjacencyList.get(current)) {
//                Point3D neighbor = edge.getEndPoint();
//                double tentativeDistance = distances.get(current) + edge.getLength();
//
//                // If a shorter path to this neighbor is found
//                if (tentativeDistance < distances.get(neighbor)) {
//                    distances.replace(neighbor, tentativeDistance);
//                    predecessors.put(neighbor, current);
//                    queue.add(neighbor);
//                }
//            }
//        }
//
//        // Construct the shortest path from start to end by walking backwards from end
//        List<Point3D> shortestPath = new ArrayList<>();
//        for (Point3D vertex = end; vertex != null; vertex = predecessors.get(vertex)) {
//            shortestPath.add(0, vertex);
//        }
//
//        Log.i("Min path (Start: " + start.toString() + ", End: " + end.toString() + ")", shortestPath.toString());
//
//        return shortestPath;
//    }

//    static class AdjListNode {
//        int vertex, weight;
//
//        AdjListNode(int v, int w)
//        {
//            vertex = v;
//            weight = w;
//        }
//        int getVertex() { return vertex; }
//        int getWeight() { return weight; }
//    }

    // Function to find the shortest distance of all the
    // vertices from the source vertex S.
    public List<Point3D> computeShortestPath()
    {
        Map<Point3D, Point3D> predecessors = new HashMap<>(); // point to prev point
        final Map<Point3D, Double> distances = new HashMap<>(); // point to distance

        int V = vertices.size();

        //        // Initialize the distances with infinity, except for the start point
        Log.i("Taufiq", "Initialized predecessors");
        for (Point3D vertex : vertices) {
            distances.put(vertex, Double.POSITIVE_INFINITY);
        }
        distances.replace(start, 0.0);

        Log.i("Taufiq", "Initialized distances");
        PriorityQueue<Point3D> pq = new PriorityQueue<>(new Comparator<Point3D>() {
                        @Override
            public int compare(Point3D p1, Point3D p2) {
                return Double.compare(distances.get(p1), distances.get(p2));
            }
        });
        Log.i("Taufiq", "Initialized priority queue");
//        PriorityQueue<AdjListNode> pq = new PriorityQueue<>(
//                (v1, v2) -> v1.getWeight() - v2.getWeight());
        pq.add(start);

        while (pq.size() > 0) {
            Point3D current = pq.poll();
            Log.i("Taufiq", "Popped from queue");

            for (Edge e :
                    adjacencyList.get(current)) {
                Point3D n = e.getEndPoint();
                double tentativeDistance = distances.get(current) + e.getLength();

                if (tentativeDistance < distances.get(n)) {
                    distances.replace(n, tentativeDistance);
                    predecessors.put(n, current);
                    pq.add(n);
                    Log.i("Taufiq", "Pushed to queue");
                }
            }
        }

        // Construct the shortest path from start to end by walking backwards from end
        List<Point3D> shortestPath = new ArrayList<>();
        for (Point3D vertex = end; vertex != null; vertex = predecessors.get(vertex)) {
            shortestPath.add(0, vertex);
        }
        Log.i("Taufiq", "Generated shortest path");

        // If you want to calculate distance from source to
        // a particular target, you can return
        // distance[target]
        return shortestPath;
    }

    private void addEdge(Point3D point1, Point3D point2) {
        Edge edge = new Edge(point1, point2);
        adjacencyList.get(point1).add(edge);
    }

}


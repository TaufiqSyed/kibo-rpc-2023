package jp.jaxa.iss.kibo.rpc.uae;

import java.util.ArrayList;
import java.util.List;

public class Robot {
    private Point3D currentPosition;
    private float size;

    public Robot(Point3D initialPosition, float size) {
        this.currentPosition = initialPosition;
        this.size = size;
    }

    public void moveToTarget(List<Obstacle> obstacles, List<Target> targets) {
        while (!targets.isEmpty()) {
            // Update obstacles with robot size.
            List<Obstacle> enlargedObstacles = new ArrayList<>();
            for (Obstacle obstacle : obstacles) {
                enlargedObstacles.add(obstacle.enlarge(this.size));
            }

            // Find closest active target.
            Target closestTarget = findClosestActiveTarget(targets);

            // Construct visibility graph.
            Graph graph = new Graph(currentPosition, closestTarget, enlargedObstacles);

            // Compute shortest path.
            List<Point3D> shortestPath = graph.computeShortestPath();

            // Move robot along shortest path.
            for (Point3D point : shortestPath) {
                // TODO: Call API to move to the point.
                // api.moveTo(point, new Quaternion(-0.5f, -0.5f, -0.5f, 0.5f), true);
                currentPosition = point;

                // If a new target is activated, break out of the current path and recompute.
                Target newTarget = findClosestActiveTarget(targets);
                if (!newTarget.equals(closestTarget)) {
                    break;
                }
            }

            // Remove the reached target from the list.
            targets.remove(closestTarget);
        }
    }

    // This is a placeholder for the method to find the closest active target.
    public Target findClosestActiveTarget(List<Target> targets) {
        Target closestActiveTarget = null;
        double closestDistance = Double.MAX_VALUE;

        for (Target target : targets) {
            if (target.isActive()) {
                double distance = currentPosition.distanceTo(target);
                if (distance < closestDistance) {
                    closestActiveTarget = target;
                    closestDistance = distance;
                }
            }
        }

        return closestActiveTarget;
    }
}

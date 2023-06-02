package jp.jaxa.iss.kibo.rpc.uae;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import gov.nasa.arc.astrobee.types.*;
import gov.nasa.arc.astrobee.Result;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;

public class Robot {
    static final int MAX_ITERATIONS = 1;
    private Point3D currentPosition;
    private float size;
    KiboRpcApi api;

    static int iteration = 0;


    public Robot(Point3D initialPosition, float size, KiboRpcApi api) {
        this.currentPosition = initialPosition;
        this.size = size;
        this.api = api;
    }

    public void start(List<Obstacle> obstacles, List<Target> targets) {
        while (!targets.isEmpty() && (iteration < 50)) {
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

            // logging path points
            Log.i("omar", "Path details for ID: " + closestTarget.getId());
            for(Point3D p : shortestPath) {
                Log.i("omar", p.toString());
            }

            boolean newTargetFound = false;
            // Move robot along shortest path.
            for (Point3D point : shortestPath) {
                Result result;
                int trial_i = 0;
                do {
                    float offset = 0f;
                    if (iteration == 0) {
                        Point p = point.toPoint();
                        p = new Point(p.getX(), p.getY() - 1.4f, p.getZ());
                        result = api.moveTo(p, closestTarget.getOrientation(), true);
                    } else {
                        result = api.moveTo(point.toPoint(), closestTarget.getOrientation(), true);
                    }

                    ++trial_i;
                } while (!result.hasSucceeded() && (trial_i < MAX_ITERATIONS));

                currentPosition = point;

                // update target list using API
                updateTargets((ArrayList<Target>) targets);

                // If a new target is activated, break out of the current path and recompute.
                Target newTarget = findClosestActiveTarget(targets);
                if (!newTarget.equals(closestTarget)) {
                    newTargetFound = true;
                    break;
                }
            }

            if (!newTargetFound) {
                api.laserControl(true);
                api.takeTargetSnapshot(closestTarget.getId());
            }
            ++iteration;
            // Remove the reached target from the list if we didn't change trajectory
            if (!newTargetFound)
                targets.remove(closestTarget);
        }
        api.reportMissionCompletion("my report");
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

    void updateTargets(ArrayList<Target> targets) {
        targets.clear();
        for(int i : api.getActiveTargets()) {
            targets.add(YourService.targets.get(i - 1));
        }
    }

}

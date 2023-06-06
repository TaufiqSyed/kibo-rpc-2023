package jp.jaxa.iss.kibo.rpc.uae;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import gov.nasa.arc.astrobee.types.*;
import gov.nasa.arc.astrobee.Result;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;

public class Robot {
    static final int MAX_ITERATIONS = 5;
    private Point3D currentPosition;
    private float size;
    KiboRpcApi api;

    static int iteration = 0;


    public Robot(Point3D initialPosition, float size, KiboRpcApi api) {
        this.currentPosition = initialPosition;
        this.size = size;
        this.api = api;
    }

    public void start(List<Obstacle> obstacles) {
        ArrayList<Target> activatedTargets = new ArrayList<>();
        updateTargets(activatedTargets);

        Log.i("Taufiq", "First Update Targets");

        while (iteration < 200) {
            if (activatedTargets.isEmpty()) {
                Log.i("Taufiq", "Targets found empty; i = " + iteration);
                try {
                    Thread.sleep(500);
                } catch (Exception e) {}
                updateTargets((ArrayList<Target>)(activatedTargets));

                Log.i("Taufiq", "Updated Targets");
                continue;
            }
            // Update obstacles with robot size.
//            List<Obstacle> enlargedObstacles = new ArrayList<>();
//            for (Obstacle obstacle : obstacles) {
//                enlargedObstacles.add(obstacle.enlarge(this.size));
//            }

            List<Obstacle> enlargedObstacles = obstacles;

            // Find closest active target.
            Target closestTarget = findClosestActiveTarget(activatedTargets);
            Log.i("Taufiq", "Found closest target; i = " + iteration);

            // Construct visibility graph.
            Graph graph = new Graph(currentPosition, closestTarget, enlargedObstacles);
            Log.i("Taufiq", "Constructed visibility graph; i = " + iteration);


            // Compute shortest path.
            List<Point3D> shortestPath = graph.computeShortestPath();
            Log.i("Taufiq", "Computed shortest path; i = " + iteration);

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
                        p = new Point(p.getX(), p.getY(), p.getZ());
                        result = api.moveTo(p, closestTarget.getOrientation(), true);
                    } else {
                        result = api.moveTo(point.toPoint(), closestTarget.getOrientation(), true);
                    }


                    ++trial_i;
                } while (!result.hasSucceeded() && (trial_i < MAX_ITERATIONS));

                currentPosition = point;

                // update target list using API
                updateTargets((ArrayList<Target>) activatedTargets);

                // If a new target is activated, break out of the current path and recompute.
                Target newTarget = findClosestActiveTarget(activatedTargets);
                if (!newTarget.equals(closestTarget)) {
                    newTargetFound = true;
                    break;
                }
            }

            Log.i("omar2", "moving towards");
            moveTowards(closestTarget.getPoint3D(), closestTarget.getOrientation());

            if (!newTargetFound) {
                api.laserControl(true);
                api.takeTargetSnapshot(closestTarget.getId());
                Log.i("omar", "shining laser at target: " + closestTarget.getId());
            }
            ++iteration;
            // Remove the reached target from the list if we didn't change trajectory
            if (!newTargetFound)
                activatedTargets.remove(closestTarget);
        }
        api.reportMissionCompletion("my report");
    }

    // This is a placeholder for the method to find the closest active target.
    public Target findClosestActiveTarget(List<Target> targets) {
        Target closestActiveTarget = null;
        double closestDistance = Double.MAX_VALUE;

        for (Target target : targets) {
//            if (target.isActive()) {
                double distance = currentPosition.distanceTo(target);
                if (distance < closestDistance) {
                    closestActiveTarget = target;
                    closestDistance = distance;
                }
//            }
        }

        return closestActiveTarget;
    }

    void updateTargets(ArrayList<Target> targets) {
        targets.clear();
        for(int i : api.getActiveTargets()) {
            Target t = YourService.targets.get(i-1).clone();
            targets.add(YourService.targets.get(i - 1));
        }
    }

    public void moveTowards(Point3D targetPosition, Quaternion targetOrientation) {
        double distance = this.currentPosition.distanceTo(targetPosition);

        // Calculate direction vector from robot to target
        Point3D direction = new Point3D(targetPosition.getX() - this.currentPosition.getX(),
                targetPosition.getY() - this.currentPosition.getY(),
                targetPosition.getZ() - this.currentPosition.getZ());

        // Normalize direction vector
        double magnitude = Math.sqrt(direction.getX() * direction.getX() +
                direction.getY() * direction.getY() +
                direction.getZ() * direction.getZ());
        Point3D normalizedDirection = new Point3D(direction.getX()/magnitude,
                direction.getY()/magnitude,
                direction.getZ()/magnitude);

        float proportion = 0.75f;
        // Move the robot by 0.5 that distance in the direction of the target
        Point3D newPosition = new Point3D(this.currentPosition.getX() + normalizedDirection.getX() * proportion * distance,
                this.currentPosition.getY() + normalizedDirection.getY() * proportion * distance,
                this.currentPosition.getZ() + normalizedDirection.getZ() * proportion * distance);

        // Assuming moveTo is a method in Robot class that moves robot to the new position.
        // Modify this according to your actual method for moving the robot.
        Result result;
        int trial_i = 0;
        do {
            result = api.moveTo(newPosition.toPoint(), targetOrientation, true);
            ++trial_i;
        } while (!result.hasSucceeded() && (trial_i < MAX_ITERATIONS));
    }

}

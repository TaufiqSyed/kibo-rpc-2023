package jp.jaxa.iss.kibo.rpc.uae;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import gov.nasa.arc.astrobee.Result;
import gov.nasa.arc.astrobee.types.*;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;

/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee
 */

public class YourService extends KiboRpcService {
    // write your plan 1 here

    final static int START = 0;
    final static int END = 8;
    final static int QR = 7;
    static HashMap<Integer, Point> coords = new HashMap<>();
    static HashMap<Integer, Quaternion> orientations = new HashMap<>();
    static{
        coords.put(1, new Point(11.2625, -10.58, 5.3625));
        coords.put(2, new Point(10.513384, -9.085172, 3.76203));
        coords.put(3, new Point(10.6031, -7.71007, 3.76093));
        coords.put(4, new Point(9.866984, -6.673972, 5.09531));
        coords.put(5, new Point(11.102, -8.0304, 5.9076));
        coords.put(6, new Point(12.023, -8.989, 4.8305));
        coords.put(START, new Point(9.815, -9.806, 4.293));
        coords.put(END, new Point(11.143, -6.7607, 4.9654));
        coords.put(QR, new Point(11.381944, -8.566172, 3.76203));

        orientations.put(1, new Quaternion(0.707F,0,0,0.707F));
        orientations.put(2, new Quaternion(0,0,0,1F));
        orientations.put(3, new Quaternion(0.707F,0,0,0.707F));
        orientations.put(4, new Quaternion(-0.5F,0.5F,-0.5F,0.5F));
        orientations.put(5, new Quaternion(1,0,0,0));
        orientations.put(6, new Quaternion(0.5F,0.5F,-0.5F,-0.5F));
        orientations.put(START, new Quaternion(1,0,0,0));
        orientations.put(END, new Quaternion(0,0,-0.707F, 0.707F));
        orientations.put(QR, new Quaternion(0,0,0,1));
    }

    static ArrayList<Obstacle> obstacles = new ArrayList<>();
    static {
        obstacles.add(new Obstacle(new Point3D(10.783, -9.8899, 4.8385), new Point3D(11.071, -9.6929, 5.0665)));
        obstacles.add(new Obstacle(new Point3D(10.8652, -9.0734, 4.3861), new Point3D(10.9628, -8.7314, 4.6401)));
        obstacles.add(new Obstacle(new Point3D(10.185, -8.3826, 4.1475), new Point3D(11.665, -8.2826, 4.6725)));
        obstacles.add(new Obstacle(new Point3D(10.7955, -8.0635, 5.1055), new Point3D(11.3525, -7.7305, 5.1305)));
        obstacles.add(new Obstacle(new Point3D(10.563, -7.1449, 4.6544), new Point3D(10.709, -6.8099, 4.8164)));
    }

    public static ArrayList<Target> targets = new ArrayList<>();
    static {
        targets.add(new Target(1, 11.2746, -9.92284, 5.2988, new Quaternion(0f, 0f, -0.707f, 0.707f)));
        targets.add(new Target(2, 10.612, -9.0709, 4.48, new Quaternion(0.5f, 0.5f, -0.5f, 0.5f)));
        targets.add(new Target(3, 10.71, -7.7, 4.48, new Quaternion(0, 0.707f, 0, 0.707f)));
        targets.add(new Target(4, 10.51, -6.7185, 5.1804, new Quaternion(0, 0, -1, 0)));
        targets.add(new Target(5, 11.114, -7.9756, 5.3393, new Quaternion(-0.5f, -0.5f, -0.5f, 0.5f)));
        targets.add(new Target(6, 11.355, -8.9929, 4.7818, new Quaternion(0, 0, 0, 1)));

//        targets.add(new Target(11.2625, -10.58, 5.3625, new Quaternion(0.707f, 0, 0, 0.707f), 1));
//        targets.add(new Target(10.513384, -9.085172, 3.76203, new Quaternion(0, 0, 0, 1), 2));
//        targets.add(new Target(10.6031, -7.71007, 3.76093, new Quaternion(0.707f, 0, 0, 0.707f), 3));
//        targets.add(new Target(9.866984, -6.673972, 5.09531, new Quaternion(-0.5f, 0.5f, -0.5f, 0.5f), 4));
//        targets.add(new Target(11.102, -8.0304, 5.9076, new Quaternion(1, 0, 0, 0), 5));
//        targets.add(new Target(12.023, -8.989, 4.8305, new Quaternion(0.5f, 0.5f, -0.5f, -0.5f), 6));
    }

    @Override
    protected void runPlan1() {
        api.startMission();
        Log.i("omar", "Moving to mid point between KIZs");
        api.moveTo(new Point(10.4f, -9.9f, 4.56f), api.getRobotKinematics().getOrientation(), true);


        api.moveTo(Util.toPoint(targets.get(5-1)), targets.get(5-1).getOrientation(), true);
        Robot robot = new Robot(Util.toPoint3D(api.getRobotKinematics().getPosition()), 0.0f, api);
        robot.start(obstacles);

//        final int MAX_ITERATIONS = 5;
//        int trial_i = 0;
//
//        Point point = new Point(11.114, -7.9756, 5.3393);
//        Quaternion quaternion = new Quaternion(-0.5f, -0.5f, -0.5f, 0.5f);
//        Result result;
//
//        api.notifyGoingToGoal();
//
//        do {
//            result = api.moveTo(point, quaternion, true);
//            ++trial_i;
//        } while (!result.hasSucceeded() && (trial_i < MAX_ITERATIONS));
//
//        api.flashlightControlFront(1);
//        api.flashlightControlFront(0);
//
//        Bitmap image_2 = api.getBitmapNavCam();
//        api.saveBitmapImage(image_2, "image_test_2");
//
//        api.laserControl(true);
//        api.takeTargetSnapshot(1);
//
//        api.reportMissionCompletion("my report");
    }
}

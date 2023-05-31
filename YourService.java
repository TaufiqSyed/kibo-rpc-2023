package jp.jaxa.iss.kibo.rpc.uae;

import android.graphics.Bitmap;

import java.util.List;

import gov.nasa.arc.astrobee.*;
import gov.nasa.arc.astrobee.types.*;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;

/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee
 */

public class YourService extends KiboRpcService {
    // write your plan 1 here
    @Override
    protected void runPlan1() {
        api.startMission();

        final int MAX_ITERATIONS = 5;
        int trial_i = 0;

        Point point = new Point(11.114, -7.9756, 5.3393);
        Quaternion quaternion = new Quaternion(-0.5f, -0.5f, -0.5f, 0.5f);
        Result result;

        api.notifyGoingToGoal();

        do {
            result = api.moveTo(point, quaternion, true);
            ++trial_i;
        } while (!result.hasSucceeded() && (trial_i < MAX_ITERATIONS));

        api.flashlightControlFront(1);
        api.flashlightControlFront(0);

        Bitmap image_2 = api.getBitmapNavCam();
        api.saveBitmapImage(image_2, "image_test_2");

        api.laserControl(true);
        api.takeTargetSnapshot(1);

        api.reportMissionCompletion("my report");
    }
}

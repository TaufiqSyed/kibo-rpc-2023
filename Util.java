package jp.jaxa.iss.kibo.rpc.uae;

import gov.nasa.arc.astrobee.types.Point;

public class Util {
    static Point3D toPoint3D(Point point) {
        return new Point3D(point.getX(), point.getY(), point.getZ());
    }
    static Point toPoint(Point3D point) {
        return new Point(point.getX(), point.getY(), point.getZ());
    }
}

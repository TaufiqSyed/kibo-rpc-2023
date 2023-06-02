package jp.jaxa.iss.kibo.rpc.uae;

import org.joml.*;

class LineSegment3D {
    private Vector3f start;
    private Vector3f end;

    public LineSegment3D(Vector3f start, Vector3f end) {
        this.start = start;
        this.end = end;
    }

    public LineSegment3D(Point3D start, Point3D end) {
        this.start = new Vector3f((float)start.getX(), (float)start.getY(), (float)start.getZ());
        this.end = new Vector3f((float)end.getX(), (float)end.getY(), (float)end.getZ());
    }

    public boolean intersects(Obstacle obstacle) {
        Point3D[] vertices = obstacle.getVerticesAABB();
        Vector3f min = new Vector3f((float)vertices[0].getX(), (float)vertices[0].getY(), (float)vertices[0].getZ());
        Vector3f max = new Vector3f((float)vertices[1].getX(), (float)vertices[1].getY(), (float)vertices[1].getZ());

        return Intersectionf.testRayAab(start, end, min, max);
    }
}

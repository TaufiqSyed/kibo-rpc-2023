package jp.jaxa.iss.kibo.rpc.uae;

import java.util.ArrayList;
import java.util.List;

public class Obstacle {
    private Point3D topLeft;
    private Point3D bottomRight;

    public Obstacle(Point3D topLeft, Point3D bottomRight) {
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
    }

    public Point3D[] getVerticesAABB() {
        // We assume that topLeft and bottomRight are the minimum and maximum corners respectively.
        // That is, topLeft has the smallest x, y, z coordinates and bottomRight has the largest x, y, z coordinates.
        return new Point3D[] {topLeft, bottomRight};
    }

    public List<Point3D> getVertices() {
        List<Point3D> vertices = new ArrayList<>();

        double x1 = topLeft.getX();
        double y1 = topLeft.getY();
        double z1 = topLeft.getZ();

        double x2 = bottomRight.getX();
        double y2 = bottomRight.getY();
        double z2 = bottomRight.getZ();

        vertices.add(new Point3D(x1, y1, z1)); // top-left
        vertices.add(new Point3D(x1, y2, z1)); // bottom-left
        vertices.add(new Point3D(x2, y1, z1)); // top-right
        vertices.add(new Point3D(x2, y2, z1)); // bottom-right
        vertices.add(new Point3D(x1, y1, z2)); // top-left (other face)
        vertices.add(new Point3D(x1, y2, z2)); // bottom-left (other face)
        vertices.add(new Point3D(x2, y1, z2)); // top-right (other face)
        vertices.add(new Point3D(x2, y2, z2)); // bottom-right (other face)

        return vertices;
    }

    public Obstacle enlarge(float size) {
        // Assumes size is added equally in all directions.
        return new Obstacle(
                new Point3D(topLeft.getX() - size, topLeft.getY() - size, topLeft.getZ() - size),
                new Point3D(bottomRight.getX() + size, bottomRight.getY() + size, bottomRight.getZ() + size)
        );
    }
}

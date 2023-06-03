package jp.jaxa.iss.kibo.rpc.uae;

import gov.nasa.arc.astrobee.types.Point;

public class Point3D {
    protected double x, y, z;

    public Point3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point toPoint() {
        return new Point(x, y, z);
    }

    public double distanceTo(Point3D other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2) + Math.pow(this.z - other.z, 2));
    }

    public boolean isInsideCuboid(Point3D topLeft, Point3D bottomRight) {
        return this.x >= topLeft.getX() && this.y >= topLeft.getY() && this.z >= topLeft.getZ() &&
                this.x <= bottomRight.getX() && this.y <= bottomRight.getY() && this.z <= bottomRight.getZ();
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }

    public String toString() {
        return x + ", " + y + ", " + z;
    }

    // ... Equals, hashCode, and toString methods
}
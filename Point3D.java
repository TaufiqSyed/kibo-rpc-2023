package jp.jaxa.iss.kibo.rpc.uae;

public class Point3D {
    private double x, y, z;

    public Point3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double distanceTo(Point3D other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2) + Math.pow(this.z - other.z, 2));
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }

    // ... Equals, hashCode, and toString methods
}
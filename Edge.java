package jp.jaxa.iss.kibo.rpc.uae;

public class Edge {
    private Point3D startPoint;
    private Point3D endPoint;
    private double length;

    public Edge(Point3D startPoint, Point3D endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.length = startPoint.distanceTo(endPoint);
    }

    public Point3D getEndPoint() {
        return this.endPoint;
    }

    public double getLength() {
        return this.length;
    }
}
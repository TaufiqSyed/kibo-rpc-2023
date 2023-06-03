package jp.jaxa.iss.kibo.rpc.uae;

import java.util.ArrayList;
import java.util.Collection;

import gov.nasa.arc.astrobee.types.Quaternion;

public class Target extends Point3D {
    private boolean active;
    private Quaternion orientation;
    private int id;

    public Target(int id, double x, double y, double z, Quaternion orientation) {
        super(x, y, z);
        this.orientation = orientation;
        this.active = false;
        this.id = id;
    }
    int getId() {
        return this.id;
    }
    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return this.active;
    }

    public Quaternion getOrientation() {
        return this.orientation;
    }
}
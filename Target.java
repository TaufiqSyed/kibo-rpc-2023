package jp.jaxa.iss.kibo.rpc.uae;

import java.util.ArrayList;
import java.util.Collection;

public class Target extends Point3D {
    private boolean active;

    public Target(double x, double y, double z) {
        super(x, y, z);
        this.active = false;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return this.active;
    }
}
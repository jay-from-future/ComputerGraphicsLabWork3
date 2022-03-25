package xyz.jayfromfuture.interfaces;

import xyz.jayfromfuture.util.Point3D;

import java.util.List;

public interface ControlPanelListener {
    void setBasePoints(List<Point3D> basePoints);

    void setBaseLineVisible(boolean visible);

    void setCurvePointMarked(boolean marked);
}

package main;

import Jama.Matrix;
import interfaces.ControlPanelListener;
import interfaces.RotateListener;
import util.Point2D;
import util.Point3D;
import util.RotationUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

public class DrawPanel extends JPanel implements RotateListener, ControlPanelListener {

    private final static int AXIS_LENGTH = 350;
    private final static int POINT_SIZE = 6;

    private int width;
    private int height;

    private double alpha = 0;
    private double beta = 0;

    private Matrix rotationMatrix;

    private List<Point3D> basePoints; // базисные точки
    private List<Point3D> curvePoints; // точки кривой Безье

    private boolean isBaseLineVisible = true;
    private boolean isCurvePointMarked = false;

    public DrawPanel(int width, int height) {
        addMouseListener(new DrawPanelMouseListener(this, width, height));
        this.width = width;
        this.height = height;
        updateRotationMatrix();
        basePoints = new ArrayList<Point3D>();
    }

    @Override
    public void paint(Graphics g) {

        // заливка фона белым цветом
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // смещение цетра координатных осей на центр панели
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform offsetToCenter = new AffineTransform();
        offsetToCenter.translate(width / 2, (height - 100) / 2);
        g2d.transform(offsetToCenter);

        // рисуем координатные оси
        Point3D zeroPoint = new Point3D(0, 0, 0);
        Point3D xAxis = new Point3D(AXIS_LENGTH, 0, 0);
        Point3D yAxis = new Point3D(0, -AXIS_LENGTH, 0);
        Point3D zAxis = new Point3D(0, 0, AXIS_LENGTH);
        drawAxes(g, zeroPoint, xAxis, yAxis, zAxis);

        // рисуем базисные точки
//        if (isBaseLineVisible) {
//            g.setColor(Color.BLACK);
//            if (basePoints != null) {
//                Point2D prevPoint = null;
//                Point3D nextPoint3D = null;
//                Point2D nextPoint = null;
//                Point2D currentPoint;
//                for (Point3D p : basePoints) {
//                    p = new Point3D(p.getX(), -p.getY(), p.getZ());
//                    currentPoint = RotationUtil.orthogonalProjection(RotationUtil.convert(p, rotationMatrix));
//                    drawPointWithMark(g, currentPoint);
//                    if (prevPoint != null) {
//                        drawLine(g, prevPoint, currentPoint);
//                    }
//                    if (nextPoint != null) {
//                        drawLine(g, nextPoint, currentPoint);
//                    }
//                    prevPoint = currentPoint;
//                    nextPoint3D = basePoints.get(basePoints.indexOf(currentPoint) + 4);
//                    nextPoint3D = new Point3D(nextPoint3D.getX(), -nextPoint3D.getY(), nextPoint3D.getZ());
//                    nextPoint =  RotationUtil.orthogonalProjection(RotationUtil.convert(nextPoint3D, rotationMatrix));
//                }
//            }
//        }

        if (isBaseLineVisible) {
            g.setColor(Color.BLACK);
            if (basePoints != null) {
                List<Point3D> basePointsCopy = new ArrayList<Point3D>(basePoints);
                Point2D nearestByX;
                Point2D nearestByY;
                for (int i = 0; i < basePointsCopy.size(); i++) {
                    Point3D currBasePoint = basePointsCopy.get(i);
//                    nearestByX = RotationUtil.orthogonalProjection(RotationUtil.convert(reverseByY(
//                            findNearestByX(currBasePoint, basePointsCopy)), rotationMatrix));
//                    nearestByY = RotationUtil.orthogonalProjection(RotationUtil.convert(reverseByY(
//                            findNearestByY(currBasePoint, basePointsCopy)), rotationMatrix));

                    currBasePoint = reverseByY(currBasePoint);
                    Point2D currBasePoint2D = RotationUtil.orthogonalProjection(RotationUtil.convert(currBasePoint,
                            rotationMatrix));
                    drawPointWithMark(g, currBasePoint2D);
                    drawString(g, currBasePoint2D, String.valueOf(i));


                    if (i != 3 && i != 7 && i != 11 && i != 15) {

                        if (i + 1 < basePointsCopy.size()) {
                            drawLine(g, currBasePoint2D, RotationUtil.orthogonalProjection(RotationUtil.convert(reverseByY(basePointsCopy.get(i + 1)),
                                    rotationMatrix)));
                        }
                    }
                    if (i + 4 < basePointsCopy.size()) {
                        drawLine(g, currBasePoint2D, RotationUtil.orthogonalProjection(RotationUtil.convert(reverseByY(basePointsCopy.get(i + 4)),
                                rotationMatrix)));
                    }

//                    basePointsCopy.remove(i);
                }
            }
        }

        // расчитываем точки кривой Безье по базисным точкам
        if (basePoints != null) {
            if (!basePoints.isEmpty()) {
                curvePoints = Model.getCurvePoints(basePoints);
            }
        }

        // рисуем точки кривой Безье
//        g.setColor(Color.GRAY);
//        if (curvePoints != null) {
//            Point2D prevPoint = null;
//            Point2D currentPoint;
//            for (Point3D p : curvePoints) {
//                p = new Point3D(p.getX(), -p.getY(), p.getZ());
//                currentPoint = RotationUtil.orthogonalProjection(RotationUtil.convert(p, rotationMatrix));
//                if (isCurvePointMarked) {
//                    drawPointWithMark(g, currentPoint);
//                }
//                if (prevPoint != null) {
//                    drawLine(g, prevPoint, currentPoint);
//                }
//                prevPoint = currentPoint;
//            }
//        }
    }

    private Point3D reverseByY(Point3D currBasePoint) {
        return new Point3D(currBasePoint.getX(), -currBasePoint.getY(), currBasePoint.getZ());
    }

    private Point3D findNearestByY(Point3D currBasePoint, List<Point3D> basePoints) {
        int numberOfNearest = 0;
        double min_delta_y = Double.MAX_VALUE;
        double delta_y;
        for (int i = 0; i < basePoints.size(); i++) {
            delta_y = Math.abs(currBasePoint.getY() - basePoints.get(i).getY());
            if (delta_y < min_delta_y) {
                min_delta_y = delta_y;
                numberOfNearest = i;
            }
        }
        return basePoints.get(numberOfNearest);
    }

    private Point3D findNearestByX(Point3D currBasePoint, List<Point3D> basePoints) {
        int numberOfNearest = 0;
        double min_delta_x = Double.MAX_VALUE;
        double delta_x;
        for (int i = 0; i < basePoints.size(); i++) {
            delta_x = Math.abs(currBasePoint.getX() - basePoints.get(i).getX());
            if (delta_x < min_delta_x) {
                min_delta_x = delta_x;
                numberOfNearest = i;
            }
        }
        return basePoints.get(numberOfNearest);
    }

    @Override
    public void xRotate(double alpha) {
        this.alpha = alpha;
        updateXRotationMatrix();
        repaint();
    }

    @Override
    public void yRotate(double beta) {
        this.beta = beta;
        updateYRotationMatrix();
        repaint();
    }

    @Override
    public void setDefaultRotation() {
        this.alpha = 0;
        this.beta = 0;
        setDefaultRotationMatrix();
        repaint();
    }

    private void setDefaultRotationMatrix() {
        rotationMatrix = RotationUtil.getBaseRotation();
    }

    private void updateRotationMatrix() {
        rotationMatrix = RotationUtil.getBaseRotation();
    }

    private void updateXRotationMatrix() {
        rotationMatrix = RotationUtil.getXRotationMatrix(rotationMatrix, alpha);
    }

    private void updateYRotationMatrix() {
        rotationMatrix = RotationUtil.getYRotationMatrix(rotationMatrix, beta);
    }

    private void drawPointWithMark(Graphics g, Point2D p) {
        drawLine(g, p, p);
        int x = (int) p.getX();
        int y = (int) p.getY();
        g.drawRect(x - POINT_SIZE / 2, y - POINT_SIZE / 2, POINT_SIZE, POINT_SIZE);
    }

    private void drawLine(Graphics g, Point2D p1, Point2D p2) {
        int x1 = (int) p1.getX();
        int y1 = (int) p1.getY();
        int x2 = (int) p2.getX();
        int y2 = (int) p2.getY();

        g.drawLine(x1, y1, x2, y2);
    }

    private void drawString(Graphics g, Point2D p, String str) {
        int x = (int) p.getX();
        int y = (int) p.getY();
        g.drawString(str, x, y);
    }

    private void drawAxes(Graphics g, Point3D zeroPoint, Point3D xAxis, Point3D yAxis, Point3D zAxis) {
        xAxis = RotationUtil.convert(xAxis, rotationMatrix);
        yAxis = RotationUtil.convert(yAxis, rotationMatrix);
        zAxis = RotationUtil.convert(zAxis, rotationMatrix);

        Point2D zeroPoint2D = RotationUtil.orthogonalProjection(zeroPoint);
        Point2D xAxis2D = RotationUtil.orthogonalProjection(xAxis);
        Point2D yAxis2D = RotationUtil.orthogonalProjection(yAxis);
        Point2D zAxis2D = RotationUtil.orthogonalProjection(zAxis);

        g.setColor(Color.RED);
        drawLine(g, zeroPoint2D, xAxis2D);
        drawString(g, xAxis2D, "X");

        g.setColor(Color.GREEN);
        drawLine(g, zeroPoint2D, yAxis2D);
        drawString(g, yAxis2D, "Y");

        g.setColor(Color.BLUE);
        drawLine(g, zeroPoint2D, zAxis2D);
        drawString(g, zAxis2D, "Z");
    }


    @Override
    public void setBasePoints(List<Point3D> basePoints) {
        this.basePoints = basePoints;
        repaint();
    }

    @Override
    public void setBaseLineVisible(boolean visible) {
        this.isBaseLineVisible = visible;
        repaint();
    }

    @Override
    public void setCurvePointMarked(boolean marked) {
        this.isCurvePointMarked = marked;
        repaint();
    }
}

class DrawPanelMouseListener extends MouseAdapter {

    private RotateListener rotateListener;

    private int maxX;
    private int maxY;

    private int startX;
    private int startY;

    private int endX;
    private int endY;

    DrawPanelMouseListener(RotateListener rotateListener, int maxX, int maxY) {
        this.rotateListener = rotateListener;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        startX = e.getX();
        startY = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        endX = e.getX();
        endY = e.getY();
        rotate();
    }

    private void rotate() {

        double alpha;
        double beta;

        double x_delta = endX - startX;
        double y_delta = endY - startY;

        if (Math.abs(x_delta) > Math.abs(y_delta)) {
            beta = (x_delta / maxX) * 90;
            rotateListener.yRotate(beta);
        } else {
            alpha = (y_delta / maxY) * 90;
            rotateListener.xRotate(alpha);
        }
    }
}

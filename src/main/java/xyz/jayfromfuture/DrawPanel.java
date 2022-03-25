package xyz.jayfromfuture;

import Jama.Matrix;
import xyz.jayfromfuture.interfaces.ControlPanelListener;
import xyz.jayfromfuture.interfaces.RotateListener;
import xyz.jayfromfuture.util.Point2D;
import xyz.jayfromfuture.util.Point3D;
import xyz.jayfromfuture.util.RotationUtil;

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
    private final static int POLYHEDRON_SIZE = 4;

    private final int width;
    private final int height;
    private final Matrix defaultRotationMatrix;
    private double alpha = 0;
    private double beta = 0;
    private Matrix rotationMatrix;

    private List<Point3D> basePoints; // базисные точки
    private List<List<Point3D>> curveLines; // список кривых Безье

    private boolean isBaseLineVisible = true;
    private boolean isCurvePointMarked = true;

    public DrawPanel(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        addMouseListener(new DrawPanelMouseListener(this, width, height));
        this.width = width;
        this.height = height;
        updateRotationMatrix();
        defaultRotationMatrix = rotationMatrix;
        basePoints = new ArrayList<>();
    }

    @Override
    public void paint(Graphics g) {

        // заливка фона белым цветом
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // смещение цетра координатных осей на центр панели
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform offsetToCenter = new AffineTransform();
        offsetToCenter.translate(width / 2.0, height / 2.0);
        g2d.transform(offsetToCenter);

        // рисуем координатные оси
        Point3D zeroPoint = new Point3D(0, 0, 0);
        Point3D xAxis = new Point3D(AXIS_LENGTH, 0, 0);
        Point3D yAxis = new Point3D(0, -AXIS_LENGTH, 0);
        Point3D zAxis = new Point3D(0, 0, AXIS_LENGTH);
        drawAxes(g, zeroPoint, xAxis, yAxis, zAxis);

        // расчитываем точки поверхности Безье по базисным точкам
        if (basePoints != null) {
            if (!basePoints.isEmpty()) {
                curveLines = new ArrayList<>(Model.plotBezierSurface(basePoints));
            }
        }

        // рисуем точки поверхности Безье
        g.setColor(Color.GRAY);
        if (curveLines != null) {
            int size = curveLines.size();
            for (int i = 0; i < size; i++) {
                List<Point3D> line = new ArrayList<>();
                for (List<Point3D> curveLine : curveLines) {
                    line.add(curveLine.get(i));
                }
                curveLines.add(line);
            }
            for (List<Point3D> line : curveLines) {
                Point2D prevPoint = null;
                Point2D currentPoint;
                for (Point3D p : line) {
                    p = new Point3D(p.getX(), -p.getY(), p.getZ());
                    currentPoint = RotationUtil.orthogonalProjection(RotationUtil.convert(p, rotationMatrix));
                    if (isCurvePointMarked) {
                        drawPointWithMark(g, currentPoint);
                    }
                    if (prevPoint != null) {
                        drawLine(g, prevPoint, currentPoint);
                    }
                    prevPoint = currentPoint;
                }
            }
        }
        // рисуем базисный многоугольник по точкам
        if (isBaseLineVisible) {
            g.setColor(Color.BLACK);
            if (basePoints != null) {
                List<Point3D> basePointsCopy = new ArrayList<>(basePoints);
                for (int i = 0; i < basePointsCopy.size(); i++) {
                    Point3D currBasePoint = basePointsCopy.get(i);

                    currBasePoint = RotationUtil.reverseByY(currBasePoint);
                    Point2D currBasePoint2D = RotationUtil.orthogonalProjection(RotationUtil.convert(currBasePoint,
                            rotationMatrix));
                    drawPointWithMark(g, currBasePoint2D);
                    drawString(g, new Point2D(currBasePoint2D.getX() + 10, currBasePoint2D.getY() + 10), String.valueOf(i));

                    boolean isNotEndOfRow = true;
                    for (int j = 1; j < POLYHEDRON_SIZE; j++) {
                        if (i == (POLYHEDRON_SIZE * j - 1)) {
                            isNotEndOfRow = false;
                            break;
                        }
                    }
                    if (isNotEndOfRow && (i + 1 < basePointsCopy.size())) {
                        drawLine(g, currBasePoint2D, RotationUtil.orthogonalProjection(
                                RotationUtil.convert(RotationUtil.reverseByY(basePointsCopy.get(i + 1)), rotationMatrix)));
                    }
                    if (i + 4 < basePointsCopy.size()) {
                        drawLine(g, currBasePoint2D, RotationUtil.orthogonalProjection(RotationUtil.convert(
                                RotationUtil.reverseByY(basePointsCopy.get(i + 4)), rotationMatrix)));
                    }
                }
            }
        }
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
        xAxis = RotationUtil.convert(xAxis, defaultRotationMatrix);
        yAxis = RotationUtil.convert(yAxis, defaultRotationMatrix);
        zAxis = RotationUtil.convert(zAxis, defaultRotationMatrix);

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

    private final RotateListener rotateListener;

    private final int maxX;
    private final int maxY;

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

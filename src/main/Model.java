package main;

import Jama.Matrix;
import util.Point3D;

import java.util.ArrayList;
import java.util.List;

public class Model {

    private final static double T_STEP = 0.1; // шаг переменной t (t принадлежит [0, 1])
    private final static double[][] basisMatrix = {
            {-1, 3, -3, 1},
            {3, -6, 3, 0},
            {-3, 3, 0, 0},
            {1, 0, 0, 0}
    };

    public static List<Point3D> getCurvePoints(List<Point3D> basePoints) {
        List<Point3D> curvePoints = new ArrayList<Point3D>();
        int i = 0;
        do {
            curvePoints.addAll(getCurvePoints(basePoints.get(i), basePoints.get(i + 1), basePoints.get(i + 2),
                    basePoints.get(i + 3)));
            i += 3;
        } while (i < (basePoints.size() - 1));
        return curvePoints;
    }

    public static List<Point3D> getCurvePoints(Point3D p0, Point3D p1, Point3D p2, Point3D p3) {
        List<Point3D> curvePoints = new ArrayList<Point3D>();
        double[][] t_matrix = new double[1][4];
        // заполням матрицу точек p
        double[][] p_matrix = {
                {p0.getX(), p0.getY(), p0.getZ()},
                {p1.getX(), p1.getY(), p1.getZ()},
                {p2.getX(), p2.getY(), p2.getZ()},
                {p3.getX(), p3.getY(), p3.getZ()}
        };
        for (double t = 0; t <= 1.0; t += T_STEP) {
            // заполняем матрицу t (строка)
            for (int i = 0; i <= 3; i++) {
                t_matrix[0][i] = Math.pow(t, 3 - i);
            }
            double[][] b_point_matrix = (new Matrix(t_matrix).times(new Matrix(basisMatrix))).
                    times(new Matrix(p_matrix)).getArray();
            curvePoints.add(new Point3D(b_point_matrix[0][0], b_point_matrix[0][1], b_point_matrix[0][2]));
        }
        return curvePoints;
    }
}
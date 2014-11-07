package main;

import Jama.Matrix;
import util.Point3D;

import java.util.ArrayList;
import java.util.List;

public class Model {

    private final static double T_STEP = 0.05; // шаг переменной t (t принадлежит [0, 1])
    private final static double W_STEP = 0.05; // шаг переменной w (w принадлежит [0, 1])

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

    public static List<List<Point3D>> plotBezierSurface(final List<Point3D> basePoints) {

        /**
         * Расположение точек:
         *
         * 0 1 2 3
         * 4 5 6 7
         * 8 9 10 11
         * 12 13 14 15
         */

        double[][] x_matrix = new double[4][4];
        double[][] y_matrix = new double[4][4];
        double[][] z_matrix = new double[4][4];

        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                x_matrix[r][c] = basePoints.get(r * 4 + c).getX();
                y_matrix[r][c] = basePoints.get(r * 4 + c).getY();
                z_matrix[r][c] = basePoints.get(r * 4 + c).getZ();
            }
        }

        Matrix basis = new Matrix(basisMatrix);
        Matrix basePointsXMatrixChanged = (basis.times(new Matrix(x_matrix))).times(basis);
        Matrix basePointsYMatrixChanged = (basis.times(new Matrix(y_matrix))).times(basis);
        Matrix basePointsZMatrixChanged = (basis.times(new Matrix(z_matrix))).times(basis);

        double[][] t_matrix = new double[1][4];
        double[][] w_matrix = new double[4][1];

        List<List<Point3D>> linesOfSurface = new ArrayList<List<Point3D>>();

        for (double t = 0; t <= 1.0; t += T_STEP) {
            // заполняем матрицу t (строка)
            for (int i = 0; i <= 3; i++) {
                t_matrix[0][i] = Math.pow(t, 3 - i);
            }
            List<Point3D> currLine = new ArrayList<Point3D>();
            Matrix t_jama_matrix = new Matrix(t_matrix);
            for (double w = 0; w <= 1.0; w += W_STEP) {
                // заполняем матрицу w (столбец)
                for (int i = 0; i <= 3; i++) {
                    w_matrix[i][0] = Math.pow(w, 3 - i);
                }
                Matrix w_jama_matrix = new Matrix(w_matrix);

                double x = (t_jama_matrix.times(basePointsXMatrixChanged)).times(w_jama_matrix).get(0, 0);
                double y = (t_jama_matrix.times(basePointsYMatrixChanged)).times(w_jama_matrix).get(0, 0);
                double z = (t_jama_matrix.times(basePointsZMatrixChanged)).times(w_jama_matrix).get(0, 0);

                currLine.add(new Point3D(x, y, z));
            }
            linesOfSurface.add(currLine);
        }
        return linesOfSurface;
    }

    private static Point3D pointToNumber(Point3D point3D, double v) {
        return new Point3D(point3D.getX() * v, point3D.getY() * v, point3D.getZ() * v);
    }
}
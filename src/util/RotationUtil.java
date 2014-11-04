package util;

import Jama.Matrix;

public class RotationUtil {

    private final static double DEGREES_TO_RADIANS = Math.PI / 180;
    private final static double ALPHA = 35.264;
    private final static double BETA = 45;

    public static Matrix getBaseRotation() {
        double[][] matrixAlpha = {
                {1, 0, 0},
                {0, Math.cos(ALPHA * DEGREES_TO_RADIANS), Math.sin(ALPHA * DEGREES_TO_RADIANS)},
                {0, -Math.sin(ALPHA * DEGREES_TO_RADIANS), Math.cos(ALPHA * DEGREES_TO_RADIANS)}
        };
        double[][] matrixBeta = {
                {Math.cos(BETA * DEGREES_TO_RADIANS), 0, -Math.sin(BETA * DEGREES_TO_RADIANS)},
                {0, 1, 0},
                {Math.sin(BETA * DEGREES_TO_RADIANS), 0, Math.cos(BETA * DEGREES_TO_RADIANS)}
        };
        return new Matrix(matrixAlpha).times(new Matrix(matrixBeta));
    }

    public static Matrix getXRotationMatrix(Matrix rotationMatrix, double alpha) {
        alpha *= DEGREES_TO_RADIANS;
        double[][] matrixAlpha = {
                {1, 0, 0},
                {0, Math.cos(alpha), Math.sin(alpha)},
                {0, -Math.sin(alpha), Math.cos(alpha)}
        };
        return rotationMatrix.times(new Matrix(matrixAlpha));
    }

    public static Matrix getYRotationMatrix(Matrix rotationMatrix, double beta) {
        beta *= DEGREES_TO_RADIANS;
        double[][] matrixBeta = {
                {Math.cos(beta), 0, -Math.sin(beta)},
                {0, 1, 0},
                {Math.sin(beta), 0, Math.cos(beta)}
        };
        return rotationMatrix.times(new Matrix(matrixBeta));
    }

    public static Point3D convert(Point3D point3D, Matrix rotationMatrix) {
        double[] pointArray = point3D.getArray();
        double[][] pointMatrix = {
                {pointArray[0]},
                {pointArray[1]},
                {pointArray[2]}
        };
        double[][] rotationPoint = rotationMatrix.times(new Matrix(pointMatrix)).getArray();
        return new Point3D(rotationPoint[0][0], rotationPoint[1][0], rotationPoint[2][0]);
    }

    public static Point2D orthogonalProjection(Point3D point3D) {
        return new Point2D(point3D.getX(), point3D.getY());
    }

    public static String matrixToString(Matrix matrix) {
        StringBuilder result = new StringBuilder();
        double[][] m = matrix.getArray();
        for (int r = 0; r < matrix.getRowDimension(); r++) {
            for (int c = 0; c < matrix.getColumnDimension(); c++) {
                result.append(m[r][c]);
                result.append(" ");
            }
            result.append(System.lineSeparator());
        }
        return result.toString();
    }
}

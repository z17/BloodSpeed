package helper;

public final class MatrixHelper {
    public static int[][] multiplyMatrix(int[][] matrix, double q) {
        for (int i = 0; i < matrix.length; i++) {
            for (int k = 0; k < matrix[i].length; k++) {
                matrix[i][k] = (int) Math.round(matrix[i][k] * q);
            }
        }
        return matrix;
    }
}

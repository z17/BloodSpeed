package blood_speed.helper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class MatrixHelper {
    public static int[][] multiplyMatrix(int[][] matrix, double q) {
        int[][] m = new int[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            for (int k = 0; k < matrix[i].length; k++) {
                m[i][k] = (int) Math.round(matrix[i][k] * q);
            }
        }
        return m;
    }

    public static double[][] readDoubleMatrix(final String inTxt) {
        try {
            final List<String> strings = Files.readAllLines(Paths.get(inTxt));
            final int rows = strings.size();
            final int columns = strings.get(0).trim().split("\\s+").length;
            final double[][] matrix = new double[rows][columns];

            final NumberFormat format = NumberFormat.getInstance();
            int row = 0;
            for (String line : strings) {
                int column = 0;
                final String[] split = line.trim().split("\\s+");
                if (split.length != columns) {
                    throw new IllegalArgumentException("Not a matrix");
                }

                for (String oneNumber : split) {


                    Number number = format.parse(oneNumber.trim());
                    matrix[row][column] = number.doubleValue();
                    column++;
                }
                row++;
            }

            return matrix;

        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static int[][] readMatrix(final String inTxt) {
        try {
            final List<String> strings = Files.readAllLines(Paths.get(inTxt));
            final int rows = strings.size();
            final int columns = strings.get(0).trim().split("\\s+").length;
            final int[][] matrix = new int[rows][columns];

            int row = 0;
            for (String line : strings) {
                int column = 0;
                final String[] split = line.trim().split("\\s+");
                if (split.length != columns) {
                    throw new IllegalArgumentException("Not a matrix");
                }

                for (String oneNumber : split) {
                    matrix[row][column] = Integer.valueOf(oneNumber.trim());
                    column++;
                }
                row++;
            }

            return matrix;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeMatrix(final String name, final int[][] matrix) {
        List<String> lines = new ArrayList<>();
        for (int[] m : matrix) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int k : m) {
                stringBuilder.append(String.format("%10d", k));
            }
            lines.add(stringBuilder.toString());
        }
        try {
            Files.write(Paths.get(name), lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeMatrix(final String name, final double[][] matrix) {
        List<String> lines = new ArrayList<>();
        for (double[] m : matrix) {
            StringBuilder stringBuilder = new StringBuilder();
            for (double k : m) {
                stringBuilder.append(String.format("%10f", k));
            }
            lines.add(stringBuilder.toString());
        }
        try {
            Files.write(Paths.get(name), lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends Number> void writeMatrix(final String name, final List<T> matrix) {
        List<String> lines = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        for (Number m : matrix) {
            stringBuilder.append(String.format(Locale.ROOT, "%20.5f", m.doubleValue()));
        }

        lines.add(stringBuilder.toString());
        try {
            Files.write(Paths.get(name), lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeMatrix(final String name, final Integer[][] matrix) {
        writeMatrix(name, convertMatrix(matrix));
    }

    public static int[][] copyMatrix(final int[][] matrix) {
        int[][] result = new int[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            result[i] = Arrays.copyOf(matrix[i], matrix[i].length);
        }
        return result;
    }

    public static int[][] convertMatrix(Integer[][] matrix) {
        int[][] result = new int[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            result[i] = new int[matrix[i].length];
            for (int j = 0; j < matrix[i].length; j++) {
                result[i][j] = matrix[i][j];
            }
        }
        return result;
    }
}

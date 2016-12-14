package helper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public final class MatrixHelper {
    public static int[][] multiplyMatrix(int[][] matrix, double q) {
        for (int i = 0; i < matrix.length; i++) {
            for (int k = 0; k < matrix[i].length; k++) {
                matrix[i][k] = (int) Math.round(matrix[i][k] * q);
            }
        }
        return matrix;
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
                stringBuilder.append(String.format("%7d", k));
            }
            lines.add(stringBuilder.toString());
        }
        try {
            Files.write(Paths.get(name), lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

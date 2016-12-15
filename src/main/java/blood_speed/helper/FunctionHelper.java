package blood_speed.helper;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.DoubleStream;

public final class FunctionHelper {
    public static double mean(double[] array) {
        int k = array.length;
        double sum = DoubleStream.of(array).sum();
        return sum / k;
    }

    public static double mean(double[][] matrix) {
        long n = Arrays.stream(matrix).flatMapToDouble(Arrays::stream).count();
        double sum = Arrays.stream(matrix).flatMapToDouble(Arrays::stream).sum();
        return sum / n;
    }

    public static int cols(int[][] matrix) {
        long count = Arrays.stream(matrix).map(array -> array.length).distinct().count();
        if (count > 1) {
            throw new IllegalArgumentException("Not a matrix");
        }
        return matrix[0].length;
    }

    public static int rows(int[][] matrix) {
        return matrix.length;
    }

    public static void checkIOFolders(final String input, final String output) {
        if (input != null && !Files.exists(Paths.get(input))) {
            throw new RuntimeException("Input folder not found");
        }
        try {
            Files.createDirectories(Paths.get(output));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

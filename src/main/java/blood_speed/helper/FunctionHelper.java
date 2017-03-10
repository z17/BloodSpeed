package blood_speed.helper;


import blood_speed.step.data.Point;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    public static void checkOutputFolders(final String output) {
        try {
            Files.createDirectories(Paths.get(output));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writePointsList(final String name, final List<Point> points) {
        List<String> lines = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        for (Point p : points) {
            stringBuilder
                    .append(p.getIntX())
                    .append(" ")
                    .append(p.getIntY())
                    .append(System.lineSeparator());
        }

        lines.add(stringBuilder.toString());
        try {
            Files.write(Paths.get(name), lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Point> readPointsList(final String name) {
        try {
            final List<String> strings = Files.readAllLines(Paths.get(name));
            final List<Point> result = new ArrayList<>();
            for (String s : strings) {
                if (s.isEmpty()) {
                    continue;
                }

                int spacePosition = s.indexOf(" ");
                String x = s.substring(0, spacePosition);
                String y = s.substring(spacePosition + 1);
                result.add(new Point(Integer.valueOf(x), Integer.valueOf(y)));
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Cant read file " + name, e);
        }
    }
}

package blood_speed.helper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public final class BmpHelper {

    public static void writeBmp(final String name, final Integer[][] matrix) {
        writeBmp(name, MatrixHelper.convertMatrix(matrix));
    }

    public static void writeBmp(final String name, final int[][] matrix) {

        int[] preparedArray = new int[matrix.length * matrix[0].length];

        int index = 0;
        for (int[] aMatrix : matrix) {
            for (int value : aMatrix) {
                preparedArray[index] = value;
                index++;
            }
        }

        BufferedImage img = new BufferedImage(matrix[0].length, matrix.length, BufferedImage.TYPE_BYTE_GRAY);
        img.getRaster().setPixels(0, 0, matrix[0].length, matrix.length, preparedArray);

        try {
            ImageIO.write(img, "BMP", new File(name));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeBmp(final String name, final double[][] matrix) {

        double[] preparedArray = new double[matrix.length * matrix[0].length];

        int index = 0;
        for (double[] aMatrix : matrix) {
            for (double value : aMatrix) {
                preparedArray[index] = value;
                index++;
            }
        }

        BufferedImage img = new BufferedImage(matrix[0].length, matrix.length, BufferedImage.TYPE_BYTE_GRAY);
        img.getRaster().setPixels(0, 0, matrix[0].length, matrix.length, preparedArray);

        try {
            ImageIO.write(img, "BMP", new File(name));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int[][] readBmp(final String name) {
        try {
            BufferedImage image = ImageIO.read(new File(name));
            if (image.getType() == BufferedImage.TYPE_3BYTE_BGR) {
                return readBmp3ByteBGR(image);
            } else if (image.getType() == BufferedImage.TYPE_BYTE_GRAY || image.getType() == BufferedImage.TYPE_BYTE_INDEXED) {
                return readBmpByteGray(image);
            }

            throw new RuntimeException("Unknown image type (not TYPE_3BYTE_BGR or TYPE_BYTE_GRAY");
        } catch (IOException e) {
            throw new RuntimeException("Cant read file " + name, e);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private static int[][] readBmpByteGray(final BufferedImage image) {
        int[] nullArray = null;
        int[] pixels = image.getRaster().getPixels(0, 0, image.getWidth(), image.getHeight(), nullArray);
        int[][] result = new int[image.getHeight()][image.getWidth()];
        for (int i = 0; i < image.getHeight(); i++) {
            for (int k = 0; k < image.getWidth(); k++) {
                int index = k + i * image.getWidth();
                result[i][k] = pixels[index];
            }
        }
        return result;
    }

    @SuppressWarnings("ConstantConditions")
    private static int[][] readBmp3ByteBGR(final BufferedImage image) {
        int[] nullArray = null;
        int[] pixels = image.getData().getPixels(0, 0, image.getWidth(), image.getHeight(), nullArray);

        final int[][] result = new int[image.getHeight()][image.getWidth()];
        for (int i = 0; i < pixels.length; i = i + 3) {
            int positionNumber = i / 3;
            int row = positionNumber / image.getWidth();
            int col = positionNumber % image.getWidth();
            result[row][col] = (pixels[i] + pixels[i + 1] + pixels[i + 2]) / 3;
        }
        return result;
    }

    /**
     * Преобразует значения матрицы в промежутке 0-255 для bmp формата
     */
    public static int[][] transformToImage(final int[][] matrix) {
        int[][] result = new int[FunctionHelper.rows(matrix)][FunctionHelper.cols(matrix)];

        // максимум и минимум суммы
        int min = matrix[0][0];
        int max = matrix[0][0];
        for (int[] aMatrix : matrix) {
            for (int anAMatrix : aMatrix) {
                if (min > anAMatrix) {
                    min = anAMatrix;
                }
                if (max < anAMatrix) {
                    max = anAMatrix;
                }
            }
        }

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                double coefficient = ((double) max - min) / 255;
                result[i][j] = (int) Math.round((matrix[i][j] - min) / coefficient);
            }
        }
        return result;
    }

    /**
     * Преобразует значения матрицы в промежутке 0-255 для bmp формата
     */
    public static int[][] transformToImage(final double[][] matrix) {
        int[][] result = new int[FunctionHelper.rows(matrix)][FunctionHelper.cols(matrix)];

        // максимум и минимум суммы
        double min = matrix[0][0];
        double max = matrix[0][0];
        for (double[] aMatrix : matrix) {
            for (double anAMatrix : aMatrix) {
                if (min > anAMatrix) {
                    min = anAMatrix;
                }
                if (max < anAMatrix) {
                    max = anAMatrix;
                }
            }
        }

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                double coefficient = (max - min) / 255;
                result[i][j] = (int) Math.round((matrix[i][j] - min) / coefficient);
            }
        }
        return result;
    }
}

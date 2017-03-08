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
            BufferedImage read = ImageIO.read(new File(name));
            int[] nullArray = null;
            int[] pixels = read.getRaster().getPixels(0, 0, read.getWidth(), read.getHeight(), nullArray);
            int[][] result = new int[read.getHeight()][ read.getWidth()];
            for (int i = 0; i < read.getHeight(); i++ ) {
                for (int k = 0; k < read.getWidth(); k++) {
                    int index = k + i * read.getWidth();
                    result[i][k] = pixels[index];
                }
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Cant read file " + name, e);
        }
    }

    public static int[][] readBmpColors(final String name) {
        try {
            BufferedImage image = ImageIO.read(new File(name));
            int[] nullArray = null;
            final int[][] result = new int[image.getHeight()][image.getWidth()];

            int[] pixels = image.getData().getPixels(0, 0, image.getWidth(), image.getHeight(), nullArray);

            for (int i = 0; i < pixels.length; i = i + 3) {
                int positionNumber = i / 3;
                int row = positionNumber / image.getWidth();
                int col = positionNumber % image.getWidth();
                result[row][col] = (pixels[i] +  pixels[i + 1] +  pixels[i + 2]) / 3;
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

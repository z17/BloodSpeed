package blood_speed.helper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public final class BmpHelper {
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

    @SuppressWarnings("ConstantConditions")
    public static int[][] readBmp(final String name) {
        try {
            BufferedImage read = ImageIO.read(new File(name));
            int[] opt = null;
            int[] pixels = read.getRaster().getPixels(0, 0, read.getWidth(), read.getHeight(), opt);
            int[][] result = new int[read.getHeight()][ read.getWidth()];
            for (int i = 0; i < read.getHeight(); i++ ) {
                for (int k = 0; k < read.getWidth(); k++) {
                    int index = k + i * read.getWidth();
                    result[i][k] = pixels[index];
                }
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

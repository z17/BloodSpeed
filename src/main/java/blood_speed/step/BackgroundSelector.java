package blood_speed.step;

import blood_speed.helper.BmpHelper;
import blood_speed.helper.FunctionHelper;
import blood_speed.helper.MatrixHelper;
import blood_speed.step.data.Images;

import java.util.Arrays;

/**
 * Класс для выделения контура, суммированного изображения и компенсации фона
 */
public class BackgroundSelector extends Step<Images> {
    private final Images images;
    private final String outputFolder;

    public BackgroundSelector(Images images, String outputFolder) {
        this.images = images;
        this.outputFolder = outputFolder;
        FunctionHelper.checkIOFolders(null, outputFolder);
    }

    @Override
    public Images process() {
        // сумма всех изображений
        int[][] sumImage = new int[images.getRows()][images.getCols()];
        for (int[][] matrix : images.getImagesList()) {
            for (int i = 0; i < images.getRows(); i++) {
                for (int j = 0; j < images.getCols(); j++) {
                    sumImage[i][j] += matrix[i][j];
                }
            }
        }

        MatrixHelper.writeMatrix(outputFolder + "/sum.txt", sumImage);

        // максимум и минимум суммы
        int min = sumImage[0][0];
        int max = sumImage[0][0];
        for (int i = 0; i < images.getRows(); i++) {
            for (int j = 0; j < images.getCols(); j++) {
                if (min > sumImage[i][j]) {
                    min = sumImage[i][j];
                }
                if (max < sumImage[i][j]) {
                    max = sumImage[i][j];
                }
            }
        }

        // формируем суммированное изображение
        int sum = 0;
        for (int i = 0; i < images.getRows(); i++) {
            for (int j = 0; j < images.getCols(); j++) {
                double coefficient = ((double) max - min) / 256;
                sumImage[i][j] = (int) Math.round((sumImage[i][j] - min) / coefficient);
                sum += sumImage[i][j];
            }
        }
        int middleSumImage = (int) (sum / (images.getRows() * images.getCols() * 1.56));
        BmpHelper.writeBmp(outputFolder + "/sum-image.bmp", sumImage);

        // формируем контур капилляра
        int[][] contourImage = new int[images.getRows()][images.getCols()];
        for (int i = 0; i < images.getRows(); i++) {
            for (int j = 0; j < images.getCols(); j++) {
                contourImage[i][j] = sumImage[i][j] > middleSumImage ? 255 : 0;
            }
        }
        BmpHelper.writeBmp(outputFolder + "/contour-image.bmp", contourImage);


        // вычиcляем минимум в каждой точки для всего ряда
        int[][] minValues = new int[images.getRows()][];
        int[][] first = images.getImagesList().get(0);
        for (int i = 0; i < first.length; i++) {
            minValues[i] = Arrays.copyOf(first[i], first[i].length);
        }

        for (int[][] matrix : images.getImagesList()) {
            for (int i = 0; i < images.getRows(); i++) {
                for (int j = 0; j < images.getCols(); j++) {
                    if (minValues[i][j] > matrix[i][j]) {
                        minValues[i][j] = matrix[i][j];
                    }
                }
            }
        }

        // вычитаем минимум из каждой точки и сохраняем изображение
        int currentNumber = 0;
        for (int[][] matrix : images.getImagesList()) {
            int[][] result = new int[images.getRows()][images.getCols()];
            for (int i = 0; i < images.getRows(); i++) {
                for (int j = 0; j < images.getCols(); j++) {
                    result[i][j] = (matrix[i][j] - minValues[i][j]) * 2;
                }
            }
            BmpHelper.writeBmp(outputFolder + "/background_" + currentNumber + ".bmp", result);
            currentNumber++;
        }

        return null;
    }


    public static Images loadInputData(final String inputFolder) {
        final Images result = new Images();
        for (int i = 0; i < 300; i++) {
            int[][] bmp = BmpHelper.readBmpColors(inputFolder + "img1_00000_" + String.format("%05d", i) + ".bmp");
            result.add(bmp);
        }
        return result;
    }

    public static void main(String[] args) {
        Images images = loadInputData("data/test2/out2b/");
        BackgroundSelector backgroundSelector = new BackgroundSelector(images, "data/backgroundSelector_v2/");
        backgroundSelector.process();
    }

    public static Images loadOutputData(final String inputFolder) {
        final Images result = new Images();
        for (int i = 0; i < 300; i++) {
            int[][] bmp = BmpHelper.readBmp(inputFolder + "background_" + i + ".bmp");
            result.add(bmp);
        }
        return result;
    }
}

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

        int[][] sumImage2 = new int[images.getRows()][images.getCols()];
        // вычитаем минимум из каждой точки и сохраняем изображение
        int currentNumber = 0;
        for (int[][] matrix : images.getImagesList()) {
            int[][] result = new int[images.getRows()][images.getCols()];
            for (int i = 0; i < images.getRows(); i++) {
                for (int j = 0; j < images.getCols(); j++) {
                    result[i][j] = (matrix[i][j] - minValues[i][j]) * 2;
                    sumImage2[i][j] += result[i][j];
                }
            }
            BmpHelper.writeBmp(outputFolder + "/background_" + currentNumber + ".bmp", result);
            currentNumber++;
        }

        // максимум и минимум суммы
        int min2 = sumImage2[0][0];
        int max2 = sumImage2[0][0];
        for (int i = 0; i < images.getRows(); i++) {
            for (int j = 0; j < images.getCols(); j++) {
                if (min2 > sumImage2[i][j]) {
                    min2 = sumImage2[i][j];
                }
                if (max2 < sumImage2[i][j]) {
                    max2 = sumImage2[i][j];
                }
            }
        }

        MatrixHelper.writeMatrix(outputFolder + "/sum2.txt", sumImage2);
        // формируем суммированное изображение
        for (int i = 0; i < images.getRows(); i++) {
            for (int j = 0; j < images.getCols(); j++) {
                double coefficient2 = ((double) max2 - min2) / 256;
                sumImage2[i][j] = (int) Math.round((sumImage2[i][j] - min2) / coefficient2);
            }
        }
        BmpHelper.writeBmp(outputFolder + "/sum-image2.bmp", sumImage2);

        return null;
    }


    public static Images loadInputData(final String inputFolder) {
        final Images result = new Images();
        for (int i = 800; i <= 1199; i++) {
//            int[][] bmp = BmpHelper.readBmpColors(inputFolder + "img0_00000_" + String.format("%05d", i) + ".bmp");
            int[][] bmp = BmpHelper.readBmp(inputFolder + "img0_00000_" + String.format("%05d", i) + ".bmp");
            result.add(bmp);
        }
        return result;
    }

    public static void main(String[] args) {
        Images images = loadInputData("data/tests/all_cap_smolensk/step2_stab/");
        BackgroundSelector backgroundSelector = new BackgroundSelector(images, "data/tests/all_cap_smolensk/backgroundSelector/");
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

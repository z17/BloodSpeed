package blood_speed.step;

import blood_speed.helper.BmpHelper;
import blood_speed.helper.FunctionHelper;
import blood_speed.step.data.Images;

import java.util.Arrays;

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
        int[][] circuitImage = new int[images.getRows()][images.getCols()];
        for (int i = 0; i < images.getRows(); i++) {
            for (int j = 0; j < images.getCols(); j++) {
                circuitImage[i][j] = sumImage[i][j] > middleSumImage ? 255 : 0;
            }
        }
        BmpHelper.writeBmp(outputFolder + "/circuit-image.bmp", circuitImage);


        // вычитаем миинимум каждой точки из всех изображений
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


    public static Images loadData(final String inputFolder) {
        final Images result = new Images();
        for (int i = 0; i < 300; i++) {
            int[][] bmp = BmpHelper.readBmpColors(inputFolder + "img1_00000_" + String.format("%05d", i) + ".bmp");
            result.add(bmp);
        }
        return result;
    }

    public static void main(String[] args) {
        Images images = loadData("data/test2/out2b/");
        BackgroundSelector backgroundSelector = new BackgroundSelector(images, "data/backgroundSelector_v2/");
        backgroundSelector.process();
    }
}

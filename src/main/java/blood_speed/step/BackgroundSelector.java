package blood_speed.step;

import blood_speed.helper.BmpHelper;
import blood_speed.helper.FunctionHelper;
import blood_speed.helper.MatrixHelper;
import blood_speed.step.data.Images;

/**
 * Класс для выделения контура, суммированного изображения и компенсации фона
 */
public class BackgroundSelector extends Step<Images> {

    private final Images images;
    private final String outputFolder;

    private final static String SUM_IMAGE_NAME = "sum-image.bmp";
    private final static String SUM_FILE_NAME = "sum.txt";
    private static final String CONTOUR_IMAGE_NAME = "contour-image.bmp";

    public BackgroundSelector(Images images, String outputFolder) {
        this.images = images;
        this.outputFolder = outputFolder;
        FunctionHelper.checkOutputFolders(outputFolder);
    }

    public static void main(String[] args) {
        Images images = loadInputData("data/tests/kris_2017_030_1/out2b/");
        BackgroundSelector backgroundSelector = new BackgroundSelector(images, "data/tests/kris_2017_030_1/backgroundSelector/");
        backgroundSelector.process();
    }

    @Override
    public Images process() {

        createSumImage();
        return brightnessCompensation();
    }

    private Images brightnessCompensation() {
        double[][] middleValues = new double[images.getRows()][images.getCols()];
        for (int k = 0; k <= 20; k++) {
            int[][] current = images.getImagesList().get(k);
            for (int i = 0; i < images.getRows(); i++) {
                for (int j = 0; j < images.getCols(); j++) {
                    middleValues[i][j] += current[i][j] / 21;
                }
            }
        }

        Images resultImages = new Images();
        for (int currentNumber = 0; currentNumber < images.getImagesList().size(); currentNumber++) {

            // для первых 11 и последних 10 кадров пропускаем изменение middleValue
            if (currentNumber > 10 && currentNumber < images.getImagesList().size() - 10) {
                int[][] deletedFrame = images.getImagesList().get(currentNumber - 11);
                // из среднего значения вычитаем 1 кадр, который вышел за рамки области -10 кадров..текущий..+10 кадров
                for (int i = 0; i < images.getRows(); i++) {
                    for (int j = 0; j < images.getCols(); j++) {
                        middleValues[i][j] -= deletedFrame[i][j] / 21;
                    }
                }

                int[][] addedFrame = images.getImagesList().get(currentNumber + 10);
                // из среднего значения добавляем 1 кадр, который попал в рамки  области -10 кадров..текущий..+10 кадров
                for (int i = 0; i < images.getRows(); i++) {
                    for (int j = 0; j < images.getCols(); j++) {
                        middleValues[i][j] += addedFrame[i][j] / 21;
                    }
                }
            }

            int[][] currentImage = images.getImagesList().get(currentNumber);
            int[][] currentResult = new int[images.getRows()][images.getCols()];
            for (int i = 0; i < images.getRows(); i++) {
                for (int j = 0; j < images.getCols(); j++) {
                    currentResult[i][j] = (int) Math.round(currentImage[i][j] - middleValues[i][j] + 127);
                }
            }

            resultImages.add(currentResult);
            BmpHelper.writeBmp(outputFolder + "/background_" + currentNumber + ".bmp", currentResult);
        }
        return resultImages;
    }

    private void createSumImage() {
        // сумма всех изображений
        int[][] sumImage = new int[images.getRows()][images.getCols()];
        for (int[][] matrix : images.getImagesList()) {
            for (int i = 0; i < images.getRows(); i++) {
                for (int j = 0; j < images.getCols(); j++) {
                    sumImage[i][j] += matrix[i][j];
                }
            }
        }

        MatrixHelper.writeMatrix(outputFolder + SUM_FILE_NAME, sumImage);

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
                double coefficient = ((double) max - min) / 255;
                sumImage[i][j] = (int) Math.round((sumImage[i][j] - min) / coefficient);
                sum += sumImage[i][j];
            }
        }

        int middleSumImage = (int) (sum / (images.getRows() * images.getCols() * 1.56));
        BmpHelper.writeBmp(outputFolder + SUM_IMAGE_NAME, sumImage);

        // формируем контур капилляра
        int[][] contourImage = new int[images.getRows()][images.getCols()];
        for (int i = 0; i < images.getRows(); i++) {
            for (int j = 0; j < images.getCols(); j++) {
                contourImage[i][j] = sumImage[i][j] > middleSumImage ? 255 : 0;
            }
        }
        BmpHelper.writeBmp(outputFolder + CONTOUR_IMAGE_NAME, contourImage);
    }

    public static Images loadInputData(final String inputFolder) {
        final Images result = new Images();
        for (int i = 0; i <= 500; i++) {
            int[][] bmp = BmpHelper.readBmp(inputFolder + "img1_00000_" + String.format("%05d", i) + ".bmp");
            result.add(bmp);
        }
        return result;
    }

    public static Images loadOutputData(final String inputFolder) {
        final Images result = new Images();
        for (int i = 0; i < 1600; i++) {
            int[][] bmp = BmpHelper.readBmp(inputFolder + "background_" + i + ".bmp");
            result.add(bmp);
        }
        return result;
    }
}

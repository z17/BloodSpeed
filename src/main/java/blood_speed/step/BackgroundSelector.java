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
    private final int blurDepth;

    private final static String SUM_IMAGE_CLEAR_NAME = "sum-image-clear.bmp";
    private final static String SUM_IMAGE_NAME = "sum-image.bmp";
    private final static String SUM_IMAGE_BLUR_NAME = "sum-image-blur.bmp";
    private final static String SUM_FILE_NAME = "sum.txt";
    private final static String SUM_FILE_NAME_CLEAR = "sum-clear.txt";
    private static final String CONTOUR_IMAGE_NAME = "contour-image.bmp";

    public BackgroundSelector(final Images images, final String outputFolder, final int blurDepth) {
        this.images = images;
        this.outputFolder = outputFolder;
        this.blurDepth = blurDepth;
        FunctionHelper.checkOutputFolders(outputFolder);
    }

    @Override
    public Images process() {
        createSumAndContourImages();
        return brightnessCompensation();
    }

    private Images brightnessCompensation() {
        final int blurDivider = 2 * blurDepth + 1;
        double[][] middleValues = new double[images.getRows()][images.getCols()];
        for (int k = 0; k < blurDivider; k++) {
            int[][] current = images.getImagesList().get(k);
            for (int i = 0; i < images.getRows(); i++) {
                for (int j = 0; j < images.getCols(); j++) {
                    middleValues[i][j] += (double) current[i][j] / blurDivider;
                }
            }
        }

        for (int currentNumber = 0; currentNumber < images.getImagesList().size(); currentNumber++) {

            // для первых 11 и последних 10 кадров пропускаем изменение middleValue
            if (currentNumber > blurDepth && currentNumber < images.getImagesList().size() - blurDepth) {
                int[][] addedFrame = images.getImagesList().get(currentNumber + blurDepth);
                int[][] deletedFrame = images.getImagesList().get(currentNumber - blurDepth - 1);
                // из среднего значения вычитаем 1 кадр, который вышел за рамки области -10 кадров..текущий..+10 кадров
                // из среднего значения добавляем 1 кадр, который попал в рамки  области -10 кадров..текущий..+10 кадров
                for (int i = 0; i < images.getRows(); i++) {
                    for (int j = 0; j < images.getCols(); j++) {
                        middleValues[i][j] = middleValues[i][j] - ((double) deletedFrame[i][j] / blurDivider) + ((double) addedFrame[i][j] / blurDivider);
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

            BmpHelper.writeBmp(outputFolder + "/background_" + currentNumber + ".bmp", currentResult);
            System.out.println("Image " + currentNumber + "/" + images.getImagesList().size() + "  complete");
        }
        return null;
    }

    private void createSumAndContourImages() {

        // сумма всех изображений
        int[][] sumMatrix = new int[images.getRows()][images.getCols()];
        for (int[][] matrix : images.getImagesList()) {
            for (int i = 0; i < images.getRows(); i++) {
                for (int j = 0; j < images.getCols(); j++) {
                    sumMatrix[i][j] += matrix[i][j];
                }
            }
        }

        MatrixHelper.writeMatrix(outputFolder + SUM_FILE_NAME_CLEAR, sumMatrix);

        int[][] sumImage = BmpHelper.transformToImage(sumMatrix);

        BmpHelper.writeBmp(outputFolder + SUM_IMAGE_CLEAR_NAME, sumImage);

        int[][] blurImage = BmpHelper.gaussianBlur(sumImage, 30);
        BmpHelper.writeBmp(outputFolder + SUM_IMAGE_BLUR_NAME, blurImage);

        for (int i = 0; i < images.getRows(); i++) {
            for (int j = 0; j < images.getCols(); j++) {
                sumImage[i][j] = sumImage[i][j] - blurImage[i][j];
            }
        }

        sumImage = BmpHelper.transformToImage(sumImage);

        int sum = 0;
        for (int i = 0; i < images.getRows(); i++) {
            for (int j = 0; j < images.getCols(); j++) {
                sum += sumImage[i][j];
            }
        }

        BmpHelper.writeBmp(outputFolder + SUM_IMAGE_NAME, sumImage);
        MatrixHelper.writeMatrix(outputFolder + SUM_FILE_NAME, sumImage);

        int middleSumImage = (int) (sum / (images.getRows() * images.getCols() * 1.1));
        // формируем контур капилляра
        int[][] contourImage = new int[images.getRows()][images.getCols()];
        for (int i = 0; i < images.getRows(); i++) {
            for (int j = 0; j < images.getCols(); j++) {
                contourImage[i][j] = sumImage[i][j] > middleSumImage ? 0 : 255;
            }
        }
        BmpHelper.writeBmp(outputFolder + CONTOUR_IMAGE_NAME, contourImage);
    }

    public static Images loadInputData(final String inputFolder, final int count) {
        return loadInputData(inputFolder, 0, count);
    }

    // todo: set input pattern (img0_... or img1_...)
    public static Images loadInputData(final String inputFolder, final int start, final int count) {
        final Images result = new Images();
        for (int i = start; i < start + count; i++) {
            int[][] bmp = BmpHelper.readBmp(inputFolder + "img0_00000_" + String.format("%05d", i) + ".bmp");
            result.add(bmp);
        }
        return result;
    }

    public static Images loadOutputData(final String inputFolder, final int count) {
        final Images result = new Images();
        for (int i = 0; i < count; i++) {
            int[][] bmp = BmpHelper.readBmp(inputFolder + "background_" + i + ".bmp");
            result.add(bmp);
        }
        return result;
    }
}

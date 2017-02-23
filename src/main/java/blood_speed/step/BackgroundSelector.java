package blood_speed.step;

import blood_speed.helper.BmpHelper;
import blood_speed.helper.FunctionHelper;
import blood_speed.step.data.Images;

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
        Images resultImages = new Images();
        // вычисляем сумму всех изображений
        int sum = 0;
        int currentNumber = 0;

        for (int[][] matrix : images.getImagesList()) {
            for (int i = 0; i < images.getRows(); i++) {
                for (int j = 0; j < images.getCols(); j++) {
                    sum += matrix[i][j];
                }
            }
        }

        // среднее значения всех изображений
        int middleImagesValue = sum / (images.getRows() * images.getCols() * images.getImagesList().size());

        // вычисляем изображения +  среднее значение всех изображений и сумму всех таких изображений
        int[][] sumMiddleImage = new int[images.getRows()][images.getCols()];
        long sumAllMiddleImages = 0;
        for (int[][] matrix : images.getImagesList()) {
            // изображение + среднее значение всех изображений
            int[][] result = new int[images.getRows()][images.getCols()];
            for (int i = 0; i < images.getRows(); i++) {
                for (int j = 0; j < images.getCols(); j++) {
                    int diff = (int) ((matrix[i][j] + middleImagesValue) * 0.7);
                    if (diff > 255) {
                        diff = 255;
                    }
                    result[i][j] = diff;
                    sumMiddleImage[i][j] += diff;
                    sumAllMiddleImages += diff;
                }
            }
            BmpHelper.writeBmp("data/backgroundSelector/background_" + currentNumber + ".bmp", result);
            currentNumber++;
        }

        // вычисляем среднее значение всех изображений и контур
        long middleValue = (long) (sumAllMiddleImages / (images.getCols() * images.getRows() * images.getImagesList().size()) / 1.2);
        int[][] circuit = new int[images.getRows()][images.getCols()];
        int[][] middleImage = new int[images.getRows()][images.getCols()];
        for (int i = 0; i < images.getRows(); i++) {
            for (int j = 0; j < images.getCols(); j++) {
                middleImage[i][j] = sumMiddleImage[i][j] / images.getImagesList().size();
                if (middleImage[i][j] > middleValue) {
                    circuit[i][j] = 255;
                } else {
                    circuit[i][j] = 0;
                }
            }
        }

        BmpHelper.writeBmp("data/backgroundSelector/00_middleImage.bmp", middleImage);
        BmpHelper.writeBmp("data/backgroundSelector/00_circuit.bmp", circuit);

        for (int i = 0; i < images.getRows(); i++) {
            for (int j = 0; j < images.getCols(); j++) {

            }
        }
        return null;
    }


    public static Images loadData(final String inputFolder) {
        final Images result = new Images();
        for (int i = 0; i < 100; i++) {
            int[][] bmp = BmpHelper.readBmpColors(inputFolder + "img1_00000_" + String.format("%05d", i) + ".bmp");
            result.add(bmp);
        }
        return result;
    }

    public static void main(String[] args) {
        Images images = loadData("data/test2/out2b/");
        BackgroundSelector backgroundSelector = new BackgroundSelector(images, "data/backgroundSelector/");
        backgroundSelector.process();
    }
}

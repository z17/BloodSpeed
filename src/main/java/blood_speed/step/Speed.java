package blood_speed.step;

import blood_speed.helper.BmpHelper;
import blood_speed.helper.FunctionHelper;
import blood_speed.helper.MatrixHelper;
import blood_speed.step.data.Images;
import blood_speed.step.data.SpeedData;

import java.util.List;

public class Speed extends Step<int[][]> {

    private final String outputFolder;
    private final String outputNameClearFile;
    private final String outputNameClearImage;
    private final String outputNameImageFile;
    private final Images images;
    private final int resultCoefficient;

    public Speed(final String outputFolder, String outputNameClearFile, String outputNameClearImage, String outputNameImageFile, final Images images, final int resultCoefficient) {
        this.outputNameClearFile = outputNameClearFile;
        this.outputNameClearImage = outputNameClearImage;
        this.outputNameImageFile = outputNameImageFile;
        FunctionHelper.checkIOFolders(null, outputFolder);
        this.outputFolder = outputFolder;
        this.images = images;
        this.resultCoefficient = resultCoefficient;
    }

    public int[][] process() {
        System.out.println("Started analyzing blur");

        int[][] resultMatrix = new int[images.getRows()][images.getCols()];

        for (int currentRow = 0; currentRow < images.getRows(); currentRow++) {
            for (int currentCol = 0; currentCol < images.getCols(); currentCol++) {
                resultMatrix[currentRow][currentCol] = getMinimum(currentRow, currentCol, images.getImagesList());
            }
        }

        System.out.println("Writing result");
        BmpHelper.writeBmp(outputFolder + outputNameClearImage, resultMatrix);
        MatrixHelper.writeMatrix(outputFolder + outputNameClearFile, resultMatrix);

        int[][] resultMatrixCoef = MatrixHelper.multiplyMatrix(resultMatrix, resultCoefficient);
        BmpHelper.writeBmp(outputFolder + outputNameImageFile, resultMatrixCoef);

        return resultMatrix;
    }

    private int getMinimum(int currentRow, int currentCol, List<SpeedData> imagesList) {
        int minimum = imagesList.get(0).pd[currentRow][currentCol];
        int minimumNumber = 0;

        for (SpeedData current : imagesList) {
            if (current.pd[currentRow][currentCol] < minimum) {
                minimum = current.pd[currentRow][currentCol];
                minimumNumber = current.speed;
            }
        }
        return minimumNumber;
    }

    @SuppressWarnings("unused")
    public static Images loadData(final String dir, final String prefix, final int stepsNumber, final int startStep, final int maxSpeed) {
        System.out.println("Reading blur images");
        Images images = new Images();

        for (int i = startStep; i <= stepsNumber; i++) {

            int currentSpeed = maxSpeed * i / stepsNumber;
            final String name = dir + "/" + prefix + "sm" + "_" + currentSpeed + ".bmp";
            images.addImage(new SpeedData(currentSpeed, BmpHelper.readBmp(name), null));
        }

        return images;
    }
}

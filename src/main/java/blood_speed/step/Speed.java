package blood_speed.step;

import blood_speed.helper.BmpHelper;
import blood_speed.helper.FunctionHelper;
import blood_speed.helper.MatrixHelper;
import blood_speed.step.data.SpeedImages;
import blood_speed.step.data.SpeedData;

import java.util.List;

public class Speed extends Step<double[][]> {

    private final String outputFolder;
    private final String outputNameClearFile;
    private final String outputNameClearImage;
    private final String outputNameImageFile;
    private final SpeedImages images;

    public Speed(final String outputFolder, String outputNameClearFile, String outputNameClearImage, String outputNameImageFile, final SpeedImages images) {
        this.outputNameClearFile = outputNameClearFile;
        this.outputNameClearImage = outputNameClearImage;
        this.outputNameImageFile = outputNameImageFile;
        FunctionHelper.checkIOFolders(null, outputFolder);
        this.outputFolder = outputFolder;
        this.images = images;
    }

    public double[][] process() {
        System.out.println("Started analyzing blur files");

        double[][] resultMatrix = new double[images.getRows()][images.getCols()];

        for (int currentRow = 0; currentRow < images.getRows(); currentRow++) {
            for (int currentCol = 0; currentCol < images.getCols(); currentCol++) {
                resultMatrix[currentRow][currentCol] = getMinimum(currentRow, currentCol, images.getImagesList());
            }
        }

        System.out.println("Writing result");
        BmpHelper.writeBmp(outputFolder + outputNameClearImage, resultMatrix);
        MatrixHelper.writeMatrix(outputFolder + outputNameClearFile, resultMatrix);


        int[][] resultMatrixCoef = BmpHelper.transformToImage(resultMatrix);
        BmpHelper.writeBmp(outputFolder + outputNameImageFile, resultMatrixCoef);

        return resultMatrix;
    }

    private double getMinimum(int currentRow, int currentCol, List<SpeedData> imagesList) {
        int minimum = imagesList.get(0).matrix[currentRow][currentCol];
        double minimumNumber = 0;

        for (SpeedData current : imagesList) {
            if (current.matrix[currentRow][currentCol] < minimum) {
                minimum = current.matrix[currentRow][currentCol];
                minimumNumber = current.speed;
            }
        }
        return minimumNumber;
    }

    @SuppressWarnings("unused")
    public static SpeedImages loadData(final String dir, final String prefix, final int stepsNumber, final int startStep, final int maxSpeed) {
        System.out.println("Reading blur images");
        SpeedImages images = new SpeedImages();

        for (int i = startStep; i <= stepsNumber; i++) {

            int currentSpeed = maxSpeed * i / stepsNumber;
            final String name = dir + "/" + prefix + "sm" + "_" + currentSpeed + ".bmp";
            images.add(new SpeedData(currentSpeed, BmpHelper.readBmp(name), null));
        }

        return images;
    }
}

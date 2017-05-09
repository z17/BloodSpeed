package blood_speed.step.speed;

import blood_speed.helper.BmpHelper;
import blood_speed.helper.FunctionHelper;
import blood_speed.helper.MatrixHelper;
import blood_speed.step.Step;
import blood_speed.step.data.SpeedImages;
import blood_speed.step.data.SpeedData;

import java.util.ArrayList;
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

    private double getMinimum(int currentRow, int currentCol, final List<SpeedData> imagesList) {
//        вывод значений для одной точки
//        currentCol = 106;
//        currentRow = 624;
//        List<Integer> array = new ArrayList<>(imagesList.size());

        int minimum = imagesList.get(0).matrix[currentRow][currentCol];
        double minimumNumber = 0;

        for (SpeedData current : imagesList) {
            if (current.matrix[currentRow][currentCol] < minimum) {
                minimum = current.matrix[currentRow][currentCol];
                minimumNumber = current.speed;
            }
//            array.add(current.matrix[currentRow][currentCol]);
        }

//        MatrixHelper.writeMatrix("desync.txt", array);
//        System.exit(1);
        return minimumNumber;
    }

    @SuppressWarnings("unused")
    public static SpeedImages loadData(final String dir, final String prefix, final int stepsNumber, final int startStep, final int maxSpeed) {
        System.out.println("Reading blur images");
        SpeedImages images = new SpeedImages();
        double step = ((double)maxSpeed - startStep) / stepsNumber;
        for (double currentSpeed = startStep; currentSpeed < maxSpeed; currentSpeed = currentSpeed + step) {
            final String name = dir + "/" + prefix + "sm" + "_" + currentSpeed + ".txt";
            images.add(new SpeedData(currentSpeed, MatrixHelper.readMatrix(name), null));
        }

        return images;
    }
}

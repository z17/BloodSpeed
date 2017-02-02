package blood_speed.step;

import blood_speed.helper.BmpHelper;
import blood_speed.helper.FunctionHelper;
import blood_speed.helper.MatrixHelper;
import blood_speed.step.data.Images;

import java.util.List;

public class Speed extends Step<int[][]>{

    private final String outputName;
    private final Images images;
    private final int resultCoefficient;

    public Speed(final String outputFolder, final Images images, final int resultCoefficient) {
        FunctionHelper.checkIOFolders(null, outputFolder);
        this.outputName = outputFolder;
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
        BmpHelper.writeBmp(outputName + "/result-clear.bmp", resultMatrix);
        MatrixHelper.writeMatrix(outputName + "/result-clear.txt", resultMatrix);

        int[][] resultMatrixCoef = MatrixHelper.multiplyMatrix(resultMatrix, resultCoefficient);
        BmpHelper.writeBmp(outputName + "/result.bmp", resultMatrixCoef);
        MatrixHelper.writeMatrix(outputName + "/result.txt", resultMatrixCoef);

        return resultMatrix;
    }

    private int getMinimum(int currentRow, int currentCol, List<int[][]> imagesList) {
        int minimum = imagesList.get(0)[currentRow][currentCol];
        int minimumNumber = 0;

        for (int i = 0; i < imagesList.size(); i++ ) {
            int[][] current = imagesList.get(i);
            if (current[currentRow][currentCol] < minimum) {
                minimum = current[currentRow][currentCol];
                minimumNumber = i;
            }
        }
        return minimumNumber;
    }

    public static Images loadBlurImages(final String dir, final String prefix, final int ndv, final int minNdv) {
        System.out.println("Reading blur images");
        Images images = new Images();

        for (int i = minNdv; i <= ndv; i++) {
            final String name = dir + "/" + prefix + ndv + "_" + (ndv + i) + ".bmp";
            images.addImage(BmpHelper.readBmp(name));
        }

        return images;
    }
}

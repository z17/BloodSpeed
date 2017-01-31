package blood_speed.step;

import blood_speed.helper.BmpHelper;
import blood_speed.helper.FunctionHelper;
import blood_speed.helper.MatrixHelper;

import java.util.ArrayList;
import java.util.List;

public class Speed {

    private final String outputName;
    private final Images images;

    public Speed(final String inputFolder, final String outputFolder, final int ndv, final int minNdv, final String prefix) {
        FunctionHelper.checkIOFolders(inputFolder, outputFolder);
        this.outputName = outputFolder;
        images = loadBlurImages(inputFolder, prefix + "sm", ndv, minNdv);
    }

    public Speed(final String outputFolder, final List<int[][]> images) {
        FunctionHelper.checkIOFolders(null, outputFolder);
        this.outputName = outputFolder;
        this.images = new Images(images);
    }

    public void check(final int resultCoefficient) {
        System.out.println("Started analyzing blur");

        int[][] resultMatrix = new int[images.getRows()][images.getCols()];

        for (int currentRow = 0; currentRow < images.getRows(); currentRow++) {
            for (int currentCol = 0; currentCol < images.getCols(); currentCol++) {
                resultMatrix[currentRow][currentCol] = getMinimum(currentRow, currentCol, images.getImagesList());
            }
        }

        MatrixHelper.multiplyMatrix(resultMatrix, resultCoefficient);
        System.out.println("Writing result");
        BmpHelper.writeBmp(outputName + "/result.bmp", resultMatrix);
        MatrixHelper.writeMatrix(outputName + "/result.txt", resultMatrix);
    }

    private int getMinimum(int currentRow, int currentCol, List<int[][]> imagesList) {
        int minimum = imagesList.get(0)[currentRow][currentCol];

        for (int[][] current : imagesList) {
            if (current[currentRow][currentCol] < minimum) {
                minimum = current[currentRow][currentCol];
            }
        }
        return minimum;
    }

    private Images loadBlurImages(final String dir, final String prefix, final int ndv, final int minNdv) {
        System.out.println("Reading blur images");
        Images images = new Images();

        for (int i = minNdv; i <= ndv; i++) {
            final String name = dir + "/" + prefix + ndv + "_" + (ndv + i) + ".bmp";
            images.addImage(BmpHelper.readBmp(name));
        }

        return images;
    }

    private static class Images {
        private List<int[][]> imagesList;
        private int cols;
        private int rows;

        Images(List<int[][]> images) {
            this.imagesList = images;
            this.cols = FunctionHelper.cols(images.get(0));
            this.rows = FunctionHelper.rows(images.get(0));
        }

        Images() {
            imagesList = new ArrayList<>();
            cols = 0;
            rows = 0;
        }

        void addImage(final int[][] image) {
            int cols = FunctionHelper.cols(image);
            int rows = FunctionHelper.rows(image);

            if (this.cols == 0 && this.rows == 0) {
                this.cols = cols;
                this.rows = rows;
            }

            if (this.cols != cols || this.rows != rows) {
                throw new RuntimeException("Different matrix size");
            }

            imagesList.add(image);
        }

        List<int[][]> getImagesList() {
            return imagesList;
        }

        int getCols() {
            return cols;
        }

        int getRows() {
            return rows;
        }
    }
}
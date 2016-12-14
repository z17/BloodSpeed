import helper.BmpHelper;
import helper.FunctionHelper;
import helper.MatrixHelper;

import java.util.ArrayList;
import java.util.List;

class Speed {

    private final String inputDir;
    private final String outputName;

    Speed(final String inputDir, final String outputFolder) {
        this.inputDir = inputDir;
        this.outputName = outputFolder;
    }

    void check(final int ndv, final int minNdv) {
        Images images = loadBlurImages(inputDir, "shift1_sm", ndv, minNdv);

        int[][] resultMatrix = new int[images.getRows()][images.getCols()];

        for (int currentRow = 0; currentRow < images.getRows(); currentRow++) {
            for (int currentCol = 0; currentCol < images.getCols(); currentCol++) {
                resultMatrix[currentRow][currentCol] = getMinimum(currentRow, currentCol, images.getImagesList());
            }
        }

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

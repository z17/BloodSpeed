package blood_speed.step.data;

import blood_speed.helper.FunctionHelper;

import java.util.ArrayList;
import java.util.List;

public class Images {
    private int cols;
    private List<int[][]> imagesList;
    private int rows;

    public Images() {
        imagesList = new ArrayList<>();
        cols = 0;
        rows = 0;
    }

    public void add(final int[][] data) {
        int cols = FunctionHelper.cols(data);
        int rows = FunctionHelper.rows(data);

        if (this.cols == 0 && this.rows == 0) {
            this.cols = cols;
            this.rows = rows;
        }

        if (this.cols != cols || this.rows != rows) {
            throw new RuntimeException("Different matrix size");
        }

        imagesList.add(data);
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    public List<int[][]> getImagesList() {
        return imagesList;
    }
}
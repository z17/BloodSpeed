package blood_speed.step.data;

import blood_speed.helper.FunctionHelper;

import java.util.ArrayList;
import java.util.List;

public class Images {
    private List<int[][]> imagesList;
    private int cols;
    private int rows;

    public Images(List<int[][]> images) {
        this.imagesList = images;
        this.cols = FunctionHelper.cols(images.get(0));
        this.rows = FunctionHelper.rows(images.get(0));
    }

    public Images() {
        imagesList = new ArrayList<>();
        cols = 0;
        rows = 0;
    }

    public void addImage(final int[][] image) {
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

    public List<int[][]> getImagesList() {
        return imagesList;
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }
}
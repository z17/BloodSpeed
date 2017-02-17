package blood_speed.step.data;

import blood_speed.helper.FunctionHelper;

import java.util.ArrayList;
import java.util.List;

public class Images {
    private List<SpeedData> imagesList;
    private int cols;
    private int rows;

    public Images(List<SpeedData> images) {
        this.imagesList = images;
        this.cols = FunctionHelper.cols(images.get(0).pd);
        this.rows = FunctionHelper.rows(images.get(0).pd);
    }

    public Images() {
        imagesList = new ArrayList<>();
        cols = 0;
        rows = 0;
    }

    public void addImage(final SpeedData data) {
        int cols = FunctionHelper.cols(data.pd);
        int rows = FunctionHelper.rows(data.pd);

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

    public List<SpeedData> getImagesList() {
        return imagesList;
    }
}
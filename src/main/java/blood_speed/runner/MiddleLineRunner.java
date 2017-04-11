package blood_speed.runner;

import blood_speed.helper.BmpHelper;
import blood_speed.helper.MatrixHelper;
import blood_speed.step.BackgroundSelector;
import blood_speed.step.MiddleLineSelector;
import blood_speed.step.data.Images;
import blood_speed.step.data.Point;

import java.util.Properties;

class MiddleLineRunner implements AbstractRunner {
    @Override
    public void run(Properties properties) {
        String inputFolder = properties.getProperty("middle_input_folder");
        int count = Integer.valueOf(properties.getProperty("middle_count"));
        String outputFolder = properties.getProperty("middle_output_folder");
        String contourName = properties.getProperty("middle_contour");
        String sumMatrixName = properties.getProperty("middle_sum_matrix");
        String sumImageName = properties.getProperty("middle_sum_image");


        int x = Integer.valueOf(properties.getProperty("middle_point_x"));
        int y = Integer.valueOf(properties.getProperty("middle_point_y"));
        int regionSize = Integer.valueOf(properties.getProperty("middle_region_size"));
        int maxSpeed = Integer.valueOf(properties.getProperty("middle_max_speed"));
        int angleLimit = Integer.valueOf(properties.getProperty("middle_angle_limit"));
        int maxCentralPoints = Integer.valueOf(properties.getProperty("middle_max_central_points"));

        run(inputFolder,
                outputFolder,
                "",
                count,
                contourName,
                sumMatrixName,
                sumImageName,
                new Point(x, y),
                regionSize,
                maxSpeed,
                angleLimit,
                maxCentralPoints
        );
    }

    public static void main(String[] args) {
        new MiddleLineRunner().run(
                "data/tests/capillary_dec94_pasha4_cap1/backgroundSelector/",
                "data/tests/capillary_dec94_pasha4_cap1/middle-line/",
                "v1",
                300,
                "data/tests/capillary_dec94_pasha4_cap1/backgroundSelector/contour-image-photoshop.bmp",
                "data/tests/capillary_dec94_pasha4_cap1/backgroundSelector/sum.txt",
                "data/tests/capillary_dec94_pasha4_cap1/backgroundSelector/sum-image.bmp",
                new Point(25, 152),
                4,
                15,
                30,
                21
        );
    }

    private void run(String inputFolder,
                     String outputFolder,
                     String outputPrefix,
                     int count,
                     String contourName,
                     String sumMatrixName,
                     String sumImageName,
                     Point startPoint,
                     int regionSize,
                     int maxSpeed,
                     int angleLimit,
                     int maxCentralPoints) {
        Images images = BackgroundSelector.loadOutputData(inputFolder, count);
        int[][] contour = BmpHelper.readBmp(contourName);

        // изображение суммы
        int[][] sumMatrix = MatrixHelper.readMatrix(sumMatrixName);
        int[][] sumImage = BmpHelper.readBmp(sumImageName);

        MiddleLineSelector selector = new MiddleLineSelector(
                startPoint,
                images,
                contour,
                sumMatrix,
                sumImage,
                outputFolder,
                outputPrefix,
                regionSize,
                maxSpeed,
                angleLimit,
                maxCentralPoints);
        selector.process();
    }
}

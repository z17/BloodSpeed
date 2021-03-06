package blood_speed.runner;

import blood_speed.Main;
import blood_speed.helper.BmpHelper;
import blood_speed.helper.MatrixHelper;
import blood_speed.step.BackgroundSelector;
import blood_speed.step.MiddleLineSelector;
import blood_speed.step.data.Images;
import blood_speed.step.data.Point;

import java.util.Properties;

final class MiddleLineRunner extends AbstractRunner {
    @Override
    public void runMethod(Properties properties) {
        String inputFolder = properties.getProperty("middle_input_folder");
        int count = Integer.valueOf(properties.getProperty("middle_count"));
        String outputFolder = properties.getProperty("middle_output_folder");
        String outputPointsName = properties.getProperty("middle_output_file_name");
        String contourName = properties.getProperty("middle_contour");
        String sumMatrixName = properties.getProperty("middle_sum_matrix");
        String sumImageName = properties.getProperty("middle_sum_image");


        int x = Integer.valueOf(properties.getProperty("middle_point_x"));
        int y = Integer.valueOf(properties.getProperty("middle_point_y"));
        int regionSize = Integer.valueOf(properties.getProperty("middle_region_size"));
        int maxSpeed = Integer.valueOf(properties.getProperty("middle_max_speed"));
        int angleLimit = Integer.valueOf(properties.getProperty("middle_angle_limit"));
        int vectorBlurRadius = Integer.valueOf(properties.getProperty("middle_vector_blur_radius"));
        boolean fast = Boolean.valueOf(properties.getProperty("middle_fast"));

        run(inputFolder,
                outputFolder,
                "",
                outputPointsName,
                count,
                contourName,
                sumMatrixName,
                sumImageName,
                new Point(x, y),
                regionSize,
                maxSpeed,
                angleLimit,
                vectorBlurRadius,
                fast
        );
    }

    public static void main(String... args) {
        new MiddleLineRunner().run(Main.getSettings());
    }

    private void run(String inputFolder,
                     String outputFolder,
                     String outputPrefix,
                     String outputPointsName,
                     int count,
                     String contourName,
                     String sumMatrixName,
                     String sumImageName,
                     Point startPoint,
                     int regionSize,
                     int maxSpeed,
                     int angleLimit,
                     int vectorBlurRadius,
                     boolean fast) {
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
                outputPointsName,
                regionSize,
                maxSpeed,
                angleLimit,
                vectorBlurRadius,
                fast);
        selector.process();
    }
}

package blood_speed.runner;

import blood_speed.helper.BmpHelper;
import blood_speed.helper.FunctionHelper;
import blood_speed.step.BackgroundSelector;
import blood_speed.step.Step;
import blood_speed.step.Transformer;
import blood_speed.step.data.Images;
import blood_speed.step.data.Point;

import java.util.List;
import java.util.Properties;

class TransformRunner implements AbstractRunner {
    @Override
    public void run(Properties properties) {
        String centralPointsFile = properties.getProperty("transformer_central_points");
        String inputFolder = properties.getProperty("transformer_input_folder");
        String outputFolder = properties.getProperty("transformer_output_folder");
        int count = Integer.valueOf(properties.getProperty("transformer_count"));
        String contourFile = properties.getProperty("transformer_contour");
        String sumImageFile = properties.getProperty("transformer_sum_image");
        int perpendicularStep = Integer.valueOf(properties.getProperty("transformer_perpendicular_step"));
        int indent = Integer.valueOf(properties.getProperty("transformer_indent"));
        int oneStepSize = Integer.valueOf(properties.getProperty("transformer_step_size"));
        int stepsCount = Integer.valueOf(properties.getProperty("transformer_steps_count"));
        boolean truncateByContour = Boolean.valueOf(properties.getProperty("transformer_truncate_by_contour"));

        run(centralPointsFile,
                inputFolder,
                outputFolder,
                "",
                count,
                sumImageFile,
                contourFile,
                perpendicularStep,
                indent,
                oneStepSize,
                stepsCount,
                truncateByContour);
    }

    public static void main(String[] args) {
        new TransformRunner().run(
                "data/tests/capillary_2k_m2/my/middle-line/middle-full-points.txt",
                "data/tests/capillary_2k_m2/my/backgroundSelector/",
                "data/tests/capillary_2k_m2/my/transformedImages",
                "result_",
                2000,
                "data/tests/capillary_2k_m2/my/backgroundSelector/sum-image.bmp",
                "data/tests/capillary_2k_m2/my/res3_gr2.bmp",
                6,
                4,
                1,
                10,
                true
        );
    }

    private void run(String centralPointsFile,
                     String inputFolder,
                     String outputFolder,
                     String outputPrefix,
                     int count,
                     String sumImageFile,
                     String contourFile,
                     int perpendicularStep,
                     int indent,
                     int oneStepSize,
                     int stepsCount,
                     boolean truncateByContour
    ) {
        List<Point> middleLine = FunctionHelper.readPointsList(centralPointsFile);
        Images data = BackgroundSelector.loadOutputData(inputFolder, count);
        int[][] sum = BmpHelper.readBmp(sumImageFile);
        int[][] contour = BmpHelper.readBmp(contourFile);
        Step<Void> step = new Transformer(
                middleLine,
                data,
                sum,
                contour,
                perpendicularStep,
                outputFolder,
                outputPrefix,
                indent,
                oneStepSize,
                stepsCount,
                truncateByContour);
        step.process();
    }
}

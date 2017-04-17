package blood_speed.runner;

import blood_speed.helper.BmpHelper;
import blood_speed.helper.FunctionHelper;
import blood_speed.step.BackgroundSelector;
import blood_speed.step.MiddleLineSelector;
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
                stepsCount);
    }

    public static void main(String[] args) {
        new TransformRunner().run(
                "data/tests/kris_2017_03_06_kr1_4/middle-line/v1_middle-full-points.txt",
                "data/tests/kris_2017_03_06_kr1_4/backgroundSelector/",
                "data/tests/kris_2017_03_06_kr1_4/transformedImages",
                "result_",
                700,
                "data/tests/kris_2017_03_06_kr1_4/backgroundSelector/sum-image.bmp",
                "data/tests/kris_2017_03_06_kr1_4/backgroundSelector/contour-image-photoshop.bmp",
                7,
                4,
                1,
                23
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
                     int stepsCount
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
                stepsCount);
        step.process();
    }
}

package blood_speed.runner;

import blood_speed.Main;
import blood_speed.step.BackgroundSelector;
import blood_speed.step.data.Images;

import java.util.Properties;

final class BackgroundSelectorRunner extends AbstractRunner {
    @Override
    protected void runMethod(Properties properties) {
        String inputFolder = properties.getProperty("background_input_folder");
        int count = Integer.valueOf(properties.getProperty("background_count"));
        String outputFolder = properties.getProperty("background_output_folder");
        int blurDepth = Integer.valueOf(properties.getProperty("background_blur_depth"));

        run(inputFolder, outputFolder, count, blurDepth);
    }

    private void run(String inputFolder, String outputFolder, int count, int blurDepth) {
        Images images = BackgroundSelector.loadInputData(inputFolder, count);
        BackgroundSelector backgroundSelector = new BackgroundSelector(images, outputFolder, blurDepth);
        backgroundSelector.process();
    }

    public static void main(String... args) {
        new BackgroundSelectorRunner().run(Main.getSettings());
    }
}

package blood_speed;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Main {
    private final static int DIGITS_IN_FILES_NAME = 5;
    private final static String SETTINGS_FILE = "settings.ini";

    public static void main(final String[] args) {
        final Properties properties = getSettings();
        int stepsNumber = Integer.valueOf(properties.getProperty("stepsNumber"));
        int startStep = Integer.valueOf(properties.getProperty("startStep"));
        int framesNumber = Integer.valueOf(properties.getProperty("framesNumber"));              // количество кадров
        int maxSpeed = Integer.valueOf(properties.getProperty("maxSpeed"));
        int r = Integer.valueOf(properties.getProperty("r"));             // радиус области
        int dr = Integer.valueOf(properties.getProperty("dr"));
        int dt = Integer.valueOf(properties.getProperty("dt"));            // скорее всего это step сравнения (сравниваем n кадра и n + dt)
        String prefix = properties.getProperty("prefix");                   // префикс имени входных файлов

        // радиусы и прочее для блюра
        int s1dn1 = Integer.valueOf(properties.getProperty("s1dn1"));
        int s1dn2 = Integer.valueOf(properties.getProperty("s1dn2"));
        int s1dn1st = Integer.valueOf(properties.getProperty("s1dn1st"));
        int s1dn2st = Integer.valueOf(properties.getProperty("s1dn2st"));
        int s2dn1 = Integer.valueOf(properties.getProperty("s2dn1"));
        int s2dn2 = Integer.valueOf(properties.getProperty("s2dn2"));

        int resultCoefficient = Integer.valueOf(properties.getProperty("result_coefficient"));

        // папки
        String folderInput = properties.getProperty("input_folder");
        String circuitImage = properties.getProperty("circuit_image");
        String step1FolderOutput = properties.getProperty("correlation_folder");
        String step2FolderOutput = properties.getProperty("blur_folder");
        String step3FolderOutput = properties.getProperty("result_folder");


        StepRunner.StepData data = new StepRunner.StepData();
        data
                .setFilePrefix(prefix)

                .setStep1InputFolder(folderInput)
                .setStep1OutputFolder(step1FolderOutput)
                .setNumberOfDigitsInStep1FileNames(DIGITS_IN_FILES_NAME)
                .setCircuitImageName(circuitImage)
                .setMaxSpeed(maxSpeed)
                .setStepsNumber(stepsNumber)
                .setStartStep(startStep)
                .setFramesNumber(framesNumber)
                .setR(r)
                .setDr(dr)
                .setDt(dt)

                .setBlurStepOutputFolder(step2FolderOutput)
                .setS1dn1(s1dn1)
                .setS1dn2(s1dn2)
                .setS1dn1st(s1dn1st)
                .setS1dn2st(s1dn2st)
                .setS2dn1(s2dn1)
                .setS2dn2(s2dn2)

                .setStep3OutputFolder(step3FolderOutput)
                .setResultCoefficient(resultCoefficient)

                .setMiddleStepOutputFile("data/result/middle-speed2.txt")
                .setAffectedCols(309);

        StepRunner runner = new StepRunner();
        runner.run(data);
    }

    private static Properties getSettings() {
        final Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(SETTINGS_FILE));
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

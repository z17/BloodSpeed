package blood_speed;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Main {
    private final static int DIGITS_IN_FILES_NAME = 5;
    private final static String SETTINGS_FILE = "settings.ini";

    public static void main(final String[] args) {
        final Properties properties = getSettings();
        int middleSpeed = stage1(properties);
        stage2(properties, middleSpeed);
    }

    private static int stage1(Properties properties) {
        int stepsNumber = Integer.valueOf(properties.getProperty("stepsNumber"));
        int startStep = Integer.valueOf(properties.getProperty("startStep"));
        int framesNumber = Integer.valueOf(properties.getProperty("framesNumber"));             // количество кадров
        int maxSpeed = Integer.valueOf(properties.getProperty("maxSpeed"));
        int r = Integer.valueOf(properties.getProperty("r"));                                   // радиус области
        int dr = Integer.valueOf(properties.getProperty("dr"));
        int dt = Integer.valueOf(properties.getProperty("dt"));                                 // скорее всего это step сравнения (сравниваем n кадра и n + dt)
        String prefix = properties.getProperty("prefix");                                       // префикс имени входных файлов

        // радиусы и прочее для блюра
        int s1dn1 = Integer.valueOf(properties.getProperty("s1dn1"));
        int s1dn2 = Integer.valueOf(properties.getProperty("s1dn2"));
        int s1dn1st = Integer.valueOf(properties.getProperty("s1dn1st"));
        int s1dn2st = Integer.valueOf(properties.getProperty("s1dn2st"));
        int s2dn1 = Integer.valueOf(properties.getProperty("s2dn1"));
        int s2dn2 = Integer.valueOf(properties.getProperty("s2dn2"));

        int resultCoefficient = Integer.valueOf(properties.getProperty("result_coefficient"));
        String speedOutputImage = properties.getProperty("speed_output_image");
        String speedOutputFile = properties.getProperty("speed_output_file");
        String speedOutputImageWithCoufficient = properties.getProperty("speed_output_image_with_coufficient");

        int middleStepAffectedCols = Integer.valueOf(properties.getProperty("affected_cols"));
        String middleStepOutputFile = properties.getProperty("middle_output_file");

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
                .setStep3OutputNameClearFile(speedOutputFile)
                .setStep3OutputNameClearImage(speedOutputImage)
                .setStep3OoutputNameImageFile(speedOutputImageWithCoufficient)

                .setMiddleStepOutputFile(middleStepOutputFile)
                .setAffectedCols(middleStepAffectedCols);

        StepRunner runner = new StepRunner();
        return runner.run(data);
    }


    private static void stage2(Properties properties, int middleSpeed) {
        int speedDeviation = Integer.valueOf(properties.getProperty("stage2_speed_deviation"));
        int stepSize= Integer.valueOf(properties.getProperty("stage2_step_size"));
        int framesNumber = Integer.valueOf(properties.getProperty("framesNumber"));
        int r = Integer.valueOf(properties.getProperty("stage2_r"));
        int dr = Integer.valueOf(properties.getProperty("stage2_dr"));
        int dt = Integer.valueOf(properties.getProperty("stage2_dt"));
        String prefix = properties.getProperty("prefix");

        // радиусы и прочее для блюра
        int s1dn1 = Integer.valueOf(properties.getProperty("stage2_s1dn1"));
        int s1dn2 = Integer.valueOf(properties.getProperty("stage2_s1dn2"));
        int s1dn1st = Integer.valueOf(properties.getProperty("stage2_s1dn1st"));
        int s1dn2st = Integer.valueOf(properties.getProperty("stage2_s1dn2st"));
        int s2dn1 = Integer.valueOf(properties.getProperty("stage2_s2dn1"));
        int s2dn2 = Integer.valueOf(properties.getProperty("stage2_s2dn2"));

        int resultCoefficient = Integer.valueOf(properties.getProperty("result_coefficient"));
        String speedOutputImage = properties.getProperty("stage2_speed_output_image");
        String speedOutputFile = properties.getProperty("stage2_speed_output_file");
        String speedOutputImageWithCoufficient = properties.getProperty("stage2_speed_output_image_with_coufficient");

        int middleStepAffectedCols = Integer.valueOf(properties.getProperty("affected_cols"));
        String middleStepOutputFile = properties.getProperty("stage2_middle_output_file");

        // папки
        String folderInput = properties.getProperty("input_folder");
        String circuitImage = properties.getProperty("circuit_image");
        String step1FolderOutput = properties.getProperty("stage2_correlation_folder");
        String step2FolderOutput = properties.getProperty("stage2_blur_folder");
        String step3FolderOutput = properties.getProperty("stage2_result_folder");

        int startStep = middleSpeed - speedDeviation;
        int maxSpeed = middleSpeed + speedDeviation;
        int stepsNumber = (maxSpeed - startStep) / stepSize;

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
                .setStep3OutputNameClearFile(speedOutputFile)
                .setStep3OutputNameClearImage(speedOutputImage)
                .setStep3OoutputNameImageFile(speedOutputImageWithCoufficient)

                .setMiddleStepOutputFile(middleStepOutputFile)
                .setAffectedCols(middleStepAffectedCols);

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

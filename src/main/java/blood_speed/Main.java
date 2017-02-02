package blood_speed;

import blood_speed.step.AcPdfFst;
import blood_speed.step.Blur;
import blood_speed.step.Speed;
import blood_speed.step.data.Images;
import blood_speed.step.data.Step1Result;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Main {
    public static final double[] G1 = initG1();
    private final static int DIGITS_IN_FILES_NAME = 5;

    public static void main(final String[] args) {
        final Properties properties = getSettings();
        int stepsNumber =  Integer.valueOf(properties.getProperty("stepsNumber"));
        int startStep =  Integer.valueOf(properties.getProperty("startStep"));
        int framesNumber =  Integer.valueOf(properties.getProperty("framesNumber"));              // количество кадров
        int maxSpeed =  Integer.valueOf(properties.getProperty("maxSpeed"));
        int r =  Integer.valueOf(properties.getProperty("r"));             // радиус области
        int dr =  Integer.valueOf(properties.getProperty("dr"));
        int dt =  Integer.valueOf(properties.getProperty("dt"));            // скорее всего это step сравнения (сравниваем n кадра и n + dt)
        String prefix = properties.getProperty("prefix");                   // префикс имени входных файлов

        // радиусы и прочее для блюра
        int s1dn1 =  Integer.valueOf(properties.getProperty("s1dn1"));
        int s1dn2 =  Integer.valueOf(properties.getProperty("s1dn2"));
        int s1dn1st =  Integer.valueOf(properties.getProperty("s1dn1st"));
        int s1dn2st =  Integer.valueOf(properties.getProperty("s1dn2st"));
        int s2dn1 =  Integer.valueOf(properties.getProperty("s2dn1"));
        int s2dn2 =  Integer.valueOf(properties.getProperty("s2dn2"));

        int resultCoefficient =  Integer.valueOf(properties.getProperty("result_coefficient"));

        // папки
        String folderInput = properties.getProperty("input_folder");
        String circuitImage = properties.getProperty("circuit_image");
        String step1FolderOutput = properties.getProperty("correlation_folder");
        String step2FolderOutput = properties.getProperty("blur_folder");
        String step3FolderOutput = properties.getProperty("result_folder");

        AcPdfFst step1 = new AcPdfFst(
                DIGITS_IN_FILES_NAME,
                folderInput,
                step1FolderOutput,
                circuitImage,
                prefix,
                maxSpeed,
                stepsNumber,
                startStep,
                framesNumber,
                r,
                dr,
                dt
        );

        Step1Result step1Result = step1.process();

        // чтобы запустить чтение с диска
//        AcPdfFst.Step1Result step1Result = Blur.readData(prefix, step1FolderOutput, minNdv1, ndv);
        Blur step2 = new Blur(
                step1Result,
                step2FolderOutput,
                prefix,
                s1dn1,
                s1dn2,
                s1dn1st,
                s1dn2st,
                s2dn1,
                s2dn2
        );
        Images blurImages = step2.process();

        // Чтобы запустить с чтением с диска
//        Speed.Images images = Speed.loadBlurImages(step2FolderOutput, prefix + "sm", ndv, minNdv1);
//        Speed speed = new Speed( step3FolderOutput, images);

        Speed step3 = new Speed(step3FolderOutput, blurImages, resultCoefficient);

        step3.process();
    }

    private static double[] initG1() {
        double[] g = new double[101];
        for (int i = 0; i <= 100; i++) {
            g[i] = 0.125 * Math.pow((1 + Math.cos(Math.PI * i / 100)), 0.75);
        }
        return g;
    }

    private static Properties getSettings() {
        final Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("settings.ini"));
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

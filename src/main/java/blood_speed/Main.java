package blood_speed;

import blood_speed.step.AcPdfFst;
import blood_speed.step.Blur;
import blood_speed.step.Speed;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class Main {
    public static final double[] G1 = initG1();

    public static void main(final String[] args) {
        final Properties properties = getSettings();
        int ndv =  Integer.valueOf(properties.getProperty("ndv"));
        int minNdv1 =  Integer.valueOf(properties.getProperty("minNdv1"));
        int N =  Integer.valueOf(properties.getProperty("N"));
        int dNum =  Integer.valueOf(properties.getProperty("dNum"));
        int dv =  Integer.valueOf(properties.getProperty("dv"));
        int r =  Integer.valueOf(properties.getProperty("r"));
        int dr =  Integer.valueOf(properties.getProperty("dr"));
        int dt =  Integer.valueOf(properties.getProperty("dt"));
        String prefix = properties.getProperty("prefix");
        int s1dn1 =  Integer.valueOf(properties.getProperty("s1dn1"));
        int s1dn2 =  Integer.valueOf(properties.getProperty("s1dn2"));
        int s1dn1st =  Integer.valueOf(properties.getProperty("s1dn1st"));
        int s1dn2st =  Integer.valueOf(properties.getProperty("s1dn2st"));
        int s2dn1 =  Integer.valueOf(properties.getProperty("s2dn1"));
        int s2dn2 =  Integer.valueOf(properties.getProperty("s2dn2"));

        int resultCoefficient =  Integer.valueOf(properties.getProperty("result_coefficient"));

        String folderInput = properties.getProperty("input_folder");
        String circuitImage = properties.getProperty("circuit_image");
        String step1FolderOutput = properties.getProperty("correlation_folder");
        String step2FolderOutput = properties.getProperty("blur_folder");
        String step3FolderOutput = properties.getProperty("result_folder");

        AcPdfFst step1 = new AcPdfFst(5, folderInput, step1FolderOutput, circuitImage);

        AcPdfFst.Step1Result step1Result = step1.getV7_ac_pdf_fst(
                prefix,
                dv,
                ndv,
                minNdv1,
                N,
                dNum,
                r,
                dr,
                dt
        );

        // чтобы запустить с чтеним с диска
         //Blur blur = new Blur(step1FolderOutput, step2FolderOutput, prefix, ndv, minNdv1);
        Blur step2 = new Blur(step1Result, step2FolderOutput, prefix, ndv, minNdv1);
        List<int[][]> blurImages = step2.getV6_ac_pd_2dblurf(
                N,
                dNum,
                s1dn1,
                s1dn2,
                s1dn1st,
                s1dn2st,
                s2dn1,
                s2dn2
        );

        // Чтобы запустить с чтением с диска
        // Speed speed = new Speed(step2FolderOutput, step3FolderOutput, ndv, minNdv1, prefix);
        Speed step3 = new Speed(step3FolderOutput, blurImages);
        step3.check(resultCoefficient);
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

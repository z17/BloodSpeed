package blood_speed;

import blood_speed.step.AcPdfFst;
import blood_speed.step.Blur;
import blood_speed.step.Speed;

import java.util.List;

public class Main {
    public static final double[] G1 = initG1();


    public static void main(final String[] args) {
        AcPdfFst acPdfFst = new AcPdfFst(5, "data/input", "data/step1_output", "data/res3_cr2.bmp");

        int ndv = 10;
        int minNdv1 = 0;
        int N = 40000;
        int dNum = 153;
        int dv = 20;
        int r = 7;
        int dr = 1;
        int dt = 6;
        String prefix = "shift1_";

        AcPdfFst.Step1Result step1Result = acPdfFst.getV7_ac_pdf_fst(
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

        final int s1dn1 = 70;
        final int s1dn2 = 16;
        final int s1dn1st = 10;
        final int s1dn2st = 4;
        final int s2dn1 = 5;
        final int s2dn2 = 4;

        // чтобы запустить с чтеним с диска
         //Blur blur = new Blur("data/step1_output", "data/blur_output", prefix, ndv, minNdv1);
        Blur blur = new Blur(step1Result, "data/blur_output", prefix, ndv, minNdv1);
        List<int[][]> blurImages = blur.getV6_ac_pd_2dblurf(
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
        // Speed speed = new Speed("data/blur_output", "data/result", ndv, minNdv1);
        Speed speed = new Speed("data/result", blurImages);
        speed.check();
    }

    private static double[] initG1() {
        double[] g = new double[101];
        for (int i = 0; i <= 100; i++) {
            g[i] = 0.125 * Math.pow((1 + Math.cos(Math.PI * i / 100)), 0.75);
        }
        return g;
    }
}

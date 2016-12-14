import java.util.List;

public class Main {
    static final double[] g1 = initG1();


    public static void main(final String[] args) {
        AcPdfFst acPdfFst = new AcPdfFst(5, "data/input", "data/step1_output", "data/res3_cr2.bmp");

        int ndv = 10;
        int minNdv1 = 0;
        int N = 1000;
        int dNum = 153;

        acPdfFst.getV7_ac_pdf_fst(
                "shift1_",
                20,
                ndv,
                minNdv1,
                N,
                dNum,
                7,
                1,
                6
        );

        Blur blur = new Blur("data/step1_output", "data/blur_output");
        List<int[][]> blurImages = blur.getV6_ac_pd_2dblurf(
                "shift1_",
                ndv,
                minNdv1,
                N,
                dNum,
                70,
                16,
                10,
                4,
                5,
                4
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

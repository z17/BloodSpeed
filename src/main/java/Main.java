public class Main {
    public static final double[] g1 = initG1();


    public static void main(final String[] args) {
        BloodSpeedChecker bloodSpeedChecker = new BloodSpeedChecker();

        bloodSpeedChecker.getV7_ac_pdf_fst(
                "data\\shift1",
                20,
                10,
                0,
                1000,
                153,
                7,
                1,
                6
        );
    }

    private static double[] initG1() {
        double[] g = new double[101];
        for (int i = 0; i <= 100; i++) {
            g[i] = 0.125 * Math.pow((1 + Math.cos(Math.PI * i / 100)), 0.75);
        }
        return g;
    }
}

import helper.BmpHelper;
import helper.MatrixHelper;

import java.util.ArrayList;
import java.util.List;

class BloodSpeedChecker {
    private final static String INPUT_FOLDER = "out3cr2/";
    private final static int NUMBER_OF_DIGITS_IN_FILE_NAMES = 5;
    private final static String CIRCUIT = "res3_cr2.bmp";

    private final int[][] res3gr2;

    BloodSpeedChecker() {
        res3gr2 = BmpHelper.readBmp(CIRCUIT);
    }

    void getV7_ac_pdf_fst(final String prefix,
                          final int dv,
                          final int ndv,
                          final int min_ndv1,
                          final int N,
                          final int dNum,
                          final int r,
                          final int dr,
                          final int dt) {
        System.out.println("getV7_ac_pdf_fst started, minDv1=" + min_ndv1 + "/" + ndv);
        List<int[][]> resA3r = readInputFolder(INPUT_FOLDER, N);
        double[][] g11 = gr11_(r, dr);

        int[][] pd = new int[N + 1][dNum + 1];
        int[][] pde = new int[N + 1][dNum + 1];

        for (int ndv1 = min_ndv1; ndv1 <= ndv; ndv1++) {
            for (int n = 0; n <= N; n++) {
                int dt2 = dt / 2;
                int dt2a = dt % 2;
                int[][] resA3rn;
                int[][] resA3rn1;

                if (n < N - dt2 && n > dt2 + dt2a - 1) {
                    resA3rn = resA3r.get(n + dt2);
                    resA3rn1 = resA3r.get(n - dt2 - dt2a);
                } else if (n >= N - dt2) {
                    resA3rn = resA3r.get(N - 1);
                    resA3rn1 = resA3r.get(N - 1 - dt);
                } else {
                    resA3rn = resA3r.get(dt);
                    resA3rn1 = resA3r.get(0);
                }

                for (int dn1 = 0; dn1 <= dNum - 1; dn1++) {
                    double cTemp = dn1 + (dv * ndv1 / ndv);

                    int c = (int) Math.round(cTemp);

                    if (c <= dNum - 1 && c >= 0) {
                        double sum_add = 0;
                        double z1 = 0;
                        for (int r1 = -r; r1 <= r; r1 += dr) {
                            for (int r2 = -r; r2 <= r; r2 += dr) {
                                if (r1 * r1 + r2 * r2 > r * r) {
                                    continue;
                                }

                                int yr0 = r - r1;
                                int xr0 = dn1 + r2;
                                int yr1 = r - r1;
                                int xr1 = c + r2;

                                if (xr0 < 0 || xr0 > dNum - 1 || xr1 < 0 || xr1 > dNum - 1) {
                                    continue;
                                }

                                if (res3gr2[yr0][xr0] > 127 &&
                                        res3gr2[yr1][xr1] > 127) {
                                    int point_sh = resA3rn1[yr0][xr0] - resA3rn[yr1][xr1];
                                    double g2 = g11[r1 + r][r2 + r];
                                    sum_add = sum_add + g2 * point_sh * point_sh;
                                    z1 = z1 + g2;
                                }
                            }
                        }
                        pd[n][dn1] = (int) Math.round(sum_add / z1);
                        pde[n][dn1] = 200;

                    } else {
                        pde[n][dn1] = 0;
                        pd[n][dn1] = 0;
                    }
                }
            }

            String txtName = prefix + "m" + Integer.toString(ndv) + "_" + Integer.toString(ndv + ndv1) + ".txt";
            MatrixHelper.writeMatrix(txtName, pd);
            String bmpName = prefix + "me" + ndv + "_" + (ndv + ndv1) + ".bmp";
            BmpHelper.writeBmp(bmpName, pde);
            bmpName = prefix + "m" + ndv + "_" + (ndv + ndv1) + ".bmp";
            BmpHelper.writeBmp(bmpName, MatrixHelper.multiplyMatrix(pd, 0.025));
        }
    }

    private double[][] gr11_(final int r, final int dr) {
        double[][] g2 = new double[r * 2 + 1][r * 2 + 1];
        for (int r1 = -r; r1 <= r; r1 += dr) {
            for (int r2 = -r; r2 <= r; r2 += dr) {
                if (r1 * r1 + r2 * r2 <= r * r) {
                    int index = Math.round(((r1 * r1 + r2 * r2) * 100) / (r * r));
                    g2[r + r1][r + r2] = Main.g1[index];
                }
            }
        }

        return g2;
    }

    private List<int[][]> readInputFolder(final String dir3cr, final int N) {
        List<int[][]> result = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            result.add(i, readFile(i, dir3cr));
        }
        return result;
    }

    private int[][] readFile(final int n, final String dir) {
        final String formatted = String.format("%0" + NUMBER_OF_DIGITS_IN_FILE_NAMES + "d", n);
        final String name = dir + formatted + ".bmp";
        return BmpHelper.readBmp(name);
    }
}

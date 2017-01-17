package blood_speed.step;

import blood_speed.Main;
import blood_speed.helper.BmpHelper;
import blood_speed.helper.FunctionHelper;
import blood_speed.helper.MatrixHelper;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Blur {
    private final String outputFolder;
    private final AcPdfFst.Step1Result inputData;
    private final String prefix;
    private final int ndv;
    private final int minNdv;

    public Blur(final String inputFolder, final String outputFolder, final String prefix, final int ndv, final int minNdv) {
        FunctionHelper.checkIOFolders(inputFolder, outputFolder);
        this.outputFolder = outputFolder;
        this.prefix = prefix;
        this.ndv = ndv;
        this.minNdv = minNdv;
        this.inputData = readData(prefix, inputFolder, minNdv, ndv);
    }

    public Blur(final AcPdfFst.Step1Result inputData, final String outputFolder, final String prefix, final int ndv, final int minNdv) {
        FunctionHelper.checkIOFolders(null, outputFolder);
        this.outputFolder = outputFolder;
        this.inputData = inputData;
        this.prefix = prefix;
        this.ndv = ndv;
        this.minNdv = minNdv;
    }

    public List<int[][]> getV6_ac_pd_2dblurf(
            final int s1dn1,
            final int s1dn2,
            final int s1dn1st,
            final int s1dn2st,
            final int s2dn1,
            final int s2dn2
    ) {
        double[][] gv1 = new double[s1dn1 + 1][s1dn2 + 1];
        gv1[s1dn1][s1dn2] = 0;
        for (int dn11 = 0; dn11 <= s1dn1; dn11++) {
            for (int dn22 = 0; dn22 <= s1dn2; dn22++) {
                gv1[dn11][dn22] = ves2d(dn11, dn22, s1dn1, s1dn2);
            }
        }

        double[][] gv2 = new double[s1dn1 + 1][s1dn2 + 1];
        gv2[s2dn1][s2dn2] = 0;
        for (int dn11 = 0; dn11 <= s2dn1; dn11++) {
            for (int dn22 = 0; dn22 <= s2dn2; dn22++) {
                gv2[dn11][dn22] = ves2d(dn11, dn22, s2dn1, s2dn2);
            }
        }

        System.out.println("starting getV6_ac_pd_2blurf from mindv = " + minNdv + "/" + ndv);

        List<int[][]> result = new ArrayList<>();

        int ndv1 = minNdv;
        for (Pair<int[][], int[][]> one : inputData.getData()) {
            System.out.println("bluring file " + ndv + "_" + (ndv + ndv1));
            int[][] img = one.getKey();
            int[][] imge = one.getValue();

            Pair<int[][], int[][]> imgs = blr2(img, imge, s1dn1, s1dn2, s1dn1st, s1dn2st, gv1);
            imgs = blr2(imgs.getKey(), imge, s2dn1, s2dn2, 3, 2, gv2);
            imgs = blr2(imgs.getKey(), imge, s2dn1, s2dn2, 1, 1, gv1);

            String outBmpName1 = outputFolder + "/" + prefix + "sm" + ndv + "_" + (ndv + ndv1) + ".bmp";
            int[][] outBmp1 = MatrixHelper.multiplyMatrix(imgs.getKey(), 0.025);
            result.add(outBmp1);
            BmpHelper.writeBmp(outBmpName1, outBmp1);
            String outBmpName2 = outputFolder + "/" + prefix + "sme" + ndv + "_" + (ndv + ndv1) + ".bmp";
            BmpHelper.writeBmp(outBmpName2, imgs.getValue());

            ndv1++;
        }
        return result;
    }

    private Pair<int[][], int[][]> blr2(int[][] img, int[][] imge,
                                        int dn1, int dn2, int st1, int st2, double[][] gv) {


        double mgv = FunctionHelper.mean(gv);
        int N = FunctionHelper.rows(img);
        int dnum = FunctionHelper.cols(img);
        int sr = 0;
        int aa_min0 = 0;
        int aa_max0 = dnum - 1;
        for (int n2 = 0; n2 < dnum; n2++) {
            if (sr == 0 && imge[0][n2] == 0) {
                aa_min0 = n2;
            }
            if (imge[0][n2] != 0) {
                sr = 1;
                aa_max0 = n2;
            }
        }

        for (int n1 = 0; n1 < N; n1++) {
            for (int n2 = 0; n2 < dnum; n2++) {
                double sum = 0;
                double z = 0;

                int na_min = Math.max(0, n1 - dn1);
                int na_max = Math.min(N - 1, n1 + dn1);

                if (na_min == 0) {
                    na_min = n1 % st2;
                }

                int aa_min = Math.max(aa_min0, n2 - dn2);
                int aa_max = Math.min(aa_max0, n2 + dn2);

                if (aa_min == 0) {
                    aa_min = n2 % st2;
                }

                if (aa_max >= aa_min) {
                    for (int dn11 = na_min; dn11 <= na_max; dn11 += st1) {
                        for (int dn22 = aa_min; dn22 <= aa_max; dn22 += st2) {
                            int rsig = img[dn11][dn22];
                            if (rsig != 0) {
                                double resg = gv[Math.abs(dn11 - n1)][Math.abs(dn22 - n2)];
                                sum = sum + rsig * resg;
                                z = z + resg;
                            }
                        }
                    }
                }

                img[n1][n2] = 200;
                if (z > 0.125 * mgv) {
                    img[n1][n2] = (int) (sum / z);
                } else {
                    img[n1][n2] = 0;
                    img[n1][n2] = 0;
                }
            }
        }
        return new Pair<>(img, img);
    }

    private double ves2d(int y1, int x1, int ym, int xm) {
        double sqr = y1 * y1 + Math.pow(x1 * ym / xm, 2);

        double g2 = 0;
        if (sqr <= ym * ym)
            g2 = Main.G1[(int) Math.round(sqr * 100 / ym / ym)];

        return g2;
    }

    private AcPdfFst.Step1Result readData(final String prefix, final String inputFolder, final int minNdv, final int ndv) {
        AcPdfFst.Step1Result data = new AcPdfFst.Step1Result();

        System.out.println("Reading data for blur");

        for (int i = minNdv; i <= ndv; i++) {
            String inTxt = inputFolder + "/" + prefix + "m" + ndv + "_" + (ndv + i) + ".txt";
            int[][] pd = MatrixHelper.readMatrix(inTxt);

            String inBmp = inputFolder + "/" + prefix + "me" + ndv + "_" + (ndv + i) + ".bmp";
            int[][] pde = BmpHelper.readBmp(inBmp);

            data.add(pd, pde);
        }

        return data;
    }
}

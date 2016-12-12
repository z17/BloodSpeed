import helper.BmpHelper;
import helper.FunctionHelper;
import helper.MatrixHelper;
import javafx.util.Pair;

class Blur {
    public void getV6_ac_pd_2blurf(
            final String prefix,
            final int ndv,
            final int min_ndv,
            final int N,
            final int dNum,
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

        int[][] pd = new int[N][dNum];
        pd[N - 1][dNum - 1] = 0;

        System.out.println("starting getV6_ac_pd_2blurf from mindv = " + min_ndv + "/" + ndv);

        for (int ndv1 = min_ndv; ndv1 <= ndv; ndv1++) {
            String inTxt = prefix + "m" + ndv + "_" + (ndv + ndv1) + ".txt";
            System.out.println("reading " + inTxt);
            int[][] img = MatrixHelper.readMatrix(inTxt);

            String inBmp = prefix + "me" + ndv + "_" + (ndv + ndv1) + ".bmp";
            System.out.println("reading " + inBmp);
            int[][] imge = BmpHelper.readBmp(inBmp);

            Pair<int[][], int[][]> imgs = blr2(inTxt, img, imge, s1dn1, s1dn2, s1dn1st, s1dn2st, gv1, 1);
            imgs = blr2(inTxt, imgs.getKey(), imge, s2dn1, s2dn2, 3, 2, gv2, 2);
            imgs = blr2(inTxt, imgs.getKey(), imge, s2dn1, s2dn2, 1, 1, gv1, 3);

            String outBmp1 = prefix + "sm" + ndv + "_" + (ndv + ndv1) + ".bmp";
            BmpHelper.writeBmp(outBmp1, MatrixHelper.multiplyMatrix(imgs.getKey(), 0.025));
            String outBmp2 = prefix + "sme" + ndv + "_" + (ndv + ndv1) + ".bmp";
            BmpHelper.writeBmp(outBmp2, imgs.getValue());
        }
    }

    private Pair<int[][], int[][]> blr2(String str, int[][] img, int[][] imge,
                                        int dn1, int dn2, int st1, int st2, double[][] gv, int bnum) {


        double mgv = FunctionHelper.mean(gv);
        int[][] imgs = img;
        int[][] imgse = img;
        int N = FunctionHelper.rows(img);
        int dnum = FunctionHelper.cols(img);
        int sr = 0;
        int aa_min0 = 0;
        int aa_max0 = dnum - 1;
        for (int n2 = 0; n2 < dnum; n2++) {
            if (imge[0][n2] == 0 && sr == 0) {
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
                if (n1 % 100 == 0 &&  n2 ==0) {
                    System.out.println("bluring file " + str);
                }

                int na_min = Math.max(0, n1 - dn1);
                int na_max = Math.min(N-1, n1+dn1);
                if (na_min == 0) {
                    na_min = n1 % st1;
                }

                int aa_min = Math.max(aa_min0, n2 - dn2);
                int aa_max = Math.min(aa_max0, n2 + dn2);

                if (aa_max >= aa_min) {
                    for (int dn11 = na_min; dn11 <= na_max; dn11 += st1) {
                        for (int dn22 = aa_min; dn22 <= aa_max; dn22 += st1) {
                            int rsig = img[dn11][dn22];
                            if (rsig !=0) {
                                double resg = gv[dn11 - n1][dn22 - n2];
                                sum = sum + rsig * resg;
                                z = z + resg;
                            }
                        }
                    }
                }

                imgse[n1][n2] = 200;
                if (z > 0.125 * mgv) {
                        imgs[n1][n2] = (int) (sum / z);
                } else {
                    imgs[n1][n2] = 0;
                    imgse[n1][n2] = 0;
                }
            }
        }
        return new Pair<>(imgs, imgse);
    }

    private double ves2d(int y1, int x1, int ym, int xm) {
        double sqr = y1 * y1 + Math.pow(x1 * ym / xm, 2);

        double g2 = 0;
        if (sqr <= ym * ym)
            g2 = Main.g1[(int) Math.round(sqr * 100 / ym / ym)];

        return g2;
    }
}

package blood_speed.step;

import blood_speed.Main;
import blood_speed.helper.BmpHelper;
import blood_speed.helper.FunctionHelper;
import blood_speed.helper.MatrixHelper;
import blood_speed.step.data.Images;
import blood_speed.step.data.Step1Result;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

public class Blur extends Step<Images> {
    private final String outputFolder;
    private final Step1Result inputData;
    private final String prefix;
    private final int s1dn1;
    private final int s1dn2;
    private final int s1dn1st;
    private final int s1dn2st;
    private final int s2dn1;
    private final int s2dn2;

    private double[][] gv1;
    private double[][] gv2;

    public Blur(
            final Step1Result inputData,
            final String outputFolder,
            final String prefix,
            final int s1dn1,
            final int s1dn2,
            final int s1dn1st,
            final int s1dn2st,
            final int s2dn1,
            final int s2dn2
    ) {
        FunctionHelper.checkIOFolders(null, outputFolder);
        this.outputFolder = outputFolder;
        this.inputData = inputData;
        this.prefix = prefix;

        this.s1dn1 = s1dn1;
        this.s1dn2 = s1dn2;
        this.s1dn1st = s1dn1st;
        this.s1dn2st = s1dn2st;
        this.s2dn1 = s2dn1;
        this.s2dn2 = s2dn2;
    }

    public Images process() {
        System.out.println("Blurring started");

        gv1 = new double[s1dn1 + 1][s1dn2 + 1];
        gv1[s1dn1][s1dn2] = 0;
        for (int dn11 = 0; dn11 <= s1dn1; dn11++) {
            for (int dn22 = 0; dn22 <= s1dn2; dn22++) {
                gv1[dn11][dn22] = ves2d(dn11, dn22, s1dn1, s1dn2);
            }
        }

        gv2 = new double[s1dn1 + 1][s1dn2 + 1];
        gv2[s2dn1][s2dn2] = 0;
        for (int dn11 = 0; dn11 <= s2dn1; dn11++) {
            for (int dn22 = 0; dn22 <= s2dn2; dn22++) {
                gv2[dn11][dn22] = ves2d(dn11, dn22, s2dn1, s2dn2);
            }
        }

        int number = 1;
        List<ForkJoinTask<int[][]>> tasks = new ArrayList<>();
        ForkJoinPool executor = ForkJoinPool.commonPool();

        for (Pair<int[][], int[][]> one : inputData.getData()) {
            final int currentNumber = number;
            tasks.add(executor.submit(() -> blurFile(one, currentNumber)));
            number++;
        }

        Images result = new Images();
        for (ForkJoinTask<int[][]> task : tasks) {
            result.addImage(task.join());
        }

        return result;
    }

    private int[][] blurFile(Pair<int[][], int[][]> one, int number) {
        System.out.println("Blurring file " + number);

        int[][] img = one.getKey();
        int[][] imge = one.getValue();

        Pair<int[][], int[][]> imgs = blr2(img, imge, s1dn1, s1dn2, s1dn1st, s1dn2st, gv1);
        imgs = blr2(imgs.getKey(), imge, s2dn1, s2dn2, 3, 2, gv2);
        imgs = blr2(imgs.getKey(), imge, s2dn1, s2dn2, 1, 1, gv1);

        String outBmpName1 = outputFolder + "/" + prefix + "sm" + "_" + number + ".bmp";
        int[][] outBmp1 = MatrixHelper.multiplyMatrix(imgs.getKey(), 0.025);

        BmpHelper.writeBmp(outBmpName1, outBmp1);
        String outBmpName2 = outputFolder + "/" + prefix + "sme" + "_" + number + ".bmp";
        BmpHelper.writeBmp(outBmpName2, imgs.getValue());

        return outBmp1;
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

    public static Step1Result readData(final String prefix, final String inputFolder, final int startStep, final int stepsNumber) {
        Step1Result data = new Step1Result();

        System.out.println("Reading data for blur");

        for (int i = startStep; i <= stepsNumber; i++) {
            String inTxt = inputFolder + "/" + prefix + "m" + stepsNumber + "_" + (stepsNumber + i) + ".txt";
            int[][] pd = MatrixHelper.readMatrix(inTxt);

            String inBmp = inputFolder + "/" + prefix + "me" + stepsNumber + "_" + (stepsNumber + i) + ".bmp";
            int[][] pde = BmpHelper.readBmp(inBmp);

            data.add(pd, pde);
        }

        return data;
    }

    public static void buildGraphic(final List<int[][]> bluredImages, final int x, final int y) {
        List<Integer> values = new ArrayList<>();

        for (int[][] data : bluredImages) {
            values.add(data[y][x]);
        }

        System.out.println();
        for (Integer a : values) {
            System.out.print(a);
            System.out.print("\t");
        }
        System.out.println();
    }
}

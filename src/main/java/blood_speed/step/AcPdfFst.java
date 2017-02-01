package blood_speed.step;

import blood_speed.Main;
import blood_speed.helper.BmpHelper;
import blood_speed.helper.FunctionHelper;
import blood_speed.helper.MatrixHelper;
import javafx.util.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class AcPdfFst {
    private final int[][] res3gr2;
    private final String inputFolder;
    private final String outputFolder;
    private final int numberOfDigitsInFileNames;

    public AcPdfFst(final int numberOfDigitsInFileNames, final String inputFolder, final String outputFolder, final String circuit) {
        FunctionHelper.checkIOFolders(inputFolder, outputFolder);
        res3gr2 = BmpHelper.readBmp(circuit);
        this.inputFolder = inputFolder;
        this.outputFolder = outputFolder;
        this.numberOfDigitsInFileNames = numberOfDigitsInFileNames;
    }

    public Step1Result getV7_ac_pdf_fst(final String prefix,
                          final int dv,
                          final int ndv,
                          final int minNdv,
                          final int N,
                          final int dNum,
                          final int r,
                          final int dr,
                          final int dt) {

//        for (int i = 0; i < res3gr2.length; i++) {
//            System.out.println(res3gr2[i][34]);
//        }
        System.out.println("getV7_ac_pdf_fst started, minDv1=" + minNdv + "/" + ndv);
        List<int[][]> inputFiles = readInputFolder(inputFolder, N);
        double[][] g11 = gr11_(r, dr);

        Step1Result result = new Step1Result();

        // цикл по всем шагам
        for (int ndv1 = minNdv; ndv1 < ndv; ndv1++) {
            int[][] pd = new int[N][dNum];
            int[][] pde = new int[N][dNum];

            System.out.println("Processing ndv1 = " + ndv1);

            // цикл по всем кадрам
            for (int n = 0; n < N; n++) {
                int dt2 = dt / 2;
                int dt2a = dt % 2;
                int[][] resA3rn;
                int[][] resA3rn1;

                // выбор исходных картинок, которые сравниваем
                if (n < N - dt2 && n > dt2 + dt2a - 1) {
                    resA3rn = inputFiles.get(n + dt2);
                    resA3rn1 = inputFiles.get(n - dt2 - dt2a);
                } else if (n >= N - dt2) {
                    resA3rn = inputFiles.get(N - 1);
                    resA3rn1 = inputFiles.get(N - 1 - dt);
                } else {
                    resA3rn = inputFiles.get(dt);
                    resA3rn1 = inputFiles.get(0);
                }

                // цикл по ширине кадра
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

                                // если попадаем в контур
                                if (res3gr2[yr0][xr0] > 0 &&
                                        res3gr2[yr1][xr1] > 0) {
                                    double point_sh = (double)resA3rn1[yr0][xr0] - (double)resA3rn[yr1][xr1];
                                    double g2 = g11[r1 + r][r2 + r];
                                    sum_add = sum_add + g2 * point_sh * point_sh;
//                                    int point_sh = resA3rn1[yr0][xr0] * resA3rn[yr1][xr1];
//                                    sum_add = sum_add + g2 * point_sh ;
                                    z1 = z1 + g2;
                                }
                            }
                        }
                        pd[n][dn1] = (int) Math.round(sum_add / z1);

                        if ( pd[n][dn1] == 0 ) {
                            System.out.println("error!!!");
                        }
                        pde[n][dn1] = 200;

                    } else {
                        pde[n][dn1] = 0;
                        pd[n][dn1] = 0;
                    }
                }
            }

            String txtName = outputFolder + "/" + prefix + "m" + ndv + "_" + (ndv + ndv1) + ".txt";
            MatrixHelper.writeMatrix(txtName, pd);
            String bmpName1 = outputFolder + "/" + prefix + "me" + ndv + "_" + (ndv + ndv1) + ".bmp";
            BmpHelper.writeBmp(bmpName1, pde);
            result.add(pd, pde);

//            String bmpName2 = outputFolder + "/" + prefix + "m" + ndv + "_" + (ndv + ndv1) + ".bmp";
//            BmpHelper.writeBmp(bmpName2, MatrixHelper.multiplyMatrix(pd, 0.025));
        }

        return result;
    }

    private double[][] gr11_(final int r, final int dr) {
        double[][] g2 = new double[r * 2 + 1][r * 2 + 1];
        for (int r1 = -r; r1 <= r; r1 += dr) {
            for (int r2 = -r; r2 <= r; r2 += dr) {
                if (r1 * r1 + r2 * r2 <= r * r) {
                    int index = Math.round(((r1 * r1 + r2 * r2) * 100) / (r * r));
                    g2[r + r1][r + r2] = Main.G1[index];
                }
            }
        }
        return g2;
    }

    private List<int[][]> readInputFolder(final String dir, final int N) {
        List<int[][]> result = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            result.add(i, readFile(i, dir));
        }
        return result;
    }

    private int[][] readFile(final int n, final String dir) {
        final String formatted = String.format("%0" + numberOfDigitsInFileNames + "d", n);
        final String name = dir + "/" + formatted + ".bmp";
        return BmpHelper.readBmp(name);
    }

    public static class Step1Result {
        private List<Pair<int[][], int[][]>> data;

        Step1Result() {
            data = new ArrayList<>();
        }

        void add(int[][] pd, int[][] pde) {
            data.add(new Pair<>(pd, pde));
        }

        List<Pair<int[][], int[][]>> getData() {
            return data;
        }
    }
}

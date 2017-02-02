package blood_speed.step;

import blood_speed.Main;
import blood_speed.helper.BmpHelper;
import blood_speed.helper.FunctionHelper;
import blood_speed.helper.MatrixHelper;
import blood_speed.step.data.Step1Result;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

public class AcPdfFst extends Step<Step1Result> {
    private final int[][] circuitImage;
    private final String outputFolder;
    private final int numberOfDigitsInFileNames;
    private final String outputFilePrefix;
    private final int maxSpeed;
    private final int stepsNumber;
    private final int startStep;
    private final int framesNumber;
    private final int imageWidth;
    private final int r;
    private final int dr;
    private final int dt;

    private final double[][] g11;

    private List<int[][]> inputFiles;

    public AcPdfFst(final int numberOfDigitsInFileNames,
                    final String inputFolder,
                    final String outputFolder,
                    final String circuitImageName,
                    final String outputFilePrefix,
                    final int maxSpeed,
                    final int stepsNumber,
                    final int startStep,
                    final int framesNumber,
                    final int r,
                    final int dr,
                    final int dt
    ) {
        FunctionHelper.checkIOFolders(inputFolder, outputFolder);

        this.circuitImage = BmpHelper.readBmp(circuitImageName);
        this.imageWidth = FunctionHelper.cols(circuitImage);
        this.outputFolder = outputFolder;
        this.numberOfDigitsInFileNames = numberOfDigitsInFileNames;
        this.outputFilePrefix = outputFilePrefix;
        this.maxSpeed = maxSpeed;
        this.stepsNumber = stepsNumber;
        this.startStep = startStep;
        this.framesNumber = framesNumber;
        this.r = r;
        this.dr = dr;
        this.dt = dt;
        this.inputFiles = readInputFolder(inputFolder, framesNumber);

        this.g11 = gr11_(r, dr);
    }


    @Override
    public Step1Result process() {
        System.out.println("Step1: getV7_ac_pdf_fst started, minDv1=" + startStep + "/" + stepsNumber);



        List<ForkJoinTask<Pair<int[][], int[][]>>> tasks = new ArrayList<>();
        ForkJoinPool executor = ForkJoinPool.commonPool();

        // цикл по всем шагам
        for (int currentStep = startStep; currentStep < stepsNumber; currentStep++) {
            final int step = currentStep;
            tasks.add(executor.submit(() -> oneStep(step)));
        }

        Step1Result result = new Step1Result();
        for(ForkJoinTask<Pair<int[][], int[][]>> task : tasks) {
            result.add(task.join());
        }
        return result;
    }

    private Pair<int[][], int[][]> oneStep(int currentStep) {
        int[][] pd = new int[framesNumber][imageWidth];
        int[][] pde = new int[framesNumber][imageWidth];

        System.out.println("Processing currentStep = " + currentStep);

        int dt2 = dt / 2;
        int dt2a = dt % 2;

        // цикл по всем кадрам
        for (int currentFrame = 0; currentFrame < framesNumber; currentFrame++) {
            int[][] firstImage;
            int[][] secondImage;

            // выбор исходных картинок, которые сравниваем
            if (currentFrame < framesNumber - dt2 && currentFrame > dt2 + dt2a - 1) {
                firstImage = inputFiles.get(currentFrame + dt2);
                secondImage = inputFiles.get(currentFrame - dt2 - dt2a);
            } else if (currentFrame >= framesNumber - dt2) {
                firstImage = inputFiles.get(framesNumber - 1);
                secondImage = inputFiles.get(framesNumber - 1 - dt);
            } else {
                firstImage = inputFiles.get(dt);
                secondImage = inputFiles.get(0);
            }

            // цикл по ширине кадра
            for (int x = 0; x <= imageWidth - 1; x++) {
                double cTemp = x + (maxSpeed * currentStep / stepsNumber);
                int shift = (int) Math.round(cTemp);

                if (shift < imageWidth && shift >= 0) {
                    double sumRate = 0;
                    double z1 = 0;
                    for (int r1 = -r; r1 <= r; r1 += dr) {
                        for (int r2 = -r; r2 <= r; r2 += dr) {
                            if (r1 * r1 + r2 * r2 > r * r) {
                                continue;
                            }

                            int yr0 = r - r1;
                            int xr0 = x + r2;
                            int yr1 = r - r1;
                            int xr1 = shift + r2;

                            if (xr0 < 0 || xr0 >= imageWidth || xr1 < 0 || xr1 >= imageWidth) {
                                continue;
                            }

                            // если попадаем в контур
                            if (circuitImage[yr0][xr0] > 0 &&
                                    circuitImage[yr1][xr1] > 0) {
                                double point_sh = (double) secondImage[yr0][xr0] - (double) firstImage[yr1][xr1];
                                double g2 = g11[r1 + r][r2 + r];
                                sumRate = sumRate + g2 * point_sh * point_sh;
//                                    int point_sh = resA3rn1[yr0][xr0] * resA3rn[yr1][xr1];
//                                    sum_add = sum_add + g2 * point_sh ;
                                z1 = z1 + g2;
                            }
                        }
                    }
                    pd[currentFrame][x] = (int) Math.round(sumRate / z1);
                    pde[currentFrame][x] = 200;

                } else {
                    pde[currentFrame][x] = 0;
                    pd[currentFrame][x] = 0;
                }
            }
        }

        String txtName = outputFolder + "/" + outputFilePrefix + "m" + stepsNumber + "_" + (stepsNumber + currentStep) + ".txt";
        MatrixHelper.writeMatrix(txtName, pd);
        String bmpName1 = outputFolder + "/" + outputFilePrefix + "me" + stepsNumber + "_" + (stepsNumber + currentStep) + ".bmp";
        BmpHelper.writeBmp(bmpName1, pde);

        return new Pair<>(pd, pde);
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
        System.out.println("Step1: reading input data");
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
}

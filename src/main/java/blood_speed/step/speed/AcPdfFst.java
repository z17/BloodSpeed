package blood_speed.step.speed;

import blood_speed.runner.SpeedSteps;
import blood_speed.helper.BmpHelper;
import blood_speed.helper.FunctionHelper;
import blood_speed.helper.MathHelper;
import blood_speed.helper.MatrixHelper;
import blood_speed.step.Step;
import blood_speed.step.data.Point;
import blood_speed.step.data.SpeedData;
import blood_speed.step.data.SpeedImages;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

public class AcPdfFst extends Step<SpeedImages> {
    private final int[][] circuitImage;
    private final String outputFolder;
    private final int numberOfDigitsInFileNames;
    private final String inputFilePrefix;
    private final String outputFilePrefix;
    private final int maxSpeed;
    private final int stepsNumber;
    private final int startSpeed;
    private final int framesNumber;
    private final int imageWidth;
    private final int r;
    private final int middleHeight;
    private final int dr;
    private final int dt;

    private final double[][] g11;

    private List<int[][]> inputFiles;

    public AcPdfFst(final int numberOfDigitsInFileNames,
                    final String inputFolder,
                    final String outputFolder,
                    final String circuitImageName,
                    final String inputFilePrefix,
                    final String outputFilePrefix,
                    final int maxSpeed,
                    final int stepsNumber,
                    final int startSpeed,
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
        this.inputFilePrefix = inputFilePrefix;
        this.outputFilePrefix = outputFilePrefix;
        this.maxSpeed = maxSpeed;
        this.stepsNumber = stepsNumber;
        this.startSpeed = startSpeed;
        this.framesNumber = framesNumber;
        this.r = r;
        this.dr = dr;
        this.dt = dt;
        this.inputFiles = readInputFolder(inputFolder, framesNumber);
        this.middleHeight = circuitImage.length / 2;
        this.g11 = gr11_(r, dr);
    }

    @Override
    public SpeedImages process() {
        System.out.println("Step1: getV7_ac_pdf_fst started, minDv1=" + startSpeed + "/" + stepsNumber);

        List<ForkJoinTask<SpeedData>> tasks = new ArrayList<>();
        ForkJoinPool executor = ForkJoinPool.commonPool();
        System.err.printf("Using %d threads%s", executor.getParallelism(), System.lineSeparator());

        // цикл по всем шагам
        for (int currentStep = 0; currentStep < stepsNumber; currentStep++) {
            final int step = currentStep;
            tasks.add(executor.submit(() -> oneStep(step)));
        }

        SpeedImages result = new SpeedImages();
        for (ForkJoinTask<SpeedData> task : tasks) {
            result.add(task.join());
        }
        return result;
    }

    private SpeedData oneStep(int currentStep) {
        double currentSpeed = (((double) maxSpeed - startSpeed) / stepsNumber * currentStep) + startSpeed;
        int[][] pd = new int[framesNumber][imageWidth];
        int[][] pde = new int[framesNumber][imageWidth];

        System.out.println("Processing, currentSpeed = " + currentSpeed);

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
                double shift = x + currentSpeed;

                if (shift < imageWidth && shift >= 0) {
                    double sumRate = 0;
                    double z1 = 0;

                    for (int r1 = -r; r1 <= r; r1 += dr) {
                        for (int r2 = -r; r2 <= r; r2 += dr) {
                            if (r1 * r1 + r2 * r2 > r * r) {
                                continue;
                            }

                            final Point point0 = new Point(x + r2, middleHeight - r1);
                            final Point point1 = new Point(x + currentSpeed + r2, middleHeight - r1);

                            if (point0.getX() < 0 || point0.getX() > imageWidth - 1 - MathHelper.EPSILON || point1.getX() < 0 || point1.getX() > imageWidth- 1 - MathHelper.EPSILON) {
                                continue;
                            }

                            if (point0.getY() < 0 || point0.getY() > circuitImage.length - 1- MathHelper.EPSILON || point1.getY() < 0 || point1.getY() > circuitImage.length - 1- MathHelper.EPSILON) {
                                continue;
                            }

                                // если попадаем в контур
                                if (circuitImage[point0.getIntY()][point0.getIntX()] > 0 &&
                                        circuitImage[point1.getIntY()][point1.getIntX()] > 0) {
                                    // Desync
                                    double point_sh = MathHelper.getPointValue(point0, secondImage) - MathHelper.getPointValue(point1, firstImage);
                                    // Correlation
//                                    double point_sh = MathHelper.getPointValue(point0, secondImage) * MathHelper.getPointValue(point1, firstImage);

                                    double g2 = g11[r1 + r][r2 + r];
                                    sumRate = sumRate + g2 * point_sh * point_sh;
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

        String txtName = outputFolder + "/" + outputFilePrefix + "m" + maxSpeed + "_" + currentSpeed + ".txt";
        MatrixHelper.writeMatrix(txtName, pd);

        String bmpName = outputFolder + "/" + outputFilePrefix + "m" + maxSpeed + "_" + currentSpeed + ".bmp";
        BmpHelper.writeBmp(bmpName, BmpHelper.transformToImage(pd));

        String bmpName1 = outputFolder + "/" + outputFilePrefix + "me" + maxSpeed + "_" + currentSpeed + ".bmp";
        BmpHelper.writeBmp(bmpName1, pde);

        return new SpeedData(currentSpeed, pd, pde);
    }

    private double[][] gr11_(final int r, final int dr) {
        double[][] g2 = new double[r * 2 + 1][r * 2 + 1];
        for (int r1 = -r; r1 <= r; r1 += dr) {
            for (int r2 = -r; r2 <= r; r2 += dr) {
                if (r1 * r1 + r2 * r2 > r * r) {
                    continue;
                }
                int index = Math.round(((r1 * r1 + r2 * r2) * 100) / (r * r));
                g2[r + r1][r + r2] = SpeedSteps.G1[index];
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
        final String name = dir + "/" + inputFilePrefix + formatted + ".bmp";
        return BmpHelper.readBmp(name);
    }
}

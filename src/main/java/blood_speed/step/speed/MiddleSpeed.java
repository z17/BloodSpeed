package blood_speed.step.speed;

import blood_speed.helper.FunctionHelper;
import blood_speed.helper.MatrixHelper;
import blood_speed.step.Step;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;

/**
 * Класс для определения средней скорости
 */
public class MiddleSpeed extends Step<Integer> {
    private final double[][] speedMatrix;
    private final String outputFile;
    private final String outputBlurFile;
    private final int affectedCols;

    private static final int BLUR_SIZE = 15;
    private static final double BLUR[] =  getBlurData();
    private static final double BLUR_SUM = DoubleStream.of(BLUR).sum();

    public MiddleSpeed(double[][] speedMatrix, String outputFile, String outputBlurFile, int affectedCols) {
        this.speedMatrix = speedMatrix;
        this.outputFile = outputFile;
        this.outputBlurFile = outputBlurFile;
        this.affectedCols = affectedCols;
    }

    private static double[] getBlurData() {
        int n = BLUR_SIZE * 2 + 1;
        double[] blur = new double[n];
        for (int i =0; i < n; i++) {
            blur[i] = Math.sin(i / (5*Math.PI));
        }
        return blur;
    }

    @Override
    public Integer process() {
        System.out.println("Processing middle speed");
        int rows = FunctionHelper.rows(speedMatrix);
        List<Double> signal = new ArrayList<>();
        int fullSum = 0;
        for (int i = 0; i < rows; i++) {
            int sum = 0;
            for (int j = 0; j < affectedCols; j++) {
                sum += speedMatrix[i][j];
                fullSum += speedMatrix[i][j];
            }
            signal.add((double)sum / affectedCols);
        }

        List<Double> signalBlur = new ArrayList<>(signal.size());

        for (int i = 0; i < signal.size(); i++) {
            if (i - BLUR_SIZE  <= 0 || i + BLUR_SIZE >= signal.size()) {
                signalBlur.add(signal.get(i));
                continue;
            }

            double blurSignal = 0;
            int blurIndex = 0;
            for (double blurCoefficient : BLUR) {
                blurSignal += blurCoefficient * signal.get(i - BLUR_SIZE + blurIndex);
                blurIndex++;
            }
            signalBlur.add(blurSignal / BLUR_SUM);
        }

        MatrixHelper.writeMatrix(outputFile, signal);
        MatrixHelper.writeMatrix(outputBlurFile, signalBlur);

        int middleSpeed =  fullSum / (affectedCols * rows);
        System.err.println("Middle speed = " + middleSpeed);
        return middleSpeed;
    }
}

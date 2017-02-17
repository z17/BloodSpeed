package blood_speed.step;

import blood_speed.helper.FunctionHelper;
import blood_speed.helper.MatrixHelper;

import java.util.ArrayList;
import java.util.List;

public class MiddleSpeed extends Step<Integer> {
    private final int[][] speedMatrix;
    private final String outputFile;
    private final int affectedCols;

    public MiddleSpeed(int[][] speedMatrix, String outputFile, int affectedCols) {
        this.speedMatrix = speedMatrix;
        this.outputFile = outputFile;
        this.affectedCols = affectedCols;
    }

    @Override
    public Integer process() {
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

        MatrixHelper.writeMatrix(outputFile, signal);
        int middleSpeed =  fullSum / (affectedCols * rows);
        System.err.println("Middle speed = " + middleSpeed);
        return middleSpeed;
    }
}

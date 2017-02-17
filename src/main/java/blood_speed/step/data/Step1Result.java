package blood_speed.step.data;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Step1Result {
    private List<SpeedData> data;

    public Step1Result() {
        data = new ArrayList<>();
    }

    public void add(int speed, int[][] rateMatrix, int[][] scopeMatrix) {
        data.add(new SpeedData(speed, rateMatrix, scopeMatrix));
    }

    public void add(int speed, Pair<int[][], int[][]> pair) {
        data.add(new SpeedData(speed, pair.getKey(), pair.getValue()));
    }

    public void add(SpeedData data) {
        this.data.add(data);
    }

    public List<SpeedData> getData() {
        return data;
    }
}
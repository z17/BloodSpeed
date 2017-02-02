package blood_speed.step.data;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Step1Result {
    private List<Pair<int[][], int[][]>> data;

    public Step1Result() {
        data = new ArrayList<>();
    }

    public void add(int[][] rateMatrix, int[][] scopeMatrix) {
        data.add(new Pair<>(rateMatrix, scopeMatrix));
    }

    public void add(Pair<int[][], int[][]> pair) {
        data.add(pair);
    }

    public List<Pair<int[][], int[][]>> getData() {
        return data;
    }
}
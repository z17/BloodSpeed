package blood_speed;

import blood_speed.helper.FunctionHelper;
import blood_speed.helper.MatrixHelper;

import java.util.ArrayList;
import java.util.List;

public class Helper {
    public static void main(String[] args) {

        int[][] matrix = MatrixHelper.readMatrix("data/result/result.txt");
        int cols = FunctionHelper.cols(matrix);
        int rows = FunctionHelper.rows(matrix);
        List<Integer> signal1 = new ArrayList<>();
        List<Integer> signal2 = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            int sum = 0;
            for (int j = 0; j < 309; j++) {
                sum += matrix[i][j];
            }
            signal2.add(matrix[i][241]);
            signal1.add(sum);
//            System.out.print("\t" + sum);
        }

        MatrixHelper.writeMatrix("data/signal1.txt", signal1);
        MatrixHelper.writeMatrix("data/signal2.txt", signal2);

    }
}

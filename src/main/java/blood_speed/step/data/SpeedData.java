package blood_speed.step.data;

public class SpeedData {
    public final int speed;
    public final int[][] matrix;
    public final int[][] pde;

    public SpeedData(int speed, int[][] matrix, int[][] pde) {
        this.speed = speed;
        this.matrix = matrix;
        this.pde = pde;
    }
}

package blood_speed.step.data;

public class SpeedData {
    public final int speed;
    public final int[][] pd;
    public final int[][] pde;

    public SpeedData(int speed, int[][] pd, int[][] pde) {
        this.speed = speed;
        this.pd = pd;
        this.pde = pde;
    }
}

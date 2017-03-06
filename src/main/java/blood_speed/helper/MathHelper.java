package blood_speed.helper;

public class MathHelper {
    public static double distancePointToLine(double a, double b, double c, double x, double y) {
        return Math.abs(a * x + b * y + c) / Math.sqrt(a * a + b * b);
    }
}

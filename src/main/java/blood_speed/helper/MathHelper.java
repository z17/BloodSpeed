package blood_speed.helper;

import blood_speed.step.data.Point;

public class MathHelper {
    public static double distancePointToLine(double a, double b, double c, double x, double y) {
        return Math.abs(a * x + b * y + c) / Math.sqrt(a * a + b * b);
    }

    public static double distance(Point currentPoint, Point nextPoint) {
        return Math.sqrt(Math.pow(nextPoint.x - currentPoint.x, 2) + Math.pow(nextPoint.y - currentPoint.y, 2));
    }

    public static Point middlePoint(Point currentPoint, Point nextPoint) {
        return new Point((currentPoint.x + nextPoint.x) / 2, (currentPoint.y + nextPoint.y) / 2);
    }
}

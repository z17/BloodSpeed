package blood_speed.helper;

import blood_speed.step.data.LineSegment;
import blood_speed.step.data.Point;

public class MathHelper {
    public static final double EPSILON = 0.001;

    public static double distancePointToLine(double a, double b, double c, double x, double y) {
        return Math.abs(a * x + b * y + c) / Math.sqrt(a * a + b * b);
    }

    public static double distance(Point currentPoint, Point nextPoint) {
        return Math.sqrt(Math.pow(nextPoint.getX() - currentPoint.getX(), 2) + Math.pow(nextPoint.getY() - currentPoint.getY(), 2));
    }

    public static Point middlePoint(Point currentPoint, Point nextPoint) {
        return new Point((currentPoint.getX() + nextPoint.getX()) / 2, (currentPoint.getY() + nextPoint.getY()) / 2);
    }

    public static boolean inCircle(int centerX, int centerY, int x, int y, double r) {
        return Math.sqrt(Math.pow(centerX - x, 2) + Math.pow(centerY - y, 2)) < r;
    }

    public static Point getInterSectionPointWithCircleAndSegment(final LineSegment segment, final Point circleCenter, final double r) {
        LineSegment segmentNormalize = new LineSegment(
                new Point(segment.getP1().getX() - circleCenter.getX(), segment.getP1().getY() - circleCenter.getY()),
                new Point(segment.getP2().getX() - circleCenter.getX(), segment.getP2().getY() - circleCenter.getY())
        );

        double x0 = - (segmentNormalize.getA() * segmentNormalize.getC()) / (Math.pow(segmentNormalize.getA(), 2) + Math.pow(segmentNormalize.getB(),2));
        double y0 = - (segmentNormalize.getB() * segmentNormalize.getC()) / (Math.pow(segmentNormalize.getA(), 2) + Math.pow(segmentNormalize.getB(),2));

        Point nearestNormalizePoint = new Point(x0, y0);
        double distanceToNearest = distance(new Point(0, 0), nearestNormalizePoint);

        if (distanceToNearest > r) {
            return null;
        }
        // todo: check this with epsilon
        if (distanceToNearest == r) {
            if (segmentNormalize.isPointOnSegment(nearestNormalizePoint)) {
                return new Point(nearestNormalizePoint.getX() + circleCenter.getX(), nearestNormalizePoint.getY() + circleCenter.getY());
            }
            return null;
        }

        double d = Math.sqrt(Math.pow(r, 2) - Math.pow(segmentNormalize.getC(), 2) / (Math.pow(segmentNormalize.getA(),2) + Math.pow(segmentNormalize.getB(),2)));
        double mult = Math.sqrt(Math.pow(d, 2) / (Math.pow(segmentNormalize.getA(),2) + Math.pow(segmentNormalize.getB(),2)));

        Point a = new Point(x0 + segmentNormalize.getB() * mult, y0 - segmentNormalize.getA() * mult);
        Point b = new Point(x0 - segmentNormalize.getB() * mult, y0 + segmentNormalize.getA() * mult);

        if (segmentNormalize.isPointOnSegment(a) && !segmentNormalize.isPointOnSegment(b)) {
            return a;
        } else if (segmentNormalize.isPointOnSegment(b) && !segmentNormalize.isPointOnSegment(a)) {
            return b;
        }

        throw new RuntimeException("Both points on segment");
    }
}

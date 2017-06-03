package blood_speed.helper;

import blood_speed.step.data.Line;
import blood_speed.step.data.LineSegment;
import blood_speed.step.data.Point;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MathHelper {
    public static final double EPSILON = 0.1;

    public static boolean pointInImage(final Point p, final int width, final int height) {
        return pointInImage(p.getIntX(), p.getIntY(), width, height);
    }

    public static boolean pointInImage(final int x, final int y, final int width, final int height) {
        if (x < 0 || x > width - 1 - MathHelper.EPSILON || y < 0 || y > height - 1 - MathHelper.EPSILON) {
            return false;
        }
        return true;
    }

    public static boolean doubleEquals(double a, double b) {
        return Math.abs(a - b) < EPSILON;
    }

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

    /**
     * Точки пересечения линии line и окружности с центров в circleCenter и радиусом r
     */
    public static List<Point> getInterSectionPointWithCircleAndLine(final Line line, final Point circleCenter, final double r) {
        Point p1, p2;
        if (!doubleEquals(line.getA(), 0)) {
            p1 = new Point(line.getX(10), 10);
            p2 = new Point(line.getX(11), 11);
        } else {
            p1 = new Point(10, line.getY(10));
            p2 = new Point(11, line.getY(11));
        }

        LineSegment segmentNormalize = new LineSegment(
                new Point(p1.getX() - circleCenter.getX(), p1.getY() - circleCenter.getY()),
                new Point(p2.getX() - circleCenter.getX(), p2.getY() - circleCenter.getY())
        );

        double x0 = -(segmentNormalize.getA() * segmentNormalize.getC()) / (Math.pow(segmentNormalize.getA(), 2) + Math.pow(segmentNormalize.getB(), 2));
        double y0 = -(segmentNormalize.getB() * segmentNormalize.getC()) / (Math.pow(segmentNormalize.getA(), 2) + Math.pow(segmentNormalize.getB(), 2));

        Point nearestNormalizePoint = new Point(x0, y0);
        double distanceToNearest = distance(new Point(0, 0), nearestNormalizePoint);

        if (doubleEquals(distanceToNearest, r)) {
            return Collections.singletonList(new Point(nearestNormalizePoint.getX() + circleCenter.getX(), nearestNormalizePoint.getY() + circleCenter.getY()));
        }

        if (distanceToNearest > r) {
            return null;
        }

        double d = Math.sqrt(Math.pow(r, 2) - Math.pow(segmentNormalize.getC(), 2) / (Math.pow(segmentNormalize.getA(), 2) + Math.pow(segmentNormalize.getB(), 2)));
        double mult = Math.sqrt(Math.pow(d, 2) / (Math.pow(segmentNormalize.getA(), 2) + Math.pow(segmentNormalize.getB(), 2)));

        Point a = new Point(x0 + segmentNormalize.getB() * mult + circleCenter.getX(), y0 - segmentNormalize.getA() * mult + circleCenter.getY());
        Point b = new Point(x0 - segmentNormalize.getB() * mult + circleCenter.getX(), y0 + segmentNormalize.getA() * mult + circleCenter.getY());

        return Arrays.asList(a, b);
    }

    /**
     * Возвращает точку пересечения отрезка и окружности с центром в circleCenter и радиусом r
     * Если их две - кидает ошибку
     */
    public static Point getInterSectionPointWithCircleAndSegment(final LineSegment segment, final Point circleCenter, final double r) {
        List<Point> points = getInterSectionPointWithCircleAndLine(segment, circleCenter, r);

        if (points == null || points.size() == 0) {
            return null;
        }

        if (points.size() == 1) {
            Point a = points.get(0);
            if (segment.isPointOnSegment(a)) {
                return a;
            }
            return null;
        }


        Point a = points.get(0);
        Point b = points.get(1);
        if (segment.isPointOnSegment(a) && !segment.isPointOnSegment(b)) {
            return a;
        } else if (segment.isPointOnSegment(b) && !segment.isPointOnSegment(a)) {
            return b;
        } else if (segment.isPointOnSegment(a) && segment.isPointOnSegment(b)) {
            throw new RuntimeException("Both points on segment");
        }
        return null;
    }

    /**
     * билиейная интерполяция
     * https://ru.wikipedia.org/wiki/%D0%91%D0%B8%D0%BB%D0%B8%D0%BD%D0%B5%D0%B9%D0%BD%D0%B0%D1%8F_%D0%B8%D0%BD%D1%82%D0%B5%D1%80%D0%BF%D0%BE%D0%BB%D1%8F%D1%86%D0%B8%D1%8F
     */
    public static double getPointValue(Point point, int[][] matrix) {
        double x = point.getX();
        double y = point.getY();

        // если числа целые - возвращаем значение по ним
        if (x == Math.floor(x) && y == Math.floor(y)) {
            return matrix[point.getIntY()][point.getIntX()];
        }

        int x1 = (int) Math.floor(x);
        int y1 = (int) Math.floor(y);
        int x2 = x1 + 1;
        int y2 = y1 + 1;

        double r1 = (x2 - x) / (x2 - x1) * matrix[y1][x1] + (x - x1) / (x2 - x1) * matrix[y1][x2];
        double r2 = (x2 - x) / (x2 - x1) * matrix[y2][x1] + (x - x1) / (x2 - x1) * matrix[y2][x2];
        return (y2 - y) / (y2 - y1) * r1 + (y - y1) / (y2 - y1) * r2;
    }

    /**
     * Генерирует маску с нормальным распределением, 1 в центре
     */
    public static double[][] generateMask(int size) {
        double[][] res = new double[size][size];
        double middle = ((double) size - 1) / 2;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                double x = Math.sqrt(Math.pow(i - middle, 2) + Math.pow(j - middle, 2));
                res[i][j] = func(8 * x / size, 0, 2);
            }
        }
        return res;
    }

    /**
     * Нормальное распределение
     */
    private static double func(double x, double m, double o) {
        return Math.exp(-(Math.pow(x - m, 2) / (2 * Math.pow(o, 2))));
    }
}

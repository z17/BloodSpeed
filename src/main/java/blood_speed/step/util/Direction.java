package blood_speed.step.util;

import blood_speed.helper.MathHelper;
import blood_speed.step.VectorSelector2;
import blood_speed.step.data.Point;

import java.util.function.Function;

public enum Direction {
    TOP(p -> new Point(p.x, p.y - 1),
            d -> Math.abs(d.getLeft() - d.getRight()),
            d -> (Math.abs(d.getLeft() - d.getRight()) + 0.1 * Math.abs(d.getTopRight() - d.getBottomLeft()) + 0.1 * Math.abs(d.getTopLeft() - d.getBottomLeft()))),
    LEFT(p -> new Point(p.x - 1, p.y),
            d -> Math.abs(d.getTop() - d.getBottom()),
            d -> (Math.abs(d.getTop() - d.getBottom()) + 0.1 * Math.abs(d.getTopRight() - d.getBottomLeft()) + 0.1 * Math.abs(d.getTopLeft() - d.getBottomRight()))),
    RIGHT(p -> new Point(p.x + 1, p.y),
            d -> Math.abs(d.getTop() - d.getBottom()),
            d -> (Math.abs(d.getTop() - d.getBottom()) + 0.1 * Math.abs(d.getTopRight() - d.getBottomLeft()) + 0.1 * Math.abs(d.getTopLeft() - d.getBottomRight()))),
    BOTTOM(p -> new Point(p.x, p.y + 1),
            d -> Math.abs(d.getLeft() - d.getRight()),
            d -> (Math.abs(d.getLeft() - d.getRight()) + 0.1 * Math.abs(d.getTopRight() - d.getBottomLeft()) + 0.1 * Math.abs(d.getTopLeft() - d.getBottomLeft()))),
    TOP_RIGHT(p -> new Point(p.x + 1, p.y - 1),
            d -> Math.abs(d.getTopLeft() - d.getBottomRight()),
            d -> (Math.abs(d.getTopLeft() - d.getBottomRight()) + 0.1 * Math.abs(d.getLeft() - d.getRight()) + 0.1 * Math.abs(d.getBottom() - d.getTop()))),
    TOP_LEFT(p -> new Point(p.x - 1, p.y - 1),
            d -> Math.abs(d.getTopRight() - d.getBottomLeft()),
            d -> (Math.abs(d.getTopRight() - d.getBottomLeft()) + 0.1 * Math.abs(d.getLeft() - d.getRight()) + 0.1 * Math.abs(d.getBottom() - d.getTop()))),
    BOTTOM_RIGHT(p -> new Point(p.x + 1, p.y + 1),
            d -> Math.abs(d.getBottomLeft() - d.getTopRight()),
            d -> (Math.abs(d.getTopRight() - d.getBottomLeft()) + 0.1 * Math.abs(d.getLeft() - d.getRight()) + 0.1 * Math.abs(d.getBottom() - d.getTop()))),
    BOTTOM_LEFT(p -> new Point(p.x - 1, p.y + 1),
            d -> Math.abs(d.getTopLeft() - d.getBottomRight()),
            d -> (Math.abs(d.getTopLeft() - d.getBottomRight()) + 0.1 * Math.abs(d.getLeft() - d.getRight()) + 0.1 * Math.abs(d.getBottom() - d.getTop())));

    public final Function<Point, Point> nextPointFunction;
    private final Function<Distances, Double> perpendicularDiffFunction;
    private final Function<Distances, Double> perpendicularDiffFunction2;

    Direction(Function<Point, Point> nextPointFunction, Function<Distances, Double> perpendicularDiffFunction, Function<Distances, Double> perpendicularDiffFunction2) {
        this.nextPointFunction = nextPointFunction;
        this.perpendicularDiffFunction = perpendicularDiffFunction;
        this.perpendicularDiffFunction2 = perpendicularDiffFunction2;
    }

    public Direction[] getTowardsDirections() {
        switch (this) {
            case TOP:
                return new Direction[]{Direction.TOP, Direction.TOP_RIGHT, Direction.TOP_LEFT};
            case LEFT:
                return new Direction[]{Direction.LEFT, Direction.BOTTOM_LEFT, Direction.TOP_LEFT};
            case RIGHT:
                return new Direction[]{Direction.BOTTOM_RIGHT, Direction.TOP_RIGHT, Direction.RIGHT};
            case BOTTOM:
                return new Direction[]{Direction.BOTTOM, Direction.BOTTOM_RIGHT, Direction.BOTTOM_LEFT};
            case TOP_RIGHT:
                return new Direction[]{Direction.TOP_RIGHT, Direction.TOP, Direction.RIGHT};
            case TOP_LEFT:
                return new Direction[]{Direction.TOP_LEFT, Direction.TOP, Direction.LEFT};
            case BOTTOM_RIGHT:
                return new Direction[]{Direction.BOTTOM_RIGHT, Direction.BOTTOM, Direction.RIGHT};
            case BOTTOM_LEFT:
                return new Direction[]{Direction.BOTTOM_LEFT, Direction.BOTTOM, Direction.LEFT};
        }
        throw new RuntimeException();
    }

    public Direction[] getOppositeDirection() {
        switch (this) {
            case BOTTOM:
                return new Direction[]{Direction.TOP, Direction.TOP_RIGHT, Direction.TOP_LEFT};
            case RIGHT:
                return new Direction[]{Direction.LEFT, Direction.BOTTOM_LEFT, Direction.TOP_LEFT};
            case LEFT:
                return new Direction[]{Direction.BOTTOM_RIGHT, Direction.TOP_RIGHT, Direction.RIGHT};
            case TOP:
                return new Direction[]{Direction.BOTTOM, Direction.BOTTOM_RIGHT, Direction.BOTTOM_LEFT};
            case BOTTOM_LEFT:
                return new Direction[]{Direction.TOP_RIGHT, Direction.TOP, Direction.RIGHT};
            case BOTTOM_RIGHT:
                return new Direction[]{Direction.TOP_LEFT, Direction.TOP, Direction.LEFT};
            case TOP_LEFT:
                return new Direction[]{Direction.BOTTOM_RIGHT, Direction.BOTTOM, Direction.RIGHT};
            case TOP_RIGHT:
                return new Direction[]{Direction.BOTTOM_LEFT, Direction.BOTTOM, Direction.LEFT};
        }
        throw new RuntimeException();
    }

    public static Direction getByPoints(Point currentPoint, Point nextPoint) {
        // нужно не забывать, что по оси y  - увеличение значения ведёт вниз, а не вверх

        // дистанция до диагонали y = x;
        double distance0 = MathHelper.distancePointToLine(1, 1, -(currentPoint.x + currentPoint.y), nextPoint.x, nextPoint.y);

        // дистанция до вертикали x = nextPoint.x
        double distance1 = MathHelper.distancePointToLine(1, 0, -currentPoint.x, nextPoint.x, nextPoint.y);

        // дистанция до диагонали y = -x + nexPoint.x
        double distance2 = MathHelper.distancePointToLine(1, -1, -(currentPoint.x - currentPoint.y), nextPoint.x, nextPoint.y);

        // дистанция до горизонтали y = nexPoint.y
        double distance3 = MathHelper.distancePointToLine(0, 1, -currentPoint.y, nextPoint.x, nextPoint.y);

        double[] distances = new double[]{
                distance0,
                distance1,
                distance2,
                distance3
        };

        byte minIndex = 0;
        double min = distances[0];
        for (byte i = 0; i < distances.length; i++) {
            if (distances[i] < min) {
                min = distances[i];
                minIndex = i;
            }
        }

        switch (minIndex) {
            case 0:
                if (nextPoint.x > currentPoint.x) {
                    return TOP_RIGHT;
                } else {
                    return BOTTOM_LEFT;
                }
            case 1:
                if (nextPoint.y < currentPoint.y)
                    return TOP;
                else
                    return BOTTOM;
            case 2:
                if (nextPoint.y < currentPoint.y)
                    return TOP_LEFT;
                else
                    return BOTTOM_RIGHT;
            case 3:
                if (nextPoint.x < currentPoint.x)
                    return LEFT;
                else
                    return RIGHT;
            default:
                throw new RuntimeException("Error");
        }
    }
}

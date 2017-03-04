package blood_speed.step;

import blood_speed.helper.BmpHelper;
import blood_speed.helper.FunctionHelper;
import blood_speed.helper.MatrixHelper;
import blood_speed.step.data.Images;
import blood_speed.step.data.Point;
import javafx.util.Pair;

import java.util.*;
import java.util.function.Function;

@SuppressWarnings({"SameParameterValue", "Duplicates"})
public class VectorSelector2 extends Step<Images> {
    private final Images data;
    private final int[][] circuit;

    public VectorSelector2(Images data, int[][] circuit) {
        this.data = data;
        this.circuit = circuit;
    }

    private boolean inCircle(int centerX, int centerY, int x, int y, double r) {
        return Math.sqrt(Math.pow(centerX - x, 2) + Math.pow(centerY - y, 2)) < r;
    }

    @Override
    public Images process() {
        final int regionSize = 3;
        final int maxSpeed = 15;

        // выбираем стартовую точку
        Point currentPoint = new Point(47, 143);

        int n = 0;
        while (true) {

            int[][] dissynchronizationFactor = findDirection(currentPoint, regionSize, maxSpeed);
            Point minDissynchronizationPoint = getMinDissynchronizationPoint(dissynchronizationFactor, currentPoint, maxSpeed);

            // получаем точки окружности, с центром в текущей точек и радиусом = расстояние от текущей до минимума десинхронизации
            double r = Math.sqrt(Math.pow(minDissynchronizationPoint.x - currentPoint.x, 2) + Math.pow(minDissynchronizationPoint.y - currentPoint.y, 2));

            Set<Point> circle = new HashSet<>();
            for (int j = (int) Math.round(currentPoint.x - r); j < currentPoint.x + r; j++) {
                double a = 1;
                double b = -2 * currentPoint.y;
                double c = Math.pow(j, 2) - 2 * j * currentPoint.x + Math.pow(currentPoint.x, 2) + Math.pow(currentPoint.y, 2) - Math.pow(r, 2);
                double d = b * b - 4 * a * c;
                double y1 = (-b + Math.sqrt(d)) / (2 * a);
                double y2 = (-b - Math.sqrt(d)) / (2 * a);

                circle.add(new Point(j, (int) Math.round(y1)));
                circle.add(new Point(j, (int) Math.round(y2)));
            }
            for (int i = (int) Math.round(currentPoint.y - r); i < currentPoint.y + r; i++) {
                double a = 1;
                double b = -2 * currentPoint.x;
                double c = Math.pow(i, 2) - 2 * i * currentPoint.y + Math.pow(currentPoint.y, 2) + Math.pow(currentPoint.x, 2) - Math.pow(r, 2);
                double d = b * b - 4 * a * c;
                double x1 = (-b + Math.sqrt(d)) / (2 * a);
                double x2 = (-b - Math.sqrt(d)) / (2 * a);

                circle.add(new Point((int) Math.round(x1), i));
                circle.add(new Point((int) Math.round(x2), i));
            }

            List<Point> candidates = new ArrayList<>();
            for (Point c : circle) {
                double a = Math.sqrt(Math.pow(c.x - minDissynchronizationPoint.x, 2) + Math.pow(c.y - minDissynchronizationPoint.y, 2));
                double angle = Math.toDegrees(2 * Math.asin(a / 2 / r));
                if (angle > 30 || Double.isNaN(angle)) {
                    continue;
                }
                candidates.add(c);
            }

            double minDifferent = 0;
            for (Point oneOfNextPoint : candidates) {
                if (circuit[oneOfNextPoint.y][oneOfNextPoint.x] != 0) {
                    continue;
                }

                Distances borderDistance = findBorderDistance(oneOfNextPoint.x, oneOfNextPoint.y);
//          double currentDiff = d.perpendicularDiffFunction.apply(borderDistance);
                double minDirection = borderDistance.getMinDirectionValue();
                if (minDirection > minDifferent) {
                    minDifferent = minDirection;
                    currentPoint = oneOfNextPoint;
                }
            }

            circuit[currentPoint.y][currentPoint.x] = 127;

            if (n > 30) {
                break;
            }
            n++;
        }


        BmpHelper.writeBmp("data/text.bmp", circuit);

        System.exit(1);

//
//        Point
//
//        System.exit(1);
//
//        Direction nextDirection = Direction.TOP;
//
//        int n = 0;
//        List<Point> middles = new ArrayList<>();
//        while (nextDirection != null) {
//            try {
//                middles.add(currentPoint);
//                final Point currentPoint = currentPoint;
//                final Direction currentDirection = nextDirection;
//
//                Direction[] towardsDirections = currentDirection.getTowardsDirections();
//
//                nextDirection = null;
//                currentPoint = null;
//
//                double minDifferent = 0;
//                for (Direction d : towardsDirections) {
//                    Point oneOfNextPoint = d.nextPointFunction.apply(currentPoint);
//                    if (circuit[oneOfNextPoint.y][oneOfNextPoint.x] != 0) {
//                        continue;
//                    }
//                    Distances borderDistance = findBorderDistance(oneOfNextPoint.x, oneOfNextPoint.y);
////                    double currentDiff = d.perpendicularDiffFunction.apply(borderDistance);
//                    double minDirection = borderDistance.getMinDirectionValue();
//                    if (minDirection > minDifferent && !middles.contains(oneOfNextPoint)) {
//                        minDifferent = minDirection;
//                        nextDirection = d;
//                        currentPoint = oneOfNextPoint;
//                    }
//                }
//            } catch (RuntimeException e) {
//                break;
//            }
//
//            if (n > 300) {
//                break;
//            }
//            n++;
//
//        }
//
//
//        int[][] trackImage = new int[data.getRows()][data.getCols()];
//        for (int i = 0; i < data.getRows(); i++) {
//            for (int j = 0; j < data.getCols(); j++) {
//                if (circuit[i][j] == 0) {
//                    trackImage[i][j] = 127;
//
//                } else {
//                    trackImage[i][j] = 255;
//                }
//            }
//        }
//        int currentColor = 0;
//        for (Point p : middles) {
//            trackImage[p.y][p.x] = currentColor;
//            currentColor += 20;
//            if (currentColor > 254) {
//                currentColor = 0;
//            }
//        }
//
//        BmpHelper.writeBmp("data/test-result5-2.bmp", trackImage);
        return null;

    }

    private Point getMinDissynchronizationPoint(final int[][] dissynchronizationFactor, final Point currentPoint, final int maxSpeed) {
        int minFactor = Integer.MAX_VALUE;
        int directionX = 0;
        int directionY = 0;
        for (int i = 0; i < dissynchronizationFactor.length; i++) {
            for (int j = 0; j < dissynchronizationFactor[i].length; j++) {
                if (dissynchronizationFactor[i][j] > 0 && dissynchronizationFactor[i][j] < minFactor) {
                    minFactor = dissynchronizationFactor[i][j];
                    directionX = j;
                    directionY = i;
                }
            }
        }

        return new Point(currentPoint.x + directionX - maxSpeed, currentPoint.y + directionY - maxSpeed);
    }

    private int[][] findDirection(Point point, int pointRegionSize, int maxSpeed) {
        final int minSpeed = maxSpeed - pointRegionSize;

        int[][] dissynchronizationFactor = new int[2 * (maxSpeed + 1)][2 * (maxSpeed + 1)];
        for (int i = point.y - maxSpeed; i < point.y + maxSpeed; i++) {
            for (int j = point.x - maxSpeed; j < point.x + maxSpeed; j++) {
                if (i < pointRegionSize || j < pointRegionSize || i > data.getRows() - pointRegionSize || j > data.getCols() - pointRegionSize) {
                    // если проверяемая точка стоит на границе изображения
                    continue;
                }

                boolean inRegion = inCircle(point.x, point.y, j, i, maxSpeed);
                boolean inPointRegion = inCircle(point.x, point.y, j, i, minSpeed);
                if (!inRegion || inPointRegion) {
                    // пропускаем, если проверяемая точка попала за пределы максимальной или минимальной скорости или за пределы контура
                    continue;
                }

                if (circuit[i][j] == 255) {
                    // если вышли за контур
                    continue;
                }

                Point currentPoint = new Point(j, i);
                for (int k = 0; k < data.getImagesList().size() - 1; k++) {
                    int[][] img1 = data.getImagesList().get(k);
                    int regionSum1 = getRegionSum(img1, point, pointRegionSize);

                    int[][] img2 = data.getImagesList().get(k + 1);
                    int regionSum2 = getRegionSum(img2, currentPoint, pointRegionSize);
                    dissynchronizationFactor[i - point.y + maxSpeed][j - point.x + maxSpeed] += Math.abs(regionSum1 - regionSum2);
                }
            }
        }

        BmpHelper.writeBmp("data/dissynchronizationFactor.bmp", dissynchronizationFactor);
        MatrixHelper.writeMatrix("data/dissynchronizationFactor.txt", dissynchronizationFactor);

        return dissynchronizationFactor;
    }

    private int getRegionSum(int[][] image, Point point, int radius) {
        int sum = 0;
        for (int i = point.y - radius; i < point.y + radius; i++) {
            for (int j = point.x - radius; j < point.x + radius; j++) {
                boolean inRegion = Math.sqrt(Math.pow(point.y - i, 2) + Math.pow(point.x - j, 2)) < radius;
                if (inRegion) {
                    continue;
                }

                sum += image[i][j];
            }
        }
        return sum;
    }

    private Distances findBorderDistance(int x, int y) {
        if (circuit[y][x] != 0) {
            throw new RuntimeException("error");
        }

        int tempY;
        int tempX;

        tempY = y;
        do {
            tempY--;
        } while (tempY >= 0 && circuit[tempY][x] != 255);
        int distanceTop = Math.abs(y - tempY);

        tempX = x;
        do {
            tempX++;
        } while (tempX < data.getCols() && circuit[y][tempX] != 255);
        int distanceRight = Math.abs(x - tempX);

        tempY = y;
        do {
            tempY++;
        } while (tempY < data.getRows() && circuit[tempY][x] != 255);
        int distanceBottom = Math.abs(y - tempY);

        tempX = x;
        do {
            tempX--;
        } while (tempX >= 0 && circuit[y][tempX] != 255);
        int distanceLeft = Math.abs(x - tempX);

        tempY = y;
        tempX = x;
        do {
            tempY--;
            tempX--;
        } while (tempY >= 0 && tempX >= 0 && circuit[tempY][tempX] != 255);
        double distanceTopLeft = Math.sqrt(Math.pow(y - tempY, 2) + Math.pow(x - tempX, 2));

        tempY = y;
        tempX = x;
        do {
            tempY--;
            tempX++;
        } while (tempY >= 0 && tempX < data.getCols() && circuit[tempY][tempX] != 255);
        double distanceTopRight = Math.sqrt(Math.pow(y - tempY, 2) + Math.pow(x - tempX, 2));

        tempY = y;
        tempX = x;
        do {
            tempY++;
            tempX--;
        } while (tempY < data.getRows() && tempX >= 0 && circuit[tempY][tempX] != 255);
        double distanceBottomLeft = Math.sqrt(Math.pow(y - tempY, 2) + Math.pow(x - tempX, 2));

        tempY = y;
        tempX = x;
        do {
            tempY++;
            tempX++;
        } while (tempY < data.getRows() && tempX < data.getCols() && circuit[tempY][tempX] != 255);
        double distanceBottomRight = Math.sqrt(Math.pow(y - tempY, 2) + Math.pow(x - tempX, 2));

        return new Distances(distanceTop, distanceBottom, distanceRight, distanceLeft, distanceTopRight, distanceTopLeft, distanceBottomRight, distanceBottomLeft);
    }


    private static Images loadData(final String inputFolder) {
        final Images result = new Images();
        for (int i = 0; i < 300; i++) {
            int[][] bmp = BmpHelper.readBmp(inputFolder + "background_" + i + ".bmp");
            result.add(bmp);
        }
        return result;
    }

    public static void main(String[] args) {
        Images images = loadData("data/backgroundSelector_v2/");
        int[][] circuit = BmpHelper.readBmp("data/backgroundSelector_v2/circuit-image.bmp");
        VectorSelector2 selector = new VectorSelector2(images, circuit);
        selector.process();
    }

    private static final class Distances {
        final Pair<Direction, Double> top;
        final Pair<Direction, Double> bottom;
        final Pair<Direction, Double> right;
        final Pair<Direction, Double> left;

        final Pair<Direction, Double> topRight;
        final Pair<Direction, Double> topLeft;
        final Pair<Direction, Double> bottomRight;
        final Pair<Direction, Double> bottomLeft;

        public Distances(int top, int bottom, int right, int left, double topRight, double topLeft, double bottomRight, double bottomLeft) {
            this.top = new Pair<>(Direction.TOP, (double) top);
            this.bottom = new Pair<>(Direction.BOTTOM, (double) bottom);
            this.right = new Pair<>(Direction.RIGHT, (double) right);
            this.left = new Pair<>(Direction.LEFT, (double) left);
            this.topRight = new Pair<>(Direction.TOP_RIGHT, topRight);
            this.topLeft = new Pair<>(Direction.TOP_LEFT, topLeft);
            this.bottomRight = new Pair<>(Direction.BOTTOM_RIGHT, bottomRight);
            this.bottomLeft = new Pair<>(Direction.BOTTOM_LEFT, bottomLeft);
        }

        public Double getMinDirectionValue() {
            List<Pair<Direction, Double>> result = new ArrayList<>();
            result.add(top);
            result.add(right);
            result.add(left);
            result.add(bottom);
            result.add(bottomRight);
            result.add(bottomLeft);
            result.add(topLeft);
            result.add(topRight);
            result.sort(Comparator.comparing(Pair::getValue));
            return result.get(0).getValue();
        }

        Double getTop() {
            return top.getValue();
        }

        Double getBottom() {
            return bottom.getValue();
        }

        Double getRight() {
            return right.getValue();
        }

        Double getLeft() {
            return left.getValue();
        }

        Double getTopRight() {
            return topRight.getValue();
        }

        Double getTopLeft() {
            return topLeft.getValue();
        }

        Double getBottomRight() {
            return bottomRight.getValue();
        }

        Double getBottomLeft() {
            return bottomLeft.getValue();
        }
    }

    private enum Direction {
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

        private final Function<Point, Point> nextPointFunction;
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
    }

}

package blood_speed.step;

import blood_speed.helper.BmpHelper;
import blood_speed.step.data.Images;
import blood_speed.step.data.Point;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings("SameParameterValue")
public class VectorSelector extends Step<Images> {
    private final Images data;
    private final int[][] circuit;

    public VectorSelector(Images data, int[][] circuit) {
        this.data = data;
        this.circuit = circuit;
    }

    @Override
    public Images process() {
        int[][] image1 = data.getImagesList().get(0);
        int[][] image2 = data.getImagesList().get(1);

//        Point nextPoint = new Point(47, 152);
        Point nextPoint = new Point(36, 207);
        Direction nextDirection = Direction.TOP;

        int n = 0;
        List<Point> middles = new ArrayList<>();
        while (nextDirection != null) {
            try {
                middles.add(nextPoint);
                final Point currentPoint = nextPoint;
                final Direction currentDirection = nextDirection;

                Direction[] towardsDirections = currentDirection.getTowardsDirections();

                nextDirection = null;
                nextPoint = null;

                double minDifferent = 0;
                for (Direction d : towardsDirections) {
                    Point oneOfNextPoint = d.nextPointFunction.apply(currentPoint);
                    if (circuit[oneOfNextPoint.y][oneOfNextPoint.x] != 0) {
                        continue;
                    }
                    Distances borderDistance = findBorderDistance(oneOfNextPoint.x, oneOfNextPoint.y);
//                    double currentDiff = d.perpendicularDiffFunction.apply(borderDistance);
                    double minDirection = borderDistance.getMinDirectionValue();
                    if (minDirection > minDifferent && !middles.contains(oneOfNextPoint) ) {
                        minDifferent = minDirection;
                        nextDirection = d;
                        nextPoint = oneOfNextPoint;
                    }
                }
            } catch (RuntimeException e) {
                break;
            }

            if (n > 300) {
                break;
            }
            n++;

        }


        int[][] trackImage = new int[data.getRows()][data.getCols()];
        for (int i = 0; i < data.getRows(); i++) {
            for (int j = 0; j < data.getCols(); j++) {
                if (circuit[i][j] == 0) {
                    trackImage[i][j] = 127;

                } else {
                    trackImage[i][j] = 255;
                }
            }
        }
        int currentColor = 0;
        for (Point p : middles) {
            trackImage[p.y][p.x] = currentColor;
            currentColor += 20;
            if (currentColor >254) {
                currentColor = 0;
            }
        }

        BmpHelper.writeBmp("data/test-result5-2.bmp", trackImage);
        return null;

//
//        List<Point> middles = new ArrayList<>();
//        int prevValue = circuit[0][0];
//
//        double dist = Math.sqrt(2);
//        List<Point> pointsToView = new ArrayList<>();
//        pointsToView.add(new Point(36, 207));
//        int currentPointIndex = 0;
//        while (currentPointIndex < pointsToView.size()) {
//            Point point = pointsToView.get(currentPointIndex);
//            int i = point.y;
//            int j = point.x;
//            Distances borderDistance = findBorderDistance(j, i);
//            if ((Math.abs(borderDistance.getTop() - borderDistance.getBottom()) <= 1
//                    || Math.abs(borderDistance.getRight() - borderDistance.getLeft()) <= 1
//                    || Math.abs(borderDistance.getTopRight() - borderDistance.getBottomLeft()) <= 1 * dist
//                    || Math.abs(borderDistance.getTopLeft() - borderDistance.getBottomRight()) <= 1 * dist)) {
//                middles.add(new Point(j, i));
//            }
//
//            if ((Math.abs(borderDistance.getTop() - borderDistance.getBottom()) <= 4
//                    || Math.abs(borderDistance.getRight() - borderDistance.getLeft()) <= 4
//                    || Math.abs(borderDistance.getTopRight() - borderDistance.getBottomLeft()) <= 4 * dist
//                    || Math.abs(borderDistance.getTopLeft() - borderDistance.getBottomRight()) <= 4 * dist)) {
//
//                Direction[] maxDirections = borderDistance.getMinDirectionValue();
//                for (Direction d : maxDirections) {
//                    Point apply = d.nextPointFunction.apply(point);
//                    if (!pointsToView.contains(apply)) {
//                        pointsToView.add(d.nextPointFunction.apply(point));
//                    }
//                }
//
//            }
//            currentPointIndex++;
//        }
//
//        int[][] trackImage = new int[data.getRows()][data.getCols()];
//        for (Point p : middles) {
//            trackImage[p.y][p.x] = 255;
//        }
//
//        BmpHelper.writeBmp("data/test-result5.bmp", trackImage);
//        return null;
    }

//
//    public Images process2() {
//        int[][] image1 = data.getImagesList().get(0);
//        int[][] image2 = data.getImagesList().get(1);
//
//        List<Point> middles = new ArrayList<>();
//        int prevValue = circuit[0][0];
//
//        for (int i = 0; i < data.getRows(); i++) {
//            for (int j = 0; j < data.getCols(); j++) {
//                if (circuit[i][j] == 0) {
//                    Distances borderDistance = findBorderDistance(j, i);
//                    double dist = Math.sqrt(2);
////                    if ((Math.abs(borderDistance.top - borderDistance.bottom) <= 1
////                            || Math.abs(borderDistance.right - borderDistance.left) <= 1
////                            || Math.abs(borderDistance.topRight - borderDistance.bottomLeft) <= dist
////                            || Math.abs(borderDistance.topLeft - borderDistance.bottomRight) <= dist)
////                            && borderDistance.top != 1
////                            && borderDistance.bottom != 1
////                            && borderDistance.left != 1
////                            && borderDistance.right != 1
////                            && borderDistance.bottomRight > dist
////                            && borderDistance.bottomLeft > dist
////                            && borderDistance.topRight > dist
////                            && borderDistance.topLeft > dist
////                            )
////                    {
////                        middles.add(new Point(j, i));
////                    }
//                    if (i == 60 && j == 28) {
//                        System.err.println("");
//                    }
//                    if (!(borderDistance.top != 1
//                            && borderDistance.bottom != 1
//                            && borderDistance.left != 1
//                            && borderDistance.right != 1
//                            && borderDistance.bottomRight > dist
//                            && borderDistance.bottomLeft > dist
//                            && borderDistance.topRight > dist
//                            && borderDistance.topLeft > dist)) {
//                        continue;
//                    }
//                    if (Math.abs(borderDistance.top - borderDistance.bottom) <= 1
//                            || Math.abs(borderDistance.left - borderDistance.right) <= 1
//                            || Math.abs(borderDistance.topRight - borderDistance.bottomLeft) <= dist
//                            || Math.abs(borderDistance.topLeft - borderDistance.bottomRight) <= dist
//                            ) {
//                        middles.add(new Point(j, i));
//                    }
//
//                    if (Math.abs(borderDistance.top - borderDistance.bottom) <= 1
//                            && (borderDistance.top + borderDistance.bottom) * 2 < borderDistance.left + borderDistance.right
//                            || Math.abs(borderDistance.left - borderDistance.right) <= 1
//                            && (borderDistance.left + borderDistance.right) * 2 < borderDistance.top + borderDistance.bottom
//                            || Math.abs(borderDistance.topRight - borderDistance.bottomLeft) <= dist
//                            && (borderDistance.topRight + borderDistance.bottomLeft) * 2 < borderDistance.topLeft + borderDistance.bottomRight
//                            || Math.abs(borderDistance.topLeft - borderDistance.bottomRight) <= dist
//                            && (borderDistance.topLeft + borderDistance.bottomRight) * 2 < borderDistance.bottomLeft + borderDistance.topRight
//                            ) {
////                        middles.add(new Point(j, i));
//                    } else {
//                        int distanceTopBottom = borderDistance.top + borderDistance.bottom;
//                        int distanceLeftRight = borderDistance.left + borderDistance.right;
//                        double distanceDiagonal1 = borderDistance.topRight + borderDistance.bottomLeft;
//                        double distanceDiagonal2 = borderDistance.topLeft + borderDistance.bottomRight;
//                        if ((Math.abs(borderDistance.top - borderDistance.bottom) <= 3
//                                || Math.abs(borderDistance.right - borderDistance.left) <= 3
//                                || Math.abs(borderDistance.topRight - borderDistance.bottomLeft) <= 3 * dist
//                                || Math.abs(borderDistance.topLeft - borderDistance.bottomRight) <= 3 * dist)
//                                && Math.abs(distanceLeftRight - distanceTopBottom) < 10
//                                && Math.abs(distanceDiagonal1 - distanceDiagonal2) < 10 * dist) {
////                            middles.add(new Point(j, i));
//                        }
//                    }
//
//                }
//            }
//        }
//
//        int[][] trackImage = new int[data.getRows()][data.getCols()];
//        for (Point p : middles) {
//            trackImage[p.y][p.x] = 255;
//        }
//
//        // фильтрация
//        for (Point p : middles) {
//
//        }
//
//        BmpHelper.writeBmp("data/test-result4.bmp", trackImage);
//        return null;
//    }

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
        for (int i = 0; i < 100; i++) {
            int[][] bmp = BmpHelper.readBmpColors(inputFolder + "background_" + i + ".bmp");
            result.add(bmp);
        }
        return result;
    }

    public static void main(String[] args) {
        Images images = loadData("data/backgroundSelector/");
        int[][] circuit = BmpHelper.readBmp("data/backgroundSelector/00_circuit.bmp");
        VectorSelector selector = new VectorSelector(images, circuit);
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
                d -> (Math.abs(d.getLeft() - d.getRight()) + 0.1* Math.abs(d.getTopRight() - d.getBottomLeft()) + 0.1* Math.abs(d.getTopLeft() - d.getBottomLeft())) ),
        LEFT(p -> new Point(p.x - 1, p.y),
                d -> Math.abs(d.getTop() - d.getBottom()),
                d -> (Math.abs(d.getTop() - d.getBottom()) + 0.1* Math.abs(d.getTopRight() - d.getBottomLeft()) + 0.1* Math.abs(d.getTopLeft() - d.getBottomRight())) ),
        RIGHT(p -> new Point(p.x + 1, p.y),
                d -> Math.abs(d.getTop() - d.getBottom()),
                d -> (Math.abs(d.getTop() - d.getBottom()) + 0.1* Math.abs(d.getTopRight() - d.getBottomLeft()) + 0.1* Math.abs(d.getTopLeft() - d.getBottomRight())) ),
        BOTTOM(p -> new Point(p.x, p.y + 1),
                d -> Math.abs(d.getLeft() - d.getRight()),
                d -> (Math.abs(d.getLeft() - d.getRight()) + 0.1* Math.abs(d.getTopRight() - d.getBottomLeft()) + 0.1* Math.abs(d.getTopLeft() - d.getBottomLeft())) ),
        TOP_RIGHT(p -> new Point(p.x + 1, p.y - 1),
                d -> Math.abs(d.getTopLeft() - d.getBottomRight()),
                d -> (Math.abs(d.getTopLeft() - d.getBottomRight()) + 0.1* Math.abs(d.getLeft() - d.getRight()) + 0.1* Math.abs(d.getBottom() - d.getTop())) ),
        TOP_LEFT(p -> new Point(p.x - 1, p.y - 1),
                d -> Math.abs(d.getTopRight() - d.getBottomLeft()),
                d -> (Math.abs(d.getTopRight() - d.getBottomLeft()) + 0.1* Math.abs(d.getLeft() - d.getRight()) + 0.1* Math.abs(d.getBottom() - d.getTop())) ),
        BOTTOM_RIGHT(p -> new Point(p.x + 1, p.y + 1),
                d -> Math.abs(d.getBottomLeft() - d.getTopRight()),
                d -> (Math.abs(d.getTopRight() - d.getBottomLeft()) + 0.1* Math.abs(d.getLeft() - d.getRight()) + 0.1* Math.abs(d.getBottom() - d.getTop())) ),
        BOTTOM_LEFT(p -> new Point(p.x - 1, p.y + 1),
                d -> Math.abs(d.getTopLeft() - d.getBottomRight()),
                d -> (Math.abs(d.getTopLeft() - d.getBottomRight()) + 0.1* Math.abs(d.getLeft() - d.getRight()) + 0.1* Math.abs(d.getBottom() - d.getTop())) );

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

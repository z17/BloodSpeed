package blood_speed.step;

import blood_speed.helper.BmpHelper;
import blood_speed.helper.FunctionHelper;
import blood_speed.helper.MathHelper;
import blood_speed.helper.MatrixHelper;
import blood_speed.step.data.Images;
import blood_speed.step.data.LineSegment;
import blood_speed.step.data.Point;
import blood_speed.step.util.Direction;
import blood_speed.step.util.Distances;

import java.util.*;

/**
 * Класс для выделения центральной линии капилляра
 */
@SuppressWarnings({"SameParameterValue"})
public class MiddleLineSelector extends Step<List<Point>> {
    private final Point start;
    private final Images data;
    private final int[][] contour;
    private final int[][] sumMatrix;
    private final int[][] sumImage;
    private final String outputPrefix;
    private final int regionSize;
    private final int maxSpeed;
    private final int angleLimit;

    private final static String MIDDLE_POINTS_IMAGE_FILENAME = "middle-points.bmp";
    private final static String MIDDLE_FULL_POINTS_IMAGE_FILENAME = "middle-full-points.bmp";
    public final static String MIDDLE_FULL_POINTS_POSITION_FILENAME = "middle-full-points.txt";

    public MiddleLineSelector(Point start, Images data, int[][] contour, int sumMatrix[][], int sumImage[][], String outputFolder, final String outputPrefix, int regionSize, int maxSpeed, int angleLimit) {
        this.start = start;
        this.data = data;
        this.contour = contour;
        this.sumMatrix = sumMatrix;
        this.sumImage = sumImage;
        this.regionSize = regionSize;
        this.maxSpeed = maxSpeed;
        this.angleLimit = angleLimit;
        FunctionHelper.checkOutputFolders(outputFolder);
        this.outputPrefix = outputFolder + "/" + outputPrefix + "_";
    }

    public static void main(String[] args) {
        Images images = BackgroundSelector.loadOutputData("data/tests/test2/backgroundSelector/");

        // контур
        int[][] contour = BmpHelper.readBmp("data/tests/test2/backgroundSelector/contour-image-photoshop.bmp");
//        int[][] contour = BmpHelper.readBmp("data/backgroundSelector_v2/circuit-image.bmp");

        // изображение суммы
        int[][] sumMatrix = MatrixHelper.readMatrix("data/tests/test2/backgroundSelector/sum.txt");
        int[][] sumImage = BmpHelper.readBmp("data/tests/test2/backgroundSelector/sum-image.bmp");

        // выбираем стартовую точку
        final Point startPoint = new Point(51, 200);
//        final Point start = new Point(47, 140);

        MiddleLineSelector selector = new MiddleLineSelector(
                startPoint,
                images,
                contour,
                sumMatrix,
                sumImage,
                "data/tests/test2/middle-line/",
                "v1",
                3,
                15,
                30);
        selector.process();
    }

    @Override
    public List<Point> process() {
        final List<Point> centralPoints = getCentralPoints(start, regionSize, maxSpeed);
        drawTrack(centralPoints, "middle_points1.bmp");

        List<Point> neighboringPoints = getNeighboringPoints(centralPoints);
        drawTrack(neighboringPoints, "middle_points2.bmp");

        List<Point> refinedPoints = refinePoints(neighboringPoints, 3);
        drawTrack(refinedPoints, "middle_points3.bmp");
        List<Point> result = refinePoints(refinedPoints, 1);
        drawTrack(result, "middle_points4.bmp");


        List<Point> result2 = getNeighboringPoints(result);
        drawTrack(result2, "middle_points5.bmp");

        List<Point> result3= refinePoints(result2, 3);
        drawTrack(result3, "middle_points6.bmp");
        List<Point> result4 = refinePoints(result3, 1);
        drawTrack(result4, "middle_points7.bmp");

        FunctionHelper.writePointsList(outputPrefix + MIDDLE_FULL_POINTS_POSITION_FILENAME, result4);
        return result4;
    }

    private List<Point> refinePoints(final List<Point> points, final double distance) {
        List<Point> result = new ArrayList<>();

        int currentPointIndex = 0;

        int currentSegmentEndIndex = 1;
        int currentSegmentStartIndex = 0;
        Point currentPoint = points.get(currentPointIndex);
        result.add(currentPoint);

        while (currentSegmentEndIndex != points.size() - 1) {
            Point currentSegmentStartPoint = currentPoint;
            Point currentSegmentEndPoint = points.get(currentSegmentEndIndex);

            Point nextPointCandidate;
            while (true) {
                LineSegment segment = new LineSegment(currentSegmentStartPoint, currentSegmentEndPoint);
                nextPointCandidate = MathHelper.getInterSectionPointWithCircleAndSegment(segment, currentPoint, distance);
                if (nextPointCandidate != null) {
                    break;
                }

                currentSegmentStartIndex++;
                currentSegmentEndIndex++;
                currentSegmentStartPoint = points.get(currentSegmentStartIndex);
                if (currentSegmentEndIndex == points.size() - 1) {
                    break;
                }
                currentSegmentEndPoint = points.get(currentSegmentEndIndex);
            }

            if (nextPointCandidate != null) {
                currentPoint = nextPointCandidate;
                result.add(currentPoint);
            }
        }

        return result;

    }

    private List<Point> getNeighboringPoints(final List<Point> centralPoints) {
        List<Point> resultPoints = new ArrayList<>();
        for (int i = 0; i < centralPoints.size() - 1; i++) {
            final Point startPoint = centralPoints.get(i);
            final Point finishPoint = centralPoints.get(i + 1);

            Point currentPoint = startPoint;
            boolean flagStop = false;
            while (!flagStop) {
                Direction direction = Direction.getByPoints(currentPoint, finishPoint);
                Direction[] towardsDirections = direction.getTowardsDirections();

                double minDifferent = 0;
                Point nextPoint = null;
                for (Direction d : towardsDirections) {
                    Point oneOfNextPoint = d.nextPointFunction.apply(currentPoint);
                    if (oneOfNextPoint.equals(finishPoint)) {
                        flagStop = true;
                    }

                    if (!inContour(oneOfNextPoint)) {
                        continue;
                    }

                    Distances borderDistance = findBorderDistance(oneOfNextPoint);
                    double minDirection = borderDistance.getMinDirectionValue();
                    if (minDirection > minDifferent && !resultPoints.contains(oneOfNextPoint)) {
                        minDifferent = minDirection;
//                    nextDirection = d;
                        nextPoint = oneOfNextPoint;
                    }
                }
                if (nextPoint == null) {
                    break;
                }
                currentPoint = nextPoint;
                resultPoints.add(currentPoint);
            }
        }

        return resultPoints;
    }

    private Point getBestMiddlePoint(final Point fistPoint, final Point secondPoint) {
        // добавляем промежуточную точку, выбирая её с радиусом 1/2 расстояния между текуей и слуд
        double halfDistance = MathHelper.distance(fistPoint, secondPoint) / 2;
        Set<Point> halfCirclePoints = getCirclePoints(fistPoint, halfDistance);
        List<Point> halfCandidates = getCandidates(halfCirclePoints, MathHelper.middlePoint(fistPoint, secondPoint), halfDistance, 25);
        return choiceBestPoint(halfCandidates);
    }

    private List<Point> getCentralPoints(final Point startPoint, final int regionSize, final int maxSpeed) {
        final List<Point> points = new ArrayList<>();
        int n = 0;

        Point currentPoint = startPoint;
        while (true) {
            int[][] dissynchronizationFactor = findDissynchronizationFactor(currentPoint, regionSize, maxSpeed);
            Point minDissynchronizationPoint = getMinDissynchronizationPoint(dissynchronizationFactor, currentPoint, maxSpeed);

            // получаем точки окружности, с центром в текущей точек и радиусом = расстояние от текущей до минимума десинхронизации
            double r = MathHelper.distance(minDissynchronizationPoint, currentPoint);

            Set<Point> circle = getCirclePoints(currentPoint, r);

            List<Point> candidates = getCandidates(circle, minDissynchronizationPoint, r, angleLimit);


            Point nextPoint = choiceBestPoint(candidates);

            if (nextPoint == null) {
                break;
            }

            Direction nextDirection = Direction.getByPoints(currentPoint, nextPoint);
            System.err.println(nextDirection);
            points.add(currentPoint);

//             добавляем промежуточную точку, выбирая её с радиусом 1/2 расстояния между текуей и след
            Point nextHalfPoint = getBestMiddlePoint(currentPoint, nextPoint);

            if (nextHalfPoint != null) {
                points.add(nextHalfPoint);
            }


            currentPoint = nextPoint;
//            currentDirection = nextDirection;
            // todo: придумать как ограничить поиск
            if (n > 26) {
                break;
            }
            n++;
        }

        return points;
    }

    private Point getMinDissynchronizationPoint(final int[][] dissynchronizationFactor, final Point currentPoint, final int maxSpeed) {
        int minFactor = Integer.MAX_VALUE;
        int directionX = 0;
        int directionY = 0;
        for (int i = 0; i < dissynchronizationFactor.length; i++) {
            for (int j = 0; j < dissynchronizationFactor[i].length; j++) {
                if (dissynchronizationFactor[i][j] == 0) {
                    continue;
                }

                if (dissynchronizationFactor[i][j] < minFactor) {
                    minFactor = dissynchronizationFactor[i][j];
                    directionX = j;
                    directionY = i;
                }
            }
        }

        return new Point(currentPoint.getIntX() + directionX - maxSpeed, currentPoint.getIntY() + directionY - maxSpeed);
    }

    private int[][] findDissynchronizationFactor(Point point, int pointRegionSize, int maxSpeed) {
        final int minSpeed = maxSpeed - pointRegionSize;

        // массив, где true означает что до этой точки региона можно дойти из исходной
        boolean checkRegion[][] = new boolean[2 * maxSpeed + 1][2 * maxSpeed + 1];
        List<Point> stack = new ArrayList<>();
        stack.add(point);
        for (int i = 0; i < stack.size(); i++) {
            Point currentPoint = stack.get(i);
            for (Direction d : Direction.values()) {
                Point candidate = d.nextPointFunction.apply(currentPoint);
                if (inContour(candidate)
                        && candidate.getIntY() >= point.getIntY() - maxSpeed
                        && candidate.getIntY() <= point.getIntY() + maxSpeed
                        && candidate.getIntX() >= point.getIntX() - maxSpeed
                        && candidate.getIntX() <= point.getIntX() + maxSpeed
                        && !stack.contains(candidate)) {
                    stack.add(candidate);
                    checkRegion[candidate.getIntY() - point.getIntY() + maxSpeed][candidate.getIntX() - point.getIntX() + maxSpeed] = true;
                }
            }
        }

        int[][] dissynchronizationFactor = new int[2 * maxSpeed + 1][2 * maxSpeed + 1];
        for (int i = point.getIntY() - maxSpeed; i <= point.getIntY() + maxSpeed; i++) {
            for (int j = point.getIntX() - maxSpeed; j <= point.getIntX() + maxSpeed; j++) {
                if (i <= pointRegionSize || j <= pointRegionSize || i >= data.getRows() - pointRegionSize - 1 || j >= data.getCols() - pointRegionSize - 1) {
                    // если проверяемая точка стоит на границе изображения
                    continue;
                }

                if (!checkRegion[i - point.getIntY() + maxSpeed][j - point.getIntX() + maxSpeed]) {
                    continue;
                }

                boolean inRegion = MathHelper.inCircle(point.getIntX(), point.getIntY(), j, i, maxSpeed);
                boolean inPointRegion = MathHelper.inCircle(point.getIntX(), point.getIntY(), j, i, minSpeed);
                if (!inRegion || inPointRegion) {
                    // пропускаем, если проверяемая точка попала за пределы максимальной или минимальной скорости или за пределы контура
                    continue;
                }

                // если вышли за контур
                if (!inContour(j, i)) {
                    continue;
                }

                Point currentPoint = new Point(j, i);
                for (int k = 0; k < data.getImagesList().size() - 1; k++) {
                    int[][] img1 = data.getImagesList().get(k);
                    int regionSum1 = getRegionSum(img1, point, pointRegionSize);

                    int[][] img2 = data.getImagesList().get(k + 1);
                    int regionSum2 = getRegionSum(img2, currentPoint, pointRegionSize);
                    dissynchronizationFactor[i - point.getIntY() + maxSpeed][j - point.getIntX() + maxSpeed] += Math.abs(regionSum1 - regionSum2);
                }
            }
        }

//        BmpHelper.writeBmp("data/dissynchronizationFactor.bmp", dissynchronizationFactor);
//        MatrixHelper.writeMatrix("data/dissynchronizationFactor.txt", dissynchronizationFactor);

        return dissynchronizationFactor;
    }

    private int getRegionSum(int[][] image, Point point, int radius) {
        int sum = 0;
        for (int i = point.getIntY() - radius; i < point.getIntY() + radius; i++) {
            for (int j = point.getIntX() - radius; j < point.getIntX() + radius; j++) {
                boolean inRegion = Math.sqrt(Math.pow(point.getIntY() - i, 2) + Math.pow(point.getIntX() - j, 2)) < radius;
                if (inRegion) {
                    continue;
                }

                sum += image[i][j];
            }
        }
        return sum;
    }

    private Distances findBorderDistance(final Point p) {
        int x = p.getIntX();
        int y = p.getIntY();
        if (contour[y][x] != 0) {
            throw new RuntimeException("error");
        }

        int tempY;
        int tempX;

        tempY = y;
        do {
            tempY--;
        } while (tempY >= 0 && contour[tempY][x] != 255);
        int distanceTop = Math.abs(y - tempY);

        tempX = x;
        do {
            tempX++;
        } while (tempX < data.getCols() && contour[y][tempX] != 255);
        int distanceRight = Math.abs(x - tempX);

        tempY = y;
        do {
            tempY++;
        } while (tempY < data.getRows() && contour[tempY][x] != 255);
        int distanceBottom = Math.abs(y - tempY);

        tempX = x;
        do {
            tempX--;
        } while (tempX >= 0 && contour[y][tempX] != 255);
        int distanceLeft = Math.abs(x - tempX);

        tempY = y;
        tempX = x;
        do {
            tempY--;
            tempX--;
        } while (tempY >= 0 && tempX >= 0 && contour[tempY][tempX] != 255);
        double distanceTopLeft = Math.sqrt(Math.pow(y - tempY, 2) + Math.pow(x - tempX, 2));

        tempY = y;
        tempX = x;
        do {
            tempY--;
            tempX++;
        } while (tempY >= 0 && tempX < data.getCols() && contour[tempY][tempX] != 255);
        double distanceTopRight = Math.sqrt(Math.pow(y - tempY, 2) + Math.pow(x - tempX, 2));

        tempY = y;
        tempX = x;
        do {
            tempY++;
            tempX--;
        } while (tempY < data.getRows() && tempX >= 0 && contour[tempY][tempX] != 255);
        double distanceBottomLeft = Math.sqrt(Math.pow(y - tempY, 2) + Math.pow(x - tempX, 2));

        tempY = y;
        tempX = x;
        do {
            tempY++;
            tempX++;
        } while (tempY < data.getRows() && tempX < data.getCols() && contour[tempY][tempX] != 255);
        double distanceBottomRight = Math.sqrt(Math.pow(y - tempY, 2) + Math.pow(x - tempX, 2));

        return new Distances(distanceTop, distanceBottom, distanceRight, distanceLeft, distanceTopRight, distanceTopLeft, distanceBottomRight, distanceBottomLeft);
    }


    private Set<Point> getCirclePoints(final Point point, final double r) {
        Set<Point> circle = new HashSet<>();
        for (int j = (int) Math.round(point.getIntX() - r); j < point.getIntX() + r; j++) {
            double a = 1;
            double b = -2 * point.getIntY();
            double c = Math.pow(j, 2) - 2 * j * point.getIntX() + Math.pow(point.getIntX(), 2) + Math.pow(point.getIntY(), 2) - Math.pow(r, 2);
            double d = b * b - 4 * a * c;
            double y1 = (-b + Math.sqrt(d)) / (2 * a);
            double y2 = (-b - Math.sqrt(d)) / (2 * a);

            circle.add(new Point(j, (int) Math.round(y1)));
            circle.add(new Point(j, (int) Math.round(y2)));
        }

        for (int i = (int) Math.round(point.getIntY() - r); i < point.getIntY() + r; i++) {
            double a = 1;
            double b = -2 * point.getIntX();
            double c = Math.pow(i, 2) - 2 * i * point.getIntY() + Math.pow(point.getIntY(), 2) + Math.pow(point.getIntX(), 2) - Math.pow(r, 2);
            double d = b * b - 4 * a * c;
            double x1 = (-b + Math.sqrt(d)) / (2 * a);
            double x2 = (-b - Math.sqrt(d)) / (2 * a);

            circle.add(new Point((int) Math.round(x1), i));
            circle.add(new Point((int) Math.round(x2), i));
        }
        return circle;
    }

    private List<Point> getCandidates(final Collection<Point> circle, final Point point, final double r, double angleLimit) {
        List<Point> candidates = new ArrayList<>();
        for (Point c : circle) {
            double a = Math.sqrt(Math.pow(c.getIntX() - point.getIntX(), 2) + Math.pow(c.getIntY() - point.getIntY(), 2));
            double angle = Math.toDegrees(2 * Math.asin(a / 2 / r));
            if (angle > angleLimit || Double.isNaN(angle)) {
                continue;
            }
//                Direction tempDirection = Direction.getByPoints(currentPoint, c);
//                if (!Arrays.asList(currentDirection.getOppositeDirection()).contains(tempDirection)) {
            candidates.add(c);
//                }
        }
        return candidates;
    }


    private Point choiceBestPoint(final Collection<Point> candidates) {
        return choiceBestPointByMinValue(candidates);
    }

    private Point choiceBestPointByMinValue(Collection<Point> candidates) {
        Point nextPoint = null;

        int minSum = Integer.MAX_VALUE;
        for (Point oneOfNextPoint : candidates) {
            if (!inContour(oneOfNextPoint)) {
                continue;
            }

            if (minSum > sumMatrix[oneOfNextPoint.getIntY()][oneOfNextPoint.getIntX()]) {
                minSum = sumMatrix[oneOfNextPoint.getIntY()][oneOfNextPoint.getIntX()];
                nextPoint = oneOfNextPoint;
            }
        }
        return nextPoint;
    }

    private Point choiceBestPointByRadius(Collection<Point> candidates) {
        double minDifferent = 0;
        Point nextPoint = null;

        for (Point oneOfNextPoint : candidates) {
            if (!inContour(oneOfNextPoint)) {
                continue;
            }

            Distances borderDistance = findBorderDistance(oneOfNextPoint);
//          double currentDiff = d.perpendicularDiffFunction.apply(borderDistance);
            double minDirectionValue = borderDistance.getMinDirectionValue();
            if (minDirectionValue > minDifferent) {
                minDifferent = minDirectionValue;
                nextPoint = oneOfNextPoint;
            }
        }
        return nextPoint;
    }

    private boolean inContour(final Point point) {
        return inContour(point.getIntX(), point.getIntY());
    }

    private boolean inContour(final int x, final int y) {
        if (x >= 0 && y >= 0) {
            if (y < contour.length && x < contour[0].length) {
                return contour[y][x] == 0;
            }
        }
        return false;
    }

    private void drawTrack(List<Point> points, final String name) {
        FunctionHelper.drawPointsOnImage(points, outputPrefix + name, sumImage);
    }
}

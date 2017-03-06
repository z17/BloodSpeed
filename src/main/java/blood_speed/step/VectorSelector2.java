package blood_speed.step;

import blood_speed.helper.BmpHelper;
import blood_speed.helper.MathHelper;
import blood_speed.helper.MatrixHelper;
import blood_speed.step.data.Images;
import blood_speed.step.data.Point;
import blood_speed.step.util.Direction;
import blood_speed.step.util.Distances;

import java.util.*;

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
        Direction currentDirection = Direction.TOP;

        int n = 0;
        while (currentPoint != null) {

            circuit[currentPoint.y][currentPoint.x] = 127;

            int[][] dissynchronizationFactor = findDirection(currentPoint, regionSize, maxSpeed);
            Point minDissynchronizationPoint = getMinDissynchronizationPoint(dissynchronizationFactor, currentPoint, maxSpeed);

            // получаем точки окружности, с центром в текущей точек и радиусом = расстояние от текущей до минимума десинхронизации
            double r = MathHelper.distance(minDissynchronizationPoint, currentPoint);

            Set<Point> circle = getCirclePoints(currentPoint, r);

            List<Point> candidates = getCandidates(circle, minDissynchronizationPoint, r, 35);


            double minDifferent = 0;
            Point nextPoint = null;
            for (Point oneOfNextPoint : candidates) {
                if (circuit[oneOfNextPoint.y][oneOfNextPoint.x] != 0) {
                    continue;
                }

                Distances borderDistance = findBorderDistance(oneOfNextPoint.x, oneOfNextPoint.y);
//          double currentDiff = d.perpendicularDiffFunction.apply(borderDistance);
                double minDirection = borderDistance.getMinDirectionValue();
                if (minDirection > minDifferent) {
                    minDifferent = minDirection;
                    nextPoint = oneOfNextPoint;
                }
            }

            if (nextPoint == null) {
                break;
            }

            Direction nextDirection = Direction.getByPoints(currentPoint, nextPoint);

            System.err.println(nextDirection);


            // добавляем промежуточную точку, выбирая её с радиусом 1/2 расстояния между текуей и слуд
            double halfDistance = MathHelper.distance(currentPoint, nextPoint) / 2;
            Set<Point> halfCirclePoints = getCirclePoints(currentPoint, halfDistance);
            List<Point> halfCandidates = getCandidates(halfCirclePoints, MathHelper.middlePoint(currentPoint, nextPoint), r, 20);

            double minHalfDifferent = 0;
            Point nextHalfPoint = null;
            for (Point oneOfNextPoint : halfCandidates ) {
                if (circuit[oneOfNextPoint.y][oneOfNextPoint.x] != 0) {
                    continue;
                }

                Distances borderDistance = findBorderDistance(oneOfNextPoint.x, oneOfNextPoint.y);
//          double currentDiff = d.perpendicularDiffFunction.apply(borderDistance);
                double minDirection = borderDistance.getMinDirectionValue();
                if (minDirection > minHalfDifferent) {
                    minHalfDifferent = minDirection;
                    nextHalfPoint = oneOfNextPoint;
                }
            }
            if (nextHalfPoint != null) {
                circuit[nextHalfPoint.y][nextHalfPoint.x] = 50;
            }


            currentPoint = nextPoint;
            currentDirection = nextDirection;

            if (n > 21) {
                break;
            }
            n++;
        }


        BmpHelper.writeBmp("data/text.bmp", circuit);

        System.exit(1);

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
                if (i < pointRegionSize || j < pointRegionSize || i > data.getRows() - pointRegionSize - 1 || j > data.getCols() - pointRegionSize - 1) {
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


    private Set<Point> getCirclePoints(final  Point point, final  double r) {
        Set<Point> circle = new HashSet<>();
        for (int j = (int) Math.round(point.x - r); j < point.x + r; j++) {
            double a = 1;
            double b = -2 * point.y;
            double c = Math.pow(j, 2) - 2 * j * point.x + Math.pow(point.x, 2) + Math.pow(point.y, 2) - Math.pow(r, 2);
            double d = b * b - 4 * a * c;
            double y1 = (-b + Math.sqrt(d)) / (2 * a);
            double y2 = (-b - Math.sqrt(d)) / (2 * a);

            circle.add(new Point(j, (int) Math.round(y1)));
            circle.add(new Point(j, (int) Math.round(y2)));
        }

        for (int i = (int) Math.round(point.y - r); i < point.y + r; i++) {
            double a = 1;
            double b = -2 * point.x;
            double c = Math.pow(i, 2) - 2 * i * point.y + Math.pow(point.y, 2) + Math.pow(point.x, 2) - Math.pow(r, 2);
            double d = b * b - 4 * a * c;
            double x1 = (-b + Math.sqrt(d)) / (2 * a);
            double x2 = (-b - Math.sqrt(d)) / (2 * a);

            circle.add(new Point((int) Math.round(x1), i));
            circle.add(new Point((int) Math.round(x2), i));
        }
        return circle;
    }

    private List<Point> getCandidates(final Collection<Point> circle, final Point point, final  double r, double angleLimit) {
        List<Point> candidates = new ArrayList<>();
        for (Point c : circle) {
            double a = Math.sqrt(Math.pow(c.x - point.x, 2) + Math.pow(c.y - point.y, 2));
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
}

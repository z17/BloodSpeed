package blood_speed.step;

import blood_speed.helper.BmpHelper;
import blood_speed.helper.FunctionHelper;
import blood_speed.helper.MathHelper;
import blood_speed.helper.MatrixHelper;
import blood_speed.step.data.*;
import blood_speed.step.util.Direction;
import blood_speed.step.util.Distances;
import blood_speed.util.Pair;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.Collectors;

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
    private final String outputPointsName;
    private final int regionSize;
    private final double[][] mask;
    private final int maxSpeed;
    private final int angleLimit;
    private final int vectorBlurRadius;
    private final boolean fast;

    private final static String MIDDLE_POINTS_IMAGE_FILENAME = "middle-points%d.bmp";

    public MiddleLineSelector(Point start, Images data, int[][] contour, int sumMatrix[][], int sumImage[][], String outputFolder, final String outputPrefix, String outputPointsName, int regionSize, int maxSpeed, int angleLimit, int vectorBlurRadius, boolean fast) {
        this.start = start;
        this.data = data;
        this.contour = contour;
        this.sumMatrix = sumMatrix;
        this.sumImage = sumImage;
        this.outputPointsName = outputPointsName;
        this.regionSize = regionSize;
        this.mask = MathHelper.generateMask(regionSize * 2 + 1);
        this.maxSpeed = maxSpeed;
        this.angleLimit = angleLimit;
        this.vectorBlurRadius = vectorBlurRadius;
        this.fast = fast;
        FunctionHelper.checkOutputFolders(outputFolder);
        this.outputPrefix = outputFolder + "/" + outputPrefix;
    }

    @Override
    public List<Point> process() {
        System.out.println("Middle line started");

        /*
        // код для чтеия уже ранее посчитанного массива векторов
        double[][] xV = MatrixHelper.readDoubleMatrix(outputPrefix + "x-blur.txt");
        double[][] yV = MatrixHelper.readDoubleMatrix(outputPrefix + "y-blur.txt");
        final Point[][] vectors = new Point[data.getRows()][data.getCols()];

        for (int y = 0; y < data.getRows(); y++) {
            for (int x = 0; x < data.getCols(); x++) {
                vectors[y][x] = new Point(xV[y][x], yV[y][x]);
            }
        }
        */

        int numberOfFile = 0;
        List<Point> result;
        if (fast) {
            System.err.println("It is fast variant of algorithm. If you want more accuracy, but slow, change 'middle_fast' option in config file");
            result = getCentralPoints(start);
            drawTrack(result, String.format(MIDDLE_POINTS_IMAGE_FILENAME, ++numberOfFile));

            FunctionHelper.writePointsList(outputPrefix + outputPointsName, result);

            result = refinePoints(result);
            drawTrack(result, String.format(MIDDLE_POINTS_IMAGE_FILENAME, ++numberOfFile));

        } else {

            System.err.println("It is slow variant of algorithm. If you want fast middle line selector, change 'middle_fast' option in config file");
            Point[][] vectors = createVectorsMap();

            result = findPointsByPreparedVectors(start, vectors);
            drawTrack(result, String.format(MIDDLE_POINTS_IMAGE_FILENAME, ++numberOfFile));
        }

        result = refinePointsByLength(result, 3);
        drawTrack(result, String.format(MIDDLE_POINTS_IMAGE_FILENAME, ++numberOfFile));

        result = refinePoints(result);
        drawTrack(result, String.format(MIDDLE_POINTS_IMAGE_FILENAME, ++numberOfFile));

        result = refinePointsByLength(result, 2);
        drawTrack(result, String.format(MIDDLE_POINTS_IMAGE_FILENAME, ++numberOfFile));

        result = refinePointsByLength(result, 1);
        drawTrack(result, String.format(MIDDLE_POINTS_IMAGE_FILENAME, ++numberOfFile));

        FunctionHelper.writePointsList(outputPrefix + outputPointsName, result);
        System.out.println("Middle line complete");
        return result;
    }

    private List<Point> refinePoints(List<Point> points) {
        Set<Point> result = new LinkedHashSet<>();
        result.add(points.get(0));
        for (int i = 0; i < points.size(); i++) {
            Point a;
            Point b;
            if (i < 2) {
                a = points.get(0);
            } else {
                a = points.get(i - 2);
            }
            if (i >= points.size() - 2) {
                b = points.get(points.size() - 1);
            } else {
                b = points.get(i + 2);
            }

            Point middlePoint = new Point((a.getX() + b.getIntX()) / 2, (a.getY() + b.getY()) / 2);
            if (i < 2) {
                middlePoint = a;
            }

            if (i >= points.size() - 2) {
                middlePoint = b;
            }

            Set<Point> candidates = new HashSet<>();
            candidates.add(points.get(i));
            candidates.addAll(findCandidatesOnPerpendicular(a, b, middlePoint));

            result.add(choiceBestPoint(candidates));
        }
        result.add(points.get(points.size() - 1));
        return new ArrayList<>(result);
    }

    /**
     * Из ряда точек получаем другой ряд, точки которого стоян на расстоянии distance
     */
    private List<Point> refinePointsByLength(final List<Point> points, final double distance) {
        List<Point> result = new ArrayList<>();

        int currentPointIndex = 0;

        int currentSegmentEndIndex = 1;
        int currentSegmentStartIndex = 0;
        Point currentPoint = points.get(currentPointIndex);
        result.add(currentPoint);

        while (currentSegmentEndIndex != points.size()) {
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
                if (currentSegmentEndIndex >= points.size() - 1) {
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

    private Point getBestMiddlePoint(final Point fistPoint, final Point secondPoint) {
        // добавляем промежуточную точку, выбирая её с радиусом 1/2 расстояния между текуей и слуд
        double halfDistance = MathHelper.distance(fistPoint, secondPoint) / 2;
        Set<Point> halfCirclePoints = getCirclePoints(fistPoint, halfDistance);
        List<Point> halfCandidates = getCandidates(halfCirclePoints, MathHelper.middlePoint(fistPoint, secondPoint), halfDistance, 10);
        return choiceBestPoint(filterCandidatesByArea(halfCandidates, fistPoint));
    }

    /**
     * Рассчитывает карту векторой. В каждой ячейке координата конца вектора из данной точки
     */
    private Point[][] createVectorsMap() {
        System.err.println("Calculating vectors in parallel");

        List<ForkJoinTask<Pair<Integer, Point[]>>> tasks = new ArrayList<>();
        final ForkJoinPool executor = ForkJoinPool.commonPool();
        System.err.printf("Using %d threads%s", executor.getParallelism(), System.lineSeparator());

        for (int y = 0; y < data.getRows(); y++) {
            final int Y = y;
            tasks.add(executor.submit(() -> createVectorsLine(Y)));
        }

        final Point[][] vectors = new Point[data.getRows()][];
        for (ForkJoinTask<Pair<Integer, Point[]>> task : tasks) {
            Pair<Integer, Point[]> result = task.join();
            vectors[result.getKey()] = result.getValue();
        }

        splitVectorsToImage(vectors, "x", "y");

        Point[][] blurVectors = new Point[data.getRows()][data.getCols()];
        double[][] mask = MathHelper.generateMask(vectorBlurRadius * 2 + 1);
        for (int y = 0; y < data.getRows(); y++) {
            for (int x = 0; x < data.getCols(); x++) {
                if (!inContour(x, y)) {
                    continue;
                }

                double sumX = 0;
                double sumY = 0;
                double sumK = 0;

                for (int yBlur = y - vectorBlurRadius; yBlur <= y + vectorBlurRadius; yBlur++) {
                    for (int xBlur = x - vectorBlurRadius; xBlur <= x + vectorBlurRadius; xBlur++) {
                        if (!inContour(xBlur, yBlur)) {
                            continue;
                        }

                        double g = mask[yBlur - y + vectorBlurRadius][xBlur - x + vectorBlurRadius];
                        sumX += vectors[yBlur][xBlur].getX() * g;
                        sumY += vectors[yBlur][xBlur].getY() * g;
                        sumK += g;
                    }
                }

                blurVectors[y][x] = new Point(sumX / sumK, sumY / sumK);
            }
        }

        splitVectorsToImage(blurVectors, "x-blur", "y-blur");

        return blurVectors;
    }

    /**
     * Рессчитывает вектора строки y
     */
    private Pair<Integer, Point[]> createVectorsLine(final int y) {
        Point[] line = new Point[data.getCols()];
        for (int x = 0; x < data.getCols(); x++) {
            if (!inContour(x, y)) {
                continue;
            }

            Point currentPoint = new Point(x, y);
            final int[][] dissynchronizationFactor = findDissynchronizationFactor(currentPoint, false);
            List<Point> dissynchronizationPoints = getDissynchronizationPoints(dissynchronizationFactor, currentPoint);
            final Point minDissynchronizationPoint = getMinDissynchronizationPoint(dissynchronizationFactor, dissynchronizationPoints, currentPoint, maxSpeed);
            line[x] = minDissynchronizationPoint;
        }
        System.out.printf("%d / %d lines of image analyzed\n", y + 1, data.getRows());
        return new Pair<>(y, line);
    }

    private void splitVectorsToImage(final Point[][] points, final String xName, final String yName) {
        double[][] vectorsX = new double[data.getRows()][data.getCols()];
        double[][] vectorsY = new double[data.getRows()][data.getCols()];
        for (int y = 0; y < data.getRows(); y++) {
            for (int x = 0; x < data.getCols(); x++) {
                if (!inContour(x, y)) {
                    continue;
                }
                vectorsX[y][x] = points[y][x].getX();
                vectorsY[y][x] = points[y][x].getY();
            }
        }

        MatrixHelper.writeMatrix(outputPrefix + xName + ".txt", vectorsX);
        MatrixHelper.writeMatrix(outputPrefix + yName + ".txt", vectorsY);

        int[][] imageVectorsX = BmpHelper.transformToImage(vectorsX);
        int[][] imageVectorsY = BmpHelper.transformToImage(vectorsY);
        BmpHelper.writeBmp(outputPrefix + xName + ".bmp", imageVectorsX);
        BmpHelper.writeBmp(outputPrefix + yName + ".bmp", imageVectorsY);
    }

    private List<Point> findPointsByPreparedVectors(final Point startPoint, final Point[][] vectors) {
        int[][] visualise = MatrixHelper.copyMatrix(sumImage);
        final List<Point> points = new ArrayList<>();

        Point currentPoint = startPoint;
        int color = 0;
        int count = 0;
        while (true) {

            final Point minDissynchronizationPoint = vectors[currentPoint.getIntY()][currentPoint.getIntX()];
            drawLine(currentPoint, minDissynchronizationPoint, visualise, color);
            color += 80;
            if (color > 255) {
                color = 0;
            }

            // получаем точки окружности, с центром в текущей точек и радиусом = расстояние от текущей до минимума десинхронизации
            double r = MathHelper.distance(minDissynchronizationPoint, currentPoint);

            Set<Point> circle = getCirclePoints(currentPoint, r);

            final List<Point> candidates = getCandidates(circle, minDissynchronizationPoint, r, angleLimit);
            final List<Point> filtered = filterCandidatesByArea(candidates, currentPoint);
            final Point nextPoint = choiceBestPoint(filtered);

            count++;
            points.add(currentPoint);

            if (nextPoint == null) {
                break;
            }

            if (count > 100) {
                System.err.println("It found more than 100 points on first step. Is it error?");
                break;
            }

            // добавляем промежуточную точку, выбирая её с радиусом 1/2 расстояния между текуей и след
            Point nextHalfPoint = getBestMiddlePoint(currentPoint, nextPoint);
            if (nextHalfPoint != null) {
                points.add(nextHalfPoint);
            }

            currentPoint = nextPoint;
        }
        BmpHelper.writeBmp(outputPrefix + "vectors.bmp", visualise);
        return points;
    }

    private List<Point> getCentralPoints(final Point startPoint) {
        int[][] visualise = MatrixHelper.copyMatrix(sumImage);
        final List<Point> points = new ArrayList<>();

        Point currentPoint = startPoint;
        Direction currentDirection = null;
        int color = 0;
        int count = 0;
        while (true) {
            final int[][] dissynchronizationFactor = findDissynchronizationFactor(currentPoint, true);
            List<Point> dissynchronizationPoints = getDissynchronizationPoints(dissynchronizationFactor, currentPoint);

            dissynchronizationPoints = filterDissynchronizationPoints(dissynchronizationPoints, currentPoint, currentDirection);

            if (dissynchronizationPoints.size() == 0) {
                break;
            }

            final Point minDissynchronizationPoint = getMinDissynchronizationPoint(dissynchronizationFactor, dissynchronizationPoints, currentPoint, maxSpeed);
            drawLine(currentPoint, minDissynchronizationPoint, visualise, color);
            color += 80;
            if (color > 255) {
                color = 0;
            }

            // получаем точки окружности, с центром в текущей точек и радиусом = расстояние от текущей до минимума десинхронизации
            double r = MathHelper.distance(minDissynchronizationPoint, currentPoint);

            Set<Point> circle = getCirclePoints(currentPoint, r);

            final List<Point> candidates = getCandidates(circle, minDissynchronizationPoint, r, angleLimit);
            final List<Point> filteredCandidates = filterCandidates(candidates, dissynchronizationPoints);

            Point nextPoint = choiceBestPoint(filteredCandidates);

            count++;
            points.add(currentPoint);

            if (nextPoint == null) {
                break;
            }

            if (count > 100) {
                System.err.println("It found more than 100 points on first step. Is it error?");
                break;
            }

            // направление точки
            Direction nextDirection = Direction.getByPoints(currentPoint, nextPoint);

            // добавляем промежуточную точку, выбирая её с радиусом 1/2 расстояния между текуей и след
            Point nextHalfPoint = getBestMiddlePoint(currentPoint, nextPoint);

            if (nextHalfPoint != null) {
                points.add(nextHalfPoint);
            }

            currentPoint = nextPoint;
            currentDirection = nextDirection;
        }

        BmpHelper.writeBmp(outputPrefix + "vectors.bmp", visualise);
        return points;
    }

    private List<Point> filterDissynchronizationPoints(final List<Point> dissynchronizationPoints, final Point currentPoint, final Direction prevDirection) {

        if (prevDirection == null) {
            return dissynchronizationPoints;
        }

        return dissynchronizationPoints.stream()
                .filter(p -> {
                    Direction nextDirection = Direction.getByPoints(currentPoint, p);
                    Direction[] oppositeDirection = nextDirection.getOppositeDirection();
                    return !Arrays.asList(oppositeDirection).contains(prevDirection);
                }).collect(Collectors.toList());
    }

    /**
     * Фильтрует точки только на входящие в область
     */
    private List<Point> filterCandidates(final List<Point> candidates, final Collection<Point> dissynchronizationPoints) {
        return candidates.stream()
                .filter(dissynchronizationPoints::contains)
                .collect(Collectors.toList());
    }

    private List<Point> getDissynchronizationPoints(final int[][] dissynchronizationFactor, final Point currentPoint) {
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < dissynchronizationFactor.length; i++) {
            for (int j = 0; j < dissynchronizationFactor[i].length; j++) {
                if (dissynchronizationFactor[i][j] == 0) {
                    continue;
                }
                Point p = new Point(currentPoint.getIntX() + j - maxSpeed, currentPoint.getIntY() + i - maxSpeed);
                points.add(p);
            }
        }

        return points;
    }

    private void drawLine(final Point startPoint, final Point endPoint, final int[][] visualise, final int color) {
        final LineSegment segment = new LineSegment(startPoint, endPoint);

        double distance = MathHelper.distance(startPoint, endPoint);
        for (int i = 1; i < distance; i++) {
            List<Point> points = MathHelper.getInterSectionPointWithCircleAndLine(segment, startPoint, i);
            if (points == null) {
                throw new RuntimeException();
            }

            if (segment.isPointOnSegment(points.get(0))) {
                visualise[points.get(0).getIntY()][points.get(0).getIntX()] = color;
            } else {
                visualise[points.get(1).getIntY()][points.get(1).getIntX()] = color;
            }
        }
    }

    private Point getMinDissynchronizationPoint(final int[][] dissynchronizationFactor, List<Point> dissynchronizationPoints, Point currentPoint, final int maxSpeed) {
        // координаты начала diss factor
        final int x0 = currentPoint.getIntX() - maxSpeed;
        final int y0 = currentPoint.getIntY() - maxSpeed;

        Point minDissPoint = dissynchronizationPoints.get(0);
        int minFactor = dissynchronizationFactor[minDissPoint.getIntY() - y0][minDissPoint.getIntX() - x0];

        for (Point p : dissynchronizationPoints) {
            int curFactor = dissynchronizationFactor[p.getIntY() - y0][p.getIntX() - x0];
            if (curFactor < minFactor) {
                minFactor = curFactor;
                minDissPoint = p;
            }
        }
        return minDissPoint;
    }

    private List<Point> filterCandidatesByArea(final List<Point> candidates, final Point center) {
        boolean[][] regionAvailability = createRegionAvailabilityMap(center, true);
        return candidates.stream()
                .filter(p -> regionAvailability[p.getIntY() - center.getIntY() + maxSpeed][p.getIntX() - center.getIntX() + maxSpeed])
                .collect(Collectors.toList());
    }

    /**
     * Возвращает матрицу boolean, в которой true означает что до этой точки можно дойти из Point. сама точке Point считается что лежит в центре этой матрицы
     * При filterInContour = true, показывает можно ли пройти по капилляру. Если false, то назначение метода не очень понятно.
     */
    private boolean[][] createRegionAvailabilityMap(final Point point, final boolean filterInContour) {
        // массив, где true означает что до этой точки региона можно дойти из исходной
        boolean checkRegion[][] = new boolean[2 * maxSpeed + 1][2 * maxSpeed + 1];
        List<Point> stack = new ArrayList<>();
        Set<Point> pointsSet = new HashSet<>();
        stack.add(point);
        for (int i = 0; i < stack.size(); i++) {
            Point currentPoint = stack.get(i);
            for (Direction d : Direction.values()) {
                Point candidate = d.nextPointFunction.apply(currentPoint);
                if (MathHelper.distance(candidate, point) <= maxSpeed
                        && !pointsSet.contains(candidate)
                        && (!filterInContour || inContour(candidate))   // либо мы в контуре и фильтркем, либо не учитываем контур
                        && inImage(candidate)) {
                    stack.add(candidate);
                    pointsSet.add(candidate);
                    checkRegion[candidate.getIntY() - point.getIntY() + maxSpeed][candidate.getIntX() - point.getIntX() + maxSpeed] = true;
                }
            }
        }
        return checkRegion;
    }

    private int[][] findDissynchronizationFactor(final Point point, final boolean filterInContour) {
        // массив, где true означает что до этой точки региона можно дойти из исходной
        boolean checkRegion[][] = createRegionAvailabilityMap(point, filterInContour);

        int[][] dissynchronizationFactor = new int[2 * maxSpeed + 1][2 * maxSpeed + 1];
        for (int i = point.getIntY() - maxSpeed; i <= point.getIntY() + maxSpeed; i++) {
            for (int j = point.getIntX() - maxSpeed; j <= point.getIntX() + maxSpeed; j++) {
                if (i <= regionSize || j <= regionSize || i >= data.getRows() - regionSize - 1 || j >= data.getCols() - regionSize - 1) {
                    // если проверяемая точка стоит на границе изображения
                    continue;
                }

                if (!checkRegion[i - point.getIntY() + maxSpeed][j - point.getIntX() + maxSpeed]) {
                    continue;
                }

                boolean inRegion = MathHelper.inCircle(point.getIntX(), point.getIntY(), j, i, maxSpeed);
                if (!inRegion) {
                    // пропускаем, если проверяемая точка попала за пределы максимальной скорости
                    continue;
                }

                Point currentPoint = new Point(j, i);
                for (int k = 0; k < data.getImagesList().size() - 1; k++) {
                    int[][] img1 = data.getImagesList().get(k);
                    int regionSum1 = getRegionSum(img1, point);

                    int[][] img2 = data.getImagesList().get(k + 1);
                    int regionSum2 = getRegionSum(img2, currentPoint);
                    dissynchronizationFactor[i - point.getIntY() + maxSpeed][j - point.getIntX() + maxSpeed] += Math.abs(regionSum1 - regionSum2);
                }
            }
        }

        return dissynchronizationFactor;
    }


    private int getRegionSum(int[][] image, Point point) {
        int sum = 0;
        for (int i = point.getIntY() - regionSize; i <= point.getIntY() + regionSize; i++) {
            for (int j = point.getIntX() - regionSize; j <= point.getIntX() + regionSize; j++) {

                boolean inImage = MathHelper.pointInImage(j, i, image[0].length, image.length);
                boolean inRegion = MathHelper.inCircle(point.getIntX(), point.getIntY(), j, i, regionSize);
                if (!inRegion || !inImage) {
                    continue;
                }
                sum += image[i][j] * mask[i - point.getIntY() + regionSize][j - point.getIntX() + regionSize];
            }
        }
        return sum;
    }

    private Distances findBorderDistance(final Point p) {
        int x = p.getIntX();
        int y = p.getIntY();
        if (!inContour(x, y)) {
            throw new RuntimeException("error");
        }

        int tempY;
        int tempX;

        tempY = y;
        do {
            tempY--;
        } while (tempY >= 0 && inContour(x, tempY));
        int distanceTop = Math.abs(y - tempY);

        tempX = x;
        do {
            tempX++;
        } while (tempX < data.getCols() && inContour(tempX, y));
        int distanceRight = Math.abs(x - tempX);

        tempY = y;
        do {
            tempY++;
        } while (tempY < data.getRows() && inContour(x, tempY));
        int distanceBottom = Math.abs(y - tempY);

        tempX = x;
        do {
            tempX--;
        } while (tempX >= 0 && inContour(tempX, y));
        int distanceLeft = Math.abs(x - tempX);

        tempY = y;
        tempX = x;
        do {
            tempY--;
            tempX--;
        } while (tempY >= 0 && tempX >= 0 && inContour(tempX, tempY));
        double distanceTopLeft = Math.sqrt(Math.pow(y - tempY, 2) + Math.pow(x - tempX, 2));

        tempY = y;
        tempX = x;
        do {
            tempY--;
            tempX++;
        } while (tempY >= 0 && tempX < data.getCols() && inContour(tempX, tempY));
        double distanceTopRight = Math.sqrt(Math.pow(y - tempY, 2) + Math.pow(x - tempX, 2));

        tempY = y;
        tempX = x;
        do {
            tempY++;
            tempX--;
        } while (tempY < data.getRows() && tempX >= 0 && inContour(tempX, tempY));
        double distanceBottomLeft = Math.sqrt(Math.pow(y - tempY, 2) + Math.pow(x - tempX, 2));

        tempY = y;
        tempX = x;
        do {
            tempY++;
            tempX++;
        } while (tempY < data.getRows() && tempX < data.getCols() && inContour(tempX, tempY));
        double distanceBottomRight = Math.sqrt(Math.pow(y - tempY, 2) + Math.pow(x - tempX, 2));

        return new Distances(distanceTop, distanceBottom, distanceRight, distanceLeft, distanceTopRight, distanceTopLeft, distanceBottomRight, distanceBottomLeft);
    }


    /**
     * Ну и треш я написал. Возвращает некий набор точек, лежащих на окружности с центром в точке point и радиусом r
     */
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

    /**
     * Возвращаем набор точек из circle, для которых угол межу P, центром окружности и точками не больше angleLimit
     *
     * @param circle     набор точек на окружности
     * @param point      точка на окружности
     * @param r          радиус окружности
     * @param angleLimit лимит градусов
     */
    private List<Point> getCandidates(final Collection<Point> circle, final Point point, final double r, double angleLimit) {
        List<Point> candidates = new ArrayList<>();
        for (Point c : circle) {
            double a = Math.sqrt(Math.pow(c.getIntX() - point.getIntX(), 2) + Math.pow(c.getIntY() - point.getIntY(), 2));
            double angle = Math.toDegrees(2 * Math.asin(a / 2 / r));
            if (angle > angleLimit || Double.isNaN(angle)) {
                continue;
            }
            candidates.add(c);
        }
        return candidates;
    }


    private List<Point> findCandidatesOnPerpendicular(final Point startLinePoint, final Point endLinePoint, final Point point) {
        Line line = new Line(startLinePoint, endLinePoint);
        Line perpendicular = line.getPerpendicular(point);
        List<Point> candidates = new ArrayList<>();
        candidates.add(point);

        boolean inContourLeftLine = true;
        boolean inContourRightLine = true;

        for (int k = 1; k < 5; k++) {
            List<Point> pointsCandidates = MathHelper.getInterSectionPointWithCircleAndLine(perpendicular, point, k);
            if (pointsCandidates == null || pointsCandidates.size() != 2) {
                throw new RuntimeException("Unknown error");
            }
            Point a = pointsCandidates.get(0);
            Point b = pointsCandidates.get(0);

            // Жесть какая-то. Смотрим с какой стороны от прямой лежат полученные точки и проверяем, не выходили ли мы за контур
            if (MathHelper.getVectorDirection(endLinePoint, a, point)) {
                // (point, a) left of (point, endLinePoint)
                if (!inContour(a)) {
                    inContourLeftLine = false;
                }
                if (!inContour(b)) {
                    inContourRightLine = false;
                }

                if (inContourLeftLine) {
                    candidates.add(a);
                }
                if (inContourRightLine) {
                    candidates.add(b);
                }
            } else {
                if (!inContour(a)) {
                    inContourRightLine = false;
                }
                if (!inContour(b)) {
                    inContourLeftLine = false;
                }

                if (inContourLeftLine) {
                    candidates.add(b);
                }
                if (inContourRightLine) {
                    candidates.add(a);
                }
            }
        }

        return candidates;
    }

    private Point choiceBestPoint(final Collection<Point> candidates) {
        return choiceBestPointByMinValue(candidates);
    }

    /**
     * Выбирает лучшую точку ЦЛК из кандидатов на основе максимума суммированного изображения
     */
    private Point choiceBestPointByMinValue(final Collection<Point> candidates) {
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

    /**
     * Выбирает лучшую точку ЦЛК из кандидатов по максимальной отдолённости от краёв контура
     */
    @SuppressWarnings("unused")
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
                return contour[y][x] > 0;
            }
        }
        return false;
    }

    private boolean inImage(final Point point) {
        if (point.getIntX() >= 0 && point.getIntY() >= 0) {
            if (point.getIntY() < contour.length && point.getIntX() < contour[0].length) {
                return true;
            }
        }
        return false;

    }

    private void drawTrack(final Collection<Point> points, final String name) {
        System.out.println("Draw " + name);
        FunctionHelper.drawPointsOnImage(points, outputPrefix + name, sumImage);
    }
}

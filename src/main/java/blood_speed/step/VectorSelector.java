package blood_speed.step;

import blood_speed.helper.BmpHelper;
import blood_speed.step.data.Images;
import blood_speed.step.data.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

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

        List<Point> middles = new ArrayList<>();
        int prevValue = circuit[0][0];

        int i = 207;
        int j = 36;
        double dist = Math.sqrt(2);
        while (true) {
            Distances borderDistance = findBorderDistance(j, i);
            if ((Math.abs(borderDistance.top - borderDistance.bottom) <= 1
                    || Math.abs(borderDistance.right - borderDistance.left) <= 1
                    || Math.abs(borderDistance.topRight - borderDistance.bottomLeft) <= dist
                    || Math.abs(borderDistance.topLeft - borderDistance.bottomRight) <= dist) {
                middles.add(new Point(j, i));
                List<Double> values = borderDistance.getValues();
                Collections.sort(values);

            }
        }

        return null;
    }


    public Images process2() {
        int[][] image1 = data.getImagesList().get(0);
        int[][] image2 = data.getImagesList().get(1);

        List<Point> middles = new ArrayList<>();
        int prevValue = circuit[0][0];

        for (int i = 0; i < data.getRows(); i++) {
            for (int j = 0; j < data.getCols(); j++) {
                if (circuit[i][j] == 0) {
                    Distances borderDistance = findBorderDistance(j, i);
                    double dist = Math.sqrt(2);
//                    if ((Math.abs(borderDistance.top - borderDistance.bottom) <= 1
//                            || Math.abs(borderDistance.right - borderDistance.left) <= 1
//                            || Math.abs(borderDistance.topRight - borderDistance.bottomLeft) <= dist
//                            || Math.abs(borderDistance.topLeft - borderDistance.bottomRight) <= dist)
//                            && borderDistance.top != 1
//                            && borderDistance.bottom != 1
//                            && borderDistance.left != 1
//                            && borderDistance.right != 1
//                            && borderDistance.bottomRight > dist
//                            && borderDistance.bottomLeft > dist
//                            && borderDistance.topRight > dist
//                            && borderDistance.topLeft > dist
//                            )
//                    {
//                        middles.add(new Point(j, i));
//                    }
                    if (i == 60 && j == 28) {
                        System.err.println("");
                    }
                    if (!(borderDistance.top != 1
                            && borderDistance.bottom != 1
                            && borderDistance.left != 1
                            && borderDistance.right != 1
                            && borderDistance.bottomRight > dist
                            && borderDistance.bottomLeft > dist
                            && borderDistance.topRight > dist
                            && borderDistance.topLeft > dist)) {
                        continue;
                    }
                    if (Math.abs(borderDistance.top - borderDistance.bottom) <= 1
                            || Math.abs(borderDistance.left - borderDistance.right) <= 1
                            || Math.abs(borderDistance.topRight - borderDistance.bottomLeft) <= dist
                            || Math.abs(borderDistance.topLeft - borderDistance.bottomRight) <= dist
                            ) {
                        middles.add(new Point(j, i));
                    }

                    if (Math.abs(borderDistance.top - borderDistance.bottom) <= 1
                            && (borderDistance.top + borderDistance.bottom) * 2 < borderDistance.left + borderDistance.right
                            || Math.abs(borderDistance.left - borderDistance.right) <= 1
                            && (borderDistance.left + borderDistance.right) * 2 < borderDistance.top + borderDistance.bottom
                            || Math.abs(borderDistance.topRight - borderDistance.bottomLeft) <= dist
                            && (borderDistance.topRight + borderDistance.bottomLeft) * 2 < borderDistance.topLeft + borderDistance.bottomRight
                            || Math.abs(borderDistance.topLeft - borderDistance.bottomRight) <= dist
                            && (borderDistance.topLeft + borderDistance.bottomRight) * 2 < borderDistance.bottomLeft + borderDistance.topRight
                            ) {
//                        middles.add(new Point(j, i));
                    } else {
                        int distanceTopBottom = borderDistance.top + borderDistance.bottom;
                        int distanceLeftRight = borderDistance.left + borderDistance.right;
                        double distanceDiagonal1 = borderDistance.topRight + borderDistance.bottomLeft;
                        double distanceDiagonal2 = borderDistance.topLeft + borderDistance.bottomRight;
                        if ((Math.abs(borderDistance.top - borderDistance.bottom) <= 3
                                || Math.abs(borderDistance.right - borderDistance.left) <= 3
                                || Math.abs(borderDistance.topRight - borderDistance.bottomLeft) <= 3 * dist
                                || Math.abs(borderDistance.topLeft - borderDistance.bottomRight) <= 3 * dist)
                                && Math.abs(distanceLeftRight - distanceTopBottom) < 10
                                && Math.abs(distanceDiagonal1 - distanceDiagonal2) < 10 * dist) {
//                            middles.add(new Point(j, i));
                        }
                    }

                }
            }
        }

        int[][] trackImage = new int[data.getRows()][data.getCols()];
        for (Point p : middles) {
            trackImage[p.y][p.x] = 255;
        }

        // фильтрация
        for (Point p : middles) {

        }

        BmpHelper.writeBmp("data/test-result4.bmp", trackImage);
        return null;
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


    public static Images loadData(final String inputFolder) {
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
        final double top;
        final double bottom;
        final double right;
        final double left;

        final double topRight;
        final double topLeft;
        final double bottomRight;
        final double bottomLeft;

        public Distances(int top, int bottom, int right, int left, double topRight, double topLeft, double bottomRight, double bottomLeft) {
            this.top = top;
            this.bottom = bottom;
            this.right = right;
            this.left = left;
            this.topRight = topRight;
            this.topLeft = topLeft;
            this.bottomRight = bottomRight;
            this.bottomLeft = bottomLeft;
        }

        public Direction[][] getMaxDirections() {
            List<Double> result = new ArrayList<>();
            result.add(top);
            result.add(right);
            result.add(left);
            result.add(bottom);
            result.add(bottomRight);
            result.add(bottomLeft);
            result.add(topLeft);
            result.add(topRight);

        }
    }

    private enum Direction {
        TOP(p -> new Point(p.x, p.y - 1)),
        LEFT(p -> new Point(p.x-1, p.y)),
        RIGHT(p -> new Point(p.x+1, p.y )),
        BOTTOM(p -> new Point(p.x, p.y + 1)),
        TOP_RIGHT(p -> new Point(p.x+1, p.y - 1)),
        TOP_LEFT(p -> new Point(p.x-1, p.y - 1)),
        BOTTOM_LEFT(p -> new Point(p.x-1, p.y + 1)),
        BOTTOM_RIGHT(p -> new Point(p.x+1, p.y + 1));

        private final Function<Point, Point> function;

        Direction(Function<Point, Point> function) {
            this.function = function;
        }
    }
}

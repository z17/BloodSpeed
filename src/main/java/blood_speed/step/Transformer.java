package blood_speed.step;

import blood_speed.helper.BmpHelper;
import blood_speed.helper.FunctionHelper;
import blood_speed.helper.MathHelper;
import blood_speed.step.data.Images;
import blood_speed.step.data.Line;
import blood_speed.step.data.LineSegment;
import blood_speed.step.data.Point;
import blood_speed.step.util.Direction;

import java.util.Arrays;
import java.util.List;

/**
 * Класс для выпрямления капилляра вдоль центральной линии
 */
public class Transformer extends Step<Images> {
    // набор точек средней линии
    private final List<Point> middleLine;
    // обрабатываемые изображения
    private final Images data;
    // контур капилляра
    private final int[][] contour;
    private final String outputDir;
    // шаг для построения направления (линия берётся из точек (currentPointIndex - indent; currentPointIndex + indent)
    private final int indent;
    // размер шага по перпендикуляру
    private final double oneStepSize;
    // кол-во шагов по перпендикуляру
    private final int stepsCount;

    public Transformer(final List<Point> middleLine, final Images data, final int[][] contour, final String outputDir, int indent, double oneStepSize, int stepsCount) {
        this.middleLine = middleLine;
        this.data = data;
        this.contour = contour;
        this.outputDir = outputDir;
        this.indent = indent;
        this.oneStepSize = oneStepSize;
        this.stepsCount = stepsCount;
        FunctionHelper.checkOutputFolders(outputDir);
    }

    public static void main(String[] args) {
        List<Point> middleLine = FunctionHelper.readPointsList("data/tests/dec94_pasha4_cap1/middle-line/v1_" + MiddleLineSelector.MIDDLE_FULL_POINTS_POSITION_FILENAME);
        Images data = BackgroundSelector.loadOutputData("data/tests/dec94_pasha4_cap1/backgroundSelector/");
        int[][] contour = BmpHelper.readBmp("data/tests/dec94_pasha4_cap1/backgroundSelector/contour-image-photoshop.bmp");
        Step<Images> step = new Transformer(middleLine, data, contour, "data/tests/dec94_pasha4_cap1/transformedImages", 4, 1, 5);
        step.process();
    }


    @Override
    public Images process() {
        int currentNumberFile = 0;
        for (int[][] matrix : data.getImagesList()) {
            int[][] result = new int[stepsCount * 2 + 1][middleLine.size()];
            for (int i = indent; i < middleLine.size() - indent; i++) {
                Point p1 = middleLine.get(i - indent);
                Point p2 = middleLine.get(i + indent);
                final LineSegment segmentP1P2 = new LineSegment(p1, p2);

                double yMiddle = (p1.getIntY() + p2.getIntY()) / 2;
                double xMiddle = (p1.getIntX() + p2.getIntX()) / 2;
                final Point middlePoint = new Point(xMiddle, yMiddle);

                double A1 = -segmentP1P2.getB();
                double B1 = segmentP1P2.getA();
                double C1 = -segmentP1P2.getA() * yMiddle + segmentP1P2.getB() * xMiddle;
                final Line perpendicularLine = new Line(A1, B1, C1);
                result[stepsCount][i] = (int) Math.ceil(MathHelper.getPointValue(middlePoint, matrix));

                for (int k = 1; k <= stepsCount; k++) {
                    double r = oneStepSize * k;
                    List<Point> points = MathHelper.getInterSectionPointWithCircleAndLine(perpendicularLine, middlePoint, r);
                    if (points == null || points.size() != 2) {
                        throw new RuntimeException("Error");
                    }
                    Point a = points.get(0);
                    Point b = points.get(1);
                    Direction byPointA = Direction.getByPoints(middlePoint, a);
                    if (Arrays.asList(Direction.BOTTOM, Direction.BOTTOM_LEFT, Direction.LEFT, Direction.BOTTOM_RIGHT, Direction.TOP_LEFT).contains(byPointA)) {
                        result[stepsCount - k][i] = (int) Math.ceil(MathHelper.getPointValue(a, matrix));
                        result[stepsCount + k][i] = (int) Math.ceil(MathHelper.getPointValue(b, matrix));
                    } else {
                        result[stepsCount - k][i] = (int) Math.ceil(MathHelper.getPointValue(b, matrix));
                        result[stepsCount + k][i] = (int) Math.ceil(MathHelper.getPointValue(a, matrix));
                    }
//                    result[stepsCount - k][i] = (int)Math.ceil(MathHelper.getPointValue(a, matrix));
//                    result[stepsCount + k][i] = (int)Math.ceil(MathHelper.getPointValue(b, matrix));

                }

//                int[][] contourTemp = MatrixHelper.copyMatrix(contour);
//                for (int y1 = 0; y1 < data.getRows(); y1++) {
//                    int x1 = (int) Math.round(segmentP1P2.getX(y1));
//                    if (x1 > 0 && x1 < data.getCols())
//                        contourTemp[y1][x1] = 180;
//
//                    int x2 = (int) Math.round(perpendicularLine.getX(y1));
//                    if (x2 > 0 && x2 < data.getCols())
//                        contourTemp[y1][x2] = 80;
//                }
//
//                for (int x1 = 0; x1 < data.getCols(); x1++) {
//                    int y1 = (int) Math.round(segmentP1P2.getY(x1));
//                    if (y1 > 0 && y1 < data.getRows())
//                        contourTemp[y1][x1] = 180;
//
//                    int y2 = (int) Math.round(perpendicularLine.getY(x1));
//                    if (y2 > 0 && y2 < data.getRows())
//                        contourTemp[y2][x1] = 80;
//                }
//
//                contourTemp[middleLine.get(i).getIntY()][middleLine.get(i).getIntX()] = 100;
//
//                BmpHelper.writeBmp(outputDir + "/test_" + i + ".bmp", contourTemp);
            }
            BmpHelper.writeBmp(outputDir + "/result_" + currentNumberFile + ".bmp", result);
            currentNumberFile++;
        }
        return null;
    }
}

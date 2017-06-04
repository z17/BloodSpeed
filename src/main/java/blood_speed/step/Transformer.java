package blood_speed.step;

import blood_speed.helper.BmpHelper;
import blood_speed.helper.FunctionHelper;
import blood_speed.helper.MathHelper;
import blood_speed.helper.MatrixHelper;
import blood_speed.step.data.Images;
import blood_speed.step.data.Line;
import blood_speed.step.data.LineSegment;
import blood_speed.step.data.Point;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Класс для выпрямления капилляра вдоль центральной линии
 */
public class Transformer extends Step<Void> {
    // набор точек средней линии
    private final List<Point> middleLine;
    // обрабатываемые изображения
    private final Images data;
    // сумма капилляра
    private final int[][] sum;
    // контур
    private final int[][] contour;
    // шаг построения перпендикуляров
    private final int perpendicularStep;

    private final Path outputDir;

    private final String outputPrefix;
    // шаг для построения направления (линия берётся из точек (currentPointIndex - indent; currentPointIndex + indent)
    private final int indent;
    // размер шага по перпендикуляру
    private final double oneStepSize;
    // кол-во шагов по перпендикуляру
    private final int stepsCount;

    // обрезать ли по контуру изображение
    private final boolean truncateByContour;

    public Transformer(final List<Point> middleLine, final Images data, int[][] sum, int[][] contour, int perpendicularStep, final String outputDir, final String outputPrefix, int indent, double oneStepSize, int stepsCount, boolean truncateByContour) {
        this.middleLine = middleLine;
        this.data = data;
        this.sum = sum;
        this.contour = contour;
        this.perpendicularStep = perpendicularStep;
        this.outputDir = Paths.get(outputDir);
        this.outputPrefix = outputPrefix;
        this.indent = indent;
        this.oneStepSize = oneStepSize;
        this.stepsCount = stepsCount;
        this.truncateByContour = truncateByContour;
        FunctionHelper.checkOutputFolders(outputDir);
    }

    @Override
    public Void process() {
        Point[][] transformerPoints = getTransformImagePoints();

        int[][] contourTransformed = transformImage(contour, transformerPoints, null);
        BmpHelper.writeBmp(outputDir.toAbsolutePath() + "/contour.bmp", contourTransformed);
        int currentNumberFile = 0;
        for (int[][] matrix : data.getImagesList()) {
            final String name = outputPrefix + String.format("%05d", currentNumberFile) + ".bmp";
            int[][] result = transformImage(matrix, transformerPoints, contourTransformed);
            BmpHelper.writeBmp(outputDir.resolve(name).toString(), result);
            currentNumberFile++;

            System.out.println("Image " + currentNumberFile + "/" + data.getImagesList().size() + "  complete");
        }

        return null;
    }

    private Point[][] getTransformImagePoints() {
        int[][] imagePerpendicularOne = MatrixHelper.copyMatrix(sum);
        int[][] imagePerpendicularTwo = MatrixHelper.copyMatrix(sum);

        int currentStep = 0;
        Point[][] result = new Point[stepsCount * 2 + 1][middleLine.size() - 2 * indent];
        for (int i = indent; i < middleLine.size() - indent; i++) {
            Point p1 = middleLine.get(i - indent);
            Point p2 = middleLine.get(i + indent);
            final LineSegment segmentP1P2 = new LineSegment(p1, p2);

            double yMiddle = (p1.getIntY() + p2.getIntY()) / 2;
            double xMiddle = (p1.getIntX() + p2.getIntX()) / 2;
            final Point middlePoint = new Point(xMiddle, yMiddle);

            final Line perpendicularLine = segmentP1P2.getPerpendicular(middlePoint);
            result[stepsCount][i - indent] = middlePoint;

            for (int k = 1; k <= stepsCount; k++) {
                double r = oneStepSize * k;
                List<Point> points = MathHelper.getInterSectionPointWithCircleAndLine(perpendicularLine, middlePoint, r);
                if (points == null || points.size() != 2) {
                    throw new RuntimeException("Unknown error");
                }

                Point a = points.get(0);
                Point b = points.get(1);

                if (MathHelper.getVectorDirection(p1, a, middlePoint)) {
                    if (currentStep % perpendicularStep == 0) {
                        drawPoint(a, imagePerpendicularOne);
                        drawPoint(b, imagePerpendicularTwo);
                    }
                    result[stepsCount - k][i - indent] = a;
                    result[stepsCount + k][i - indent] = b;
                } else {
                    if (currentStep % perpendicularStep == 0) {
                        drawPoint(b, imagePerpendicularOne);
                        drawPoint(a, imagePerpendicularTwo);
                    }
                    result[stepsCount - k][i - indent] = b;
                    result[stepsCount + k][i - indent] = a;
                }
            }

            currentStep++;

            imagePerpendicularOne[middleLine.get(i).getIntY()][middleLine.get(i).getIntX()] = 160;
            imagePerpendicularTwo[middleLine.get(i).getIntY()][middleLine.get(i).getIntX()] = 160;

        }

        BmpHelper.writeBmp(outputDir + "/__track1.bmp", imagePerpendicularOne);
        BmpHelper.writeBmp(outputDir + "/__track2.bmp", imagePerpendicularTwo);
        return result;
    }

    private int[][] transformImage(final int[][] image, final Point[][] transformerPoints, final int[][] contour) {
        int[][] result = new int[transformerPoints.length][transformerPoints[0].length];

        for (int y = 0; y < transformerPoints.length; y++) {
            for (int x = 0; x < transformerPoints[y].length; x++) {
                Point currentPoint = transformerPoints[y][x];
                int value;
                if (MathHelper.pointInImage(currentPoint, data.getCols(), data.getRows())) {
                    value = (int) Math.ceil(MathHelper.getPointValue(currentPoint, image));
                } else {
                    value = 0;
                }

                if (contour != null && truncateByContour && !inContour(x, y, contour)) {
                    value = 0;
                }

                result[y][x] = value;
            }
        }
        return result;
    }



    private void drawPoint(Point p, int[][] imagePerpendicular) {
        if (MathHelper.pointInImage(p, data.getCols(), data.getRows())) {
            imagePerpendicular[p.getIntY()][p.getIntX()] = 200;
        }
    }

    private boolean inContour(final int x, final int y, int[][] contour) {
        if (x >= 0 && y >= 0) {
            if (y < contour.length && x < contour[0].length) {
                return contour[y][x] > 0;
            }
        }
        return false;
    }
}

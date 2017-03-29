package blood_speed.step;

import blood_speed.helper.BmpHelper;
import blood_speed.helper.FunctionHelper;
import blood_speed.helper.MathHelper;
import blood_speed.helper.MatrixHelper;
import blood_speed.step.data.Images;
import blood_speed.step.data.Line;
import blood_speed.step.data.LineSegment;
import blood_speed.step.data.Point;

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

    private final String outputDir;
    // шаг для построения направления (линия берётся из точек (currentPointIndex - indent; currentPointIndex + indent)
    private final int indent;
    // размер шага по перпендикуляру
    private final double oneStepSize;
    // кол-во шагов по перпендикуляру
    private final int stepsCount;

    public Transformer(final List<Point> middleLine, final Images data, int[][] sum, int[][] contour, int perpendicularStep, final String outputDir, int indent, double oneStepSize, int stepsCount) {
        this.middleLine = middleLine;
        this.data = data;
        this.sum = sum;
        this.contour = contour;
        this.perpendicularStep = perpendicularStep;
        this.outputDir = outputDir;
        this.indent = indent;
        this.oneStepSize = oneStepSize;
        this.stepsCount = stepsCount;
        FunctionHelper.checkOutputFolders(outputDir);
    }

    public static void main(String[] args) {
        List<Point> middleLine = FunctionHelper.readPointsList("data/tests/capillary_dec94_pasha4_cap1/middle-line/v1_" + MiddleLineSelector.MIDDLE_FULL_POINTS_POSITION_FILENAME);
        Images data = BackgroundSelector.loadOutputData("data/tests/capillary_dec94_pasha4_cap1/backgroundSelector/");
        int[][] sum = BmpHelper.readBmp("data/tests/capillary_dec94_pasha4_cap1/backgroundSelector/sum-image.bmp");
        int[][] contour = BmpHelper.readBmp("data/tests/capillary_dec94_pasha4_cap1/backgroundSelector/contour-image-photoshop.bmp");
        Step<Void> step = new Transformer(
                middleLine,
                data,
                sum,
                contour,
                7,
                "data/tests/capillary_dec94_pasha4_cap1/transformedImages",
                4,
                1,
                6);
        step.process();
    }


    @Override
    public Void process() {
        int currentNumberFile = 0;
        for (int[][] matrix : data.getImagesList()) {
            if (currentNumberFile == 0) {
                int[][] result = transformImage(matrix, true);
                BmpHelper.writeBmp(outputDir + "/result_" + currentNumberFile + ".bmp", result);
            } else {
                int[][] result = transformImage(matrix, false);
                BmpHelper.writeBmp(outputDir + "/result_" + currentNumberFile + ".bmp", result);
            }
            currentNumberFile++;
        }

        int[][] contourTransformed = transformImage(contour, false);
        for (int i = 0; i < contourTransformed.length; i++) {
            for (int j = 0; j < contourTransformed[i].length; j++) {
                if (contourTransformed[i][j] > 128) {
                    contourTransformed[i][j] = 0;
                } else {
                    contourTransformed[i][j] = 255;
                }
            }
        }
        BmpHelper.writeBmp(outputDir + "/contour.bmp", contourTransformed);
        return null;
    }


    /*
    Можно сделать оптимальнее: сначала рассчитать координаты точек, которые войдут в трансформированное изображение, а потом уже получать значения этих точек для каждого изображения
    Сейчас координаты рассчитываются для каждого отдельно, при том что они всегда одинаковые.
     */
    private int[][] transformImage(int[][] image, boolean drawPerpendiculars) {

        // todo: отрефакторить момент записи перпендикуляров
        int[][] imagePerpendicularOne = null;
        int[][] imagePerpendicularTwo = null;

        if (drawPerpendiculars) {
            imagePerpendicularOne = MatrixHelper.copyMatrix(sum);
            imagePerpendicularTwo = MatrixHelper.copyMatrix(sum);
        }

        int currentStep = 0;
        int[][] result = new int[stepsCount * 2 + 1][middleLine.size() - 2 * indent];
        for (int i = indent; i < middleLine.size() - indent; i++) {
            Point p1 = middleLine.get(i - indent);
            Point p2 = middleLine.get(i + indent);
            final LineSegment segmentP1P2 = new LineSegment(p1, p2);

            double yMiddle = (p1.getIntY() + p2.getIntY()) / 2;
            double xMiddle = (p1.getIntX() + p2.getIntX()) / 2;
            final Point middlePoint = new Point(xMiddle, yMiddle);

            final Line perpendicularLine = segmentP1P2.getPerpendicular(middlePoint);
            result[stepsCount][i - indent] = (int) Math.ceil(MathHelper.getPointValue(middlePoint, image));

            for (int k = 1; k <= stepsCount; k++) {
                double r = oneStepSize * k;
                List<Point> points = MathHelper.getInterSectionPointWithCircleAndLine(perpendicularLine, middlePoint, r);
                if (points == null || points.size() != 2) {
                    throw new RuntimeException("Unknown error");
                }

                Point a = points.get(0);
                Point b = points.get(1);

                if (getVectorDirection(p1, a, middlePoint)) {
                    if (currentStep % perpendicularStep == 0 && drawPerpendiculars) {
                        imagePerpendicularOne[a.getIntY()][a.getIntX()] = 200;
                        imagePerpendicularTwo[b.getIntY()][b.getIntX()] = 200;
                    }
                    result[stepsCount - k][i - indent] = (int) Math.ceil(MathHelper.getPointValue(a, image));
                    result[stepsCount + k][i - indent] = (int) Math.ceil(MathHelper.getPointValue(b, image));
                } else {
                    if (currentStep % perpendicularStep == 0 && drawPerpendiculars) {
                        imagePerpendicularOne[b.getIntY()][b.getIntX()] = 200;
                        imagePerpendicularTwo[a.getIntY()][a.getIntX()] = 200;
                    }
                    result[stepsCount - k][i - indent] = (int) Math.ceil(MathHelper.getPointValue(b, image));
                    result[stepsCount + k][i - indent] = (int) Math.ceil(MathHelper.getPointValue(a, image));
                }
            }

            currentStep++;

            if (drawPerpendiculars) {
                imagePerpendicularOne[middleLine.get(i).getIntY()][middleLine.get(i).getIntX()] = 160;
                imagePerpendicularTwo[middleLine.get(i).getIntY()][middleLine.get(i).getIntX()] = 160;
            }

        }

        if (drawPerpendiculars) {
            BmpHelper.writeBmp(outputDir + "/__track1.bmp", imagePerpendicularOne);
            BmpHelper.writeBmp(outputDir + "/__track2.bmp", imagePerpendicularTwo);
        }

        return result;
    }

    // если произведение векторов из одной точки больше нуля, то второй вектор направлен влево от первого
    private boolean getVectorDirection(final Point a, final Point b, final Point start) {
        Point pointVector = new Point(b.getX() - start.getX(), b.getY() - start.getY());
        Point vectorA = new Point(a.getX() - start.getX(), a.getY() - start.getY());
        return 1 / pointVector.getX() * vectorA.getY() - pointVector.getY() * vectorA.getX() < 0;
    }
}

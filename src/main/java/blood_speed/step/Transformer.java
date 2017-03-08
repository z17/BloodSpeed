package blood_speed.step;

import blood_speed.helper.BmpHelper;
import blood_speed.helper.FunctionHelper;
import blood_speed.helper.MatrixHelper;
import blood_speed.step.data.Images;
import blood_speed.step.data.Point;

import java.util.List;

/**
 * Класс для выпрямления капилляра вдоль центральной линии
 */
public class Transformer extends Step<Images> {
    private final List<Point> middleLine;
    private final Images data;
    private final int[][] contour;
    private final String outputDir;

    public Transformer(final List<Point> middleLine, final Images data, final int[][] contour, final String outputDir) {
        this.middleLine = middleLine;
        this.data = data;
        this.contour = contour;
        this.outputDir = outputDir;
        FunctionHelper.checkOutputFolders(outputDir);
    }

    public static void main(String[] args) {
        List<Point> middleLine = FunctionHelper.readPointsList("data/middle-line/v1__points.txt");
        Images data = BackgroundSelector.loadOutputData("data/backgroundSelector_v2/");
        int[][] contour = BmpHelper.readBmp("data/backgroundSelector_v2/circuit-image_photoshop.bmp");
        Step<Images> step = new Transformer(middleLine, data, contour, "data/transformedImages");
        step.process();
    }


    @Override
    public Images process() {
        for (int i = 1; i < middleLine.size() - 1; i++) {
            int[][] contourTemp = MatrixHelper.copyMatrix(contour);
            Point p1 = middleLine.get(i - 1);
            Point p2 = middleLine.get(i + 1);
            double A = p2.y - p1.y;
            double B = p1.x - p2.x;
            double C = -(p1.x * p2.y - p2.x * p1.y);


            double yMiddle = (p1.y + p2.y) / 2;
            double xMiddle = (p1.x + p2.x) / 2;

            double A1 = -B;
            double B1 = A;
            double C1 = -A * yMiddle + B * xMiddle;


            for (int y1 = 0; y1 < data.getRows(); y1++) {
                int x1 = (int) Math.round((-B * y1 - C) / A);
                if (x1 > 0 && x1 < data.getCols())
                    contourTemp[y1][x1] = 180;


                int x2 = (int) Math.round((-B1 * y1 - C1) / A1);
                if (x2 > 0 && x2 < data.getCols())
                    contourTemp[y1][x2] = 80;
            }

            for (int x1 = 0; x1 < data.getCols(); x1++) {
                int y1 = (int) Math.round((-A * x1 - C) / B);
                if (y1 > 0 && y1 < data.getRows())
                    contourTemp[y1][x1] = 180;

                int y2 = (int) Math.round((-A1 * x1 - C1) / B1);
                if (y2 > 0 && y2 < data.getRows())
                    contourTemp[y2][x1] = 80;
            }

            contourTemp[middleLine.get(i).y][middleLine.get(i).x] = 100;

            BmpHelper.writeBmp(outputDir + "/test_" + i + ".bmp", contourTemp);
        }
        return null;
    }
}

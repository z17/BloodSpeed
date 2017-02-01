package blood_speed;

import blood_speed.helper.BmpHelper;
import blood_speed.helper.FunctionHelper;
import blood_speed.helper.MatrixHelper;
import blood_speed.step.AcPdfFst;
import blood_speed.step.Blur;
import blood_speed.step.Speed;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Helper {
    public static void main(String[] args) {
//        final Properties properties = getSettings();
//        int ndv =  Integer.valueOf(properties.getProperty("ndv"));
//        int minNdv1 =  Integer.valueOf(properties.getProperty("minNdv1"));
//        int N =  Integer.valueOf(properties.getProperty("N"));              // количество кадров
//        int dNum =  Integer.valueOf(properties.getProperty("dNum"));        // ширина каждого кадра
//        int dv =  Integer.valueOf(properties.getProperty("dv"));
//        int r =  Integer.valueOf(properties.getProperty("r"));             // радиус области
//        int dr =  Integer.valueOf(properties.getProperty("dr"));
//        int dt =  Integer.valueOf(properties.getProperty("dt"));            // скорее всего это step сравнения (сравниваем n кадра и n + dt)
//        String prefix = properties.getProperty("prefix");                   // префикс имени входных файлов
//
//
//        // папки
//        String folderInput = properties.getProperty("input_folder");
//        String circuitImage = properties.getProperty("circuit_image");
//        String step1FolderOutput = properties.getProperty("correlation_folder");
//        String step2FolderOutput = properties.getProperty("blur_folder");
//        String step3FolderOutput = properties.getProperty("result_folder");
//
//
//        Speed.Images images = Speed.loadBlurImages(step2FolderOutput, prefix + "sm", ndv, minNdv1);
//        AcPdfFst.Step1Result step1Result = Blur.readData(prefix, step1FolderOutput, minNdv1, ndv);
//        Blur.buildGraphic(images.getImagesList(), 194,2764);
//        Blur.buildGraphic(step1Result, 194,2764);
//        System.exit(1);


        int[][] matrix = BmpHelper.readBmp("data/result/result.bmp");

        int cols = FunctionHelper.cols(matrix);
        int rows = FunctionHelper.rows(matrix);

        System.out.println(cols);
        System.out.println(rows);

        for (int i = 0; i < rows; i++) {
//            if (matrix[i][250] > 0 && matrix[i][250] < 250){
                System.out.print(matrix[i][250]);
                System.out.print("\t");
//            }
        }

        System.out.println();

    }



    private static Properties getSettings() {
        final Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("settings.ini"));
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

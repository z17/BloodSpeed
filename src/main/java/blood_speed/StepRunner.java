package blood_speed;

import blood_speed.step.*;
`import blood_speed.step.data.SpeedImages;

public final class StepRunner {

    public static final double[] G1 = initG1();

    private static double[] initG1() {
        double[] g = new double[101];
        for (int i = 0; i <= 100; i++) {
            g[i] = 0.125 * Math.pow((1 + Math.cos(Math.PI * i / 100)), 0.75);
        }
        return g;
    }

    int run(StepData data) {

        if (!data.ready()) {
            throw new RuntimeException("Not enough parameters to run steps");
        }

        Step<SpeedImages> step1 = new AcPdfFst(
                data.numberOfDigitsInStep1FileNames,
                data.step1InputFolder,
                data.step1OutputFolder,
                data.circuitImageName,
                data.filePrefix,
                data.maxSpeed,
                data.stepsNumber,
                data.startStep,
                data.framesNumber,
                data.r,
                data.dr,
                data.dt
        );
        SpeedImages step1Result = step1.process();

        Step<SpeedImages> step2 = new Blur(
                step1Result,
                data.blurStepOutputFolder,
                data.filePrefix,
                data.s1dn1,
                data.s1dn2,
                data.s1dn1st,
                data.s1dn2st,
                data.s2dn1,
                data.s2dn2
        );
        SpeedImages blurImages = step2.process();

        Step<int[][]> step3 = new Speed(
                data.step3OutputFolder,
                data.step3OutputNameClearFile,
                data.step3OutputNameClearImage,
                data.step3OoutputNameImageFile,
                blurImages,
                data.resultCoefficient
        );
        int[][] speedMatrix = step3.process();

        Step<Integer> middleSpeed = new MiddleSpeed(speedMatrix, data.middleStepOutputFile, data.affectedCols);

        return middleSpeed.process();

//        чтобы запустить чтение с диска
//        AcPdfFst.Step1Result step1Result = Blur.loadData(prefix, step1FolderOutput, minNdv1, ndv);
//        Чтобы запустить с чтением с диска
//        Speed.SpeedImages images = Speed.loadBlurImages(step2FolderOutput, prefix + "sm", ndv, minNdv1);
//        Speed speed = new Speed( step3FolderOutput, images);

    }

    static final class StepData {
        private byte[] counter = new byte[26];

        // common
        private String filePrefix;

        // step1 : get speed
        private String step1InputFolder;
        private String step1OutputFolder;
        private int numberOfDigitsInStep1FileNames;
        private String circuitImageName;
        private int maxSpeed;
        private int stepsNumber;
        private int startStep;
        private int framesNumber;
        private int r;
        private int dr;
        private int dt;

        // step2 : blur
        private String blurStepOutputFolder;
        private int s1dn1;
        private int s1dn2;
        private int s1dn1st;
        private int s1dn2st;
        private int s2dn1;
        private int s2dn2;

        // step3 : analyze
        private String step3OutputFolder;
        private String step3OutputNameClearFile;
        private String step3OutputNameClearImage;
        private String step3OoutputNameImageFile;
        private int resultCoefficient;

        // step4 : middle speed
        private String middleStepOutputFile;
        private int affectedCols;

        boolean ready() {
            for (byte b : counter) {
                if (b != 1)
                    return false;
            }
            return true;
        }

        StepData setFilePrefix(String filePrefix) {
            this.filePrefix = filePrefix;
            counter[0] = 1;
            return this;
        }

        StepData setStep1InputFolder(String step1InputFolder) {
            this.step1InputFolder = step1InputFolder;
            counter[1] = 1;
            return this;
        }

        StepData setStep1OutputFolder(String step1OutputFolder) {
            this.step1OutputFolder = step1OutputFolder;
            counter[2] = 1;
            return this;
        }

        StepData setNumberOfDigitsInStep1FileNames(int numberOfDigitsInStep1FileNames) {
            this.numberOfDigitsInStep1FileNames = numberOfDigitsInStep1FileNames;
            counter[3] = 1;
            return this;
        }

        StepData setCircuitImageName(String circuitImageName) {
            this.circuitImageName = circuitImageName;
            counter[4] = 1;
            return this;
        }

        StepData setMaxSpeed(int maxSpeed) {
            this.maxSpeed = maxSpeed;
            counter[5] = 1;
            return this;
        }

        StepData setStepsNumber(int stepsNumber) {
            this.stepsNumber = stepsNumber;
            counter[6] = 1;
            return this;
        }

        StepData setStartStep(int startStep) {
            this.startStep = startStep;
            counter[7] = 1;
            return this;
        }

        StepData setFramesNumber(int framesNumber) {
            this.framesNumber = framesNumber;
            counter[8] = 1;
            return this;
        }

        StepData setR(int r) {
            this.r = r;
            counter[9] = 1;
            return this;
        }

        StepData setDr(int dr) {
            this.dr = dr;
            counter[10] = 1;
            return this;
        }

        StepData setDt(int dt) {
            this.dt = dt;
            counter[11] = 1;
            return this;
        }

        StepData setBlurStepOutputFolder(String blurStepOutputFolder) {
            this.blurStepOutputFolder = blurStepOutputFolder;
            counter[12] = 1;
            return this;
        }

        StepData setS1dn1(int s1dn1) {
            this.s1dn1 = s1dn1;
            counter[13] = 1;
            return this;
        }

        StepData setS1dn2(int s1dn2) {
            this.s1dn2 = s1dn2;
            counter[14] = 1;
            return this;
        }

        StepData setS1dn1st(int s1dn1st) {
            this.s1dn1st = s1dn1st;
            counter[15] = 1;
            return this;
        }

        StepData setS1dn2st(int s1dn2st) {
            this.s1dn2st = s1dn2st;
            counter[16] = 1;
            return this;
        }

        StepData setS2dn1(int s2dn1) {
            this.s2dn1 = s2dn1;
            counter[17] = 1;
            return this;
        }

        StepData setS2dn2(int s2dn2) {
            this.s2dn2 = s2dn2;
            counter[18] = 1;
            return this;
        }

        StepData setStep3OutputFolder(String step3OutputFolder) {
            this.step3OutputFolder = step3OutputFolder;
            counter[19] = 1;
            return this;
        }

        StepData setResultCoefficient(int resultCoefficient) {
            this.resultCoefficient = resultCoefficient;
            counter[20] = 1;
            return this;
        }

        StepData setMiddleStepOutputFile(String middleStepOutputFile) {
            this.middleStepOutputFile = middleStepOutputFile;
            counter[21] = 1;
            return this;
        }

        StepData setAffectedCols(int affectedCols) {
            this.affectedCols = affectedCols;
            counter[22] = 1;
            return this;
        }

        StepData setStep3OutputNameClearFile(String step3OutputNameClearFile) {
            this.step3OutputNameClearFile = step3OutputNameClearFile;
            counter[23] = 1;
            return this;
        }

        StepData setStep3OutputNameClearImage(String step3OutputNameClearImage) {
            this.step3OutputNameClearImage = step3OutputNameClearImage;
            counter[24] = 1;
            return this;
        }

        StepData setStep3OoutputNameImageFile(String step3OoutputNameImageFile) {
            this.step3OoutputNameImageFile = step3OoutputNameImageFile;
            counter[25] = 1;
            return this;
        }
    }
}

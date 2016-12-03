public class Main {
    public static void main(String[] args) {
        BloodSpeedChecker bloodSpeedChecker = new BloodSpeedChecker();

        bloodSpeedChecker.getV7_ac_pdf_fst(
                "data\\shift1",
                20,
                10,
                0,
                1000,
                153,
                7,
                1,
                6
        );
    }
}

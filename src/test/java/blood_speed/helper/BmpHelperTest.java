package blood_speed.helper;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class BmpHelperTest {
    @Test
    public void writeBmp() throws Exception {
        final String name = "test.bmp";
        int[][] test = new int[][] {
                {1, 10, 20, 30},
                {40, 50, 60, 70}
        };
        BmpHelper.writeBmp(name, test);

        assertThat(Files.exists(Paths.get(name)), is(true));
        Files.deleteIfExists(Paths.get(name));
    }

    @Test
    public void readBmp() throws Exception {
        final String name = "test.bmp";
        int[][] test = new int[][] {
                {0, 100, 12, 5},
                {0, 100, 12, 5},
                {0, 100, 12, 5}
        };
        BmpHelper.writeBmp(name, test);
        int[][] read = BmpHelper.readBmp(name);
        assertThat(read, equalTo(test));
        Files.deleteIfExists(Paths.get(name));
    }
}
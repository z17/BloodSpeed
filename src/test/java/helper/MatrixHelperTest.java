package helper;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class MatrixHelperTest {

    @Test
    public void writeMatrix() throws Exception {
        int[][] test = {
                {1, 2, 3},
                {4, 5, 6}
        };
        final String name = "test.txt";
        MatrixHelper.writeMatrix(name, test);

        assertThat(Files.exists(Paths.get(name)), is(true));
        Files.deleteIfExists(Paths.get(name));
    }

    @Test
    public void readMatrix() throws Exception {
        int[][] test = {
                {1, 2, 3},
                {4, 5, 6}
        };
        final String name = "test.txt";
        MatrixHelper.writeMatrix(name, test);

        int[][] readTest = MatrixHelper.readMatrix(name);

        assertThat(test, equalTo(readTest));
    }
}
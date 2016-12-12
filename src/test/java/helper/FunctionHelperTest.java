package helper;

import org.junit.Test;

import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.*;

public class FunctionHelperTest {
    @Test
    public void mean() throws Exception {
        double[][] test = {
                {1, 2},
                {5.2, 3},
                {6.5, 7.5, 3}
        };

        double mean = FunctionHelper.mean(test);

        assertThat(mean, closeTo(4.02, 0.1));
    }

}
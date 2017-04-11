package blood_speed.runner;

import blood_speed.runner.SpeedSteps;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class StepDataTest {
    @Test
    public void ready() throws Exception {
        SpeedSteps.StepData data = new SpeedSteps.StepData();


        assertThat(data.ready(), is(false));

        data
                .setOutputFilePrefix(null)

                .setStep1InputFolder(null)
                .setStep1OutputFolder(null)
                .setNumberOfDigitsInStep1FileNames(1)
                .setCircuitImageName(null)
                .setMaxSpeed(2)
                .setStepsNumber(3)
                .setStartStep(5)
                .setFramesNumber(2)
                .setR(1)
                .setDr(2)
                .setDt(5);

        assertThat(data.ready(), is(false));

        data
                .setBlurStepOutputFolder(null)
                .setS1dn1(3)
                .setS1dn2(6)
                .setS1dn1st(1)
                .setS1dn2st(2)
                .setS2dn1(6)
                .setS2dn2(22)

                .setStep3OutputFolder(null);

        assertThat(data.ready(), is(true));
    }

}
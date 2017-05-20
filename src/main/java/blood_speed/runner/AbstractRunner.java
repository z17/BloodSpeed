package blood_speed.runner;

import java.util.Properties;

public abstract class AbstractRunner {
    public final void run(final Properties properties) {
        long startTime = System.nanoTime();
        runMethod(properties);
        long endTime = System.nanoTime();

        double duration = (double)(endTime - startTime) / 1000000 / 1000;  //seconds.
        System.err.printf("Duration: %.1f seconds\n", duration);
    }

    abstract protected void runMethod(final Properties properties);
}
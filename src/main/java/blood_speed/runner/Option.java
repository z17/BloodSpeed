package blood_speed.runner;

public enum Option {
    Background("background", BackgroundSelectorRunner.class),
    MiddleLine("middle-line", MiddleLineRunner.class),
    Transform("transform", TransformRunner.class),
    Speed("speed", SpeedRunner.class);

    private final String key;
    private final Class<? extends AbstractRunner> runnerClass;

    Option(final String key, Class<? extends AbstractRunner> runnerClass) {
        this.key = key;
        this.runnerClass = runnerClass;
    }

    public String getKey() {
        return key;
    }

    public AbstractRunner getRunner() {
        try {
            return runnerClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Error with running operation", e);
        }
    }

    public static Option getByKey(String key) {
        for (Option o : Option.values()) {
            if (o.getKey().equals(key)) {
                return o;
            }
        }
        throw new RuntimeException("Unknown option " + key);
    }
}

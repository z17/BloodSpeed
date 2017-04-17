package blood_speed;

import blood_speed.runner.Option;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Main {
    private final static String SETTINGS_FILE = "settings.ini";

    public static void main(final String[] args) {
        final String key = args[0];
        final Option option = Option.getByKey(key);

        final Properties properties = getSettings();
        option.getRunner().run(properties);
    }

    public static Properties getSettings() {
        final Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(SETTINGS_FILE));
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

package common.support;

import java.util.HashMap;
import java.util.Map;

/**
 * Bean class to collect all the basic configuration options used by the other system components
 */
public class Configuration {

    private Map<Object, String> settings;

    public Configuration() {
        settings = new HashMap<Object, String>();
    }

    public Configuration(final Map<Object, String> settings) {
        if (settings != null) {
            this.settings = settings;
        } else {
            this.settings = new HashMap<Object, String>();
        }
    }

    public Map<Object, String> getSettings() {
        return settings;
    }

    public void setSettings(final Map<Object, String> settings) {
        this.settings = settings;
    }

    public String get(final Object propertyKey) {
        return settings.get(propertyKey);
    }

    public boolean getBoolean(final Object propertyKey) {
        return Boolean.valueOf(get(propertyKey));
    }

    public int getInt(final Object propertyKey) {
        return getInt(propertyKey, -1);
    }

    public int getInt(final Object propertyKey, int defaultValue) {
        int value = defaultValue;
        try {
            value = Integer.valueOf(get(propertyKey));
        } catch (NumberFormatException ignored) {
        }
        return value;
    }
}

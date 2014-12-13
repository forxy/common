package common.utils.support
/**
 * Bean class to collect all the basic configuration options used by the other system components
 */
class Configuration {

    Map<Object, String> settings

    Configuration() {
        settings = new HashMap<Object, String>()
    }

    Configuration(final Map<Object, String> settings) {
        if (settings != null) {
            this.settings = settings
        } else {
            this.settings = new HashMap<Object, String>()
        }
    }

    String get(final Object propertyKey) {
        return settings.get(propertyKey)
    }

    boolean getBoolean(final Object propertyKey) {
        return Boolean.valueOf(get(propertyKey))
    }

    int getInt(final Object propertyKey) {
        return getInt(propertyKey, -1)
    }

    int getInt(final Object propertyKey, int defaultValue) {
        int value = defaultValue
        try {
            value = Integer.valueOf(get(propertyKey))
        } catch (NumberFormatException ignored) {
        }
        return value
    }
}

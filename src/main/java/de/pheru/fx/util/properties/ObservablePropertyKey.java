package de.pheru.fx.util.properties;

public class ObservablePropertyKey<T> {

    private final String key;
    private final T defaultValue;

    public ObservablePropertyKey(final String key, final T defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public String getKey() {
        return key;
    }

    public T getDefaultValue() {
        return defaultValue;
    }
}

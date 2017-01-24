package de.pheru.fx.util.properties;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;

public class ObservableProperties {

    private String filePath;
    private Properties properties;
    private Map<String, Property<?>> fxProperties;

    public ObservableProperties(final String filePath) {
        this.filePath = filePath;
        properties = new Properties() {
            // alphabetical key order
            @Override
            public synchronized Enumeration<Object> keys() {
                return Collections.enumeration(new TreeSet<>(super.keySet()));
            }
        };
        fxProperties = new HashMap<>();
    }

    public void load() throws IOException {
        properties.clear();
        fxProperties.clear();
        try (final InputStream inputStream = new FileInputStream(filePath)) {
            properties.load(inputStream);
        }
    }

    public void save(final String comments) throws IOException {
        save(comments, filePath);
    }

    public void save(final String comments, final String filePath) throws IOException {
        this.filePath = filePath;
        for (final Map.Entry<String, Property<?>> entry : fxProperties.entrySet()) {
            final String value = getPropertyValueAsString(entry.getValue());
            properties.setProperty(entry.getKey(), value);
        }
        try (final OutputStream outputStream = new FileOutputStream(filePath)) {
            properties.store(outputStream, comments);
        }
    }

    private String getPropertyValueAsString(final Property<?> property) {
        final String value;
        if (property instanceof StringProperty) {
            value = ((StringProperty) property).get();
        } else if (property instanceof BooleanProperty) {
            value = String.valueOf(((BooleanProperty) property).get());
        } else if (property instanceof IntegerProperty) {
            value = String.valueOf(((IntegerProperty) property).get());
        } else if (property instanceof LongProperty) {
            value = String.valueOf(((LongProperty) property).get());
        } else if (property instanceof FloatProperty) {
            value = String.valueOf(((FloatProperty) property).get());
        } else if (property instanceof DoubleProperty) {
            value = String.valueOf(((DoubleProperty) property).get());
        } else {
            // Should not be possible
            throw new IllegalArgumentException("Illegal type of property \"" + property.getName() + "\"!");
        }
        return value;
    }

    public StringProperty stringProperty(final ObservablePropertyKey<String> propertyKey) {
        return stringProperty(propertyKey.getKey(), propertyKey.getDefaultValue());
    }

    public StringProperty stringProperty(final String key, final String defaultValue) {
        if (fxProperties.containsKey(key)) {
            return (StringProperty) fxProperties.get(key);
        }
        String value = properties.getProperty(key);
        if (value == null) {
            value = defaultValue;
        }
        final StringProperty stringProperty = new SimpleStringProperty(value);
        fxProperties.put(key, stringProperty);
        return stringProperty;
    }

    public BooleanProperty booleanProperty(final ObservablePropertyKey<Boolean> propertyKey) {
        return booleanProperty(propertyKey.getKey(), propertyKey.getDefaultValue());
    }

    public BooleanProperty booleanProperty(final String key, final boolean defaultValue) {
        if (fxProperties.containsKey(key)) {
            return (BooleanProperty) fxProperties.get(key);
        }
        final String propertyValue = properties.getProperty(key);
        boolean value;
        if (propertyValue != null) {
            value = Boolean.valueOf(propertyValue);
        } else {
            value = defaultValue;
        }
        final BooleanProperty booleanProperty = new SimpleBooleanProperty(value);
        fxProperties.put(key, booleanProperty);
        return booleanProperty;
    }

    public IntegerProperty integerProperty(final ObservablePropertyKey<Integer> propertyKey) {
        return integerProperty(propertyKey.getKey(), propertyKey.getDefaultValue());
    }

    public IntegerProperty integerProperty(final String key, final int defaultValue) {
        if (fxProperties.containsKey(key)) {
            return (IntegerProperty) fxProperties.get(key);
        }
        int value;
        try {
            value = Integer.valueOf(properties.getProperty(key));
        } catch (final NumberFormatException | NullPointerException e) {
            value = defaultValue;
        }
        final IntegerProperty integerProperty = new SimpleIntegerProperty(value);
        fxProperties.put(key, integerProperty);
        return integerProperty;
    }

    public LongProperty longProperty(final ObservablePropertyKey<Long> propertyKey) {
        return longProperty(propertyKey.getKey(), propertyKey.getDefaultValue());
    }

    public LongProperty longProperty(final String key, final long defaultValue) {
        if (fxProperties.containsKey(key)) {
            return (LongProperty) fxProperties.get(key);
        }
        long value;
        try {
            value = Long.valueOf(properties.getProperty(key));
        } catch (final NumberFormatException | NullPointerException e) {
            value = defaultValue;
        }
        final LongProperty longProperty = new SimpleLongProperty(value);
        fxProperties.put(key, longProperty);
        return longProperty;
    }

    public FloatProperty floatProperty(final ObservablePropertyKey<Float> propertyKey) {
        return floatProperty(propertyKey.getKey(), propertyKey.getDefaultValue());
    }

    public FloatProperty floatProperty(final String key, final float defaultValue) {
        if (fxProperties.containsKey(key)) {
            return (FloatProperty) fxProperties.get(key);
        }
        float value;
        try {
            value = Float.valueOf(properties.getProperty(key));
        } catch (final NumberFormatException | NullPointerException e) {
            value = defaultValue;
        }
        final FloatProperty floatProperty = new SimpleFloatProperty(value);
        fxProperties.put(key, floatProperty);
        return floatProperty;
    }

    public DoubleProperty doubleProperty(final ObservablePropertyKey<Double> propertyKey) {
        return doubleProperty(propertyKey.getKey(), propertyKey.getDefaultValue());
    }

    public DoubleProperty doubleProperty(final String key, final double defaultValue) {
        if (fxProperties.containsKey(key)) {
            return (DoubleProperty) fxProperties.get(key);
        }
        double value;
        try {
            value = Double.valueOf(properties.getProperty(key));
        } catch (final NumberFormatException | NullPointerException e) {
            value = defaultValue;
        }
        final DoubleProperty doubleProperty = new SimpleDoubleProperty(value);
        fxProperties.put(key, doubleProperty);
        return doubleProperty;
    }

    public String getFilePath() {
        return filePath;
    }
}

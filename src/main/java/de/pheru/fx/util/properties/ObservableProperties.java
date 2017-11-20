package de.pheru.fx.util.properties;

import javafx.beans.property.*;
import javafx.util.StringConverter;

import java.io.*;
import java.util.*;

public class ObservableProperties {

    protected static final String NO_FILEPATH_EXCEPTION_MSG = "No filepath provided! Use save(comments, filepath) or make sure to call load(filepath) before saving.";

    private String filePath;
    private final Properties properties;
    private final Map<String, Property<?>> fxProperties;
    private final Map<Class<?>, StringConverter<?>> stringConverters;

    public ObservableProperties() {
        properties = new Properties() {
            // alphabetical key order
            @Override
            public synchronized Enumeration<Object> keys() {
                return Collections.enumeration(new TreeSet<>(super.keySet()));
            }
        };
        fxProperties = new HashMap<>();
        stringConverters = new HashMap<>();
    }

    public void load(final String filePath) throws IOException {
        properties.clear();
        fxProperties.clear();
        try (final InputStream inputStream = new FileInputStream(filePath)) {
            properties.load(inputStream);
        }
        this.filePath = filePath;
    }

    public void save(final String comments) throws IOException {
        if (filePath == null) {
            throw new IllegalStateException(NO_FILEPATH_EXCEPTION_MSG);
        }
        save(comments, filePath);
    }

    public void save(final String comments, final String filePath) throws IOException {
        for (final Map.Entry<String, Property<?>> entry : fxProperties.entrySet()) {
            final String value = getPropertyValueAsString(entry.getValue());
            properties.setProperty(entry.getKey(), value);
        }
        try (final OutputStream outputStream = new FileOutputStream(filePath)) {
            properties.store(outputStream, comments);
        }
        this.filePath = filePath;
    }

    public <T> void registerConverter(final Class<T> clazz, final StringConverter<T> converter) {
        stringConverters.put(clazz, converter);
    }

    public boolean contains(final ObservablePropertyKey<?> key) {
        return contains(key.getKey());
    }

    public boolean contains(final String key) {
        return properties.containsKey(key);
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
        } else if (property instanceof ObjectProperty) {
            final Object object = ((ObjectProperty) property).get();
            final StringConverter stringConverter = stringConverters.get(object.getClass());
            value = stringConverter.toString(object);
        } else {
            throw new IllegalArgumentException("Illegal type of property \"" + property.getName() + "\"!");
        }
        return value;
    }

    public <T> ObjectProperty<T> objectProperty(final ObservablePropertyKey<T> propertyKey) {
        return objectProperty(propertyKey.getKey(), propertyKey.getDefaultValue());
    }

    public <T> ObjectProperty<T> objectProperty(final String key, final T defaultValue) {
        if (fxProperties.containsKey(key)) {
            return (ObjectProperty<T>) fxProperties.get(key);
        }
        final String propertyValue = properties.getProperty(key);
        T value;
        if (propertyValue != null) {
            final StringConverter<T> stringConverter = (StringConverter<T>) stringConverters.get(defaultValue.getClass());
            if (stringConverter == null) {
                throw new IllegalStateException("No StringConverter registered for " + defaultValue.getClass() + "!");
            }
            value = stringConverter.fromString(propertyValue);
        } else {
            value = defaultValue;
        }
        final ObjectProperty<T> objectProperty = new SimpleObjectProperty<T>(value);
        fxProperties.put(key, objectProperty);
        return objectProperty;
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

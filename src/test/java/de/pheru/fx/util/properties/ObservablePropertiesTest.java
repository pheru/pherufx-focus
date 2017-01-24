package de.pheru.fx.util.properties;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ObservablePropertiesTest {

    private static final String DEFAULT_STRING = "defaultValue";
    private static final boolean DEFAULT_BOOLEAN = false;
    private static final int DEFAULT_INTEGER = 9999;
    private static final long DEFAULT_LONG = 999999L;
    private static final float DEFAULT_FLOAT = 99.99F;
    private static final double DEFAULT_DOUBLE = 999.99;

    private ObservableProperties observableProperties;

    @Before
    public void setUp() throws Exception {
        observableProperties = new ObservableProperties(new File("src/test/resources/properties/testproperties.foo").getAbsolutePath());
        observableProperties.load();
    }

    @Test
    public void saveNew() throws Exception {
        final File file = new File("src/test/resources/properties/savetestnew.bar");
        if (file.exists()) {
            if (!file.delete()) {
                fail("File already exists and could not be deleted");
            }
        }

        observableProperties = new ObservableProperties(file.getAbsolutePath());
        observableProperties.stringProperty("newString", "new");
        observableProperties.integerProperty("newInteger", 123);
        observableProperties.longProperty("newLong", 12345L);
        observableProperties.floatProperty("newFloat", 123.45F);
        observableProperties.doubleProperty("newDouble", 12345.67);
        observableProperties.booleanProperty("newBoolean", true);
        observableProperties.save("comment");

        final Properties savedProperties = new Properties();
        try (final FileInputStream inputStream = new FileInputStream(file)) {
            savedProperties.load(inputStream);
        }
        assertEquals("new", savedProperties.getProperty("newString"));
        assertEquals("12345", savedProperties.getProperty("newLong"));

        file.delete();
    }

    @Test
    public void saveExisting() throws Exception {
        final File newFile = new File("src/test/resources/properties/testproperties.fooNEW");
        if (newFile.exists()) {
            if (!newFile.delete()) {
                fail("New file already exists and could not be deleted");
            }
        }

        final StringProperty stringProperty = observableProperties.stringProperty("stringKey", DEFAULT_STRING);
        stringProperty.set("newValue");
        final DoubleProperty doubleProperty = observableProperties.doubleProperty("doubleKey", DEFAULT_DOUBLE);
        doubleProperty.set(99.99);
        observableProperties.save("comment", newFile.getAbsolutePath());

        final Properties savedProperties = new Properties();
        try (final FileInputStream inputStream = new FileInputStream(newFile)) {
            savedProperties.load(inputStream);
        }
        assertEquals("1234", savedProperties.getProperty("integerKey"));
        assertEquals("newValue", savedProperties.getProperty("stringKey"));
        assertEquals("99.99", savedProperties.getProperty("doubleKey"));

        newFile.delete();
    }

    @Test
    public void stringProperty() throws Exception {
        assertEquals("stringValue", observableProperties.stringProperty("stringKey", DEFAULT_STRING).get());
        assertEquals("", observableProperties.stringProperty("stringKeyEmpty", DEFAULT_STRING).get());
        assertEquals(DEFAULT_STRING, observableProperties.stringProperty("stringKeyNull", DEFAULT_STRING).get());
    }

    @Test
    public void booleanProperty() throws Exception {
        assertEquals(true, observableProperties.booleanProperty("booleanKey", DEFAULT_BOOLEAN).get());
        assertEquals(true, observableProperties.booleanProperty(new ObservablePropertyKey<>("booleanKey", DEFAULT_BOOLEAN)).get());
        assertEquals(true, observableProperties.booleanProperty("booleanKey", DEFAULT_BOOLEAN).get());
        assertEquals(DEFAULT_BOOLEAN, observableProperties.booleanProperty("booleanKeyEmpty", DEFAULT_BOOLEAN).get());
        assertEquals(DEFAULT_BOOLEAN, observableProperties.booleanProperty("booleanKeyNull", DEFAULT_BOOLEAN).get());
    }

    @Test
    public void integerProperty() throws Exception {
        assertEquals(1234, observableProperties.integerProperty("integerKey", DEFAULT_INTEGER).get());
        assertEquals(DEFAULT_INTEGER, observableProperties.integerProperty("integerKeyEmpty", DEFAULT_INTEGER).get());
        assertEquals(DEFAULT_INTEGER, observableProperties.integerProperty("integerKeyInvalid", DEFAULT_INTEGER).get());
        assertEquals(DEFAULT_INTEGER, observableProperties.integerProperty("integerKeyNull", DEFAULT_INTEGER).get());
    }

    @Test
    public void longProperty() throws Exception {
        assertEquals(123456, observableProperties.longProperty("longKey", DEFAULT_LONG).get());
        assertEquals(DEFAULT_LONG, observableProperties.longProperty("longKeyEmpty", DEFAULT_LONG).get());
        assertEquals(DEFAULT_LONG, observableProperties.longProperty("longKeyInvalid", DEFAULT_LONG).get());
        assertEquals(DEFAULT_LONG, observableProperties.longProperty("longKeyNull", DEFAULT_LONG).get());
    }

    @Test
    public void floatProperty() throws Exception {
        assertEquals(12.34F, observableProperties.floatProperty("floatKey", DEFAULT_FLOAT).get(), 0.0F);
        assertEquals(DEFAULT_FLOAT, observableProperties.floatProperty("floatKeyEmpty", DEFAULT_FLOAT).get(), 0.0F);
        assertEquals(DEFAULT_FLOAT, observableProperties.floatProperty("floatKeyInvalid", DEFAULT_FLOAT).get(), 0.0F);
        assertEquals(DEFAULT_FLOAT, observableProperties.floatProperty("floatKeyNull", DEFAULT_FLOAT).get(), 0.0F);
        assertEquals((Float) 1234.0F, (Float) observableProperties.floatProperty("floatKeyNoDecimal", DEFAULT_FLOAT).get());
    }

    @Test
    public void doubleProperty() throws Exception {
        assertEquals(1234.56, observableProperties.doubleProperty("doubleKey", DEFAULT_DOUBLE).get(), 0.0);
        assertEquals(DEFAULT_DOUBLE, observableProperties.doubleProperty("doubleKeyEmpty", DEFAULT_DOUBLE).get(), 0.0);
        assertEquals(DEFAULT_DOUBLE, observableProperties.doubleProperty("doubleKeyInvalid", DEFAULT_DOUBLE).get(), 0.0);
        assertEquals(DEFAULT_DOUBLE, observableProperties.doubleProperty("doubleKeyNull", DEFAULT_DOUBLE).get(), 0.0);
        assertEquals(123456.0, observableProperties.doubleProperty("doubleKeyNoDecimal", DEFAULT_DOUBLE).get(), 0.0);
    }

    private String getAbsoluteResourcePath(final String resourcePath) throws Exception {
        final File file = new File(getClass().getClassLoader().getResource(resourcePath).getFile());
        return file.getAbsolutePath();
    }

}
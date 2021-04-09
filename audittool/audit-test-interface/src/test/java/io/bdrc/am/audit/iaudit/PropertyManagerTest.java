package io.bdrc.am.audit.iaudit;

import org.apache.commons.io.IOUtils;
import org.junit.*;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class PropertyManagerTest {

    public PropertyManagerTest() {
        propertyFilePath = "/testResource.properties";
    }

    private InputStream inStream;

    // These properties should be in the core resource file, see "propertyPath
    private final String defaultTestProperty = "hibbidy.hobbidy.ima.freemstalizer";
    private final String UserConfigPathKey = "UserConfigPath";
    private final HashMap<String, String> userPropertyMap = new HashMap<String, String>() {{
        put("user.property.1", "u.p.2value");
        put("user.property.2", "u.p.2value");
    }};

    private final HashMap<String, String> systemPropertyMap = new HashMap<String, String>() {{
        put("sys.property.1", "s.p.2value");
        put("sys.property.2", "s.p.2value");
    }};
    private final String propertyFilePath;


    @Test
    public void PropertyManagerClassResourceTest() {

        PropertyManager pm = PropertyManager.PropertyManagerBuilder().MergeClassResource(propertyFilePath, getClass());
        testBuiltDefaultPM(defaultTestProperty, pm);
    }

    @Test
    public void PropertyManagerDefaultResourceTest() {

        PropertyManager pm =
                PropertyManager.PropertyManagerBuilder().MergeClassResource(propertyFilePath, getClass());
        testBuiltDefaultPM(defaultTestProperty, pm);
    }

    private void testBuiltDefaultPM(final String testProperty, final PropertyManager pm) {
        // region private fields
        final int expectedSequenceLength = 1234;
        int actualValue = pm.getPropertyInt(testProperty);
        assertEquals(expectedSequenceLength, actualValue);
    }

    @Test
    public void PMUserResourceTestNotExists() {
        PropertyManager pm =
                PropertyManager.PropertyManagerBuilder().MergeClassResource(propertyFilePath, getClass());

        Path userPropertiesFilePath = Paths.get(pm.getPropertyString(UserConfigPathKey));
        if (userPropertiesFilePath.toFile().exists()) {
            DeleteUserProperties(userPropertiesFilePath);
        }

        // Should fail gracefully
        pm.MergeUserConfig();

        // but still have the default
        testBuiltDefaultPM(defaultTestProperty, pm);
        testEmptyProperties(pm, userPropertyMap);

    }

    @Test
    public void PMUserResourceTestExists() throws IOException {
        PropertyManager pm =
                PropertyManager.PropertyManagerBuilder().MergeClassResource(propertyFilePath, getClass());

        Path userPropertiesFilePath = Paths.get(pm.getPropertyString(UserConfigPathKey));
        RebuildUserProperties(userPropertiesFilePath);

        pm.MergeUserConfig();

        // but still have the default
        testBuiltDefaultPM(defaultTestProperty, pm);

        // and has the user properties
        testLoadedProperties(pm, userPropertyMap);
    }

    @Test
    public void PMCommandLineTest() throws IOException {

        PropertyManager pm =
                PropertyManager.PropertyManagerBuilder().MergeClassResource(propertyFilePath, getClass());
        Path userPropertiesFilePath = Paths.get(pm.getPropertyString(UserConfigPathKey));
        RebuildUserProperties(userPropertiesFilePath);

        pm.MergeUserConfig();

        // but still have the default
        testBuiltDefaultPM(defaultTestProperty, pm);

        // and has the user properties
        testLoadedProperties(pm,userPropertyMap);

        testEmptyProperties(pm, systemPropertyMap);

        pm.MergeProperties(System.getProperties());

        testLoadedProperties(pm, systemPropertyMap);

    }

    // region test helpers methods

    /**
     * Recreates the user properties file
     *
     * @param userPropertiesFilePath Path to properties file to rebuild with hardwired hashmap
     * @throws IOException if no readwrite access to file at path
     */
    private void RebuildUserProperties(final Path userPropertiesFilePath) throws IOException {
        if (userPropertiesFilePath.toFile().exists()) {
            DeleteUserProperties(userPropertiesFilePath);
        }
        BuildUserProperties(userPropertiesFilePath, userPropertyMap);
    }


    /**
     * Validates the  properties are not loaded
     *
     * @param pm property manager under test
     * @param sourceMap properties which should not be present
     */
    private void testLoadedProperties(final PropertyManager pm, HashMap<String,String>sourceMap) {
        // and not have the property we build in BuildUserConfig
        sourceMap.forEach((key, value) -> assertEquals(pm.getPropertyString(key), value));
    }

    /**
     * Validates the  properties are  loaded
     *
     * @param pm property manager under test
     * @param sourceMap properties which are expected to be present
     */
    private void testEmptyProperties(final PropertyManager pm, HashMap<String,String>sourceMap) {
        sourceMap.forEach((key, value) -> assertEquals(pm.getPropertyString(key), ""));
    }


    // endregion

    @Before
    public void StringResource() throws IOException {

        // Set up a string for properties
        StringReader sr = new StringReader("HardWired.prop1 = value 1\nHardwired.prop2 = value 2");
        inStream = IOUtils.toInputStream(IOUtils.toString(sr));

        systemPropertyMap.forEach(System::setProperty);
    }

    @After
    public void closeup() {
        try {
            if (inStream != null) inStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // end region

    private void BuildUserProperties(Path propertiesPath, HashMap<String, String> initialProperties) throws IOException {
        FileWriter myWriter = new FileWriter(propertiesPath.toAbsolutePath().toString());
        initialProperties.forEach((key, value) -> {
            try {
                myWriter.write(String.format("%s=%s\n",
                        key,
                        value));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        myWriter.close();
    }

    private void DeleteUserProperties(Path propertiesPath) {
        Boolean didD = propertiesPath.toFile().delete();
        assertEquals(true, didD);
    }


}
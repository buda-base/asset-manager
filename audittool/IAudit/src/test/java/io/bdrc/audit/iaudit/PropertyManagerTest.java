package io.bdrc.audit.iaudit;

import org.apache.commons.io.IOUtils;
import org.junit.*;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * Test property manager.
 *
 * Property manager allows definition of properties at three file locations, which are
 * read in sequence - a property given later in this stream, overrides one given
 *  shell.properties, in classpath of main module (either this test module or the
 *  shell
 *
 *  user properties, at a location given in shell.properties "UserConfigPath"
 *
 *  On the command line, by using JVM arguments -Dsome.property=some.value
 *
 *  There is a special command line property -DEnvConfigPath=somefilevalue which loads the properties in the given file
 *  (relative path name resolved with respect to "user.dir", the current dir where invoked from.)
 *
 *
 */
public class PropertyManagerTest {

    public PropertyManagerTest() {
        propertyFilePath = "/testResource.properties";
    }

    private InputStream inStream;

    // These properties should be in the core resource file, see "propertyPath
    private final String defaultTestProperty = "hibbidy.hobbidy.ima.freemstalizer";
    private final String UserConfigPathKey = "UserConfigPath";
    private String loadedUserConfigPath ;
    private final HashMap<String, String> userPropertyMap = new HashMap<String, String>() {{
        put("user.property.1", "u.p.2value");
        put("user.property.2", "u.p.2value");
    }};

    private final HashMap<String, String> commandLinePropertySourceMap = new HashMap<String, String>() {{
        put("sys.property.1", "s.p.2value");
        put("sys.property.2", "s.p.2value");
    }};
    private final String propertyFilePath;

    private static Properties savedEnvironmentProperties = new Properties();
    @BeforeClass
    public static void SaveEnvironment()
    {
        // call by reference, so need to copy
        savedEnvironmentProperties.clear();
        System.getProperties().forEach(savedEnvironmentProperties::put);
    }

    @AfterClass
    public static void RestoreEnvironment() {
        System.setProperties(new Properties());
        savedEnvironmentProperties.forEach((key, value) -> {
            System.setProperty(key.toString(), value.toString());
        });
    }

    @Before
    public void SetupResources() throws IOException {

        RestoreEnvironment();
        // Set up a string for properties
        StringReader sr = new StringReader("HardWired.prop1 = value 1\nHardwired.prop2 = value 2");
        inStream = IOUtils.toInputStream(IOUtils.toString(sr));

        Path loadedUserConfigPath =
                PropertyManager.PropertyManagerBuilder().MergeClassResource(propertyFilePath, getClass()).getPropertyPath(UserConfigPathKey);

        BuildUserProperties(loadedUserConfigPath,userPropertyMap);

    }

    @After
    public void closeup() {
        try {
            if (inStream != null) inStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Test
    public void PropertyManagerClassResourceTest() {

        PropertyManager pm = PropertyManager.PropertyManagerBuilder().MergeClassResource(propertyFilePath, getClass());
        confirmBuiltDefaultPM(defaultTestProperty, pm);
    }

    @Test
    public void PMUserResourceTestNotExists() {
        PropertyManager pm =
                PropertyManager.PropertyManagerBuilder().MergeClassResource(propertyFilePath, getClass());

        Path userPropertiesFilePath = pm.getPropertyPath(UserConfigPathKey);
        if (userPropertiesFilePath.toFile().exists()) {
            DeleteUserProperties(userPropertiesFilePath);
        }

        // Should fail gracefully
        pm.MergeUserConfig();

        // but still have the default
        confirmBuiltDefaultPM(defaultTestProperty, pm);
        confirmEmptyProperties(pm, userPropertyMap);

    }

    @Test
    public void PMUserResourceTestExists() throws IOException {
        PropertyManager pm =
                PropertyManager.PropertyManagerBuilder().MergeClassResource(propertyFilePath, getClass());

        Path userPropertiesFilePath = Paths.get(pm.getPropertyString(UserConfigPathKey));
        RebuildUserProperties(userPropertiesFilePath, userPropertyMap);

        pm.MergeUserConfig();

        // but still have the default
        confirmBuiltDefaultPM(defaultTestProperty, pm);

        // and has the user properties
        confirmLoadedProperties(pm, userPropertyMap);
    }

    @Test
    public void PMCommandLineTest() throws IOException {

        PropertyManager pm =
                PropertyManager.PropertyManagerBuilder()
                        .MergeClassResource(propertyFilePath, getClass())
                        .MergeUserConfig();

        // but still have the default
        confirmBuiltDefaultPM(defaultTestProperty, pm);

        // and has the user properties
        confirmLoadedProperties(pm,userPropertyMap);

        confirmEmptyProperties(pm, commandLinePropertySourceMap);

        LoadTestSystemProperties();

        pm.MergeProperties(System.getProperties());

        // confirm properties we care about, should already be in System properties
        confirmLoadedProperties(pm, commandLinePropertySourceMap);

    }

    // region test overwriting properties

    @Test
    public void PMUserPropertyOverwritesDefault() throws IOException {
        PropertyManager pm =
                PropertyManager.PropertyManagerBuilder().MergeClassResource(propertyFilePath, getClass());

        String expectedDefaultPropertyValue = "UserOverrideDefault";
        Path userPropertiesFilePath = pm.getPropertyPath(UserConfigPathKey);

        // shouldnt already be here
        assertNotEquals(pm.getPropertyString(defaultTestProperty),expectedDefaultPropertyValue);

        // Rebuild with a changed property
        userPropertyMap.put(defaultTestProperty,expectedDefaultPropertyValue);
        RebuildUserProperties(userPropertiesFilePath,userPropertyMap);

        pm.MergeUserConfig();

        // should be there now
        assertEquals(pm.getPropertyString(defaultTestProperty),expectedDefaultPropertyValue);
        userPropertyMap.remove(defaultTestProperty);

        // but the rest should be unaffected
        confirmLoadedProperties(pm,userPropertyMap);
    }

    @Test
    public void PMCommandPropertyOverwritesDefault() throws IOException {

        PropertyManager pm =
                PropertyManager.PropertyManagerBuilder()
                        .MergeClassResource(propertyFilePath, getClass())
                        .MergeUserConfig();

        String expectedDefaultPropertyValue = "CommandOverrideDefault";

        // shouldnt already be here
        assertNotEquals(pm.getPropertyString(defaultTestProperty),expectedDefaultPropertyValue);

        // Add the override
        System.setProperty( defaultTestProperty,expectedDefaultPropertyValue);
        pm.MergeProperties(System.getProperties());

        // should be there now
        assertEquals(pm.getPropertyString(defaultTestProperty),expectedDefaultPropertyValue);

        // but the rest should be unaffected
        confirmLoadedProperties(pm,userPropertyMap);

        confirmEmptyProperties(pm, commandLinePropertySourceMap);

        LoadTestSystemProperties();
        pm.MergeProperties(System.getProperties());
        confirmLoadedProperties(pm, commandLinePropertySourceMap);
    }

    // endregion

    // region test helpers methods
    private void confirmBuiltDefaultPM(final String testProperty, final PropertyManager pm) {
        // region private fields
        final int expectedSequenceLength = 1234;
        int actualValue = pm.getPropertyInt(testProperty);
        assertEquals(expectedSequenceLength, actualValue);
    }



    /**
     * Validates the  properties are not loaded
     *
     * @param pm property manager under test
     * @param sourceMap properties which should not be present
     */
    private void confirmLoadedProperties(final PropertyManager pm, HashMap<String,String>sourceMap) {
        // and not have the property we build in BuildUserConfig
        sourceMap.forEach((key, value) -> assertEquals(value, pm.getPropertyString(key)));
    }

    /**
     * Validates the  properties are  loaded
     *
     * @param pm property manager under test
     * @param sourceMap properties which are expected to NOT be present
     */
    private void confirmEmptyProperties(final PropertyManager pm, HashMap<String,String>sourceMap) {
        sourceMap.forEach((key, value) -> assertEquals("",pm.getPropertyString(key) ));
    }
    // endregion
    // endregion

    /**
     * Recreates the user properties file
     *
     * @param userPropertiesFilePath Path to properties file to rebuild with hardwired hashmap
     * @throws IOException if no readwrite access to file at path
     */
    private void RebuildUserProperties(final Path userPropertiesFilePath, HashMap<String,String> propertyMap) throws IOException {
        if (userPropertiesFilePath.toFile().exists()) {
            DeleteUserProperties(userPropertiesFilePath);
        }
        BuildUserProperties(userPropertiesFilePath, propertyMap);
    }

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

    private void LoadTestSystemProperties() {
        // Set up properties for command line load
        commandLinePropertySourceMap.forEach(System::setProperty);
    }
}
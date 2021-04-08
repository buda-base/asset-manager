package io.bdrc.am.audit.iaudit;

import org.apache.commons.io.IOUtils;
import org.junit.*;

import java.io.*;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;

public class PropertyManagerTest  {

    public PropertyManagerTest() {
        propertyPath = "/testResource.properties";
    }

    private InputStream inStream;

    @Test
    public void PropertyManagerClassResourceTest() {
        final String testProperty = "hibbidy.hobbidy.ima.freemstalizer";
        PropertyManager pm = PropertyManager.PropertyManagerBuilder().MergeClassResource(propertyPath,getClass());
        // region private fields
        final int expectedSequenceLength = 1234;
        int actualValue = pm.getPropertyInt(testProperty);
        assertEquals(expectedSequenceLength, actualValue);
    }

 @Test
    public void PropertyManagerDefaultResourceTest() {
        final String testProperty = "hibbidy.hobbidy.ima.freemstalizer";

     PropertyManager pm =
             PropertyManager.PropertyManagerBuilder().MergeClassResource(propertyPath,getClass())
                     .MergeUserConfig()
                     .MergeProperties(System.getProperties());
        // region private fields
        final int expectedSequenceLength = 1234;
        int actualValue = pm.getPropertyInt(testProperty);
        assertEquals(expectedSequenceLength, actualValue);
    }

    @Test
    public void PMUserResourceTest() {

    }
    @Before
    public void StringResource() throws IOException {

        // Set up a string for properties
            StringReader sr = new StringReader("HardWired.prop1 = value 1\nHardwired.prop2 = value 2");
            inStream = IOUtils.toInputStream(IOUtils.toString(sr));

            // Set up a file for user file properties - see
        }

    @After
    public void closeup() {
        try {
            if (inStream != null) inStream.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    final private String propertyPath;
    // end region


}
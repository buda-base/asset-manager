package io.bdrc.am.audit.iaudit;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class PropertyManagerTest  {

    public PropertyManagerTest() {
        propertyPath = "/testResource.properties";
    }

    @Test
    public void getPropertyInt() {
        final String testProperty = "hibbidy.hobbidy.ima.freemstalizer";
        int actualValue = new ClassPropertyManager(propertyPath, getClass()).getPropertyInt(testProperty);
        // region private fields
        final int expectedSequenceLength = 1234;
        assertEquals(expectedSequenceLength, actualValue);
    }


    final private String propertyPath;
    // end region

    @Test
    public void TestFilePropertyManager() throws IOException {
        // Get current directory
        File testPropFile = File.createTempFile("Iaudit", "property");
        FileWriter fw = new FileWriter(testPropFile, false);
        final String propKey = "propKey";
        final String propValue = "propValue";
        fw.write(propKey + " = " + propValue);
        fw.close();
        FilePropertyManager fpm = new FilePropertyManager(testPropFile.getAbsolutePath());
        String resultPropValue = fpm.getPropertyString(propKey);
        Assert.assertEquals(propValue, resultPropValue);
    }
}
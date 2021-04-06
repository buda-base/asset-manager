package io.bdrc.am.audit.iaudit;

import org.junit.Assert;
import org.junit.BeforeClass;
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
    public void getClassPropertyInt() {
        final String testProperty = "hibbidy.hobbidy.ima.freemstalizer";
        int actualValue = new ClassPropertyManager(propertyPath, getClass()).getPropertyInt(testProperty);
        // region private fields
        final int expectedSequenceLength = 1234;
        assertEquals(expectedSequenceLength, actualValue);
    }

    @BeforeClass
    public void WriteResource() {


    }

    final private String propertyPath;
    // end region

}
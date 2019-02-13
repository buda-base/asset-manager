package io.bdrc.am.audit;

import io.bdrc.am.audit.audittests.PropertyManager;
import org.junit.*;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import static org.junit.Assert.*;

public class PropertyManagerTest extends AuditTestTestBase {

    @Test
    public void getPropertyInt() throws IOException {
        int actualValue = new PropertyManager(propertyPath).getPropertyInt(testProperty);
        assertEquals(expectedSequenceLength,actualValue);
    }


    
    // region private fields
    private int expectedSequenceLength= 1234;
    private String testProperty="hibbidy.hobbidy.ima.freemstalizer" ;
    private String propertyPath = "/testResource.properties" ;
    // end region

}
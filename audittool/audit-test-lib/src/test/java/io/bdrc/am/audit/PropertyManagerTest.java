package io.bdrc.am.audit;

import io.bdrc.am.audit.audittests.PropertyManager;
import org.junit.Test;

import static org.junit.Assert.*;

public class PropertyManagerTest extends AuditTestTestBase {

    public PropertyManagerTest() {
        propertyPath = "/testResource.properties";
    }

    @Test
    public void getPropertyInt() {
        final String testProperty = "hibbidy.hobbidy.ima.freemstalizer";
        int actualValue = new PropertyManager(propertyPath).getPropertyInt(testProperty);
        // region private fields
        final int expectedSequenceLength = 1234;
        assertEquals(expectedSequenceLength, actualValue);
    }


    private String propertyPath;
    // end region

}
package io.bdrc.audit.audittests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

// TODO: Create a mockable interface for calling eXist
//https://www.twilio.com/blog/5-ways-to-make-http-requests-in-java

/**
 * test listed parents for image groups which only contain scan request images
 */
public class ImageGroupHasImages extends ImageGroupParents {

    // For unit testing
    public ImageGroupHasImages() {
        this(LoggerFactory.getLogger((ImageGroupHasImages.class)));
    }

    public ImageGroupHasImages( Logger logger){
        this(logger, TestDictionary.NO_IMAGES_TEST_NAME);
    }

    public ImageGroupHasImages( Logger logger, String testName){
        super(testName);
        sysLogger = logger;
    }

    public class ImageGroupHasImagesOperation implements ITestOperation {
        public String getName() {
            return getTestName();
        }

        public void run() throws java.io.IOException {
            Path dir = Paths.get(getPath());
            FailTest(42,"Because I said so");
        }
    }


    @Override
    public void LaunchTest() {

        // have base class tests here?
        // Yes, under the doctrine of One responsibility
        RunBaseTests();
        if (IsTestFailed()) {
            return;
        }
        TestWrapper(new ImageGroupHasImages.ImageGroupHasImagesOperation());
    }
}

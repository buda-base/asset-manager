package io.bdrc.audit.iaudit;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Paths;

public class TestFactoryTest {

    @org.junit.Test
    @Ignore
    public void loadEmptyTests() throws MalformedURLException {
        @SuppressWarnings("ConstantConditions") TestDictionary td = TestFactory.LoadTests(null);
        Assert.assertNotNull("Expected null", td);
        Assert.assertEquals(0, td.getTestDictionary().size());

    }


    @Test
    @Ignore
    public void loadFactoryTests() throws MalformedURLException {

        URI lib = Paths.get("/Users/jimk/dev/AssetManager/asset-manager/audittool/audit-test-lib/target/audit-test" +
                "-lib-1.0-SNAPSHOT-jar-with-dependencies.jar").toFile().toURI();


        TestDictionary td = TestFactory.LoadTests(lib);
        Assert.assertNotNull("Expected null", td);
        Assert.assertEquals(0, td.getTestDictionary().size());

    }
}
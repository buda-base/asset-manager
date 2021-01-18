package io.bdrc.am.audit;

import io.bdrc.am.audit.audittests.ImageSizeTests;
import io.bdrc.am.audit.iaudit.LibOutcome;
import io.bdrc.am.audit.iaudit.IAuditTest;
import io.bdrc.am.audit.iaudit.Outcome;
import io.bdrc.am.audit.iaudit.TestResult;
import io.bdrc.am.audit.iaudit.message.TestMessage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import static io.bdrc.am.audit.audittests.TestArgNames.DERIVED_GROUP_PARENT;
import static io.bdrc.am.audit.audittests.TestArgNames.MAX_IMAGE_FILE_SIZE;

/**
 * Parameterized test
 */
@RunWith(Parameterized.class)
public class TestImageSizeParametersTest {

    private final Boolean expectedToPass;
    private final Hashtable<String, String> properties;
//endregion



    public TestImageSizeParametersTest(String pTestSizeExpression, Boolean pShouldTestPass) {
        //region members to save parameter values
        expectedToPass = pShouldTestPass;


        properties = new Hashtable<String, String>() {
            {
                // this is invariant, because it's not the variable under test
                put(DERIVED_GROUP_PARENT, "testImages");
                put(MAX_IMAGE_FILE_SIZE, pTestSizeExpression);
            }
        };
    }

//    public static Collection TestParameters()

    @Parameterized.Parameters
    public static List<?> TestParameters()
    {

        // note that  the expected result is only the result of the parsing. See the test
        return Arrays.asList(new Object[][]{

                // test parsing and regex
                {"1", true},
                {"12345K", true},
                {"", false},
                {"doomed", false},
                {"123.45.67", false},
                {".abc.def", false},
                {"1234567689.", true},
                {".123,abc", false},
                {"a55", false},

                // Test invalid scale
                {"55a", false},
                {"5IshouldntPass", false},
                {"325   L", false},

                // Valid scale
                {"324", true},
                {"325", true},
                {"321k", true},
                {"321K", true},
                {"325k", true},
                {"325K", true},
                {"325 K", true},
                {"325   K", true},
                {"325m", true},
                {"325 m", true},
                {"325   m", true},
                {"325M", true},
                {"325 M", true},
                {"325   M", true},

                {"12", true},
        });
    }

    private IAuditTest fileSizeTest;

    @Before
    public void setup() {
        fileSizeTest = new ImageSizeTests();
    }

    @Test
    public void testFileSizeParameters() {

        //region Test Raw Pattern matching
        /*
        //final String pat = "([\\d.,]+.?[\\d]*)\\s*([KMG]?)";
        final String pat = "([\\d.,]+)\\s*([KMG]?)";
        // final String pat = "([\\d.,]+)\\s*(\\w)";

        final Matcher m = Pattern.compile(pat).matcher(properties.get(MAX_IMAGE_SIZE_KEY).trim().replaceAll(",","")
                                                                                          .toUpperCase());
        int count = 0;
        while (m.find()) {
            count++;
            System.out.println("test " + properties.get(MAX_IMAGE_SIZE_KEY) + " found: " + count + " : "
                                       + m.start() + " - " + m.end() + " group(1):" +  m.group(1) + ": group(2):" +
                                       m.group(2)+ ":");
        }


        Assert.assertTrue(String.format("String %s  expected %s",properties.get
                                                                                                             (MAX_IMAGE_SIZE_KEY),expectedToPass.toString()),
                expectedToPass ==
                count > 0);
         */
        // endregion

        String fullPath= Paths.get("src/test/images/WPass").toAbsolutePath().toString();

        // must be a directory which would pass a test
        // fileSizeTest.setParams("src/test/images/WPass", properties);
        fileSizeTest.setParams(fullPath, properties);

        fileSizeTest.LaunchTest();
        TestResult tr = fileSizeTest.getTestResult();

        // the expectedToPass
        ArrayList<TestMessage> errors = tr.getErrors();

        // GNARL alert
//        Assert.assertEquals(String.format("Size test parameter %s unexpected result:",properties.get(MAX_IMAGE_SIZE_KEY))
//                ,expectedToPass,
//                tr.getOutcome() == Outcome.PASS ^ ((tr.getOutcome() == Outcome.FAIL) && (errors.get(0).getOutcome()
//                                                                                                 == LibOutcome.BAD_FILE_SIZE_ARG)));

        if (expectedToPass)
        {
            Assert.assertTrue(String.format("Size test parameter %s unexpected result:", properties.get(MAX_IMAGE_FILE_SIZE))
                    , (tr.getOutcome().equals(Outcome.PASS))
                              || (tr.getOutcome().equals(Outcome.FAIL)) && !(errors
                                                                                     .get(0)
                                                                                     .getOutcome()
                                                                                     .equals(LibOutcome
                                                                                                     .BAD_FILE_SIZE_ARG)));
        }
        else
        {
            // failure case is a little more complicated: not only must the test fail, it must fail
            // only to parse: not fail the size test

            Assert.assertTrue(String.format("Size test parameter %s unexpected result:", properties.get
                                                                                                            (MAX_IMAGE_FILE_SIZE))
                    ,
                    (tr.getOutcome().equals(Outcome.FAIL)) && (errors.get(0).getOutcome().equals(LibOutcome
                                                                                                         .BAD_FILE_SIZE_ARG)));
        }
    }

}

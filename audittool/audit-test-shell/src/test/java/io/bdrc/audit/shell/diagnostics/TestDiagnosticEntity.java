package io.bdrc.audit.shell.diagnostics;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;


import static org.junit.jupiter.api.Assertions.*;

class DiagnosticEntityTest {

    Properties _standardProperties ;

    @BeforeEach
    void setUp() {
        _standardProperties = new Properties() {{
                put("prop1","value1");
                put("prop2","value21,value22,value23");
                put("prop3","value31,value32,value33");
                put("prop4","");

        }};
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Get All Map<String, String[]>")
    void testOutputIsInput() {
        DiagnosticEntity diagnosticEntity = new DiagnosticEntity(_standardProperties,",");

        _standardProperties.forEach( (k,v) -> {
            assertTrue(diagnosticEntity.containsKey(k));
            assertEquals(Arrays.asList(v.toString().split(",")), diagnosticEntity.get(k));
        });
    }

    @Test
    @DisplayName("Validate syntax")
    void ValidateSyntax() {

    }


}

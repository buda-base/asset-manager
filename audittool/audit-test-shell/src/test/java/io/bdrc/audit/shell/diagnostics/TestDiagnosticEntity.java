package io.bdrc.audit.shell.diagnostics;

import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class DiagnosticEntityTest {

    Properties _standardProperties ;

    @BeforeEach
    void setUp() {
        _standardProperties = new Properties() {{
            put("prop1", "value1");
            put("prop2", "value21,value22,value23");
            put("prop3", "value31;value32;value33");

        }};
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Get All Properties")
    void testOutputIsInput() {
        DiagnosticEntity diagnosticEntity = new DiagnosticEntity(_standardProperties,",");
        Properties properties = diagnosticEntity.getProperties();
        assertEquals(properties, _standardProperties);
    }

    @Test
    @DisplayName("Replaced properties match")
    void ReplacedPropertiesMatch() {
        DiagnosticEntity diagnosticEntity = new DiagnosticEntity(new Properties() {{
            put("heapify", "hamming");
        }},",");
        diagnosticEntity.setProperties(_standardProperties);
        assertEquals(diagnosticEntity.getProperties(), _standardProperties, "properties mismatch");

    }

    @DisplayName("Initial multi-value-separator matches")
    @Test
    void getMultiValueSeparator() {
        String sep = ",";
        DiagnosticEntity diagnosticEntity = new DiagnosticEntity(new Properties() {{
            put("heapify", "hamming");
        }},sep);

        assertEquals(diagnosticEntity.getMultiValueSeparator(),sep,"separators not equal");
    }

    @DisplayName("Replacing multivalued separator getter")
    @Test
    void setMultiValueSeparator() {
        String sep = ",";
        String newSep = ";";
        DiagnosticEntity diagnosticEntity = new DiagnosticEntity(new Properties() {{
            put("heapify", "hamming");
        }},sep);

        diagnosticEntity.setMultiValueSeparator(newSep);
        assertEquals(diagnosticEntity.getMultiValueSeparator(),newSep,"new separator not read back");
    }

    @DisplayName("Initial properties split by initial separator match")
    @Test
    void initialSeparatorMatches() {

        List<String>reference= Arrays.asList(_standardProperties.getProperty("prop2").split(","));
        DiagnosticEntity diagnosticEntity = new DiagnosticEntity(_standardProperties,",");
        assertEquals(diagnosticEntity.getValues("prop2"),reference,"split strings dont match");
    }

    @DisplayName("Initial properties split by different separator")
    @Test
    void modifiedSeparatorMatches() {

        List<String>reference= Arrays.asList(_standardProperties.getProperty("prop3").split(";"));
        DiagnosticEntity diagnosticEntity = new DiagnosticEntity(_standardProperties,",");
        assertNotEquals(diagnosticEntity.getValues("prop3"),reference,"split strings dont match");

        diagnosticEntity.setMultiValueSeparator(";");
        assertEquals(diagnosticEntity.getValues("prop3"),reference,"split strings dont match");
    }


}

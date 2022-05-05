package io.bdrc.audit.shell.diagnostics;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.*;

import java.util.Map;


import static org.junit.jupiter.api.Assertions.*;

class DiagnosticEntityTest {

    Map<String, String[]> _standardProperties ;

    @BeforeEach
    void setUp() {
        _standardProperties = ImmutableMap.<String, String[]>builder()
                .put("prop1", new String[]{"value1"})
                .put("prop2", new String[]{"value21","value22","value23"})
                .put("prop3", new String[]{"value31","value32","value33"})
                .build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Get All Map<String, String[]>")
    void testOutputIsInput() {
        DiagnosticEntity diagnosticEntity = new DiagnosticEntity(_standardProperties);
        Map<String, String[]> properties = diagnosticEntity.getProperties();
        assertEquals(properties, _standardProperties);
    }

    @Test
    @DisplayName("Replaced properties match")
    void ReplacedPropertiesMatch() {
        DiagnosticEntity diagnosticEntity = new DiagnosticEntity(ImmutableMap.<String, String[]> builder()
                .put("heapify", new String[]{"hammify"}).build());
        diagnosticEntity.setProperties(_standardProperties);
        assertEquals(diagnosticEntity.getProperties(), _standardProperties, "properties mismatch");
    }

    @Test
    @DisplayName("Validate syntax")
    void ValidateSyntax() {

    }


}

package io.bdrc.am.audit;

import io.bdrc.am.audit.auditlib.AuditHttpRequestImpl;
import io.bdrc.am.audit.auditlib.RestOps;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AuditHttpRequestImplTest {

    @Test
    public void getReturnsSomething() throws IOException {
        // Test basic use of audit HttpRequest get

        Map<String, String> args = new HashMap<String, String>(){{
            put("format", "json");
            put("R_RES", "bdr:W1FPL2251");
        }};
        AuditHttpRequestImpl auditHttpRequest = new AuditHttpRequestImpl();
        auditHttpRequest.setGetArgs(args);
        auditHttpRequest.setURI("http://purl.bdrc.io/query/table/volumesForInstance");
        auditHttpRequest.setRESTOperation(RestOps.GET);
        auditHttpRequest.SendRequest();
        String response = auditHttpRequest.getResponseBody();

    }
}


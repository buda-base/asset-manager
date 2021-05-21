package io.bdrc.am.audit.auditlib;

import java.net.URI;
import java.util.HashMap;

public interface IAuditHttpRequest {

    void setRequestHeader(HashMap<String, String> header_map);

    void addRequestHeader(String key, String value);

    void setURI(URI uri);

    void setURI(String uri);

    void setMethod(String method);

    // returns -1 if no request was made
    Integer getResponseStatus();

    HashMap<String, String> getResponseHeaders();

    String getResponseBody();

    Integer SendRequest();

}

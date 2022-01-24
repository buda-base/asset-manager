package io.bdrc.audit.auditlib;

import org.apache.http.NameValuePair;

import java.io.IOException;
import java.util.List;
import java.util.HashMap;

/**
 * IAuditHttpRequest
 * Generic Http  Client
 *
 */
public interface IAuditHttpRequest {

    void setRequestHeader(List<NameValuePair> header_map);
    List<NameValuePair> getRequestHeader();
    void addRequestHeader(String key, String value);

    void setURI(String uri);
    String getURI();

    // GET, PUT, etc
    void setRESTOperation(RestOps method);
    RestOps getRESTOperation();

    void setArgs(List<NameValuePair> args);
    List<NameValuePair> getArgs();
    void addArg(String key, String value) ;

    // returns -1 if no request was made
    Integer getResponseStatus();

    HashMap<String, String> getResponseHeaders();

    String getResponseBody();

    Integer SendRequest() throws IOException;

}

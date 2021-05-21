package io.bdrc.am.audit.auditlib;

import java.net.URI;
import java.util.HashMap;

public class AuditHttpRequestImpl implements IAuditHttpRequest{
    @Override
    public void setRequestHeader(final HashMap<String, String> header_map) {

    }

    @Override
    public void addRequestHeader(final String key, final String value) {

    }

    @Override
    public void setURI(final URI uri) {

    }

    @Override
    public void setURI(final String uri) {

    }

    @Override
    public void setMethod(final String method) {

    }

    @Override
    public Integer getResponseStatus() {
        return null;
    }

    @Override
    public HashMap<String, String> getResponseHeaders() {
        return null;
    }

    @Override
    public String getResponseBody() {
        return null;
    }

    @Override
    public Integer SendRequest() {
        return null;
    }

    private void CreateConnection() {

    }
}

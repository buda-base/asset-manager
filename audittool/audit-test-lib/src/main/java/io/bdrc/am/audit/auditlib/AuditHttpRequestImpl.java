package io.bdrc.am.audit.auditlib;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.Closeable;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * AuditHttpRequestImpl
 * Can be used in a try-with-resources block
 */
public class AuditHttpRequestImpl implements IAuditHttpRequest, AutoCloseable {

    public AuditHttpRequestImpl() {
        _client = HttpClientBuilder.create().build();
        _requestHeader = new HashMap<>();
        _args = new HashMap<>();
        _responseHeaders = new HashMap<>();
    }

    private final CloseableHttpClient _client;
    private CloseableHttpResponse _response;


    // region Header
    private final HashMap<String, String> _requestHeader;

    @Override
    public void setRequestHeader(final Map<String, String> headerNameValuePairs) {
        _requestHeader.clear();
        headerNameValuePairs.forEach(_requestHeader::put);
    }


    @Override
    public Map<String, String> getRequestHeader() {
        return _requestHeader;
    }

    @Override
    public void addRequestHeader(final String key, final String value) {
        _requestHeader.put(key, value);

    }
    // endregion Header


    private String _uri;

    @Override
    public void setURI(final String uri) {
        _uri = uri;
    }


    @Override
    public URI getURI() {
        return URI.create(_uri);
    }

    private RestOps _restOp;

    @Override
    public void setRESTOperation(final RestOps method) {
        _restOp = method;
    }

    @Override
    public RestOps getRESTOperation() {
        return _restOp;
    }

    /**
     * Argument handling:
     * Post arguments are just a blob of text
     * Get Arguments are a list of NameValuePairs, appended to the URL
     */
    // region Arguments
    private final Map<String, String> _args;

    /**
     * set the args for a get statement
     *
     * @param args list of args
     */
    @Override
    public void setGetArgs(final Map<String, String> args) {
        _args.clear();
        args.forEach(_args::put);
    }

    @Override
    public Map<String, String> getGetArgs() {
        return _args;
    }

    @Override
    public void addGetArg(String key, String value) {
        _args.put(key, value);
    }

    private String _postArgs = "";

    @Override
    public void setPostArgs(final String value) {
        _postArgs = value;
    }

    @Override
    public String getPostArgs() {
        return _postArgs;
    }
    // endregion

    private Integer _responseStatus;

    @Override
    public Integer getResponseStatus() {
        return _responseStatus;
    }

    private final Map<String, String> _responseHeaders;

    @Override
    public Map<String, String> getResponseHeaders() {
        Header[] responses;
        if (_response != null) {
            _responseHeaders.clear();
            responses = _response.getAllHeaders();
            Arrays.stream(responses).forEach(x -> _responseHeaders.put(x.getName(), x.getValue()));
        }
        return _responseHeaders;
    }

    private String _responseBody;

    @Override
    public String getResponseBody() {
        if (_response != null)
            _responseBody = _response.getEntity().toString();
        return _responseBody;
    }

    @Override
    public void SendRequest() throws IOException {
        // https://github.com/apache/httpcomponents-client/blob/5.1.x/httpclient5/src/test/java/org/apache/hc/client5/http/examples/ClientConnectionRelease.java

        HttpRequestBase _req = getReqFromRest(getRESTOperation());

        _req.setURI(getURI());

        List<NameValuePair> nameValuePairs = new ArrayList<>();
        _args.forEach((k,v)->  nameValuePairs.add(new BasicNameValuePair(k,v)));
        try {
            URI uri = new URIBuilder(_req.getURI())
                    .addParameters(nameValuePairs)
                    .build();
            _req.setURI(uri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        _response = _client.execute(_req);
        _responseStatus = _response.getStatusLine().getStatusCode();
    }

    private HttpRequestBase getReqFromRest(RestOps restOp) {

        if (RestOps.POST == restOp) {
            return new HttpPost();
        } else if (RestOps.PUT == restOp) {
            return new HttpPut();
        } else if (RestOps.DELETE == restOp) {
            return new HttpDelete();
        } else if (RestOps.HEAD == restOp) {
            return new HttpHead();
        }

        // default fallback is GET
        else
            return new HttpGet();
    }

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * {@code try}-with-resources statement.
     *
     * <p>While this interface method is declared to throw {@code
     * Exception}, implementers are <em>strongly</em> encouraged to
     * declare concrete implementations of the {@code close} method to
     * throw more specific exceptions, or to throw no exception at all
     * if the close operation cannot fail.
     *
     * <p> Cases where the close operation may fail require careful
     * attention by implementers. It is strongly advised to relinquish
     * the underlying resources and to internally <em>mark</em> the
     * resource as closed, prior to throwing the exception. The {@code
     * close} method is unlikely to be invoked more than once and so
     * this ensures that the resources are released in a timely manner.
     * Furthermore it reduces problems that could arise when the resource
     * wraps, or is wrapped, by another resource.
     *
     * <p><em>Implementers of this interface are also strongly advised
     * to not have the {@code close} method throw {@link
     * InterruptedException}.</em>
     * <p>
     * This exception interacts with a thread's interrupted status,
     * and runtime misbehavior is likely to occur if an {@code
     * InterruptedException} is {@linkplain Throwable#addSuppressed
     * suppressed}.
     * <p>
     * More generally, if it would cause problems for an
     * exception to be suppressed, the {@code AutoCloseable.close}
     * method should not throw it.
     *
     * <p>Note that unlike the {@link Closeable#close close}
     * method of {@link Closeable}, this {@code close} method
     * is <em>not</em> required to be idempotent.  In other words,
     * calling this {@code close} method more than once may have some
     * visible side effect, unlike {@code Closeable.close} which is
     * required to have no effect if called more than once.
     * <p>
     * However, implementers of this interface are strongly encouraged
     * to make their {@code close} methods idempotent.
     *
     * @throws Exception if this resource cannot be closed
     */
    @Override
    public void close() throws Exception {
        if (_response != null) {
            _response.close();
        }
        if (_client != null) {
            _client.close();
        }
    }
}

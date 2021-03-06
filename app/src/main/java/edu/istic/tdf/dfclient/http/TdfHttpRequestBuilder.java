package edu.istic.tdf.dfclient.http;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import edu.istic.tdf.dfclient.auth.AuthHelper;
import edu.istic.tdf.dfclient.auth.Credentials;
import okhttp3.Request;

/**
 * Decorates a OKHttp3 request to append TDF requests parameters
 */
public class TdfHttpRequestBuilder {

    /**
     * The OKHTTP3 source builder
     */
    Request.Builder builder;

    /**
     * Constructs a TdfHttpRequestBuilder using OKHTTP3 Builder
     * @param requestBuilder The source OKHTTP3 Builder
     */
    public TdfHttpRequestBuilder(Request.Builder requestBuilder) {
        this.builder = requestBuilder;
    }

    /**
     * Appends some headers to request
     * @param headers
     */
    public void appendHeaders(HashMap<String, String> headers) {
        Iterator it = headers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            builder.addHeader((String) pair.getKey(), (String) pair.getValue());
            it.remove();
        }
    }

    /**
     * Sets authentication headers
     */
    public void setAuth() {
        // Todo: Make this with application context
        Credentials credentials = AuthHelper.loadCredentials();
        if(credentials != null) {
            if(credentials.isValid()) {
                HashMap<String, String> authHeaders = new HashMap<>();
                authHeaders.put("userid", credentials.getUserId());
                authHeaders.put("token", credentials.getToken());
                this.appendHeaders(authHeaders);
            }
            // TODO: Handle invalid credentials object

        }
    }

    public void setHost(String host){
        HashMap<String, String> hostHeader = new HashMap<>();
        hostHeader.put("Host", host);
        this.appendHeaders(hostHeader);
    }

    /**
     * Sets the Accept header
     * @param accept The accept content type
     */
    public void setAcceptHeaders(String accept) {
        HashMap<String, String> acceptHeaders = new HashMap<>();
        acceptHeaders.put("Accept", accept);
        this.appendHeaders(acceptHeaders);
    }

    /**
     * Gets the OKHTTP3 Builder after updates
     * @return The okhttp3 builder
     */
    public Request.Builder getBuilder() {
        return this.builder;
    }
}

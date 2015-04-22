package com.zncm.utils.http.core;


import com.zncm.utils.exception.CheckedExceptionHandler;
import com.zncm.utils.http.core.entityhandler.StringEntityHandler;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

public class SyncRequestHandler {

    private final AbstractHttpClient client;
    private final HttpContext context;
    private final StringEntityHandler entityHandler = new StringEntityHandler();

    private int executionCount = 0;

    public SyncRequestHandler(AbstractHttpClient client, HttpContext context) {
        this.client = client;
        this.context = context;
    }

    private Object makeRequestWithRetries(HttpUriRequest request)
            throws ConnectException {
        boolean retry = true;
        IOException cause = null;
        HttpRequestRetryHandler retryHandler = client
                .getHttpRequestRetryHandler();
        while (retry) {
            try {
                HttpResponse response = client.execute(request, context);
                return entityHandler.handleEntity(response.getEntity(), null);
            } catch (UnknownHostException e) {
                cause = e;
                break;
            } catch (IOException e) {
                cause = e;
                retry = retryHandler.retryRequest(cause, ++executionCount,
                        context);
            } catch (NullPointerException e) {
                // HttpClient 4.0.x 之前的一个bug
                // http://code.google.com/p/android/issues/detail?id=5255
                retry = retryHandler.retryRequest(cause, ++executionCount,
                        context);
            } catch (IllegalStateException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;

    }

    public Object sendRequest(HttpUriRequest... params) {
        try {
            return makeRequestWithRetries(params[0]);
        } catch (ConnectException e) {
            CheckedExceptionHandler.handleException(e);
        }
        return null;
    }

}

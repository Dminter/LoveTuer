package com.zncm.utils.http;


import com.zncm.lovetuer.data.base.TokeData;
import com.zncm.lovetuer.global.GlobalConstants;
import com.zncm.lovetuer.global.SharedApplication;
import com.zncm.utils.L;
import com.zncm.utils.StrUtil;
import com.zncm.utils.http.core.HttpHandler;
import com.zncm.utils.http.core.RequestCallBack;
import com.zncm.utils.http.core.RequestParams;
import com.zncm.utils.http.core.RetryHandler;
import com.zncm.utils.http.core.SyncRequestHandler;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.SyncBasicHttpContext;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;

class HttpManager {

    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String ENCODING_GZIP = "gzip";
    private static int MAX_CONNECTIONS = 20; // http请求最大并发连接数
    private int MAX_RETRIES = 3;// 错误尝试次数，错误异常表请在RetryHandler添加
    private static int httpThreadCount = 3;// http线程池数量

    private final DefaultHttpClient httpClient;
    private final HttpContext httpContext;

    private final Map<String, String> clientHeaderMap;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            Thread tread = new Thread(r, "Http #" + mCount.getAndIncrement());
            tread.setPriority(Thread.NORM_PRIORITY - 1);
            return tread;
        }
    };

    private static final Executor executor = Executors.newFixedThreadPool(httpThreadCount,
            sThreadFactory);

    public HttpManager() {

        // if (BuildConfig.DEBUG) {
        // MAX_RETRIES = 0;
        // }

        BasicHttpParams httpParams = new BasicHttpParams();

        ConnManagerParams.setTimeout(httpParams, 5000);
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(
                MAX_CONNECTIONS));
        ConnManagerParams.setMaxTotalConnections(httpParams, 10);

        HttpConnectionParams.setSoTimeout(httpParams, GlobalConstants.NETWORK_TIME_OUT);
        HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
        HttpConnectionParams.setTcpNoDelay(httpParams, true);
        HttpConnectionParams.setSocketBufferSize(httpParams, GlobalConstants.IO_BUFFER_SIZE);

        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        ThreadSafeClientConnManager cm =
                new ThreadSafeClientConnManager(httpParams, schemeRegistry);

        httpContext = new SyncBasicHttpContext(new BasicHttpContext());
        httpClient = new DefaultHttpClient(cm, httpParams);
        httpClient.addRequestInterceptor(new HttpRequestInterceptor() {
            public void process(HttpRequest request, HttpContext context) {
                if (!request.containsHeader(HEADER_ACCEPT_ENCODING)) {
                    request.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
                }
                for (String header : clientHeaderMap.keySet()) {
                    request.addHeader(header, clientHeaderMap.get(header));
                }
            }
        });

        httpClient.addResponseInterceptor(new HttpResponseInterceptor() {
            public void process(HttpResponse response, HttpContext context) {
                final HttpEntity entity = response.getEntity();
                if (entity == null) {
                    return;
                }
                final Header encoding = entity.getContentEncoding();
                if (encoding != null) {
                    for (HeaderElement element : encoding.getElements()) {
                        if (element.getName().equalsIgnoreCase(ENCODING_GZIP)) {
                            response.setEntity(new InflatingEntity(response.getEntity()));
                            break;
                        }
                    }
                }
            }
        });

        httpClient.setHttpRequestRetryHandler(new RetryHandler(MAX_RETRIES));
        HttpProtocolParams.setUseExpectContinue(httpClient.getParams(), false);
        clientHeaderMap = new HashMap<String, String>();
    }

    public DefaultHttpClient getHttpClient() {
        return this.httpClient;
    }

    public HttpContext getHttpContext() {
        return this.httpContext;
    }

    /**
     * 设置缓存
     *
     * @param cookieStore
     * @Description
     */
    public void configCookieStore(CookieStore cookieStore) {
        if (httpContext != null) {
            httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        }
    }

    /**
     * 设置UA
     *
     * @param userAgent
     * @Description
     */
    public void configUserAgent(String userAgent) {
        if (httpClient != null) {
            HttpProtocolParams.setUserAgent(this.httpClient.getParams(), userAgent);
        }
    }

    /**
     * 设置网络连接超时时间，默认为10秒钟
     *
     * @param timeout
     */
    public void configTimeout(int timeout) {
        if (httpClient != null) {
            final HttpParams httpParams = this.httpClient.getParams();
            ConnManagerParams.setTimeout(httpParams, timeout);
            HttpConnectionParams.setSoTimeout(httpParams, timeout);
            HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
        }
    }

    /**
     * 设置https请求时 的 SSLSocketFactory
     *
     * @param sslSocketFactory
     */
    public void configSSLSocketFactory(SSLSocketFactory sslSocketFactory) {
        if (httpClient != null) {
            Scheme scheme = new Scheme("https", sslSocketFactory, 443);
            this.httpClient.getConnectionManager().getSchemeRegistry().register(scheme);
        }
    }

    /**
     * 配置错误重试次数
     */
    public void configRequestExecutionRetryCount(int count) {
        if (httpClient != null) {
            this.httpClient.setHttpRequestRetryHandler(new RetryHandler(count));
        }
    }

    /**
     * 添加http请求头
     *
     * @param header
     * @param value
     */
    public void addHeader(String header, String value) {
        clientHeaderMap.put(header, value);
    }

    public static String getUrlWithQueryString(String url, RequestParams params) {
        if (params != null) {
            String paramString = params.getParamString();
            url += "?" + paramString;
        }
        return url;
    }

    public static HttpEntity paramsToEntity(RequestParams params) {
        HttpEntity entity = null;

        if (params != null) {
            entity = params.gotHttpEntity();
        }

        return entity;
    }

    public static HttpEntityEnclosingRequestBase addEntityToRequestBase(
            HttpEntityEnclosingRequestBase requestBase, HttpEntity entity) {
        if (entity != null) {
            requestBase.setEntity(entity);
        }

        return requestBase;
    }

    protected HttpHandler<File> download(String url, RequestParams params, String target,
                                         boolean isResume, RequestCallBack<File> callback) {
        final HttpGet get = new HttpGet(getUrlWithQueryString(url, params));
        HttpHandler<File> handler = new HttpHandler<File>(httpClient, httpContext, callback);
        handler.executeOnExecutor(executor, get, target, isResume);
        return handler;
    }

    // protected HttpHandler<File> download(String url, RequestParams params,
    // String target, boolean isResume, RequestCallBack<File> callback) {
    // final HttpPost post = new HttpPost(getUrlWithQueryString(url, params));
    // HttpHandler<File> handler = new HttpHandler<File>(httpClient,
    // httpContext, callback);
    // handler.executeOnExecutor(executor, post, target, isResume);
    // return handler;
    // }

    protected <T> void sendRequest(HttpUriRequest uriRequest, String contentType,
                                   RequestCallBack<T> callBack) {


        if (contentType != null) {
            uriRequest.addHeader("Content-Type", contentType);
        }
        TokeData tmp = SharedApplication.getInstance().getTokeData();
        if (tmp != null && StrUtil.notEmptyOrNull(tmp.getAccess_token())) {
            L.i("authorization:" + SharedApplication.getInstance().getTokeData().getAccess_token());
            uriRequest.addHeader("authorization", "Bearer " + SharedApplication.getInstance().getTokeData().getAccess_token());
        }
        new HttpHandler<T>(httpClient, httpContext, callBack).executeOnExecutor(executor,
                uriRequest);

    }

    protected Object sendSyncRequest(HttpUriRequest uriRequest, String contentType) {
        if (contentType != null) {
            uriRequest.addHeader("Content-Type", contentType);
        }
        return new SyncRequestHandler(httpClient, httpContext).sendRequest(uriRequest);
    }

    private static class InflatingEntity extends HttpEntityWrapper {
        public InflatingEntity(HttpEntity wrapped) {
            super(wrapped);
        }

        @Override
        public InputStream getContent() throws IOException {
            return new GZIPInputStream(wrappedEntity.getContent());
        }

        @Override
        public long getContentLength() {
            return -1;
        }
    }
}

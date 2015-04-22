package com.zncm.utils.http;


import com.zncm.utils.http.core.HttpHandler;
import com.zncm.utils.http.core.RequestCallBack;
import com.zncm.utils.http.core.RequestParams;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.File;
import java.util.ArrayList;

public class HttpUtil {

    private HttpManager httpManager;

    public static final HttpUtil getInstance() {
        return HttpUtilHolder.INSTANCE;
    }

    private static final class HttpUtilHolder {
        private static final HttpUtil INSTANCE = new HttpUtil();
    }

    private HttpUtil() {
        if (httpManager == null) {
            httpManager = new HttpManager();
        }
    }

    public void addHeader(String header, String value) {
        httpManager.addHeader(header, value);
    }

    /**
     * 设置缓存
     *
     * @param cookieStore
     * @Description
     */
    public void configCookieStore(CookieStore cookieStore) {
        httpManager.configCookieStore(cookieStore);
    }

    /**
     * 设置UA
     *
     * @param userAgent
     * @Description
     */
    public void configUserAgent(String userAgent) {
        httpManager.configUserAgent(userAgent);
    }

    /**
     * 设置网络连接超时时间，默认为10秒钟
     *
     * @param timeout
     */
    public void configTimeout(int timeout) {
        httpManager.configTimeout(timeout);
    }

    /**
     * 设置https请求时 的 SSLSocketFactory
     *
     * @param sslSocketFactory
     */
    public void configSSLSocketFactory(SSLSocketFactory sslSocketFactory) {
        httpManager.configSSLSocketFactory(sslSocketFactory);
    }

    /**
     * 配置错误重试次数
     */
    public void configRequestExecutionRetryCount(int count) {
        httpManager.configRequestExecutionRetryCount(count);
    }

    /**
     * @param url
     * @param callBack
     * @Description
     */
    public void get(String url, RequestCallBack<? extends Object> callBack) {
        get(url, null, callBack);
    }

    /**
     * @param url
     * @param params
     * @param callBack
     * @Description
     */
    public void get(String url, RequestParams params,
                    RequestCallBack<? extends Object> callBack) {
        get(url, null, params, callBack);
    }

    /**
     * @param url
     * @param headers
     * @param params
     * @param callBack
     * @Description
     */
    public void get(String url, ArrayList<Header> headers,
                    RequestParams params, RequestCallBack<? extends Object> callBack) {
        HttpUriRequest request = new HttpGet(HttpManager.getUrlWithQueryString(
                url, params));
        configHeader(headers);
        httpManager.sendRequest(request, null, callBack);
    }

    /**
     * @param url
     * @return
     * @Description
     */
    public Object getSync(String url) {
        return getSync(url, null);
    }

    /**
     * @param url
     * @param params
     * @return
     * @Description
     */
    public Object getSync(String url, RequestParams params) {
        return getSync(url, null, params);
    }

    /**
     * @param url
     * @param headers
     * @param params
     * @return
     * @Description
     */
    public Object getSync(String url, ArrayList<Header> headers,
                          RequestParams params) {
        HttpUriRequest request = new HttpGet(HttpManager.getUrlWithQueryString(
                url, params));
        configHeader(headers);
        return httpManager.sendSyncRequest(request, null);
    }

    /**
     * @param url
     * @param callBack
     * @Description
     */
    public void post(String url, RequestCallBack<? extends Object> callBack) {
        post(url, null, callBack);
    }

    /**
     * @param url
     * @param params
     * @param callBack
     * @Description
     */
    public void post(String url, RequestParams params,
                     RequestCallBack<? extends Object> callBack) {

        post(url, null, params, null, callBack);
    }

    /**
     * @param url
     * @param headers
     * @param params
     * @param contentType
     * @param callBack
     * @Description
     */
    public <T> void post(String url, ArrayList<Header> headers,
                         RequestParams params, String contentType,
                         RequestCallBack<T> callBack) {
        HttpEntityEnclosingRequestBase request = new HttpPost(url);
        configParam(params, request);
        configHeader(headers);
        httpManager.sendRequest(request, contentType, callBack);
    }

    /**
     * @param url
     * @param entity
     * @param contentType
     * @param callBack
     * @Description
     */
    public void post(String url, HttpEntity entity, String contentType,
                     RequestCallBack<? extends Object> callBack) {
        post(url, null, entity, null, callBack);
    }

    /**
     * @param url
     * @param headers
     * @param entity
     * @param contentType
     * @param callBack
     * @Description
     */
    public void post(String url, ArrayList<Header> headers, HttpEntity entity,
                     String contentType, RequestCallBack<? extends Object> callBack) {
        HttpEntityEnclosingRequestBase request = HttpManager
                .addEntityToRequestBase(new HttpPost(url), entity);
        configHeader(headers);
        httpManager.sendRequest(request, contentType, callBack);
    }

    /**
     * @param url
     * @return
     * @Description
     */
    public Object postSync(String url) {
        return postSync(url, null);
    }

    /**
     * @param url
     * @param params
     * @return
     * @Description
     */
    public Object postSync(String url, RequestParams params) {
        return postSync(url, null, params, null);
    }

    /**
     * @param url
     * @param headers
     * @param params
     * @param contentType
     * @return
     * @Description
     */
    public Object postSync(String url, ArrayList<Header> headers,
                           RequestParams params, String contentType) {
        HttpEntityEnclosingRequestBase request = new HttpPost(url);
        configParam(params, request);
        configHeader(headers);
        return httpManager.sendSyncRequest(request, contentType);
    }

    /**
     * @param url
     * @param entity
     * @param contentType
     * @return
     * @Description
     */
    public Object postSync(String url, HttpEntity entity, String contentType) {
        return postSync(url, null, entity, null);
    }

    /**
     * @param url
     * @param headers
     * @param entity
     * @param contentType
     * @return
     * @Description
     */
    public Object postSync(String url, ArrayList<Header> headers,
                           HttpEntity entity, String contentType) {
        HttpEntityEnclosingRequestBase request = HttpManager
                .addEntityToRequestBase(new HttpPost(url), entity);
        configHeader(headers);
        return httpManager.sendSyncRequest(request, contentType);
    }

    /**
     * @param url
     * @param headers
     * @param params
     * @param contentType
     * @param callBack
     * @Description
     */
    public <T> void postSync(String url, ArrayList<Header> headers,
                             RequestParams params, String contentType,
                             RequestCallBack<T> callBack) {
        HttpEntityEnclosingRequestBase request = new HttpPost(url);
        configParam(params, request);
        configHeader(headers);
        httpManager.sendRequest(request, contentType, callBack);
    }

    /**
     * @param url
     * @param callBack
     * @Description
     */
    public void put(String url, RequestCallBack<? extends Object> callBack) {
        put(url, null, callBack);
    }

    /**
     * @param url
     * @param params
     * @param callBack
     * @Description
     */
    public void put(String url, RequestParams params,
                    RequestCallBack<? extends Object> callBack) {
        put(url, null, params, null, callBack);
    }

    /**
     * @param url
     * @param entity
     * @param contentType
     * @param callBack
     * @Description
     */
    public void put(String url, HttpEntity entity, String contentType,
                    RequestCallBack<? extends Object> callBack) {
        put(url, null, entity, null, callBack);
    }

    /**
     * @param url
     * @param headers
     * @param entity
     * @param contentType
     * @param callBack
     * @Description
     */
    public void put(String url, ArrayList<Header> headers, HttpEntity entity,
                    String contentType, RequestCallBack<? extends Object> callBack) {
        HttpEntityEnclosingRequestBase request = HttpManager
                .addEntityToRequestBase(new HttpPut(url), entity);
        configHeader(headers);
        httpManager.sendRequest(request, contentType, callBack);
    }

    /**
     * @param url
     * @param headers
     * @param params
     * @param contentType
     * @param callBack
     * @Description
     */
    public void put(String url, ArrayList<Header> headers,
                    RequestParams params, String contentType,
                    RequestCallBack<? extends Object> callBack) {
        HttpEntityEnclosingRequestBase request = new HttpPut(url);
        configParam(params, request);
        configHeader(headers);
        httpManager.sendRequest(request, contentType, callBack);
    }

    /**
     * @param url
     * @return
     * @Description
     */
    public Object putSync(String url) {
        return putSync(url, null);
    }

    /**
     * @param url
     * @param params
     * @return
     * @Description
     */
    public Object putSync(String url, RequestParams params) {
        return putSync(url, HttpManager.paramsToEntity(params), null);
    }

    /**
     * @param url
     * @param entity
     * @param contentType
     * @return
     * @Description
     */
    public Object putSync(String url, HttpEntity entity, String contentType) {
        return putSync(url, null, entity, contentType);
    }

    /**
     * @param url
     * @param headers
     * @param entity
     * @param contentType
     * @return
     * @Description
     */
    public Object putSync(String url, ArrayList<Header> headers,
                          HttpEntity entity, String contentType) {
        HttpEntityEnclosingRequestBase request = HttpManager
                .addEntityToRequestBase(new HttpPut(url), entity);
        configHeader(headers);
        return httpManager.sendSyncRequest(request, contentType);
    }

    public Object putSync(String url, ArrayList<Header> headers,
                          RequestParams params, String contentType) {
        HttpEntityEnclosingRequestBase request = new HttpPut(url);
        configParam(params, request);
        configHeader(headers);
        return httpManager.sendSyncRequest(request, contentType);
    }

    /**
     * @param url
     * @param callBack
     * @Description
     */
    public void delete(String url, RequestCallBack<? extends Object> callBack) {
        delete(url, null, callBack);
    }

    /**
     * @param url
     * @param headers
     * @param callBack
     * @Description
     */
    public void delete(String url, ArrayList<Header> headers,
                       RequestCallBack<? extends Object> callBack) {
        final HttpDelete delete = new HttpDelete(url);
        configHeader(headers);
        httpManager.sendRequest(delete, null, callBack);
    }

    /**
     * @param url
     * @return
     * @Description
     */
    public Object deleteSync(String url) {
        return deleteSync(url, null);
    }

    /**
     * @param url
     * @param headers
     * @return
     * @Description
     */
    public Object deleteSync(String url, ArrayList<Header> headers) {
        final HttpDelete delete = new HttpDelete(url);
        configHeader(headers);
        return httpManager.sendSyncRequest(delete, null);
    }

    /**
     * @param url
     * @param target
     * @param callback
     * @return
     * @Description
     */
    public HttpHandler<File> download(String url, String target,
                                      RequestCallBack<File> callback) {
        return download(url, target, false, callback);
    }

    /**
     * @param url
     * @param target
     * @param isResume
     * @param callback
     * @return
     * @Description
     */
    public HttpHandler<File> download(String url, String target,
                                      boolean isResume, RequestCallBack<File> callback) {
        return httpManager.download(url, null, target, isResume, callback);
    }

    /**
     * @param params
     * @param request
     * @Description
     */
    private void configParam(RequestParams params,
                             HttpEntityEnclosingRequestBase request) {
        if (params != null)
            request.setEntity(HttpManager.paramsToEntity(params));
    }

    /**
     * @param headers
     * @Description
     */
    private void configHeader(ArrayList<Header> headers) {
        if (headers != null) {
            for (Header header : headers) {
                addHeader(header.getName(), header.getValue());
            }
        }
    }

}

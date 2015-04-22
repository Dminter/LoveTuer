package com.zncm.utils.http.core;

import android.os.AsyncTask;
import android.os.SystemClock;

import com.zncm.utils.L;
import com.zncm.utils.http.core.entityhandler.EntityCallBack;
import com.zncm.utils.http.core.entityhandler.FileEntityHandler;
import com.zncm.utils.http.core.entityhandler.StringEntityHandler;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

public class HttpHandler<T> extends AsyncTask<Object, Object, Object> implements EntityCallBack {

    private final AbstractHttpClient client;
    private final HttpContext context;

    private final StringEntityHandler mStrEntityHandler = new StringEntityHandler();
    private final FileEntityHandler mFileEntityHandler = new FileEntityHandler();

    private final RequestCallBack<T> callback;

    private int executionCount = 0;
    private String path = null; // 下载的路径
    private boolean isResume = false; // 是否断点续传

    private long time;

    private final static int UPDATE_START = 1;
    private final static int UPDATE_LOADING = 2;
    private final static int UPDATE_FAILURE = 3;
    private final static int UPDATE_SUCCESS = 4;

    public HttpHandler(AbstractHttpClient client, HttpContext context, RequestCallBack<T> callback) {
        this.client = client;
        this.context = context;
        this.callback = callback;
    }

    private void makeRequestWithRetries(HttpUriRequest request) throws IOException {
        // 恢复下载
        if (isResume && path != null) {
            File downloadFile = new File(path);
            long fileLen = 0;
            if (downloadFile.exists() && downloadFile.isFile()) {
                fileLen = downloadFile.length();
            }
            if (fileLen > 0) request.setHeader("RANGE", "bytes=" + fileLen + "-");
        }

        boolean retry = true;
        IOException cause = null;
        HttpRequestRetryHandler retryHandler = client.getHttpRequestRetryHandler();
        while (retry) {
            try {
                if (!isCancelled()) {
                    HttpResponse response = client.execute(request, context);
                    if (!isCancelled()) {
                        handleResponse(response);
                    }
                }
                return;
            } catch (UnknownHostException e) {
                publishProgress(UPDATE_FAILURE, e, "unknownHostException：can't resolve host");
                return;
            } catch (ConnectTimeoutException e) {
                publishProgress(UPDATE_FAILURE, e, "Time out");
                return;
            } catch (IOException e) {
                cause = e;
                retry = retryHandler.retryRequest(cause, ++executionCount, context);
            } catch (NullPointerException e) {
                // HttpClient 4.0.x 之前的一个bug
                // http://code.google.com/p/android/issues/detail?id=5255
                cause = new IOException("NPE in HttpClient " + e.getMessage());
                retry = retryHandler.retryRequest(cause, ++executionCount, context);
            }
        }
        // 没有能读取到网络数据
        // ConnectException ex = new ConnectException(cause.getMessage());
        // ex.initCause(cause);
        throw cause;
    }

    @Override
    protected Object doInBackground(Object... params) {
        if (params != null && params.length == 3) {
            path = String.valueOf(params[1]);
            if (L.D) {
                L.i("download file path = " + path);
            }
            isResume = (Boolean) params[2];
        }
        try {
            publishProgress(UPDATE_START); // 开始
            makeRequestWithRetries((HttpUriRequest) params[0]);

        } catch (IOException e) {
            publishProgress(UPDATE_FAILURE, e, e.getMessage()); // 结束
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onProgressUpdate(Object... values) {
        int update = Integer.valueOf(String.valueOf(values[0]));
        switch (update) {
            case UPDATE_START:
                if (callback != null) callback.onStart();
                break;
            case UPDATE_LOADING:
                if (callback != null)
                    callback.onLoading(Long.valueOf(String.valueOf(values[1])),
                            Long.valueOf(String.valueOf(values[2])));
                break;
            case UPDATE_FAILURE:
                if (callback != null)
                    callback.onFailure((Throwable) values[1], (String) values[2]);
                break;
            case UPDATE_SUCCESS:
                if (callback != null) callback.onSuccess((T) values[1]);
                break;
            default:
                break;
        }
        super.onProgressUpdate(values);
    }

    public boolean isStop() {
        return mFileEntityHandler.isStop();
    }

    public void stop() {
        mFileEntityHandler.setStop(true);
    }

    private void handleResponse(HttpResponse response) {
        StatusLine status = response.getStatusLine();
        if (status.getStatusCode() >= 300) {
            String errorMsg = "response status error code:" + status.getStatusCode();
            if (status.getStatusCode() == 416 && isResume) {
                errorMsg += " \n maybe you have download complete.";
            }
            publishProgress(UPDATE_FAILURE, new HttpResponseException(status.getStatusCode(),
                    status.getReasonPhrase()), errorMsg);
        } else {
            try {
                HttpEntity entity = response.getEntity();
                Object responseBody = null;
                if (entity != null) {
                    time = SystemClock.uptimeMillis();
                    if (path != null) {
                        responseBody =
                                mFileEntityHandler.handleEntity(entity, this, path, isResume);
                    } else {
                        responseBody = mStrEntityHandler.handleEntity(entity, this);
                    }

                }
                if (responseBody == null) {
                    String errorMsg = "do not have data";
                    Exception e = new Exception(errorMsg);
                    publishProgress(UPDATE_FAILURE, e, errorMsg);
                } else {
                    publishProgress(UPDATE_SUCCESS, responseBody);
                }

            } catch (IOException e) {
                publishProgress(UPDATE_FAILURE, e, e.getMessage());
            }

        }
    }

    @Override
    public void callBack(long count, long current, boolean mustNoticeUI) {
        if (callback != null && callback.isProgress()) {
            if (mustNoticeUI) {
                publishProgress(UPDATE_LOADING, count, current);
            } else {
                long thisTime = SystemClock.uptimeMillis();
                if (thisTime - time >= callback.getRate()) {
                    time = thisTime;
                    publishProgress(UPDATE_LOADING, count, current);
                }
            }
        }
    }

}

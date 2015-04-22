package com.zncm.component.request;


import com.zncm.utils.L;
import com.zncm.utils.http.HttpUtil;

public class ReqService {
    private static HttpUtil httpUtil;

    public static HttpUtil getHttpUtil() {
        if (httpUtil == null) {
            httpUtil = HttpUtil.getInstance();
        }

        return httpUtil;
    }

    public static void getDataFromServer(String url, ServiceParams params, ServiceRequester serviceRequester) {
        if (url != null) {
            L.i("get:" + url + " " + params);
        }
        getHttpUtil().get(url, params, serviceRequester);
    }

    public static void postDataToServer(String url, ServiceParams params, ServiceRequester serviceRequester) {

        if (url != null) {
            L.i("post:" + url + " " + params);
        }
        getHttpUtil().post(url, params, serviceRequester);
    }

    public static void delDataToServer(String url, ServiceRequester serviceRequester) {
        getHttpUtil().delete(url, serviceRequester);
    }

    public static void putDataToServer(String url, ServiceParams params, ServiceRequester serviceRequester) {
        getHttpUtil().put(url, params, serviceRequester);
    }


    private static Object sendSyncRequest(String url, ServiceParams params) {
        return getHttpUtil().postSync(url, params);
    }


}

package com.zncm.component.request;

import com.zncm.utils.L;
import com.zncm.utils.StrUtil;
import com.zncm.utils.http.core.RequestCallBack;

public abstract class ServiceRequester extends RequestCallBack<String> {

    public ServiceRequester() {
    }

    public ServiceRequester(String id) {
        this.setId(id);
    }


    @Override
    public void onSuccess(String content) {
        if (StrUtil.isEmptyOrNull(content)) {
            L.toastShort("未知错误~");
            return;
        }
        L.i("ret:" + content);
        onResult(content);
    }


    @Override
    public void onFailure(Throwable t, String strMsg) {
//        onError(strMsg);
//        L.toastShort(strMsg);

    }

    public abstract void onResult(String result);

//    public abstract void onError(String error);


}

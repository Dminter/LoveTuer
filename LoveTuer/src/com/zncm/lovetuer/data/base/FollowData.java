package com.zncm.lovetuer.data.base;


import com.alibaba.fastjson.annotation.JSONField;
import com.zncm.lovetuer.data.BaseData;

public class FollowData extends BaseData {

    private String status;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

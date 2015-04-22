package com.zncm.lovetuer.data.base;


import com.zncm.lovetuer.data.BaseData;

public class TokeData extends BaseData {


    private int tuer_uid;
    private String access_token;


    public int getTuer_uid() {
        return tuer_uid;
    }

    public void setTuer_uid(int tuer_uid) {
        this.tuer_uid = tuer_uid;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
}

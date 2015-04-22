package com.zncm.lovetuer.data.base;


import com.alibaba.fastjson.annotation.JSONField;
import com.zncm.lovetuer.data.BaseData;

public class TipData extends BaseData {
    //    {"_id":"5350940fffb6976d03000003"
// ,"content":"怕什么就会想到什么,信什么就会有了一条新回复",
// "id":3249
// ,"type":"reply"}
    @JSONField(name = "_id")
    private String tip_id;
    private String content;
    private int id;
    private String type;

    public String getTip_id() {
        return tip_id;
    }

    public void setTip_id(String tip_id) {
        this.tip_id = tip_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

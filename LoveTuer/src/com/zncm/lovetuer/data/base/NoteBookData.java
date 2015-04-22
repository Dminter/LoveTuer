package com.zncm.lovetuer.data.base;


import com.zncm.lovetuer.data.BaseData;

public class NoteBookData extends BaseData {
    //    {"id":359,"bgcolor":"0033ff","created_at":1397544193776,"name":"开发"},
    private int id;
    private String bgcolor;
    private String created_at;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBgcolor() {
        return bgcolor;
    }

    public void setBgcolor(String bgcolor) {
        this.bgcolor = bgcolor;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

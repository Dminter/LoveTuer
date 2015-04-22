package com.zncm.lovetuer.data.base;


import com.zncm.lovetuer.data.BaseData;

public class NoteData extends BaseData {
    // 29406-29481/com.zncm.myhelper I/NoteListActivity$2.onResult﹕ resultEx》》
    // {"data":[{"id":3200,"content":"才收到KEY,便紧锣密鼓的做起来,遇到授权不通过遂想法联系到作者,请教关于接口的问题,他已然在处理,更有动力做下去了.",
    // "bookname":"开发","bookid":359,"created_user":"浙水之南",
    // "pageurl":"dminter","privacy":0,"location":"","mood":"喜",
    // "weather":"晴","img":"","created_at":"2014-04-15T06:45:07.085Z"},
    private int id;
    private String content;
    private String bookname;
    private int bookid;
    private String created_user;
    private String pageurl;
    private int privacy;
    private String location;
    private String mood;
    private String weather;
    private String img;
    private String created_at;
    private int commentcount;
    private String isSelf;
    private String userid;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBookname() {
        return bookname;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
    }

    public int getBookid() {
        return bookid;
    }

    public void setBookid(int bookid) {
        this.bookid = bookid;
    }

    public String getCreated_user() {
        return created_user;
    }

    public void setCreated_user(String created_user) {
        this.created_user = created_user;
    }

    public String getPageurl() {
        return pageurl;
    }

    public void setPageurl(String pageurl) {
        this.pageurl = pageurl;
    }

    public int getPrivacy() {
        return privacy;
    }

    public void setPrivacy(int privacy) {
        this.privacy = privacy;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getCommentcount() {
        return commentcount;
    }

    public void setCommentcount(int commentcount) {
        this.commentcount = commentcount;
    }

    public String getIsSelf() {
        return isSelf;
    }

    public void setIsSelf(String isSelf) {
        this.isSelf = isSelf;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}

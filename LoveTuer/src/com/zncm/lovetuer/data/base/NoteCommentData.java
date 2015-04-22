package com.zncm.lovetuer.data.base;


import com.alibaba.fastjson.annotation.JSONField;
import com.zncm.lovetuer.data.BaseData;

public class NoteCommentData extends BaseData {
    //{"_id":"534df29fdb992eee0200000b",
// "related_id":"534dd2ccb0de3f237e000001",
// "userpage":"designsor","nick":"小爝",
// "profile":"love the way you lie ",
// "content":"兔耳的android版本就靠你了！泪流满面。\r\n\r\n学到老，活到老，赞！",
// "userid":"4f8f956b96c223ac0b000001",
// "created_at":"2014-04-16T03:01:51.325Z",
// "commentcount":""}

    @JSONField(name = "_id")
    private String comment_id;
    private String related_id;
    private String userpage;
    private String nick;
    private String profile;
    private String content;
    private String userid;
    private String created_at;
    private String isSelf;


    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getRelated_id() {
        return related_id;
    }

    public void setRelated_id(String related_id) {
        this.related_id = related_id;
    }

    public String getUserpage() {
        return userpage;
    }

    public void setUserpage(String userpage) {
        this.userpage = userpage;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getIsSelf() {
        return isSelf;
    }

    public void setIsSelf(String isSelf) {
        this.isSelf = isSelf;
    }
}

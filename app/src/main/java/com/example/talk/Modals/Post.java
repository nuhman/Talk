package com.example.talk.Modals;

import com.google.firebase.database.ServerValue;

public class Post {

    private String postKey, id, title, desc, pic, userPhoto;
    private Object timeStamp;

    public Post(String id, String title, String desc, String pic, String userPhoto) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.pic = pic;
        this.userPhoto = userPhoto;
        this.timeStamp = ServerValue.TIMESTAMP;
    }

    public Post(){

    }

    public String getPostKey() {
        return postKey;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getPic() {
        return pic;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }
}

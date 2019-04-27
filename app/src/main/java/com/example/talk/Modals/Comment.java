package com.example.talk.Modals;

import com.google.firebase.database.ServerValue;

public class Comment {
    private String userId, userName, userImageUrl, content;
    private Object timestamp;

    public Comment() {
    }

    public Comment(String userId, String userName, String userImageUrl, String content) {
        this.userId = userId;
        this.userName = userName;
        this.userImageUrl = userImageUrl;
        this.content = content;
        this.timestamp = ServerValue.TIMESTAMP;
    }

    public Comment(String userId, String userName, String userImageUrl, String content, Object timestamp) {
        this.userId = userId;
        this.userName = userName;
        this.userImageUrl = userImageUrl;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }

}

package com.imsaddam.luxevents.models;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class Comment implements Serializable {

    public User user;
    public String comments;
    public Date addedDate;

    public Comment() {
    }

    public Comment(User user, String comments) {
        this.user = user;
        this.comments = comments;
        this.addedDate = Calendar.getInstance().getTime();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Date getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Date addedDate) {
        this.addedDate = addedDate;
    }
}

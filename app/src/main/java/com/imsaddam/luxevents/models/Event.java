package com.imsaddam.luxevents.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

@IgnoreExtraProperties
public class Event implements Serializable, Parcelable {

    String key,title,image, description;
    int category;
    Date eventAddedDate;
    Date eventDate;
    User eventAddedBy;
    String location;

    //constructor
    public Event(){}

    //getter and setters press Alt+Insert

    public Event(String title, String image, String description,int category, String location, Date eventDate) {
        this.title = title;
        this.image = image;
        this.description = description;
        this.category = category;
        this.location = location;
        this.eventDate = eventDate;
        this.eventAddedDate = Calendar.getInstance().getTime();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getEventAddedDate() {
        return eventAddedDate;
    }

    public void setEventAddedDate(Date eventAddedDate) {
        this.eventAddedDate = eventAddedDate;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }



    public User getEventAddedBy() {
        return eventAddedBy;
    }

    public void setEventAddedBy(User eventAddedBy) {
        this.eventAddedBy = eventAddedBy;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
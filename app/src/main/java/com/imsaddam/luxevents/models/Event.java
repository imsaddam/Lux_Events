package com.imsaddam.luxevents.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@IgnoreExtraProperties
public class Event implements Serializable, Parcelable {

    String key, title, image, description, venue;
    int category;
    Date eventAddedDate;
    Date eventDate;
    User eventAddedBy;
    String location;

    List<String> likedId;
    List<Comment> comments;

    //constructor
    public Event() {
    }

    //getter and setters press Alt+Insert

    public Event(String title, String image, String description, int category, Date eventDate, String venue) {
        this.title = title;
        this.image = image;
        this.description = description;
        this.venue = venue;
        this.category = category;
        this.eventDate = eventDate;
        this.eventAddedDate = Calendar.getInstance().getTime();

    }


    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(key);
        parcel.writeString(title);
        parcel.writeString(image);
        parcel.writeString(description);
        parcel.writeString(venue);
        parcel.writeInt(category);
        parcel.writeString(location);
        parcel.writeValue(eventAddedBy);
        parcel.writeList(comments);
        parcel.writeList(likedId);

    }


    protected Event(Parcel in) {
        key = in.readString();
        title = in.readString();
        image = in.readString();
        description = in.readString();
        venue = in.readString();
        category = in.readInt();
        location = in.readString();
        eventAddedBy = (User) in.readValue(User.class.getClassLoader());
        comments = in.readArrayList(Comment.class.getClassLoader());
        likedId = in.readArrayList(String.class.getClassLoader());
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

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

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
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

    public List<String> getLikedId() {
        return likedId;
    }

    public void setLikedId(List<String> likedId) {
        this.likedId = likedId;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
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


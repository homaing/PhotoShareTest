package com.example.sty.photoshare;

import java.util.Date;
import java.util.UUID;

public class Photo {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mShared;
    private String mContact;
    private String comment;

    public Photo() {
        this(UUID.randomUUID());
    }

    public Photo(UUID id) {
        mId = id;
        mDate = new Date();
        mTitle = "Photo Title";
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isShared() {
        return mShared;
    }

    public void setShared(boolean shared) {
        mShared = shared;
    }

    public String getContact() {
        return mContact;
    }

    public void setContact(String contact) {
        mContact = contact;
    }

    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }
}

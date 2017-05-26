package com.example.sty.photoshare.database;


import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.sty.photoshare.Photo;
import com.example.sty.photoshare.database.PhotoDbSchema.PhotoTable;

import java.util.Date;
import java.util.UUID;

public class PhotoCursorWrapper extends CursorWrapper {
    public PhotoCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Photo getPhoto() {
        String uuidString = getString(getColumnIndex(PhotoTable.Cols.UUID));
        String title = getString(getColumnIndex(PhotoTable.Cols.TITLE));
        long date = getLong(getColumnIndex(PhotoTable.Cols.DATE));
        int isShared = getInt(getColumnIndex(PhotoTable.Cols.SHARED));
        String contact = getString(getColumnIndex(PhotoTable.Cols.CONTACT));
        String comment = getString(getColumnIndex(PhotoTable.Cols.COMMENT));

        Photo photo = new Photo(UUID.fromString(uuidString));
        photo.setTitle(title);
        photo.setDate(new Date(date));
        photo.setShared(isShared != 0);
        photo.setContact(contact);
        photo.setComment(comment);

        return photo;
    }
}

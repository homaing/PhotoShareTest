package com.example.sty.photoshare;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.example.sty.photoshare.database.PhotoBaseHelper;
import com.example.sty.photoshare.database.PhotoCursorWrapper;
import com.example.sty.photoshare.database.PhotoDbSchema.PhotoTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PhotoLab {
    //    private static final String TAG = "PhotoLab";
    private static PhotoLab sPhotoLab;

    private SQLiteDatabase mDatabase;
    private Context mApplicationContext;

    public static PhotoLab get(Context context) {
        if (sPhotoLab == null) {
            sPhotoLab = new PhotoLab(context);
        }
        return sPhotoLab;
    }

    private PhotoLab(Context context) {
        mApplicationContext = context.getApplicationContext();
        mDatabase = new PhotoBaseHelper(mApplicationContext)
                .getWritableDatabase();

    }

    public Photo getPhoto(UUID uuid) {
        PhotoCursorWrapper cursor = queryPhotos(PhotoTable.Cols.UUID + " = ?",
                new String[]{uuid.toString()});

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getPhoto();
        } finally {
            cursor.close();

        }
    }

    public List<Photo> getPhotos() {
        List<Photo> photos = new ArrayList<>();
        PhotoCursorWrapper cursor = queryPhotos(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                photos.add(cursor.getPhoto());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return photos;
    }


    private static ContentValues getContentValues(Photo photo) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PhotoTable.Cols.UUID, photo.getId().toString());
        contentValues.put(PhotoTable.Cols.TITLE, photo.getTitle());
        contentValues.put(PhotoTable.Cols.DATE, photo.getDate().getTime());
        contentValues.put(PhotoTable.Cols.SHARED, photo.isShared() ? 1 : 0);
        contentValues.put(PhotoTable.Cols.CONTACT, photo.getContact());
        contentValues.put(PhotoTable.Cols.COMMENT, photo.getComment());

        return contentValues;
    }

    //add
    public void addPhoto(Photo photo) {
        ContentValues values = getContentValues(photo);
        mDatabase.insert(PhotoTable.NAME, null, values);
    }

    //update
    public void updatePhoto(Photo photo) {
        ContentValues values = getContentValues(photo);
        String uuidString = photo.getId().toString();
        mDatabase.update(PhotoTable.NAME, values, PhotoTable.Cols.UUID + " = ?", new String[]{uuidString});
    }

    //query all photo
    private PhotoCursorWrapper queryPhotos(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(PhotoTable.NAME, null, whereClause, whereArgs, null, null, null);
        return new PhotoCursorWrapper(cursor);
    }

    //delete
    public void deletePhoto(Photo photo) {
        String uuidString = photo.getId().toString();
        mDatabase.delete(PhotoTable.NAME, PhotoTable.Cols.UUID + " = ?", new String[]{uuidString});
    }

    public File getPhotoFile(Photo photo) {
        File externalFilesDir = mApplicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

//        Log.d(TAG, "THE FILE PATH IS: " + externalFilesDir);

        if (externalFilesDir == null) {
            return null;
        }
        return new File(externalFilesDir, photo.getPhotoFilename());
    }

}

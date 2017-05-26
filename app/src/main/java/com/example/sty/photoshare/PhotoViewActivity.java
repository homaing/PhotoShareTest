package com.example.sty.photoshare;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;
import java.util.UUID;

import uk.co.senab.photoview.PhotoView;

public class PhotoViewActivity extends AppCompatActivity {

    private static final String EXTRA_PHOTO_ID = "com.example.sty.photoshare.photo_id";
    private PhotoView mPhotoView;
    private PhotoLab mPhotoLab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        UUID photoId= (UUID) getIntent().getSerializableExtra(EXTRA_PHOTO_ID);

        mPhotoView = (PhotoView) findViewById(R.id.photo_view);
        mPhotoLab = PhotoLab.get(this);

        Photo photo = mPhotoLab.getPhoto(photoId);
        File photoFile = mPhotoLab.getPhotoFile(photo);
        Uri uri=Uri.fromFile(photoFile);
        mPhotoView.setImageURI(uri);
    }

    public static Intent newIntent(Context context, UUID photoId) {
        Intent intent = new Intent(context, PhotoViewActivity.class);
        intent.putExtra(EXTRA_PHOTO_ID, photoId);
        return intent;
    }
}

package com.example.sty.photoshare;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

public class PhotoPagerActivity extends AppCompatActivity {

    private static final String EXTRA_PHOTO_ID = "com.example.sty.photoshare.photo_id";

    private ViewPager mViewPager;
    private List<Photo> mPhotos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_pager);

        UUID photoId = (UUID) getIntent().getSerializableExtra(EXTRA_PHOTO_ID);

        mViewPager = (ViewPager) findViewById(R.id.activity_photo_pager_view_pager);

        mPhotos = PhotoLab.get(this).getPhotos();
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(supportFragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Photo photo = mPhotos.get(position);
                return PhotoFragment.newInstance(photo.getId());
            }

            @Override
            public int getCount() {
                return mPhotos.size();
            }
        });

        for (int i = 0; i < mPhotos.size(); i++) {
            if (mPhotos.get(i).getId().equals(photoId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    public static Intent newIntent(Context context, UUID photoId) {

        Intent intent = new Intent(context, PhotoPagerActivity.class);
        intent.putExtra(EXTRA_PHOTO_ID, photoId);
        return intent;
    }
}

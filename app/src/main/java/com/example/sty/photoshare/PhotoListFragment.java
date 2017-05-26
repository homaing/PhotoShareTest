package com.example.sty.photoshare;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class PhotoListFragment extends Fragment {

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private boolean isInGridView;
    private boolean mSubtitleVisible;

    private RecyclerView mPhotoRecyclerView;
    private PhotoAdapter mAdapter;
    private GridPhotoAdapter mGridPhotoAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_list, container, false);

        mPhotoRecyclerView = (RecyclerView) view.findViewById(R.id.photo_recycler_view);
        //set the default layout
        mPhotoRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

//        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Photo photo = new Photo();
//                PhotoLab.get(getActivity()).addPhoto(photo);
//                Intent intent = PhotoPagerActivity.newIntent(getActivity(), photo.getId());
//                startActivity(intent);
//            }
//        });

        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        updateUI();

        updateSubtitle();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }

        MenuItem searchItem = menu.findItem(R.id.menu_item_search_photo);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        //get the query string
        searchView.setQueryHint(QueryPreferences.getStoredQuery(getActivity()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //set the query string
                QueryPreferences.setStoredQuery(getActivity(), query);
                searchView.setQueryHint(QueryPreferences.getStoredQuery(getActivity()));


                //find the photos suit the query title
                List<Photo> photos = PhotoLab.get(getActivity()).getPhotos();

                List<Photo> toRemove = new ArrayList<>();
                for (Photo photo : photos) {
                    //judge what to delete
                    if (!(photo.getTitle().contains(query))) {
                        toRemove.add(photo);
                    }
                }

                photos.removeAll(toRemove);

                mAdapter.setPhotos(photos);
                mPhotoRecyclerView.setAdapter(mAdapter);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_item_show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();

                updateSubtitle();
                return true;
            case R.id.menu_item_change_view:
                if (isInGridView) {
                    mPhotoRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    updateUI();
                } else {
                    mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                    updateGridUI();
                }
                //change the view var
                isInGridView = !isInGridView;

                return true;
            case R.id.menu_item_sort_by_name:
                List<Photo> sortByName = sortPhotos(new Comparator<Photo>() {
                    @Override
                    public int compare(Photo o1, Photo o2) {
                        return o1.getTitle().compareTo(o2.getTitle());
                    }
                });

                mAdapter.setPhotos(sortByName);
                mAdapter.notifyDataSetChanged();

                return true;
            case R.id.menu_item_sort_by_date:
                List<Photo> sortByDate = sortPhotos(new Comparator<Photo>() {
                    @Override
                    public int compare(Photo o1, Photo o2) {
                        return o1.getDate().compareTo(o2.getDate());
                    }
                });

                mAdapter.setPhotos(sortByDate);
                mAdapter.notifyDataSetChanged();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @NonNull
    private List<Photo> sortPhotos(Comparator<Photo> comparator) {
        List<Photo> photos = getPhotos();
        List<Photo> tempList;
        tempList = photos;
        Collections.sort(tempList, comparator);
        return tempList;
    }

    private List<Photo> getPhotos() {
        PhotoLab photoLab = PhotoLab.get(getActivity());
        return photoLab.getPhotos();
    }

    private void updateUI() {
        List<Photo> photos = getPhotos();

        if (mAdapter == null) {
            mAdapter = new PhotoAdapter(photos);
            mPhotoRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setPhotos(photos);
            //set the adapter
            mPhotoRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }

    }

    private void updateGridUI() {
        List<Photo> photos = getPhotos();

        if (mGridPhotoAdapter == null) {
            mGridPhotoAdapter = new GridPhotoAdapter(photos);
            mPhotoRecyclerView.setAdapter(mGridPhotoAdapter);
        } else {
            mGridPhotoAdapter.setPhotos(photos);
            //set the adapter
            mPhotoRecyclerView.setAdapter(mGridPhotoAdapter);
            mGridPhotoAdapter.notifyDataSetChanged();
        }

    }


    private class PhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mDateTextView;
        private CheckBox mSharedCheckBox;
        private TextView mTitleTextView;
        private ImageView mPhotoImageView;

        private Photo mPhoto;

        public PhotoHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_photo_title_text_view);
            mDateTextView = (TextView) itemView.findViewById(R.id.list_item_photo_date_text_view);
            mSharedCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_photo_shared_check_box);
            mPhotoImageView = (ImageView) itemView.findViewById(R.id.list_item_photo_image_view);
        }

        public void bindPhoto(Photo photo) {
            mPhoto = photo;
            mTitleTextView.setText(mPhoto.getTitle());
            mDateTextView.setText(mPhoto.getDate().toString());
            mSharedCheckBox.setChecked(mPhoto.isShared());
            mPhotoImageView.setImageURI(Uri.fromFile(PhotoLab.get(getActivity()).getPhotoFile(mPhoto)));
        }

        @Override
        public void onClick(View v) {
            UUID id = mPhoto.getId();
            Intent intent = PhotoPagerActivity.newIntent(getActivity(), id);
            startActivity(intent);
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        private List<Photo> mPhotos;

        public PhotoAdapter(List<Photo> photos) {
            mPhotos = photos;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_photo, parent, false);

            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            Photo photo = mPhotos.get(position);
            holder.bindPhoto(photo);
        }

        @Override
        public int getItemCount() {
            return mPhotos.size();
        }

        public void setPhotos(List<Photo> photos) {
            mPhotos = photos;
        }

    }

    private void updateSubtitle() {
        PhotoLab photoLab = PhotoLab.get(getActivity());
        int photoCount = photoLab.getPhotos().size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, photoCount, photoCount);

        if (!mSubtitleVisible) {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private class GridPhotoAdapter extends RecyclerView.Adapter<GridPhotoHolder> {

        private List<Photo> mPhotos;

        public GridPhotoAdapter(List<Photo> photos) {
            mPhotos = photos;
        }

        @Override
        public GridPhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity())
                    .inflate(R.layout.grid_list_item_photo, parent, false);
            return new GridPhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(GridPhotoHolder holder, int position) {
            Photo photo = mPhotos.get(position);
            holder.bindPhoto(photo);
        }

        @Override
        public int getItemCount() {
            return mPhotos.size();
        }

        public void setPhotos(List<Photo> photos) {
            mPhotos = photos;
        }
    }

    private class GridPhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mImageView;
        private TextView mPhotoTitle;
        private Photo mPhoto;

        public GridPhotoHolder(View itemView) {
            super(itemView);

            mImageView = (ImageView) itemView.findViewById(R.id.grid_photo_image_view);
            mPhotoTitle = (TextView) itemView.findViewById(R.id.grid_photo_text_view);
        }

        public void bindPhoto(Photo photo) {
            mPhoto = photo;
            mPhotoTitle.setText(mPhoto.getTitle());
            mImageView.setImageURI(Uri.fromFile(PhotoLab.get(getActivity()).getPhotoFile(mPhoto)));

        }

        @Override
        public void onClick(View v) {
            UUID id = mPhoto.getId();
            Intent intent = PhotoPagerActivity.newIntent(getActivity(), id);
            startActivity(intent);
        }
    }
}

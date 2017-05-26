package com.example.sty.photoshare;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.util.Date;
import java.util.UUID;

public class PhotoFragment extends Fragment {

    private static final String ARG_PHOTO_ID = "photo_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO = 2;

    private Photo mPhoto;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSharedCheckBox;
    private Button mReportButton;
    private Button mContactButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;
    private EditText mPhotoComment;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_photo, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_photo:
                PhotoLab.get(getActivity()).deletePhoto(mPhoto);
                //set null value to mPhoto
                mPhoto = null;
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mPhoto.setDate(date);
            updateDate();
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            String[] queryFields = {ContactsContract.Contacts.DISPLAY_NAME};

            Cursor cursor = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);

            try {
                if (cursor.getCount() == 0) {
                    return;
                }
                cursor.moveToFirst();
                String contact = cursor.getString(0);
                mPhoto.setContact(contact);
                mContactButton.setText(contact);
            } finally {
                cursor.close();
            }


        } else if (requestCode == REQUEST_PHOTO) {
            updatePhotoView();
        }

    }

    private void updateDate() {
        mDateButton.setText(mPhoto.getDate().toString());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        UUID photoId = (UUID) getArguments().getSerializable(ARG_PHOTO_ID);

        mPhoto = PhotoLab.get(getActivity()).getPhoto(photoId);
        mPhotoFile = PhotoLab.get(getActivity()).getPhotoFile(mPhoto);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo, container, false);

        mTitleField = (EditText) view.findViewById(R.id.photo_title);
        updateTitle();
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPhoto.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = (Button) view.findViewById(R.id.photo_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment dialog = DatePickerFragment.newInstance(mPhoto.getDate());
                FragmentManager manager = getFragmentManager();
                dialog.setTargetFragment(PhotoFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mSharedCheckBox = (CheckBox) view.findViewById(R.id.photo_shared);
        updateChecked();
        mSharedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPhoto.setShared(isChecked);
            }
        });

        mReportButton = (Button) view.findViewById(R.id.photo_send);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getPhotoReport());
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.photo_report_subject));
                intent = Intent.createChooser(intent, getString(R.string.send_report));
                startActivity(intent);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mContactButton = (Button) view.findViewById(R.id.photo_contact);
        mContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        if (mPhoto.getContact() != null) {
            mContactButton.setText(mPhoto.getContact());
        }

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mContactButton.setEnabled(false);
        }

        mPhotoButton = (ImageButton) view.findViewById(R.id.photo_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(packageManager) != null;
        if (canTakePhoto) {
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        mPhotoView = (ImageView) view.findViewById(R.id.photo_image);
        final Intent showView = PhotoViewActivity.newIntent(getActivity(), mPhoto.getId());
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(showView);
            }
        });

        mPhotoComment = (EditText) view.findViewById(R.id.photo_comment);
        updateComment();
        mPhotoComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPhoto.setComment(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        updatePhotoView();

        return view;
    }

    private void updateComment() {
        mPhotoComment.setText(mPhoto.getComment());
    }

    private void updateChecked() {
        mSharedCheckBox.setChecked(mPhoto.isShared());
    }

    private void updateTitle() {
        mTitleField.setText(mPhoto.getTitle());
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPhoto == null) {
            return;
        }

        PhotoLab.get(getActivity()).updatePhoto(mPhoto);
    }

    public static PhotoFragment newInstance(UUID photoId) {
        PhotoFragment fragment = new PhotoFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PHOTO_ID, photoId);
        fragment.setArguments(args);
        return fragment;
    }

    private String getPhotoReport() {
        String sharedString;
        if (mPhoto.isShared()) {
            sharedString = getString(R.string.photo_report_shared);
        } else {
            sharedString = getString(R.string.photo_report_unshared);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mPhoto.getDate()).toString();

        String contact = mPhoto.getContact();
        if (contact == null) {
            contact = getString(R.string.photo_report_no_contact);
        } else {
            contact = getString(R.string.photo_report_contact, contact);
        }

        String report = getString(R.string.photo_report, mPhoto.getTitle(), dateString, sharedString, contact, mPhoto.getComment());

        return report;
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);

//            mPhotoView.setEnabled(false);
        } else {
            Bitmap bitmap = PhotoUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

}

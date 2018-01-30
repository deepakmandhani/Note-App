package com.sa.mynotes.ui;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sa.mynotes.R;
import com.sa.mynotes.Utils;
import com.sa.mynotes.models.Note;
import com.sa.mynotes.repo.NotesRepoWrapper;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by deepak on 26/11/17.
 */

public class NotesAddOrEditActivity extends AppCompatActivity {

    public static final int LAUNCH_MODE_ADD = 1000;
    public static final int LAUNCH_MODE_EDIT = 1001;

    public static final String NOTES_DATA = "NOTES_DATA";
    public static final String LAUNCH_MODE = "LAUNCH_MODE";
    private static final String DISPLAY_DATE_FORMAT = "dd/MM/yyyy hh:mm";

    private int mLaunchMode;
    private Note mNoteData;
    private Unbinder mUnBinder;
    private Uri imageUri = null;

    @BindView(R.id.notes_edit_text)
    EditText mNotesEditText;

    @BindView(R.id.notes_label)
    TextView mNotesLabel;

    @BindView(R.id.note_image)
    ImageView mNotesImage;

    @BindView(R.id.delete_note_image)
    ImageView mDeleteImage;

    @BindView(R.id.note_no_image_text)
    TextView mNoAttachmentLabel;

    @OnClick(R.id.submit_button_view)
    public void onSubmitButtonClick() {
        if (TextUtils.isEmpty(mNotesLabel.getText().toString())) {
            Toast.makeText(this, getString(R.string.notes_title_validation_message), Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(mNotesEditText.getText().toString())) {
            Toast.makeText(this, getString(R.string.notes_validation_message), Toast.LENGTH_SHORT).show();
            return;
        }
        saveDataToRepo();
        finish();
    }

    @OnClick(R.id.camera_image)
    public void onCameraImageClick() {
        permissionCheck();
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, 0);
    }

    @OnClick(R.id.gallery_image)
    public void onGalleryImageClick() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 1);
    }

    @OnClick(R.id.delete_note_image)
    public void onDeleteNoteClick() {
        NotesRepoWrapper.getInstance().deleteNote(mNoteData);
        deleteNoteImage();
        Toast.makeText(this, getString(R.string.note_deleted_message), Toast.LENGTH_SHORT).show();
        finish();
    }

    @OnClick(R.id.back_button_view)
    public void onBackButtonClicked() {
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_or_edit_view);
        mUnBinder = ButterKnife.bind(this);
        initUI();
    }

    private void initUI() {
        mLaunchMode = getIntent().getIntExtra(LAUNCH_MODE, 0);
        if (mLaunchMode == LAUNCH_MODE_EDIT) {
            mNoteData = Parcels.unwrap(getIntent().getParcelableExtra(NOTES_DATA));
            mNotesEditText.setText(mNoteData.getDescription());
            mNotesEditText.setSelection(mNotesEditText.getText().length());
            mNotesLabel.setText(mNoteData.getTitle());
            mDeleteImage.setVisibility(View.VISIBLE);
            fetchNoteImage();
        }
    }

    private void fetchNoteImage() {
        ContextWrapper cw = new ContextWrapper(this);
        File directory = cw.getDir(Utils.IMAGE_DIRECTORY_NAME, Context.MODE_PRIVATE);
        File myImageFile = new File(directory, mNoteData.getId() + ".png");
        if (myImageFile.exists() && myImageFile.isFile()) {
            Picasso.with(this).load(myImageFile).config(Bitmap.Config.RGB_565).into(mNotesImage);
            mNoAttachmentLabel.setVisibility(View.GONE);
        }
    }

    private void deleteNoteImage() {
        ContextWrapper cw = new ContextWrapper(this);
        File directory = cw.getDir(Utils.IMAGE_DIRECTORY_NAME, Context.MODE_PRIVATE);
        File myImageFile = new File(directory, mNoteData.getId() + ".png");
        if (myImageFile.isFile()) {
            myImageFile.delete();
        }
    }

    private void saveDataToRepo() {
        if (mLaunchMode == LAUNCH_MODE_EDIT) {
            mNoteData.setDescription(mNotesEditText.getText().toString());
            mNoteData.setTitle(mNotesLabel.getText().toString());
        } else {
            mNoteData = new Note();
            mNoteData.setTitle(mNotesLabel.getText().toString());
            mNoteData.setDescription(mNotesEditText.getText().toString());
            mNoteData.setDateAdded(Utils.getTodaysDateFormatinString(DISPLAY_DATE_FORMAT));
            mNoteData.setId(Utils.getRandomNumber());
        }
        if (imageUri != null) {
            Picasso.with(this).load(imageUri).into(picassoImageTarget(this, Utils.IMAGE_DIRECTORY_NAME, mNoteData.getId() + ".png"));
            File file = new File(imageUri.getPath());
            if (file.isFile()) {
                file.deleteOnExit();
            }
        }
        NotesRepoWrapper.getInstance().addNotes(mNoteData);
    }

    private Target picassoImageTarget(Context context, final String imageDir, final String imageName) {
        ContextWrapper cw = new ContextWrapper(context);
        final File directory = cw.getDir(imageDir, Context.MODE_PRIVATE);
        final File myImageFile = new File(directory, imageName);
        return new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(myImageFile);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnBinder != null) {
            mUnBinder.unbind();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    Bitmap bitmap = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    //mNotesImage.setImageBitmap(bitmap);
                    imageUri = getImageUri(this, bitmap);
                    setNoteImage();
                }
                break;

            case 1:
                if (resultCode == RESULT_OK) {
                    imageUri = imageReturnedIntent.getData();
                    setNoteImage();
                }
                break;
        }
    }

    private void setNoteImage() {
        mNotesImage.setImageURI(imageUri);
        mNoAttachmentLabel.setVisibility(View.GONE);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void permissionCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // Explain to the user why we need to read the contacts
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Utils.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                return;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Utils.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    /*
    public Uri getImageUri1(Context inContext, Bitmap inImage) throws Exception {
        ContextWrapper cw = new ContextWrapper(this);
        final File directory = cw.getDir(Utils.IMAGE_DIRECTORY_NAME, Context.MODE_PRIVATE);
        final File myImageFile = new File(directory, mNoteData.getId() + ".png");

        FileOutputStream fos = new FileOutputStream(myImageFile);
        inImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.close();
        return Uri.parse(myImageFile.getPath());
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }*/
}

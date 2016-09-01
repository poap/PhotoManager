package com.poap.photomanager;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.poap.photomanager.db.StoryDB;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class StoryViewActivity extends AppCompatActivity {

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy년 M월 d일 H시 m분에 수정됨", Locale.KOREA) {{
        setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
    }};
    private static final SimpleDateFormat sqlDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA) {{
        setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
    }};

    private StoryDB storyDB;
    private long storyId;
    private TextView descriptionView;
    private List<String> imagePaths;
    private List<String> newImagePaths;
    private LinearLayout imagesContainer;
    private View.OnClickListener fullSizeImage;
    private View addImageView;
    private Dialog addImageDialog;
    private EditText titleView;
    private EditText memoView;
    private TextView editedView;
    private MenuItem actionEdit;
    private MenuItem actionApply;
    private MenuItem actionCancel;
    private boolean editMode;
    private String titleText;
    private String memoText;

    @Override
    public void onBackPressed() {
        if (editMode) {
            rollbackData();
            switchViewMode();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_view);
        storyDB = new StoryDB(this);
        storyId = getIntent().getLongExtra("storyId", -1);
        if (storyId < 0) {
            Toast.makeText(StoryViewActivity.this, "존재하지 않는 스토리입니다.", Toast.LENGTH_SHORT).show();
            supportFinishAfterTransition();
            return;
        }
        descriptionView = (TextView) findViewById(R.id.description);
        imagePaths = new ArrayList<>();
        newImagePaths = new ArrayList<>();
        imagesContainer = (LinearLayout) findViewById(R.id.story_images);
        fullSizeImage = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StoryViewActivity.this, FullscreenImage.class);
                intent.putExtra("path", (String) v.getTag());
                startActivity(intent);
            }
        };

        LinearLayout addImageDialogView = (LinearLayout) View.inflate(this, R.layout.dialog_image_path, null);
        addImageView = findViewById(R.id.add_image);
        addImageDialog = new AlertDialog.Builder(StoryViewActivity.this)
                .setView(addImageDialogView)
                .create();
        addImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImageDialog.show();
            }
        });
        addImageDialogView.findViewById(R.id.action_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImageDialog.dismiss();
            }
        });
        addImageDialogView.findViewById(R.id.action_apply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = ((TextView) addImageDialog.findViewById(R.id.path)).getText().toString();
                newImagePaths.add(path);
                createImage(path);
                updateDescription(imagePaths.size() + newImagePaths.size());
                addImageDialog.dismiss();
            }
        });

        titleView = (EditText) findViewById(R.id.story_title);
        memoView = (EditText) findViewById(R.id.story_memo);
        editedView = (TextView) findViewById(R.id.story_edited);
        editMode = false;

        StoryDB storyDB = new StoryDB(this);
        SQLiteDatabase rdb = storyDB.getReadableDatabase();
        Cursor cursor;

        cursor = rdb.rawQuery("SELECT title, memo, edited FROM story WHERE _id = " + storyId, null);
        {
            cursor.moveToNext();
            String title = cursor.getString(0);
            String memo = cursor.getString(1);
            String edited = cursor.getString(2);
            try {
                edited = format.format(sqlDate.parse(edited));
            } catch (ParseException pe) {
                pe.printStackTrace();
                edited = "수정된 시점을 찾을 수 없습니다";
            }
            setTitle(title);
            ((TextView) findViewById(R.id.story_title)).setText(title);
            ((TextView) findViewById(R.id.story_memo)).setText(memo);
            ((TextView) findViewById(R.id.story_edited)).setText(edited);
        }
        cursor.close();

        cursor = rdb.rawQuery("SELECT path FROM picture WHERE story = " + storyId, null);
        while (cursor.moveToNext()) {
            imagePaths.add(cursor.getString(0));
        }
        cursor.close();

        for (String path : imagePaths) {
            createImage(path);
        }
        updateDescription(imagePaths.size());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_story_view, menu);
        actionEdit = menu.findItem(R.id.action_edit);
        actionApply = menu.findItem(R.id.action_apply);
        actionCancel = menu.findItem(R.id.action_cancel);

        switchViewMode();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //TODO: show confirm alert when user apply and cancel to edit story
        //TODO: implement function for delete story
        switch (item.getItemId()) {
            case R.id.action_edit:
                switchEditMode();
                return true;
            case R.id.action_apply:
                updateData();
                switchViewMode();
                return true;
            case R.id.action_cancel:
                rollbackData();
                switchViewMode();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateDescription(int size) {
        String description = size + "장의 사진";
        descriptionView.setText(description);
    }

    private void createImage(String path) {
        ImageView thumbnailView = (ImageView) getLayoutInflater().inflate(R.layout.story_thumbnail, imagesContainer, false);
        Glide.with(this)
                .load(path)
                .centerCrop()
                .error(R.mipmap.ic_launcher)
                .into(thumbnailView);
        thumbnailView.setTag(path);
        thumbnailView.setOnClickListener(fullSizeImage);
        imagesContainer.addView(thumbnailView, imagesContainer.getChildCount() - 1);
    }

    private void updateData() {
        SQLiteDatabase wdb = storyDB.getWritableDatabase();
        Date now = new Date();
        Cursor cursor;

        titleText = titleView.getText().toString();
        memoText = memoView.getText().toString();

        cursor = wdb.rawQuery("UPDATE story SET title = '" + titleText + "', memo = '" + memoText
                + "', edited = '" + sqlDate.format(now) + "' WHERE _id = " + storyId, null);
        cursor.moveToFirst();
        cursor.close();

        for (String path : newImagePaths) {
            cursor = wdb.rawQuery("INSERT INTO picture (path, story) VALUES ('" + path + "', '" + storyId + "')", null);
            cursor.moveToFirst();
            cursor.close();
            imagePaths.add(path);
        }
        newImagePaths.clear();

        wdb.close();
        setTitle(titleText);
        updateDescription(imagePaths.size());
        editedView.setText(format.format(now));

        setResult(RESULT_OK);
    }

    private void rollbackData() {
        titleView.setText(titleText);
        memoView.setText(memoText);
        while (!newImagePaths.isEmpty()) {
            imagesContainer.removeViewAt(imagePaths.size());
            newImagePaths.remove(0);
        }
        updateDescription(imagePaths.size());
    }

    private void switchEditMode() {
        actionEdit.setVisible(false);
        actionApply.setVisible(true);
        actionCancel.setVisible(true);
        addImageView.setVisibility(View.VISIBLE);
        titleView.setEnabled(true);
        titleText = titleView.getText().toString();
        memoView.setEnabled(true);
        memoText = memoView.getText().toString();
        editMode = true;

        //TODO: implement removing image
    }

    private void switchViewMode() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(titleView.getApplicationWindowToken(), 0);

        actionEdit.setVisible(true);
        actionApply.setVisible(false);
        actionCancel.setVisible(false);
        addImageView.setVisibility(View.GONE);
        titleView.clearFocus();
        titleView.setEnabled(false);
        memoView.clearFocus();
        memoView.setEnabled(false);
        editMode = false;
    }

}

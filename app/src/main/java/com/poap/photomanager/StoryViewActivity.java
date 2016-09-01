package com.poap.photomanager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.poap.photomanager.db.StoryDB;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class StoryViewActivity extends AppCompatActivity {

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy년 M월 d일 H시 m분에 수정됨", Locale.KOREA);
    private static final SimpleDateFormat sqlDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_view);

        long storyId = getIntent().getLongExtra("storyId", -1);
        if (storyId < 0) {
            Toast.makeText(StoryViewActivity.this, "존재하지 않는 스토리입니다.", Toast.LENGTH_SHORT).show();
            supportFinishAfterTransition();
            return;
        }

        findViewById(R.id.add_image).setVisibility(View.GONE);

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
        {
            LinearLayout imagesContainer = (LinearLayout) findViewById(R.id.story_images);
            String description = cursor.getCount() + "장의 사진";
            ((TextView) findViewById(R.id.description)).setText(description);
            int index = 0;
            while (cursor.moveToNext()) {
                ImageView thumbnailView = (ImageView) getLayoutInflater().inflate(R.layout.story_thumbnail, imagesContainer, false);
                Glide.with(this)
                        .load(cursor.getString(0))
                        .centerCrop()
                        .into(thumbnailView);
                imagesContainer.addView(thumbnailView, index);
                index += 1;
            }
        }
        cursor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_story_view, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

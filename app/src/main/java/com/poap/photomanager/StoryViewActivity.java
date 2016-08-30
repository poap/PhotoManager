package com.poap.photomanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class StoryViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_view);
        setTitle("제목");
        Glide.with(this)
                .load(getIntent().getStringExtra("data"))
                .centerCrop()
                .into((ImageView) findViewById(R.id.thumbnail));
    }
}

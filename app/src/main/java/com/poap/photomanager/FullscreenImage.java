package com.poap.photomanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class FullscreenImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);

        String path = getIntent().getStringExtra("path");
        if (path != null) {
            Glide.with(this)
                    .load(path)
                    .error(R.mipmap.ic_launcher)
                    .into((ImageView) findViewById(R.id.full_image));
        } else {
            Toast.makeText(FullscreenImage.this, "사진을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            supportFinishAfterTransition();
        }
    }

}

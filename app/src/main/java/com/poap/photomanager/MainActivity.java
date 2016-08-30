package com.poap.photomanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class MainActivity extends AppCompatActivity {

    private SimpleDateFormat format = new SimpleDateFormat("yyyy년 M월 d일 H시 m분", Locale.KOREA);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.story));

        StickyListHeadersListView storyListView = (StickyListHeadersListView) findViewById(R.id.list_story);
        StoryListAdapter adapter = new StoryListAdapter(this);
        storyListView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                createStory();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createStory() {
        Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
        startActivity(intent);
    }

    public class StoryListAdapter extends BaseAdapter implements StickyListHeadersAdapter {

        private Context context;
        private LayoutInflater inflater;
        private List<StoryListElem> elems;

        public StoryListAdapter(Context context) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            elems = new ArrayList<StoryListElem>() {{
                add(new StoryListElem("http://i.telegraph.co.uk/multimedia/archive/03589/Wellcome_Image_Awa_3589699k.jpg", "from google image1", new Date()));
                add(new StoryListElem("http://www.qqxxzx.com/images/image/image-16.png", "from google image1", new Date()));
                add(new StoryListElem("https://pixabay.com/static/uploads/photo/2015/10/01/21/39/background-image-967820_960_720.jpg", "from google image1", new Date()));
            }};
        }

        @Override
        public int getCount() {
            return elems.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return elems.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.list_item_story, parent, false);
                holder.thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.created = (TextView) convertView.findViewById(R.id.created);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            StoryListElem item = elems.get(position);
            Glide.with(context)
                    .load(item.imagePath)
                    .centerCrop()
                    .into(holder.thumbnail);
            holder.title.setText(item.title);
            holder.created.setText(format.format(item.created));

            return convertView;
        }

        @Override
        public long getHeaderId(int position) {
            return position;
        }

        @Override
        public View getHeaderView(int position, View convertView, ViewGroup parent) {
            HeaderViewHolder holder;

            if (convertView == null) {
                holder = new HeaderViewHolder();
                convertView = inflater.inflate(R.layout.list_header_story, parent, false);
                holder.textView = (TextView) convertView.findViewById(R.id.header_name);
                convertView.setTag(holder);
            } else {
                holder = (HeaderViewHolder) convertView.getTag();
            }

            holder.textView.setText(String.valueOf(getHeaderId(position)));

            return convertView;
        }

        private class HeaderViewHolder {
            TextView textView;
        }

        private class ViewHolder {
            ImageView thumbnail;
            TextView title;
            TextView created;
        }

    }

    public class StoryListElem {
        public String imagePath;
        public String title;
        public Date created;

        public StoryListElem(String imagePath, String title, Date created) {
            this.imagePath = imagePath;
            this.title = title;
            this.created = created;
        }

    }

}

package com.poap.photomanager;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.poap.photomanager.db.StoryDB;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class MainActivity extends AppCompatActivity {

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy년 M월 d일 H시 m분", Locale.KOREA) {{
        setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
    }};
    private static final SimpleDateFormat sqlDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA) {{
        setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
    }};
    private static final SimpleDateFormat month = new SimpleDateFormat("yyyyMM", Locale.KOREA) {{
        setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
    }};
    private static final int REQUEST_STORY_VIEW = 1;

    private StoryDB storyDB;
    private StoryListAdapter adapter;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_STORY_VIEW) {
            if (resultCode == RESULT_OK) {
                adapter.reload();
                adapter.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        storyDB = new StoryDB(this);
        adapter = new StoryListAdapter(this);

        StickyListHeadersListView storyListView = (StickyListHeadersListView) findViewById(R.id.list_story);
        storyListView.setAdapter(adapter);
        storyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, StoryViewActivity.class);
                intent.putExtra("storyId", adapter.getItemId(position));
                startActivityForResult(intent, REQUEST_STORY_VIEW);
            }
        });

        setTitle(getString(R.string.story) + " (" + adapter.getCount() + ")");

        //TODO: implement search function
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
        //TODO: create story with image url
    }

    public class StoryListAdapter extends BaseAdapter implements StickyListHeadersAdapter {

        private Context context;
        private LayoutInflater inflater;
        private Map<Long, StoryListElem> stories;
        private List<Long> pos;

        public StoryListAdapter(Context context) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            stories = new HashMap<>();
            pos = new ArrayList<>();

            reload();
        }

        @Override
        public int getCount() {
            return stories.size();
        }

        @Override
        public long getItemId(int position) {
            return pos.get(position);
        }

        @Override
        public StoryListElem getItem(int position) {
            return stories.get(pos.get(position));
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.list_item_story, parent, false);
                holder.thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.edited = (TextView) convertView.findViewById(R.id.edited);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            StoryListElem item = getItem(position);
            Glide.with(context)
                    .load(item.imagePath.get(0))
                    .centerCrop()
                    .into(holder.thumbnail);
            holder.title.setText(item.title);
            holder.edited.setText(format.format(item.edited));

            return convertView;
        }

        @Override
        public long getHeaderId(int position) {
            return Long.valueOf(month.format(getItem(position).edited));
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

            long y = getHeaderId(position);
            long m = y % 100;
            y = y / 100;
            String headerLabel = y + "년 " + m + "월";
            holder.textView.setText(headerLabel);
            //TODO: show number of stories in each group

            return convertView;
        }

        public void reload() {
            SQLiteDatabase rdb = storyDB.getReadableDatabase();
            Cursor cursor;

            stories.clear();
            pos.clear();

            cursor = rdb.rawQuery("SELECT _id, title, memo, edited FROM story ORDER BY edited desc", null);
            {
                Date edited;
                long id;
                String title;
                while (cursor.moveToNext()) {
                    id = cursor.getLong(0);
                    title = cursor.getString(1);

                    edited = new Date(0);
                    try {
                        edited = sqlDate.parse(cursor.getString(3));
                    } catch (ParseException pe) {
                        pe.printStackTrace();
                    }

                    stories.put(id, new StoryListElem(title, edited));
                    pos.add(id);
                }
            }
            cursor.close();

            cursor = rdb.rawQuery("SELECT _id, path, story FROM picture", null);
            {
                String path;
                long storyId;
                while (cursor.moveToNext()) {
                    path = cursor.getString(1);
                    storyId = cursor.getLong(2);
                    stories.get(storyId).imagePath.add(path);
                }
            }
            cursor.close();

            rdb.close();
        }

        private class HeaderViewHolder {
            TextView textView;
        }

        private class ViewHolder {
            ImageView thumbnail;
            TextView title;
            TextView edited;
        }

    }

    public class StoryListElem {
        public String title;
        public Date edited;
        public List<String> imagePath;

        public StoryListElem(String title, Date edited) {
            this.title = title;
            this.edited = edited;
            imagePath = new ArrayList<>();
        }

    }

}

package aterbo.MemoryBite.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;

import aterbo.MemoryBite.R;
import aterbo.MemoryBite.adapters.FullScreenImageAdapter;

/**
 * Created by ATerbo on 11/4/15.
 */
public class FullScreenViewActivity extends Activity{

        private FullScreenImageAdapter adapter;
        private ViewPager viewPager;
        private ArrayList photos;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_fullscreen_view);

            viewPager = (ViewPager) findViewById(R.id.pager);


            Intent i = getIntent();
            int position = i.getIntExtra("id", 0);
            photos = i.getParcelableArrayListExtra("photoList");

            adapter = new FullScreenImageAdapter(FullScreenViewActivity.this, photos);

            viewPager.setAdapter(adapter);

            // displaying selected image first
            viewPager.setCurrentItem(position);
        }
    }


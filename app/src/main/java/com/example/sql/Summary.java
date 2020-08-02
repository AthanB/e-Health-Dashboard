package com.example.sql;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class Summary extends AppCompatActivity {
    Toolbar mToolbar;
    TabLayout mTabLayout;
    TabItem Tab1;
    TabItem Tab2;
    TabItem Tab3;
    ViewPager mPager;
    PagerController mPagerController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        mToolbar = findViewById(R.id.toolBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Summary");

        mTabLayout = findViewById(R.id.tabLayout);
        Tab1 = findViewById(R.id.Tab1);
        Tab2 = findViewById(R.id.Tab2);
        Tab3 = findViewById(R.id.Tab3);
        mPager = findViewById(R.id.viewPager);
        mPagerController = new PagerController(getSupportFragmentManager(),mTabLayout.getTabCount());
        mPager.setAdapter(mPagerController);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
    }
}

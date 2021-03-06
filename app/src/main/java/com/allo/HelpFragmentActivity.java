package com.allo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

/**
 * Created by baek_uncheon on 2015. 3. 26..
 */
public class HelpFragmentActivity extends FragmentActivity {
    static final int NUM_ITEMS = 3;

    ImageView iv_back;

    TextView tv_current;

    TextView tv_first;
    TextView tv_second;
    TextView tv_third;

    SlidePagerAdapter vpa_question;
    android.support.v4.view.ViewPager vp_question;


    ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    ArrayList<String> titles = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("HelpFragmentActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        setLayout();
        setInstance();
        setListener();

        String st_index = getIntent().getStringExtra("index");
        switch (st_index) {
            case "cash":
                clickFirst();
                break;
            case "basic":
                clickSecond();
                break;
            case "friend":
                clickThird();
                break;

        }


    }

    private void setInstance() {
        HelpFragment cashFragment = new HelpFragment();
        cashFragment.setIndex("cash");
        cashFragment.setViewPager(vp_question);

        HelpFragment basicFragment = new HelpFragment();
        basicFragment.setIndex("basic");
        basicFragment.setViewPager(vp_question);

        HelpFragment friendFragment = new HelpFragment();
        friendFragment.setIndex("friend");
        friendFragment.setViewPager(vp_question);

        fragments.add(cashFragment);
        titles.add("cash");

        fragments.add(basicFragment);
        titles.add("basic");

        fragments.add(friendFragment);
        titles.add("friend");

    }


    private void setLayout() {
        iv_back = (ImageView) findViewById(R.id.iv_back);

        tv_first = (TextView) findViewById(R.id.tv_first);
        tv_second = (TextView) findViewById(R.id.tv_second);
        tv_third = (TextView) findViewById(R.id.tv_third);

        vp_question = (ViewPager) findViewById(R.id.vp_question);
        vpa_question = new SlidePagerAdapter(getSupportFragmentManager());
        vp_question.setAdapter(vpa_question);

    }

    private void setListener() {

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_first.setOnClickListener(categoryListener);
        tv_second.setOnClickListener(categoryListener);
        tv_third.setOnClickListener(categoryListener);

        vp_question.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        clickFirst();
                        break;
                    case 1:
                        clickSecond();
                        break;
                    case 2:
                        clickThird();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    View.OnClickListener categoryListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_first:
                    if (tv_current != tv_first) {
                        clickFirst();
                    }
                    break;
                case R.id.tv_second:
                    if (tv_current != tv_second) {
                        clickSecond();
                    }
                    break;
                case R.id.tv_third:
                    if (tv_current != tv_third)
                        clickThird();
                    break;
            }
        }
    };

    private void clickFirst() {

        tv_current = tv_first;
        tv_first.setBackgroundResource(R.drawable.border_bottom_red);
//        tv_first.setTextColor(Color.parseColor("#f91e2f"));

        tv_second.setBackgroundResource(0);
//        tv_second.setTextColor(Color.parseColor("#000000"));

        tv_third.setBackgroundResource(0);
//        tv_third.setTextColor(Color.parseColor("#000000"));

        vp_question.setCurrentItem(0, true);


    }

    private void clickSecond() {
        tv_current = tv_second;

        tv_first.setBackgroundResource(0);
//        tv_first.setTextColor(Color.parseColor("#000000"));

        tv_second.setBackgroundResource(R.drawable.border_bottom_red);
//        tv_second.setTextColor(Color.parseColor("#f91e2f"));

        tv_third.setBackgroundResource(0);
//        tv_third.setTextColor(Color.parseColor("#000000"));

        vp_question.setCurrentItem(1, true);
    }

    private void clickThird() {
        tv_current = tv_third;

        tv_first.setBackgroundResource(0);
//        tv_first.setTextColor(Color.parseColor("#000000"));

        tv_second.setBackgroundResource(0);
//        tv_second.setTextColor(Color.parseColor("#000000"));

        tv_third.setBackgroundResource(R.drawable.border_bottom_red);
//        tv_third.setTextColor(Color.parseColor("#f91e2f"));

        vp_question.setCurrentItem(2, true);
    }


    public class SlidePagerAdapter extends FragmentPagerAdapter {
        public SlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public String getPageTitle(int position) {
            Log.i("title", titles.get(position));
            return titles.get(position);
        }

    }

    @Override
    protected void onStart(){
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }
}

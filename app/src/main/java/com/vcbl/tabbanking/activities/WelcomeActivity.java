package com.vcbl.tabbanking.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.vcbl.tabbanking.R;
import com.vcbl.tabbanking.adapters.MyPagerAdapter;
import com.vcbl.tabbanking.utils.PreferenceManager;

public class WelcomeActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "WelcomeActivity";
    private ViewPager mPager;
    private int[] layouts = {
            R.layout.first_slide,
            R.layout.second_slide,
            R.layout.third_slide,
            R.layout.fourth_slide
    };
    private LinearLayout layoutDots;
    private Button btn_next, btn_skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Log.i(TAG, "--------Entered---------");

        if (new PreferenceManager(this).checkPreference()) {
            loadHome();
        }

        /*if (Build.VERSION.SDK_INT >= 19) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }*/

        //Making notification bar transparent transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        changeStatusBarColor();

        mPager =  findViewById(R.id.viewPager);
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(layouts, this);
        mPager.setAdapter(myPagerAdapter);

        layoutDots =  findViewById(R.id.layoutDots);
        btn_next =  findViewById(R.id.btn_next);
        btn_skip =  findViewById(R.id.btn_skip);
        btn_next.setOnClickListener(this);
        btn_skip.setOnClickListener(this);

        createDots(0);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                createDots(position);
                if (position == layouts.length - 1) {
                    btn_next.setText(R.string.start);
                    btn_skip.setVisibility(View.INVISIBLE);
                } else {
                    btn_next.setText(R.string.next);
                    btn_skip.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    // Making notification bar transparent
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void createDots(int currentPosition) {
        if (layoutDots != null) {
            layoutDots.removeAllViews();
        }
        ImageView[] dots = new ImageView[layouts.length];
        // it is colors active and de-active colors
        //int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        //int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);
        for (int i = 0; i < layouts.length; i++) {
            dots[i] = new ImageView(this);
            if (i == currentPosition) {
                dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.active_dots));
            } else {
                dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.default_dots));
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(4, 0, 4, 0);
            layoutDots.addView(dots[i], params);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                loadNextSlide();
                break;
            case R.id.btn_skip:
                loadHome();
                new PreferenceManager(this).writePreference();
                break;
        }
    }

    public void loadHome() {
        Intent intent = new Intent(this, SplashActivity.class);
        startActivity(intent);
        finish();
    }

    private void loadNextSlide() {
        int next_slide = mPager.getCurrentItem() + 1;
        if (next_slide < layouts.length) {
            mPager.setCurrentItem(next_slide);
        } else {
            loadHome();
            new PreferenceManager(this).writePreference();
        }
    }
}

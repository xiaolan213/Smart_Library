package com.example.xiaolan.myapplication;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private List<Fragment> mFragments;
    private int lastIndex;
    private BottomNavigationView bottomNavigationView;
    FragmentPagerAdapter adapter;
    private ViewPager viewPager;

    private void init() {
        viewPager = findViewById(R.id.vp_pager);
    }

    private void initNavigation() {
        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_check:
                        viewPager.setCurrentItem(0);
                        //setFragmentPosition(0);
                        return true;
                    case R.id.navigation_book:
                        viewPager.setCurrentItem(1);
                        //setFragmentPosition(1);
                        return true;
                    case R.id.navigation_personal:
                        viewPager.setCurrentItem(2);
                        // setFragmentPosition(2);
                        return true;
                    default:
                        break;
                }
                return false;
            }
        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                bottomNavigationView.getMenu().getItem(i).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        init();
        initNavigation();
        initData();

    }

    public void initData() {
        mFragments = new ArrayList<>();
        mFragments.add(new FirstFragment());
        mFragments.add(new SecondFragment());
        mFragments.add(new ThirdFragment());
        // 初始化展示MessageFragment
        //setFragmentPosition(0);
        adapter = new MyFragmentPageAdapter(getSupportFragmentManager(), this, mFragments);
        viewPager.setAdapter(adapter);
        //重写pageTransformer改变fragment切换的动画
        viewPager.setPageTransformer(true, new MyPagerTransition());

    }
}

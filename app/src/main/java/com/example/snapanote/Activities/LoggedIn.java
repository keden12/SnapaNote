package com.example.snapanote.Activities;

import android.Manifest;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.snapanote.Fragments.Logged;
import com.example.snapanote.Fragments.Login;
import com.example.snapanote.Fragments.Modules;
import com.example.snapanote.Fragments.Register;
import com.example.snapanote.R;
import com.example.snapanote.Utils.SectionsStatePagerAdapter;
import com.google.firebase.FirebaseApp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class LoggedIn extends AppCompatActivity {


    private static final String TAG = "MainActivity";

    private SectionsStatePagerAdapter mSectionsStatePagerAdapter;
    private ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(LoggedIn.this);
        setContentView(R.layout.activity_main);

        mSectionsStatePagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);

        setupViewPager(mViewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Logged(), "Logged In");
        adapter.addFragment(new Modules(), "Modules");
        viewPager.setAdapter(adapter);
    }

    public void setViewPager(int fragmentNumber) {

        mViewPager.setCurrentItem(fragmentNumber);

    }

    public void onBackPressed() {

    }






    //Runtime Permission Check so the app won't crash


}
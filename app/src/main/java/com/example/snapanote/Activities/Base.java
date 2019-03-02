package com.example.snapanote.Activities;

import android.Manifest;
import android.os.Build;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.snapanote.Fragments.Logged;
import com.example.snapanote.Fragments.Login;
import com.example.snapanote.Fragments.Modules;
import com.example.snapanote.Fragments.Register;
import com.example.snapanote.R;
import com.example.snapanote.Utils.SectionsStatePagerAdapter;
import com.google.firebase.FirebaseApp;

public class Base extends AppCompatActivity {


    private static final String TAG = "MainActivity";

    private SectionsStatePagerAdapter mSectionsStatePagerAdapter;
    private ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(Base.this);
        setContentView(R.layout.activity_main);
        checkFilePermissions();

        mSectionsStatePagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);

        setupViewPager(mViewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Login(), "Login");
        adapter.addFragment(new Register(), "Register");
        viewPager.setAdapter(adapter);
    }

    public void setViewPager(int fragmentNumber) {

        mViewPager.setCurrentItem(fragmentNumber);

    }


    //Runtime Permission Check so the app won't crash
    private void checkFilePermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = Base.this.checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
            permissionCheck += Base.this.checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
            permissionCheck += Base.this.checkSelfPermission("Manifest.permission.CAMERA");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1001); //Any number
            }
        } else {
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    public void onBackPressed() {

    }

}

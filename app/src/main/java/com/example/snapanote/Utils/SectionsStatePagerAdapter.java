package com.example.snapanote.Utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class SectionsStatePagerAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> FragList = new ArrayList<>();
    private final List<String> FragTitleList = new ArrayList<>();

    public SectionsStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment, String title){
       FragList.add(fragment);
       FragTitleList.add(title);

    }

    @Override
    public Fragment getItem(int position) {
        return FragList.get(position);
    }

    @Override
    public int getCount() {
        return FragList.size();
    }
}

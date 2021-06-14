package com.example.newsapp;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    public SimpleFragmentPagerAdapter(FragmentManager fm) {

        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment myFragment = null;
        switch (position) {
            case 0:
                myFragment = new NewsAppFragment();
                break;
        }
        return myFragment;
    }

    @Override
    public int getCount() {
        return 1;
    }
}
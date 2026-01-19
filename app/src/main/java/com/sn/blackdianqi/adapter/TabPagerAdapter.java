package com.sn.blackdianqi.adapter;


import com.sn.blackdianqi.base.BaseFragment;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabPagerAdapter extends FragmentPagerAdapter {

    private List<BaseFragment> fragmentList;


    public TabPagerAdapter(FragmentManager fragmentManager, List<BaseFragment> fragmentList) {
        super(fragmentManager);
        this.fragmentList = fragmentList;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        if (fragmentList.isEmpty()) {
            return 0;
        }
        return fragmentList.size();
    }
}

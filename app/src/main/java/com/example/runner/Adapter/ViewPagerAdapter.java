package com.example.runner.Adapter;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.runner.R;
import com.example.runner.fragments.AllRunsFragment;
import com.example.runner.fragments.StatisticsFragment;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private final List<Fragment> fragmentList;
    private final List<String> stringList;
    private final List<Integer> drawables;

    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        stringList = new ArrayList<>();
        fragmentList = new ArrayList<>();
        drawables = new ArrayList<>();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }

    public CharSequence getPageTitle(int position){
        return stringList.get(position);
    }
    public int getDrawables(int position){
        return drawables.get(position);
    }


    public void addFragment(Fragment fragment, String title,Integer res){
        fragmentList.add(fragment);
        stringList.add(title);
        drawables.add(res);
    }
}

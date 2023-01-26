package com.example.sparktrials.main.home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * Custom adapter for viewpager in HomeFragment
 */
public class FragmentPagerAdapter extends FragmentStateAdapter {
    public static int fragmentSize = 2;

    public FragmentPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    /**
     * Creates the respective fragment for each tab.
     * @param position
     *      position of tab
     * @return
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return TabMyExperiments.newInstance();
            case 1:
                return TabSubscribed.newInstance();
            default:
                return null;
        }
    }

    /**
     * returns number of fragments in the adapter
     * @return
     */
    @Override
    public int getItemCount() {
        return 2;
    }
}


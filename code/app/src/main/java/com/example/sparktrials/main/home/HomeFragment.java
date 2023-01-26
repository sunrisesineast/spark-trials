package com.example.sparktrials.main.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.sparktrials.Callback;
import com.example.sparktrials.FirebaseManager;
import com.example.sparktrials.IdManager;
import com.example.sparktrials.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * UI Fragment class that displays user's subscribed and published experiments in their respective tabs
 */
public class HomeFragment extends Fragment {

    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    private FragmentPagerAdapter pagerAdapter;
    private TextView profileNameView;

    @Override
    public void onStart() {
        super.onStart();


    }

    /**
     * Creates the HomeFragment view
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     *      returns the HomeFragment view
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        return root;

    }

    /**
     * After HomeFragment view is created, populates whats inside the view.
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tabLayout =  getView().findViewById(R.id.tablayout_id);
        pagerAdapter = new FragmentPagerAdapter(getChildFragmentManager(),getViewLifecycleOwner().getLifecycle());
        viewPager2 = getView().findViewById(R.id.pager_id);
        viewPager2.setAdapter(pagerAdapter);
        //switches between tabs
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });



        profileNameView = getView().findViewById(R.id.profile_name);

        FirebaseManager firebaseManager = new FirebaseManager();
        IdManager idManager = new IdManager(this.getContext());
        String userID = idManager.getUserId();

        firebaseManager.get("users", userID, new Callback() {
            @Override
            public void onCallback(DocumentSnapshot document) {
                String name = "  " + (String) document.get("name");
                profileNameView.setText(name);
            }
        });



    }
}
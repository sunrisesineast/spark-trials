package com.example.sparktrials.main.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.sparktrials.CustomList;
import com.example.sparktrials.ExperimentActivity;
import com.example.sparktrials.IdManager;
import com.example.sparktrials.R;
import com.example.sparktrials.models.Experiment;

import java.util.ArrayList;

/**
 * Fragment for showing user's subscribed experiments
 * Created by viewpager in HomeFragment
 */
public class TabSubscribed extends Fragment {
    private ListView subExperiments;
    private CustomList subExperiment_adapter;
    private Context context;
    private HomeViewModel homeViewModel;


    public TabSubscribed() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of this TabSubscribed
     * @return A new instance of fragment tab_subscribed.
     */
    public static TabSubscribed newInstance() {
        return new TabSubscribed();
    }

    /**
     * When the view's is in use, it loads the required experiments and displays them
     */
    @Override
    public void onStart() {
        super.onStart();
        subExperiments = getView().findViewById(R.id.subscribed_list);
        context = getActivity();
        IdManager idManager = new IdManager(context);
        homeViewModel = new HomeViewModel(idManager.getUserId());
        //syncs tab view with firebase
        final Observer<ArrayList<Experiment>> nameObserver = new Observer<ArrayList<Experiment>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<Experiment> newList) {
                subExperiment_adapter = new CustomList(context, newList);
                subExperiments.setAdapter(subExperiment_adapter);
            }
        };
        homeViewModel.getSubExpList().observe(getViewLifecycleOwner(), nameObserver);
        subExperiments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Experiment experiment = subExperiment_adapter.getItem(position);
                startExperimentActivity(experiment.getId());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab_subscribed, container, false);
    }

    /**
     * Launches ExperimentActivity with the experiment the user clicked on
     * @param experimentID
     *      the ID of the experiment the user clicked on
     */
    private void startExperimentActivity(String experimentID){
        Intent intent = new Intent(this.getActivity(), ExperimentActivity.class);
        intent.putExtra("EXPERIMENT_ID", experimentID);
        startActivity(intent);

    }
}
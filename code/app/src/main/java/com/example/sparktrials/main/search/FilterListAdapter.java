package com.example.sparktrials.main.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sparktrials.R;
import com.example.sparktrials.models.Experiment;
import com.example.sparktrials.models.ExperimentComparator;

import java.util.ArrayList;

/**
 * This class represents an adapter for the spinner that is meant to represent the filters to be applied
 * to a search in SearchFragment.
 */
public class FilterListAdapter extends ArrayAdapter<String> {

    // adapter will display on the ListView
    private Context context;

    // These are the CheckBoxes in filter_list.xml, which represent the filters to be applied to searches.
    private CheckBox titleCheckBox, descriptionCheckBox, usernameCheckBox, activeStatusCheckBox, inactiveStatusCheckBox;

    /**
     * Constructor for a CustomList list adapter, which shows a customized list of filters that will
     * be used to filter out the experiments search
     * @param context
     *      The context which we are currently in
     * @param items
     *      The list of filters (length 5): {"Title", "Description", "Username", "Status: Active", "Status: Inactive"}
     */
    public FilterListAdapter(@NonNull Context context, String[] items) {
        super(context, 0, items); // items is just a throwaway array of length one containing ""
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            // Set the layout of list items to be based on list_content.xml
            view = LayoutInflater.from(context).inflate(R.layout.filters_closed, parent, false);
        }

        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            // Set the layout of list items to be based on list_content.xml
            view = LayoutInflater.from(context).inflate(R.layout.filter_list, parent, false);
        }

        titleCheckBox = view.findViewById(R.id.check_box_title);
        descriptionCheckBox = view.findViewById(R.id.check_box_description);
        usernameCheckBox = view.findViewById(R.id.check_box_username);
        activeStatusCheckBox = view.findViewById(R.id.check_box_status_active);
        inactiveStatusCheckBox = view.findViewById(R.id.check_box_status_inactive);

        // If one of the field checkboxes is checked, uncheck the other two.
        titleCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    descriptionCheckBox.setChecked(false);
                    usernameCheckBox.setChecked(false);
                }
            }
        });
        descriptionCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    titleCheckBox.setChecked(false);
                    usernameCheckBox.setChecked(false);
                }
            }
        });
        usernameCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    titleCheckBox.setChecked(false);
                    descriptionCheckBox.setChecked(false);
                }
            }
        });

        // If one of the status checkboxes is checked, uncheck the other one.
        activeStatusCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    inactiveStatusCheckBox.setChecked(false);
                }
            }
        });

        inactiveStatusCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    activeStatusCheckBox.setChecked(false);
                }
            }
        });

        return view;
    }

    /**
     * Gets the checked checkboxes. Meant to return an integer array of length 2, which will act
     * as a filter list for searches.
     * In position 0, 0 means none of the field (Title, Description, Username) checkboxes are checked,
     *     1 means 'Title' is checked, 2 means 'Description' is checked, 3 means 'Username' is checked.
     * In position 1, 0 means none of the status (Active, Inactive) checkboxes are checked,
     *     1 means 'Status: Active' is checked, 2 means 'Status: Inactive' is checked.
     * @return
     *      Returns the integer array of length 2 (the filter list) that represents
     *      which checkboxes are checked.
     */
    public int[] getChecked() {
        int[] result = new int[2]; // Initialized to [0, 0]

        if (titleCheckBox != null && titleCheckBox.isChecked()) {
            result[0] = 1;
        } else if (descriptionCheckBox != null && descriptionCheckBox.isChecked()) {
            result[0] = 2;
        } else if (usernameCheckBox != null && usernameCheckBox.isChecked()) {
            result[0] = 3;
        }

        if (activeStatusCheckBox != null && activeStatusCheckBox.isChecked()) {
            result[1] = 1;
        } else if (inactiveStatusCheckBox != null && inactiveStatusCheckBox.isChecked()) {
            result[1] = 2;
        }

        return result;
    }

}

package com.example.sparktrials.exp.forum;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sparktrials.FirebaseManager;
import com.example.sparktrials.IdManager;
import com.example.sparktrials.R;
import com.example.sparktrials.models.Experiment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Date;
import java.util.HashMap;

/**
 * This class represents the fragment when you click on "ask question" button.
 */
public class ForumAskQuestionFragment extends BottomSheetDialogFragment {
    private Experiment experiment;

    /**
     * Creates a new instance of ForumAskQuestionFragment.
     * @param experiment an experiment to pass to the new instance.
     * @return a new ForumAskQuestionFragment.
     */
    public static ForumAskQuestionFragment newInstance(Experiment experiment) {
        return new ForumAskQuestionFragment(experiment);
    }

    /**
     * Constructor for ForumAskQuestionFragment.
     * @param experiment an experiment to pass to the fragment.
     */
    public ForumAskQuestionFragment(Experiment experiment) {
        this.experiment = experiment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forum_ask_question, container, false);
        TextView cancelButton = view.findViewById(R.id.forum_ask_cancel);
        TextView postButton = view.findViewById(R.id.forum_ask_post);
        EditText titleText = view.findViewById(R.id.forum_ask_title);
        EditText bodyText = view.findViewById(R.id.forum_ask_body);


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseManager firebaseManager = new FirebaseManager();
                IdManager idManager = new IdManager(getContext());
                String title = titleText.getText().toString();
                String body = bodyText.getText().toString();
                String id = idManager.generateRandomId();
                String path = "experiments/" + experiment.getId() + "/posts";

                HashMap<String, Object> data = new HashMap<>();

                data.put("title", title);
                data.put("body", body);
                data.put("author", idManager.getUserId());
                data.put("date", new Date());

                firebaseManager.set(path, id, data);
                dismiss();
            }
        });

        return view;
    }


}

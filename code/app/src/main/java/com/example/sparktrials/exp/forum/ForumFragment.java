package com.example.sparktrials.exp.forum;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.example.sparktrials.CustomList;
import com.example.sparktrials.FirebaseManager;
import com.example.sparktrials.R;
import com.example.sparktrials.models.Answer;
import com.example.sparktrials.models.Experiment;
import com.example.sparktrials.models.Profile;
import com.example.sparktrials.models.Question;

import java.util.ArrayList;

/**
 * This class represents the main forum fragment.
 */
public class ForumFragment extends Fragment {
    View view;
    Experiment experiment;
    private ArrayAdapter<Question> questionAdapter;
    private ListView questionList;
    private ForumViewModel forumManager;

    private Button askQuestionButton;

    /**
     * Constructor for ForumFragment
     * @param experiment an experiment to pass to the fragment.
     */
    public ForumFragment(Experiment experiment){
        this.experiment = experiment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_forum, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        forumManager = new ForumViewModel(experiment.getId());
        questionList = view.findViewById(R.id.question_list);
        askQuestionButton = view.findViewById(R.id.forum_ask_question_button);
        TextView noPostYet = view.findViewById(R.id.forum_no_post_yet);

        final Observer<ArrayList<Question>> questionObserver = new Observer<ArrayList<Question>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<Question> questions) {
                questionAdapter = new CustomQuestionList(getContext(), questions);
                questionList.setAdapter(questionAdapter);
                if (questionAdapter.getCount() == 0)
                    noPostYet.setVisibility(View.VISIBLE);
                else
                    noPostYet.setVisibility(View.INVISIBLE);

            }
        };
        forumManager.getQuestions().observe(this, questionObserver);

        askQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForumAskQuestionFragment forumAskQuestionFragment = ForumAskQuestionFragment.newInstance(experiment);
                forumAskQuestionFragment.show(getActivity().getSupportFragmentManager(), "ask_question_dialog_fragment");
            }
        });

        questionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ForumDetailFragment forumDetailFragment = ForumDetailFragment.newInstance(forumManager.getQuestions().getValue().get(position));
                forumDetailFragment.show(getActivity().getSupportFragmentManager(), "forum_post_dialog_fragment");
            }
        });

    }
}

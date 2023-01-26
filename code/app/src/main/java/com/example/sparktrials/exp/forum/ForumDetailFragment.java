package com.example.sparktrials.exp.forum;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.sparktrials.FirebaseManager;
import com.example.sparktrials.IdManager;
import com.example.sparktrials.R;
import com.example.sparktrials.models.Answer;
import com.example.sparktrials.models.Question;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * This class represents the fragment when you click on an forum post.
 */
public class ForumDetailFragment extends BottomSheetDialogFragment {
    private Question question;
    private ArrayAdapter<Answer> answerAdapter;

    /**
     * Constructor for ForumDetailFragment.
     * @param question a question to pass to the fragment.
     */
    public ForumDetailFragment(Question question) {
        this.question = question;
    }

    /**
     * Creates a new instance of ForumDetailFragment.
     * @param question a question to pass to the new instance.
     * @return a new instance of ForumDetailFragment.
     */
    public static ForumDetailFragment newInstance(Question question) {
        return new ForumDetailFragment(question);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forum_post_detail, container, false);
        answerAdapter = new CustomAnswerList(getContext(), question.getAnswers());
        ListView answerList = view.findViewById(R.id.forum_detail_post_list);
        TextView cancelButton = view.findViewById(R.id.forum_detail_cancel);
        TextView replyButton = view.findViewById(R.id.forum_detail_reply);
        EditText comment = view.findViewById(R.id.forum_detail_add_comment);
        TextView title = view.findViewById(R.id.forum_detail_title);
        TextView noReplyYet = view.findViewById(R.id.forum_detail_no_reply_yet);

        title.setText(question.getTitle());
        answerList.setAdapter(answerAdapter);

        // Show no reply text if the post has no reply.
        if (answerAdapter.getCount() == 0)
            noReplyYet.setVisibility(View.VISIBLE);
        else
            noReplyYet.setVisibility(View.INVISIBLE);


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseManager firebaseManager = new FirebaseManager();
                IdManager idManager = new IdManager(getContext());
                String body = comment.getText().toString();
                String id = idManager.generateRandomId();
                String path = "experiments/" + question.getExpId() + "/posts/" + question.getId() + "/comments";

                HashMap<String, Object> data = new HashMap<>();

                data.put("body", body);
                data.put("author", idManager.getUserId());
                data.put("date", new Date());

                firebaseManager.set(path, id, data);
                dismiss();
            }
        });


        getDialog().setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                View bottomSheetInternal = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
                BottomSheetBehavior.from(bottomSheetInternal).setDraggable(false);
            }
        });

        return view;
    }
}
